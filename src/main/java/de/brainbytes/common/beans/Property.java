
package de.brainbytes.common.beans;

/**
 * @author Fabian Schink
 *
 */
public class Property<T> {

  private final String name;
  private final FluentChangeSupport<?, ?> changeSupport;
  private final boolean vetoable, nullable;
  private final boolean relayNestedChanges;
  private final Validator<T> validator;

  private T value;


  public static <T> Builder<T> builder(final String name,
      final FluentChangeSupport<?, ?> changeSupport, final T initialValue) {
    return new Builder<>(name, changeSupport, initialValue);
  }

  public static <T> Builder<T> builder(final String name,
      final FluentChangeSupport<?, ?> changeSupport) {
    return new Builder<>(name, changeSupport);
  }

  private Property(final Builder<T> builder) {
    this.name = builder.name;
    this.changeSupport = builder.changeSupport;
    this.value = builder.value;
    this.vetoable = builder.vetoable;
    this.nullable = builder.nullable;
    this.relayNestedChanges = builder.relayNestedChanges;
    this.validator = builder.validator;
  }


  /**
   * @return the value
   */
  public T getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(final T value) {
    this.value = value;
  }


  public static class Builder<T> {
    final String name;
    final FluentChangeSupport<?, ?> changeSupport;
    T value;
    boolean vetoable, nullable, relayNestedChanges;
    Validator<T> validator;


    /**
     * @param name
     * @param changeSupport
     */
    private Builder(final String name, final FluentChangeSupport<?, ?> changeSupport,
        final T initialValue) {
      if (name == null) {
        throw new NullPointerException("Name may not be null for Property!");
      }
      if (changeSupport == null) {
        throw new NullPointerException("ChangeSupport may not be null for Property!");
      }
      if (initialValue == null) {
        throw new NullPointerException("Initial value may not be null for non-nullable Property!");
      }

      this.name = name;
      this.changeSupport = changeSupport;
      this.value = initialValue;
    }

    /**
     * @param name
     * @param changeSupport
     */
    private Builder(final String name, final FluentChangeSupport<?, ?> changeSupport) {
      if (name == null) {
        throw new NullPointerException("Name may not be null for Property!");
      }
      if (changeSupport == null) {
        throw new NullPointerException("ChangeSupport may not be null for Property!");
      }

      this.name = name;
      this.changeSupport = changeSupport;

      // no value -> nullable
      nullable = true;

    }

    public Property<T> build() {
      return new Property<>(this);
    }

    public Builder<T> ofInitialValue(final T initialValue) {
      if (nullable || initialValue != null) {
        this.value = initialValue;
        return this;
      } else {
        throw new NullPointerException("Initial value may not be null for non-nullable Property!");
      }
    }

    public Builder<T> vetoable() {
      vetoable = true;
      return this;
    }

    public Builder<T> relayNestedChanges() {
      relayNestedChanges = true;
      return this;
    }

    public Builder<T> usingValidator(final Validator<T> validator) {
      this.validator = validator;
      return this;
    }

  }


  public interface Validator<T> {
    boolean isValid(T value);
  }
}
