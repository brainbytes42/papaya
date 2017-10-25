
package de.brainbytes.patterns;

import de.brainbytes.patterns.BuilderInheritanceDemo.Truck.TruckBuilder;
import de.brainbytes.patterns.BuilderInheritanceDemo.Vehicle.VehicleBuilder;

import java.util.Optional;

/**
 * @author Fabian Schink
 *
 */
public class BuilderInheritanceDemo {

  public static void main(final String[] args) {

    final Vehicle vehicle = new VehicleBuilder("DB5", "Aston Martin").ofColor("silver").build();
    System.out.println(vehicle);

    final Truck<Void> truck = new TruckBuilder<>("TGX D38", "MAN", Void.class).ofMaxSpeed(80.)
        .ofHazardWarningPanel("you better run").build();
    System.out.println(truck);

  }



  public static class Vehicle {

    /* non-optional attributes */
    private final String type, manufacturer;

    /* optional attributes */
    private final String color;
    private final Double maxSpeed;

    /*
     * builder has to be of it's base-type using generic wildcards, otherwise extending classes
     * couldn't call their super-constructor!
     */
    private Vehicle(final VehicleBuilderBase<?, ?> builder) {
      type = builder.type;
      manufacturer = builder.manufacturer;
      color = builder.color; // maybe null
      maxSpeed = builder.maxSpeed; // maybe null (Wrapper-type!)
    }

    public final String getType() {
      return type;
    }

    public final String getManufacturer() {
      return manufacturer;
    }

    public final Optional<String> getColor() {
      return Optional.ofNullable(color);
    }

    public final Optional<Double> getMaxSpeed() {
      return Optional.ofNullable(maxSpeed);
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + ": " + getType() + " (" + getManufacturer()
          + ") ~> color: " + getColor().orElse("no color") + " | maxSpeed: "
          + (getMaxSpeed().isPresent() ? getMaxSpeed().get() : "unknown");
    }

    /* for abstract class Vehicle: remove this Builder-Implementation */
    public static class VehicleBuilder extends VehicleBuilderBase<VehicleBuilder, Vehicle> {

      public VehicleBuilder(final String type, final String manufacturer) {
        super(type, manufacturer);
      }

      @Override
      public Vehicle build() {
        return new Vehicle(this);
      }

    }

    /**
     * @param <B> Builder-Implementation-type to be used for method-chaining
     * @param <T> Target-type to be created by this Builder
     */
    protected static abstract class VehicleBuilderBase<B extends VehicleBuilderBase<B, T>, T>
        implements Builder<T> {
      /* non-optional attributes */
      final String type, manufacturer;

      /* optional attributes */
      String color;
      Double maxSpeed;

      /**
       * 
       */
      protected VehicleBuilderBase(final String type, final String manufacturer) {
        this.type = type;
        this.manufacturer = manufacturer;
      }

      @SuppressWarnings("unchecked") // safe as of generic construction
      public B ofColor(final String color) {
        this.color = color;
        return (B) this;
      }

      @SuppressWarnings("unchecked") // safe as of generic construction
      public B ofMaxSpeed(final double maxSpeed) {
        this.maxSpeed = maxSpeed;
        return (B) this;
      }

    }

  }



  /**
   * @param <L> Type of the truck's load. Has nothing to do with builder-pattern.
   */
  public static class Truck<L> extends Vehicle {

    /* additional, non-optional attribute */
    final Class<L> loadType;

    /* additional, optional attribute */
    final String hazardWarningPanel;

    private Truck(final TruckBuilderBase<?, ?, L> builder) {
      super(builder);
      this.loadType = builder.loadType;
      this.hazardWarningPanel = builder.hazardWarningPanel;
    }

    public final Class<L> getLoadType() {
      return loadType;
    }

    public final Optional<String> getHazardWarningPanel() {
      return Optional.ofNullable(hazardWarningPanel);
    }

    @Override
    public String toString() {
      return super.toString() + " | loadType: " + getLoadType().getSimpleName()
          + " | hazardWarning: " + getHazardWarningPanel().orElse("harmless");
    }

    public static class TruckBuilder<L> extends TruckBuilderBase<TruckBuilder<L>, Truck<L>, L> {

      public TruckBuilder(final String type, final String manufacturer, final Class<L> loadType) {
        super(type, manufacturer, loadType);
      }

      @Override
      public Truck<L> build() {
        return new Truck<>(this);
      }

    }

    protected static abstract class TruckBuilderBase<B extends TruckBuilderBase<B, T, L>, T, L>
        extends VehicleBuilderBase<B, T> {

      /* additional, non-optional attribute */
      Class<L> loadType;

      /* additional, optional attribute */
      String hazardWarningPanel;

      protected TruckBuilderBase(final String type, final String manufacturer,
          final Class<L> loadType) {
        super(type, manufacturer);
        this.loadType = loadType;
      }

      @SuppressWarnings("unchecked") // safe as of generic construction
      public B ofHazardWarningPanel(final String hazardWarningPanel) {
        this.hazardWarningPanel = hazardWarningPanel;
        return (B) this;
      }

    }
  }


  /**
   * @param <T> Target-type to be created by this Builder
   */
  interface Builder<T> {

    T build();

  }
}
