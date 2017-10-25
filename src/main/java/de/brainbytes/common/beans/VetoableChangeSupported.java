
package de.brainbytes.common.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

/**
 * @author Fabian Schink
 *
 */
public interface VetoableChangeSupported extends PropertyChangeSupported {

  /**
   * @return the vcs
   */
  VetoableChangeSupport getVcs();

  /**
   * @param listener
   * @see java.beans.VetoableChangeSupport#addVetoableChangeListener(java.beans.VetoableChangeListener)
   */
  default void addVetoableChangeListener(final VetoableChangeListener listener) {
    getVcs().addVetoableChangeListener(listener);
  }

  /**
   * @param listener
   * @see java.beans.VetoableChangeSupport#removeVetoableChangeListener(java.beans.VetoableChangeListener)
   */
  default void removeVetoableChangeListener(final VetoableChangeListener listener) {
    getVcs().removeVetoableChangeListener(listener);
  }

  /**
   * @return
   * @see java.beans.VetoableChangeSupport#getVetoableChangeListeners()
   */
  default VetoableChangeListener[] getVetoableChangeListeners() {
    return getVcs().getVetoableChangeListeners();
  }

  /**
   * @param propertyName
   * @param listener
   * @see java.beans.VetoableChangeSupport#addVetoableChangeListener(java.lang.String,
   *      java.beans.VetoableChangeListener)
   */
  default void addVetoableChangeListener(final String propertyName,
      final VetoableChangeListener listener) {
    getVcs().addVetoableChangeListener(propertyName, listener);
  }

  /**
   * @param propertyName
   * @param listener
   * @see java.beans.VetoableChangeSupport#removeVetoableChangeListener(java.lang.String,
   *      java.beans.VetoableChangeListener)
   */
  default void removeVetoableChangeListener(final String propertyName,
      final VetoableChangeListener listener) {
    getVcs().removeVetoableChangeListener(propertyName, listener);
  }

  /**
   * @param propertyName
   * @return
   * @see java.beans.VetoableChangeSupport#getVetoableChangeListeners(java.lang.String)
   */
  default VetoableChangeListener[] getVetoableChangeListeners(final String propertyName) {
    return getVcs().getVetoableChangeListeners(propertyName);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   * @throws PropertyVetoException
   * @see java.beans.VetoableChangeSupport#fireVetoableChange(java.lang.String, java.lang.Object,
   *      java.lang.Object)
   */
  default void fireVetoableChange(final String propertyName, final Object oldValue,
      final Object newValue) throws PropertyVetoException {
    getVcs().fireVetoableChange(propertyName, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   * @throws PropertyVetoException
   * @see java.beans.VetoableChangeSupport#fireVetoableChange(java.lang.String, int, int)
   */
  default void fireVetoableChange(final String propertyName, final int oldValue, final int newValue)
      throws PropertyVetoException {
    getVcs().fireVetoableChange(propertyName, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   * @throws PropertyVetoException
   * @see java.beans.VetoableChangeSupport#fireVetoableChange(java.lang.String, boolean, boolean)
   */
  default void fireVetoableChange(final String propertyName, final boolean oldValue,
      final boolean newValue) throws PropertyVetoException {
    getVcs().fireVetoableChange(propertyName, oldValue, newValue);
  }

  /**
   * @param event
   * @throws PropertyVetoException
   * @see java.beans.VetoableChangeSupport#fireVetoableChange(java.beans.PropertyChangeEvent)
   */
  default void fireVetoableChange(final PropertyChangeEvent event) throws PropertyVetoException {
    getVcs().fireVetoableChange(event);
  }

  /**
   * @param propertyName
   * @return true if PropertyChangeSupport OR VetoableCHangeSupport have listeners for this
   *         property.
   * @see java.beans.PropertyChangeSupport#hasListeners(java.lang.String)
   * @see java.beans.VetoableChangeSupport#hasListeners(java.lang.String)
   */
  @Override
  default boolean hasListeners(final String propertyName) {
    return getPcs().hasListeners(propertyName) || getVcs().hasListeners(propertyName);
  }



}
