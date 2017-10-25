
package de.brainbytes.common.fx.scene.chart;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author Fabian Krippendorff
 *
 */
public class LassoScatterChartDemo extends Application {

  private static final int NUMBER_OF_SERIES = 2;
  private static final int POINTS_PER_SERIES = 10;


  public static void main(final String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {

    final LassoScatterChart<Number, Number> chart =
        new LassoScatterChart<>(new NumberAxis(), new NumberAxis());

    populateChart(chart);

    // demo for listening to selection
    chart.getCurrentSelectionUnmodifiable()
        .addListener((ListChangeListener<? super Data<Number, Number>>) dataChanged -> {
          while (dataChanged.next()) {
            System.out.printf("Current selection contains %d DataItems (%d added, %d removed).\n",
                chart.getCurrentSelectionUnmodifiable().size(), dataChanged.getAddedSize(),
                dataChanged.getRemovedSize());
          }
        });

    primaryStage.setScene(new Scene(new BorderPane(chart)));
    primaryStage.show();
  }


  /**
   * Populate Chart with random Dummy-Data.
   */
  private void populateChart(final ScatterChart<Number, Number> chart) {
    for (int i = 1; i <= NUMBER_OF_SERIES; i++) {
      final String seriesName = "Series " + (chart.getData().size() + 1);

      final ObservableList<Data<Number, Number>> dataPoints = FXCollections.observableArrayList();
      for (int j = 0; j < POINTS_PER_SERIES; j++) {
        dataPoints.add(new Data<Number, Number>(Math.random() * 2 - 1, Math.random() * 2 - 1));
      }

      chart.getData().add(new Series<>(seriesName, dataPoints));
    }
  }

}
