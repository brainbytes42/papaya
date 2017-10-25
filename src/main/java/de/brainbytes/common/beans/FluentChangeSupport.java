
package de.brainbytes.common.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Fabian Schink
 *
 * @param <S> The type of the propagated event's source.
 * @param <P> Enum declaring supported properties.
 */
public class FluentChangeSupport<S, P extends Enum<P>> implements Serializable {

  private static final long serialVersionUID = -1534073965453438406L;

  private final PropertyChangeSupport pcs;
  private final VetoableChangeSupport vcs;

  /**
   * Constructs a {@code GenericChangeSupport} object.
   *
   * @param sourceBean The bean to be given as the source for any events.
   * @throws NullPointerException If sourceBean is {@code null}
   */
  public FluentChangeSupport(final S sourceBean) {
    if (sourceBean == null) {
      throw new NullPointerException();
    }
    pcs = new PropertyChangeSupport(sourceBean);
    vcs = new VetoableChangeSupport(sourceBean);
  }

  /**
   * @param property
   * @param oldValue
   * @param <V> Type of the changed propertie's value.
   * @return
   */
  public <V> ChangeHandler<V> doChange(final P property, final V oldValue) {
    return new ChangeHandler<>(property, oldValue);
  }

  /**
   * @param property
   * @param oldValue
   * @param <V> Type of the changed propertie's value.
   * @return
   */
  public <V> VetoableChangeHandler<V> doVetoableChange(final P property, final V oldValue) {
    return new VetoableChangeHandler<>(property, oldValue);
  }



  public void relayEventsFrom(final FluentChangeSupport<?, ?> gcs) {
    // TODO
  }



  /**
   * @param listener
   * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public void addPropertyChangeListener(final PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  /**
   * @param listener
   * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public void removePropertyChangeListener(final PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  /**
   * @param property
   * @param listener
   * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String,
   *      java.beans.PropertyChangeListener)
   */
  public void addPropertyChangeListener(final P property,
      final PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(property.name(), listener);
  }

  /**
   * @param property
   * @param listener
   * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String,
   *      java.beans.PropertyChangeListener)
   */
  public void removePropertyChangeListener(final P property,
      final PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(property.name(), listener);
  }

  /**
   * @param event
   * @see java.beans.PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent)
   */
  private void firePropertyChange(final PropertyChangeEvent event) {
    pcs.firePropertyChange(event);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, java.lang.Object,
   *      java.lang.Object)
   */
  private void firePropertyChange(final P property, final Object oldValue,
      final Object newValue) {
    pcs.firePropertyChange(property.name(), oldValue, newValue);
  }

  /**
   * @param listener
   * @see java.beans.VetoableChangeSupport#addVetoableChangeListener(java.beans.VetoableChangeListener)
   */
  public void addVetoableChangeListener(final VetoableChangeListener listener) {
    vcs.addVetoableChangeListener(listener);
  }

  /**
   * @param listener
   * @see java.beans.VetoableChangeSupport#removeVetoableChangeListener(java.beans.VetoableChangeListener)
   */
  public void removeVetoableChangeListener(final VetoableChangeListener listener) {
    vcs.removeVetoableChangeListener(listener);
  }

  /**
   * @param property
   * @param listener
   * @see java.beans.VetoableChangeSupport#addVetoableChangeListener(java.lang.String,
   *      java.beans.VetoableChangeListener)
   */
  public void addVetoableChangeListener(final P property,
      final VetoableChangeListener listener) {
    vcs.addVetoableChangeListener(property.name(), listener);
  }

  /**
   * @param property
   * @param listener
   * @see java.beans.VetoableChangeSupport#removeVetoableChangeListener(java.lang.String,
   *      java.beans.VetoableChangeListener)
   */
  public void removeVetoableChangeListener(final P property,
      final VetoableChangeListener listener) {
    vcs.removeVetoableChangeListener(property.name(), listener);
  }

