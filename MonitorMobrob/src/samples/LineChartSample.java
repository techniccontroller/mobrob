package samples;

import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import javafx.animation.AnimationTimer;

import javafx.animation.Timeline;

import javafx.application.Application;

import javafx.scene.Scene;

import javafx.scene.chart.LineChart;

import javafx.scene.chart.NumberAxis;

import javafx.scene.chart.CategoryAxis;

import javafx.scene.chart.XYChart;

import javafx.scene.chart.XYChart.Series;

import javafx.stage.Stage;

import java.text.SimpleDateFormat;

import java.util.Date;



public class LineChartSample extends Application {

    private static final int MAX_DATA_POINTS = 15;



    private Series series1;

    private Series series2;

    private ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<Number>();

    private ConcurrentLinkedQueue<Number> dataQ2 = new ConcurrentLinkedQueue<Number>();

    private ExecutorService executor;

    private AddToQueue addToQueue;

    private Timeline timeline2;

    private CategoryAxis xAxis;



    private void init(Stage primaryStage) {

        xAxis = new CategoryAxis();

        xAxis.setTickLabelsVisible(false);



        NumberAxis yAxis = new NumberAxis();

        yAxis.setAutoRanging(true);



        //-- Chart

        final LineChart<String, Number> sc = new LineChart<String, Number>(xAxis, yAxis) {

            // Override to remove symbols on each data point

            @Override protected void dataItemAdded(Series<String, Number> series, int itemIndex, Data<String, Number> item) {}

        };

        sc.setAnimated(false);

        //sc.setId("liveAreaChart");

        //sc.setTitle("Animated Area Chart");



        //-- Chart Series

        series1 = new XYChart.Series<String, Number>();

        series1.setName("1");

        sc.getData().add(series1);



        series2 = new XYChart.Series<String, Number>();

        series2.setName("2");

        sc.getData().add(series2);



        primaryStage.setScene(new Scene(sc));

    }



    @Override public void start(Stage primaryStage) throws Exception {

        init(primaryStage);

        primaryStage.show();



        //-- Prepare Executor Services

        executor = Executors.newCachedThreadPool();

        addToQueue = new AddToQueue();

        executor.execute(addToQueue);

        //-- Prepare Timeline

        prepareTimeline();

    }



    public static void main(String[] args) {

        launch(args);

    }



    private class AddToQueue implements Runnable {

        @Override
		public void run() {

            try {

                // add a item of random data to queue

                dataQ1.add(Math.random());

                dataQ2.add(Math.random());

                Thread.sleep(100);

                executor.execute(this);

            } catch (InterruptedException ex) {
            	ex.printStackTrace();
                //Logger.getLogger(AreaChartSample.class.getName()).log(Level.SEVERE, null, ex);

            }

        }

    }



    //-- Timeline gets called in the JavaFX Main thread

    private void prepareTimeline() {

        // Every frame to take any data from queue and add to chart

        new AnimationTimer() {

            @Override public void handle(long now) {

                addDataToSeries();

            }

        }.start();

    }



    private void addDataToSeries() {

 	      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:S");

        String strDate = dateFormat.format(new Date());



        for (int i = 0; i < dataQ1.size(); i++) {

            series1.getData().add(new XYChart.Data(strDate, dataQ1.remove()));

        }

      	for (int i = 0; i < dataQ2.size(); i++) {

                  series2.getData().add(new XYChart.Data(strDate, dataQ2.remove()));

      	}



        // remove points to keep us at no more than MAX_DATA_POINTS

        if (series1.getData().size() > MAX_DATA_POINTS) {

            series1.getData().remove(0, series1.getData().size() - MAX_DATA_POINTS);

        }



        if (series2.getData().size() > MAX_DATA_POINTS) {

            series2.getData().remove(0, series2.getData().size() - MAX_DATA_POINTS);

        }

        // update

        //xAxis.setLowerBound(xSeriesData-MAX_DATA_POINTS);

        //xAxis.setUpperBound(xSeriesData-1);

    }

}
