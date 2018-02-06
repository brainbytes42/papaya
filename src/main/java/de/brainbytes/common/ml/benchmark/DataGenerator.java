package de.brainbytes.common.ml.benchmark;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataGenerator {

    public static void main(String[] args) {


        Map<double[], String> data = new DataGenerator().generateConcentricCircles(3, 0.3, 500, 0.05, true);

        data = new DataGenerator().generateInterleavedMoons(500, 0.05);

        printForMatlab(data);
    }

    private static void printForMatlab(Map<double[], String> data) {
        ArrayList<double[]> coordinates = new ArrayList<>(data.keySet());

        NumberFormat format = DecimalFormat.getInstance(Locale.US);
        String[] dim = new String[]{"x", "y"};
        IntStream.range(0, 2)
                 .forEach(i -> System.out.println(coordinates.stream()
                                                             .map(coord -> coord[i])
                                                             .map(format::format)
                                                             .collect(Collectors.joining("; ", dim[i] + " = [ ",
                                                                                         " ];"))));
        System.out.println(coordinates.stream()
                                      .map(data::get)
                                      .collect(Collectors.joining("; ", "labels = [ ", " ];")));
    }

    private Map<double[], String> generateConcentricCircles(int numberOfCircles,
                                                            double innerRadius,
                                                            int outerCirclePoints,
                                                            double variance,
                                                            boolean homogeneousDensity) {

        Map<double[], String> circles = new LinkedHashMap<>();

        Random random = new Random();
        IntStream.range(0, numberOfCircles).forEach(circleNumber -> {
            double r = 1 - circleNumber * (1 - innerRadius) / Math.max(numberOfCircles - 1, 1);
            int numberOfPoints = homogeneousDensity ? (int) (r * outerCirclePoints) : outerCirclePoints;
            IntStream.range(0, numberOfPoints)
                     .mapToDouble(i -> (i * 2 * Math.PI / numberOfPoints) - 0.5 * Math.PI)
                     .forEach(angle -> {
                         double x = r * Math.cos(angle) + (variance * random.nextGaussian());
                         double y = r * Math.sin(angle) + (variance * random.nextGaussian());
                         circles.put(new double[]{x, y}, Integer.toString(circleNumber));
                     });
        });

        return circles;
    }

    private Map<double[], String> generateInterleavedMoons(int pointsPerMoon, double variance) {
        Map<double[], String> circle = generateConcentricCircles(1, 0, 2 * pointsPerMoon, variance, true);
        Map<double[], String> moons = new LinkedHashMap<>(circle.size());

        new ArrayList<>(circle.keySet()).subList(0, pointsPerMoon)
                                        .forEach(xy -> moons.put(new double[]{xy[0] - 0.25, xy[1] + 0.5}, "0"));
        new ArrayList<>(circle.keySet()).subList(pointsPerMoon, circle.size())
                                        .forEach(xy -> moons.put(new double[]{xy[0] + 0.25, xy[1] - 0.5}, "1"));

        return moons;
    }

}