  /**
   * @param event
   * @throws PropertyVetoException
   * @see java.beans.VetoableChangeSupport#fireVetoableChange(java.beans.PropertyChangeEvent)
   */
  private void fireVetoableChange(final PropertyChangeEvent event) throws PropertyVetoException {
    vcs.fireVetoableChange(event);
  }



  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   * @throws PropertyVetoException
   * @see java.beans.VetoableChangeSupport#fireVetoableChange(java.lang.String, java.lang.Object,
   *      java.lang.Object)
   */
  private void fireVetoableChange(final P property, final Object oldValue,
      final Object newValue) throws PropertyVetoException {
    vcs.fireVetoableChange(property.name(), oldValue, newValue);
  }



  /**
   * @author Fabian Schink
   *
   * @param <V> Type of the changed propertie's value.
   */
  public class ChangeHandler<V> {

    protected final P property;
    protected final V oldValue;
    private V newValue;
    // private boolean announced;

    /**
     * @param genericChangeSupport
     * @param oldValue
     */
    protected ChangeHandler(final P property, final V oldValue) {
      this.property = property;
      this.oldValue = oldValue;
    }

    public ChangeResult to(final V newValue, final Consumer<V> setter) {
      this.newValue = newValue;
      setter.accept(newValue);
      announcePerformedChange();
      return new ChangeResult();
    }

    public void announcePerformedChange() {
      firePropertyChange(property, oldValue, newValue);
    }


    public class ChangeResult {

      private ChangeResult() {
        // empty
      }

    }
  }

  /**
   * @author Fabian Schink
   *
   * @param <V> Type of the changed propertie's value.
   */
  public class VetoableChangeHandler<V> extends ChangeHandler<V> {

    /**
     * @param genericChangeSupport
     * @param propertyName
     * @param oldValue
     */
    protected VetoableChangeHandler(
        final P property, final V oldValue) {
      super(property, oldValue);
    }

    /**
     * @param newValue
     * @param setter
     * @see de.brainbytes.common.beans.GenericChangeSupport.ChangeHandler#to(java.lang.Object,
     *      java.util.function.Consumer)
     */
    @Override
    public ChangeResult to(final V newValue, final Consumer<V> setter) {
      try {
        fireVetoableChange(property, oldValue, newValue);
        super.to(newValue, setter);
        return new ChangeResult(null);
      } catch (final PropertyVetoException ex) {
        return new ChangeResult(ex);
      }
    }

    public class ChangeResult extends ChangeHandler<V>.ChangeResult {

      private final PropertyVetoException veto;

      /**
       * @param ex
       */
      public ChangeResult(final PropertyVetoException veto) {
        this.veto = veto;
      }

      public void or(final Consumer<PropertyVetoException> vetoHandler) {
        if (veto != null) {
          vetoHandler.accept(veto);
        }
      }

      /**
       * @return the veto
       */
      @Deprecated
      public Optional<PropertyVetoException> getVeto() {
        return Optional.ofNullable(veto);
      }
    }
  }





  // TODO remove testing

  static class Foo {
    final FluentChangeSupport<Foo, Props> gcs = new FluentChangeSupport<>(this);

    int foo = 42;

    public final void setFoo(final int foo) {
      gcs.doChange(Props.FOO, this.foo).to(foo, value -> this.foo = value);
    }

    public final void setFooVeto(final int foo) {
      gcs.doVetoableChange(Props.FOO, this.foo).to(foo, value -> this.foo = value)
          .or(veto -> System.out.println("Veto: " + veto.getLocalizedMessage()));
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return getClass().getSimpleName() + ":" + Integer.toString(foo);
    }

    enum Props {
      FOO
    }
  }

  public static void main(final String[] args) {

    final Foo foo = new Foo();

    foo.gcs.addVetoableChangeListener(e -> {
      System.out.println("V1: " + e);
    });
    foo.gcs.addVetoableChangeListener(e -> {
      System.out.println("V2: " + e);
      if ((int) e.getNewValue() < 0) { // TODO make generic
        throw new PropertyVetoException("NO!", e);
      }
    });
    foo.gcs.addVetoableChangeListener(e -> {
      System.out.println("V3: " + e);
    });

    foo.gcs.addPropertyChangeListener(e -> {
      System.out.println("L1: " + e);
    });
    foo.gcs.addPropertyChangeListener(e -> {
      System.out.println("L2: " + foo);
    });

    foo.setFoo(21);
    System.out.println("==> Foo = " + foo);
    System.out.println("---");

    foo.setFooVeto(-42);
    System.out.println("==> Foo = " + foo);
    System.out.println("---");

    foo.setFooVeto(100);
    System.out.println("==> Foo = " + foo);
  }
}
