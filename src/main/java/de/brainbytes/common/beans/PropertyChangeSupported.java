
package de.brainbytes.common.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Fabian Schink
 *
 */
public interface PropertyChangeSupported {

  /**
   * @return the pcs
   */
  PropertyChangeSupport getPcs();

  /**
   * @param listener
   * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
   */
  default void addPropertyChangeListener(final PropertyChangeListener listener) {
    getPcs().addPropertyChangeListener(listener);
  }

  /**
   * @param listener
   * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
   */
  default void removePropertyChangeListener(final PropertyChangeListener listener) {
    getPcs().removePropertyChangeListener(listener);
  }

  /**
   * @return
   * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners()
   */
  default PropertyChangeListener[] getPropertyChangeListeners() {
    return getPcs().getPropertyChangeListeners();
  }

  /**
   * @param propertyName
   * @param listener
   * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String,
   *      java.beans.PropertyChangeListener)
   */
  default void addPropertyChangeListener(final String propertyName,
      final PropertyChangeListener listener) {
    getPcs().addPropertyChangeListener(propertyName, listener);
  }

  /**
   * @param propertyName
   * @param listener
   * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String,
   *      java.beans.PropertyChangeListener)
   */
  default void removePropertyChangeListener(final String propertyName,
      final PropertyChangeListener listener) {
    getPcs().removePropertyChangeListener(propertyName, listener);
  }

  /**
   * @param propertyName
   * @return
   * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners(java.lang.String)
   */
  default PropertyChangeListener[] getPropertyChangeListeners(final String propertyName) {
    return getPcs().getPropertyChangeListeners(propertyName);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, java.lang.Object,
   *      java.lang.Object)
   */
  default void firePropertyChange(final String propertyName, final Object oldValue,
      final Object newValue) {
    getPcs().firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, int, int)
   */
  default void firePropertyChange(final String propertyName, final int oldValue,
      final int newValue) {
    getPcs().firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, boolean, boolean)
   */
  default void firePropertyChange(final String propertyName, final boolean oldValue,
      final boolean newValue) {
    getPcs().firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * @param event
   * @see java.beans.PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent)
   */
  default void firePropertyChange(final PropertyChangeEvent event) {
    getPcs().firePropertyChange(event);
  }

  /**
   * @param propertyName
   * @param index
   * @param oldValue
   * @param newValue
   * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String, int,
   *      java.lang.Object, java.lang.Object)
   */
  default void fireIndexedPropertyChange(final String propertyName, final int index,
      final Object oldValue, final Object newValue) {
    getPcs().fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param index
   * @param oldValue
   * @param newValue
   * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String, int, int,
   *      int)
   */
  default void fireIndexedPropertyChange(final String propertyName, final int index,
      final int oldValue, final int newValue) {
    getPcs().fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param index
   * @param oldValue
   * @param newValue
   * @see java.beans.PropertyChangeSupport#fireIndexedPropertyChange(java.lang.String, int, boolean,
   *      boolean)
   */
  default void fireIndexedPropertyChange(final String propertyName, final int index,
      final boolean oldValue, final boolean newValue) {
    getPcs().fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @return
   * @see java.beans.PropertyChangeSupport#hasListeners(java.lang.String)
   */
  default boolean hasListeners(final String propertyName) {
    return getPcs().hasListeners(propertyName);
  }



}
