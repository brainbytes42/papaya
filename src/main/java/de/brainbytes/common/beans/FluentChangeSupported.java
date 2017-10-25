
package de.brainbytes.common.beans;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;

/**
 * @author Fabian Schink
 *
 */
public interface FluentChangeSupported<S, P extends Enum<P>> {

  /**
   * @return the changeSupport
   */
  FluentChangeSupport<S, P> getChangeSupport();

  /**
   * @param property
   * @param oldValue
   * @return
   * @see de.brainbytes.common.beans.FluentChangeSupport#doChange(java.lang.Enum, java.lang.Object)
   */
  default <V> FluentChangeSupport<S, P>.ChangeHandler<V> doChange(final P property,
      final V oldValue) {
    return getChangeSupport().doChange(property, oldValue);
  }

  /**
   * @param property
   * @param oldValue
   * @return
   * @see de.brainbytes.common.beans.FluentChangeSupport#doVetoableChange(java.lang.Enum,
   *      java.lang.Object)
   */
  default <V> FluentChangeSupport<S, P>.VetoableChangeHandler<V> doVetoableChange(final P property,
      final V oldValue) {
    return getChangeSupport().doVetoableChange(property, oldValue);
  }

  /**
   * @param gcs
   * @see de.brainbytes.common.beans.FluentChangeSupport#relayEventsFrom(de.brainbytes.common.beans.FluentChangeSupport)
   */
  default void relayEventsFrom(final FluentChangeSupport<?, ?> gcs) {
    getChangeSupport().relayEventsFrom(gcs);
  }

  /**
   * @param listener
   * @see de.brainbytes.common.beans.FluentChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
   */
  default void addPropertyChangeListener(final PropertyChangeListener listener) {
    getChangeSupport().addPropertyChangeListener(listener);
  }

  /**
   * @param listener
   * @see de.brainbytes.common.beans.FluentChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
   */
  default void removePropertyChangeListener(final PropertyChangeListener listener) {
    getChangeSupport().removePropertyChangeListener(listener);
  }

  /**
   * @param property
   * @param listener
   * @see de.brainbytes.common.beans.FluentChangeSupport#addPropertyChangeListener(java.lang.Enum,
   *      java.beans.PropertyChangeListener)
   */
  default void addPropertyChangeListener(final P property, final PropertyChangeListener listener) {
    getChangeSupport().addPropertyChangeListener(property, listener);
  }

  /**
   * @param property
   * @param listener
   * @see de.brainbytes.common.beans.FluentChangeSupport#removePropertyChangeListener(java.lang.Enum,
   *      java.beans.PropertyChangeListener)
   */
  default void removePropertyChangeListener(final P property,
      final PropertyChangeListener listener) {
    getChangeSupport().removePropertyChangeListener(property, listener);
  }

  /**
   * @param listener
   * @see de.brainbytes.common.beans.FluentChangeSupport#addVetoableChangeListener(java.beans.VetoableChangeListener)
   */
  default void addVetoableChangeListener(final VetoableChangeListener listener) {
    getChangeSupport().addVetoableChangeListener(listener);
  }

  /**
   * @param listener
   * @see de.brainbytes.common.beans.FluentChangeSupport#removeVetoableChangeListener(java.beans.VetoableChangeListener)
   */
  default void removeVetoableChangeListener(final VetoableChangeListener listener) {
    getChangeSupport().removeVetoableChangeListener(listener);
  }

  /**
   * @param property
   * @param listener
   * @see de.brainbytes.common.beans.FluentChangeSupport#addVetoableChangeListener(java.lang.Enum,
   *      java.beans.VetoableChangeListener)
   */
  default void addVetoableChangeListener(final P property, final VetoableChangeListener listener) {
    getChangeSupport().addVetoableChangeListener(property, listener);
  }

  /**
   * @param property
   * @param listener
   * @see de.brainbytes.common.beans.FluentChangeSupport#removeVetoableChangeListener(java.lang.Enum,
   *      java.beans.VetoableChangeListener)
   */
  default void removeVetoableChangeListener(final P property,
      final VetoableChangeListener listener) {
    getChangeSupport().removeVetoableChangeListener(property, listener);
  }



}
