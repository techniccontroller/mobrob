package koos;


import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;

import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import javafx.scene.transform.Affine;


public class KOOSCanvas extends Canvas {
	
	private double xmax, ymax, ratio;
	private double scale;
	private GraphicsContext gc;
	
	public KOOSCanvas() {
		this.gc = getGraphicsContext2D();
		this.xmax = 4000.0;
		clear();
		
		setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				if(!((xmax < 500 && event.getDeltaY() < 0) || (xmax > 12000 && event.getDeltaY() > 0))) {
					xmax = xmax + ratio * event.getDeltaY()*3;
					scale = getHeight() / 2.0 / xmax;
					clear();
				}
				
			}
			
		});
	}
	
	public void clear() {
		gc.setTransform(new Affine());
		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.setFill(Color.DARKSLATEBLUE);
		gc.fillRect(0, 0, getWidth(), getHeight());
		drawCross();
		//drawSamples();
	}
	
	public void drawSamples() {
		// Canvas-Hintergrund als Rechteck löschen
		gc.clearRect(0, 0, getWidth(), getHeight());
		
		gc.setFill(Color.DARKSLATEBLUE);
		gc.fillRect(0, 0, getWidth(), getHeight());
		
		// Oval zeichnen
		gc.setStroke(Color.DARKGOLDENROD);
		gc.setLineWidth(4);
		gc.strokeOval(10, 20, 40, 40);

		// Abgerundetes Rechteck füllen
		gc.setFill(Color.BLUE);
		gc.fillRoundRect(60, 20, 40, 40, 10, 10);

		// Pfad definieren
		gc.setStroke(Color.FIREBRICK);
		gc.beginPath();
		gc.moveTo(110, 30);
		gc.lineTo(170, 20);
		gc.bezierCurveTo(150, 110, 130, 30, 110, 40);
		gc.closePath();

		// Pfad malen
		gc.stroke();

		// Gefülltes Tortenstück darstellen
		gc.setFill(Color.web("dodgerblue"));
		gc.fillArc(180, 30, 30, 30, 45, 270, ArcType.ROUND);
	}
	
	public void drawDataPoint(double x, double y, double width, double height, Color color) {
		gc.setFill(color);
		double lenAxisY = getWidth() / 2;
		double lenAxisX = getHeight() / 2;
		if(x*scale > -lenAxisX && x*scale < lenAxisX &&  y*scale > -lenAxisY && y*scale < lenAxisY)
			gc.fillOval(((x-width/2) * scale), ((y-height/2) * scale), (width * scale), (height * scale));
	}
	
	private void drawCircle(double radius) {
		gc.strokeOval(0-radius, 0-radius, radius*2, radius*2);
	}
	
	public void drawCross() {
		if(getWidth() > 0) {
			this.ymax = xmax * getWidth()*1.0/getHeight();
			this.scale = getHeight() / 2.0 / xmax;
			this.ratio = xmax/ymax;
			double lenAxisY = getWidth() / 2;
			double lenAxisX = getHeight() / 2;
			
			gc.setTransform(new Affine());
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(2);
			gc.setFill(Color.WHITE);
			gc.fillText("y", 20, lenAxisX - 10);
			gc.fillText("x", lenAxisY + 10, 20);
			
						
			// Add lables for 1000, 2000, 3000, 4000, ...
			for(int i = 1000; i < ymax || i < xmax; i+=1000) {
				gc.setTransform(0, -1, 1, 0, getWidth()/2, getHeight()/2);
				gc.setStroke(Color.BLACK);
				gc.setLineWidth(1);
				drawCircle(i*scale);
				// labeling x-Axis
				gc.setTransform(new Affine());
				gc.fillText("" + i, lenAxisY - 50, lenAxisX - i*scale + 5);
				
				// labeling y-Axis
				gc.setTransform(0, -1, 1, 0, getWidth()/2, getHeight()/2);
				gc.fillText("" + i, -50, -i*scale + 5);
			}
			
			gc.setTransform(0, -1, -1, 0, getWidth()/2, getHeight()/2);
								
			gc.setStroke(Color.LIGHTGREY);
			gc.strokeLine(0, -lenAxisY, 0, lenAxisY); // y
			gc.strokeLine(-lenAxisX, 0, lenAxisX, 0); // x
			
			gc.strokeLine(-5, lenAxisY - 10, 0, lenAxisY); // y-Pfeil
			gc.strokeLine(5, lenAxisY - 10, 0, lenAxisY);
			gc.strokeLine(lenAxisX - 10, 5, lenAxisX, 0); // x-Pfeil
			gc.strokeLine(lenAxisX - 10, -5, lenAxisX, 0);
	
			double tics, len;
			gc.setStroke(Color.LIGHTGREY);
			tics = (200 * scale);
			len = 4;
			for (double i = 0; i <= lenAxisY; i += tics) { // y-Unterteilung
				gc.strokeLine(-len, i, len, i);
				gc.strokeLine(-len, -i, len, -i);
			}
				
			for (double i = 0; i <= lenAxisY; i += tics) { // x-Unterteilung
				gc.strokeLine(i, -len, i, len);
				gc.strokeLine(-i, -len, -i, len);
			}
			gc.setStroke(Color.LIGHTGREY);
			tics = (1000 * scale);
			len = 8;
			for (double i = 0; i <= lenAxisY; i += tics) { // y-Unterteilung
				gc.strokeLine(-len, i, len, i);
				gc.strokeLine(-len, -i, len, -i);
			}
				
			for (double i = 0; i <= lenAxisY; i += tics) { // x-Unterteilung
				gc.strokeLine(i, -len, i, len);
				gc.strokeLine(-i, -len, -i, len);
			}
		}
	}
}
