package koos;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;

public class KOOSCanvas extends Canvas {
	
	private double xmax, ymax;
	private double scalex, scaley;
	private Affine transform;
	private GraphicsContext gc;
	
	public KOOSCanvas() {
		this.gc = getGraphicsContext2D();
		this.xmax = 4000.0;
		this.ymax = 3000.0;
		this.scalex = 800 / 2.0 / xmax;
		this.scaley = 600 / 2.0 / ymax;

		double xshift = getWidth() / 2;
		double yshift = getHeight() / 2;
		double xmult = 1;
		double ymult = -1;

		transform = new Affine(xmult, 0, xshift, 0, ymult, yshift);
		//transform = new Translate(ymult, yshift);

		clear();
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
	
	public void drawDataPoint(double x, double y, double width, double height) {
		gc.setFill(Color.WHITE);
		gc.fillOval((x * scalex), (y * scaley), (width * scalex), (height * scaley));
	}
	
	public void drawCross() {
		double w = getWidth() / 2;
		double h = getHeight() / 2;
		int pad = 0;
		
		gc.setTransform(new Affine());
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(2);
		gc.setFill(Color.WHITE);
		gc.fillText("x", 2 * w - 20, h - 10);
		gc.fillText("y", w + 10, 20);

		gc.fillText("" + (ymax / 2), w - 50, (h / 2) + 5);
		gc.fillText("" + (xmax / 2), (3 * w / 2) - 5, h + 20);

		// ----- transform --------
		gc.setTransform(1, 0, 0, -1, getWidth()/2, getHeight()/2);
		gc.setStroke(Color.LIGHTGREY);
		gc.strokeLine(0, -h, 0, h); // y
		gc.strokeLine(-w, 0, w, 0); // x
		
		gc.strokeLine(-5, h - 10, 0, h); // y-Pfeil
		gc.strokeLine(5, h - 10, 0, h);
		gc.strokeLine(w - 10, 5, w, 0); // x-Pfeil
		gc.strokeLine(w - 10, -5, w, 0);

		int tics, len;
		gc.setStroke(Color.LIGHTGREY);
		tics = 10;
		len = 4;
		for (double i = -w + tics; i <= w - tics; i += tics) // y-Unterteilung
			gc.strokeLine(-len, i, len, i);
		for (double i = -w + tics; i <= w - tics; i += tics) // x-Unterteilung
			gc.strokeLine(i, -len, i, len);
		gc.setStroke(Color.LIGHTGREY);
		tics = 50;
		len = 8;
		for (double i = -w + tics; i <= w - tics; i += tics) // y-Unterteilung
			gc.strokeLine(-len, i, len, i);
		for (double i = -w + tics; i <= w - tics; i += tics) // x-Unterteilung
			gc.strokeLine(i, -len, i, len);
	}
}
