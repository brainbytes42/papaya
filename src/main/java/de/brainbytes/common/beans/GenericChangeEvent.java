
package de.brainbytes.common.beans;

import java.beans.PropertyChangeEvent;

/**
 * @author Fabian Schink
 *
 * @param <P> Enum declaring supported properties.
 * @param <S> The type of the propagated event's source.
 * @param <V> Type of the changed propertie's value.
 * 
 *        Inspired by
 *        {@link http://www.developer.com/java/other/article.php/3621276/Typed-and-Targeted-Property-Change-Events-in-Java.htm}.
 */
public class GenericChangeEvent<P extends Enum<P>, S, V> extends PropertyChangeEvent {


  private static final long serialVersionUID = -1890690460539143860L;

  /**
   * @param source
   * @param property
   * @param oldValue
   * @param newValue
   */
  public GenericChangeEvent(final S source, final P property, final V oldValue, final V newValue) {
    super(source, property.name(), oldValue, newValue);
  }

  /**
   * @param event
   * 
   *        TODO secure to do? / if fallback is necessary (it is!), is this beneficial then? in
   *        context of {@link GenericChangeSupport}, one could leave compatibility out... but not
   *        for relay-events?
   */
  @SuppressWarnings("unchecked")
  public GenericChangeEvent(final PropertyChangeEvent event, final Class<P> propertyType)
      throws NullPointerException, IllegalArgumentException, ClassCastException {
    this((S) event.getSource(), Enum.valueOf(propertyType, event.getPropertyName()),
        (V) event.getOldValue(), (V) event.getNewValue());
    setPropagationId(event.getPropagationId());
  }

  /**
   * @return
   * @see java.beans.PropertyChangeEvent#getNewValue()
   */
  @SuppressWarnings("unchecked") // ensured by constructor
  @Override
  public V getNewValue() {
    return (V) super.getNewValue();
  }

  /**
   * @return
   * @see java.beans.PropertyChangeEvent#getOldValue()
   */
  @SuppressWarnings("unchecked") // ensured by constructor
  @Override
  public V getOldValue() {
    return (V) super.getOldValue();
  }
}
