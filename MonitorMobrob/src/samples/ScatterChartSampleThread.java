package samples;

import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import javafx.animation.AnimationTimer;

import javafx.animation.Timeline;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

import javafx.scene.chart.XYChart.Series;

import javafx.stage.Stage;

import java.awt.Point;
import java.text.SimpleDateFormat;

import java.util.Date;

public class ScatterChartSampleThread extends Application {

	private static final int MAX_DATA_POINTS = 1000;

	private Series series1;

	private Series series2;

	private ConcurrentLinkedQueue<Point> dataQ1 = new ConcurrentLinkedQueue<Point>();

	private ConcurrentLinkedQueue<Point> dataQ2 = new ConcurrentLinkedQueue<Point>();

	private ExecutorService executor;

	private AddToQueue addToQueue;

	private Timeline timeline2;

	// private CategoryAxis xAxis;

	private void init(Stage primaryStage) {

		primaryStage.setTitle("Scatter Chart Sample with Thread");
		final NumberAxis xAxis = new NumberAxis(0, 1000, 100);
		final NumberAxis yAxis = new NumberAxis(0, 1000, 100);
		final ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);
		xAxis.setLabel("Age (years)");
		yAxis.setLabel("Returns to date");
		sc.setTitle("Investment Overview");

		sc.setAnimated(false);

		// -- Chart Series

		series1 = new XYChart.Series<Number, Number>();

		series1.setName("1");

		series2 = new XYChart.Series<Number, Number>();

		series2.setName("2");

		sc.getData().addAll(series1, series2);

		primaryStage.setScene(new Scene(sc));

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		init(primaryStage);

		primaryStage.show();

		// -- Prepare Executor Services

		executor = Executors.newCachedThreadPool();

		addToQueue = new AddToQueue();

		executor.execute(addToQueue);
		
	     

		// -- Prepare Timeline

		prepareTimeline();

	}

	public static void main(String[] args) {

		launch(args);

	}

	private class AddToQueue implements Runnable {

		@Override
		public void run() {

			try {
				Platform.runLater(new Runnable() {
                    @Override public void run() {
                    	for(int i = 0 ; i < 10000; i++) {
        					if (series1.getData().size() > i) {
        						//series1.getData().set(i, new XYChart.Data((int) (Math.random() * 1000), (int) (Math.random() * 1000)));
        						((XYChart.Data) series1.getData().get(i)).setXValue((int) (Math.random() * 1000));
        						((XYChart.Data) series1.getData().get(i)).setYValue((int) (Math.random() * 1000));
        					}
        					else{
        						//dataQ1.add(new Point((int) (Math.random() * 1000), (int) (Math.random() * 1000)));
        						series1.getData().add(new XYChart.Data((int) (Math.random() * 1000), (int) (Math.random() * 1000)));
        					}
        					
        					//dataQ2.add(new Point((int) (Math.random() * 1000), (int) (Math.random() * 1000)));
        				}
                    }
                });
				// add a item of random data to queue
				
				

				Thread.sleep(80);

				executor.execute(this);

			} catch (InterruptedException ex) {
				ex.printStackTrace();
				// Logger.getLogger(AreaChartSample.class.getName()).log(Level.SEVERE, null,
				// ex);

			}

		}

	}

	// -- Timeline gets called in the JavaFX Main thread

	private void prepareTimeline() {

		// Every frame to take any data from queue and add to chart

		new AnimationTimer() {

			@Override
			public void handle(long now) {

				addDataToSeries();
				System.out.println(now);
			}

		}.start();

	}

	private void addDataToSeries() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:S");

		String strDate = dateFormat.format(new Date());
		System.out.println(dataQ1.size());
		int len = dataQ1.size();
		for (int i = 0; i < len; i++) {

			//Point tempp = dataQ1.remove();

			//series1.getData().add(new XYChart.Data(tempp.getX(), tempp.getY()));

		}

		/*for (int i = 0; i < dataQ2.size(); i++) {
			Point tempp = dataQ2.remove();

			series2.getData().add(new XYChart.Data(tempp.getX(), tempp.getY()));

		}*/

		// remove points to keep us at no more than MAX_DATA_POINTS

		/*if (series1.getData().size() > MAX_DATA_POINTS) {

			series1.getData().remove(0, series1.getData().size() - MAX_DATA_POINTS);

		}

		if (series2.getData().size() > MAX_DATA_POINTS) {

			series2.getData().remove(0, series2.getData().size() - MAX_DATA_POINTS);

		}*/

		// update

		// xAxis.setLowerBound(xSeriesData-MAX_DATA_POINTS);

		// xAxis.setUpperBound(xSeriesData-1);

	}

}
