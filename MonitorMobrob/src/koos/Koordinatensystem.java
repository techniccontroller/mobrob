package koos;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.awt.image.*;

/**
 * Diese Klasse stellt ein zweidimensionales Koordinatensystem in einem Fenster
 * dar.
 */
public class Koordinatensystem extends JFrame {
	private Grafikpanel graf;

	/**
	 * Erzeugt ein kartesisches Koordinatensystem mit dem Ursprung in der Mitte der
	 * Grafik
	 * 
	 * @param width  Breite der Grafik in Pixeln
	 * @param height Hoehe der Grafik in Pixeln
	 * @param xmax   groesster positiver Wert in x-Richtung
	 * @param ymax   groesster positiver Wert in y-Richtung
	 */
	public Koordinatensystem(int width, int height, int xmax, int ymax) {
		super("Koordinatensystem");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(graf = new Grafikpanel(width, height, xmax, ymax));
		pack();
		setVisible(true);
	}

	/**
	 * Zeichnet eine Linie zwischen den zwei angegebenen Punkten<br>
	 * Wenn das Zeichnen beendet ist, sollte repaint() aufgerufen werden, um
	 * sicherzustellen, dass die Linien direkt gezeichnet werden.
	 * 
	 * @param x1 x-Koordinate des 1. Punktes
	 * @param y1 y-Koordinate des 1. Punktes
	 * @param x2 x-Koordinate des 2. Punktes
	 * @param y2 y-Koordinate des 2. Punktes
	 */
	public void drawLine(double x1, double y1, double x2, double y2) {
		graf.drawLine(x1, y1, x2, y2);
	}

	/**
	 * Loescht den Inhalt des Koordinatensystems
	 */
	public void clear() {
		graf.clear();
	}

	public void drawDot(double x, double y, double width, double height) {
		graf.drawDot((int) x, (int) y, (int) width, (int) height);
	}

	public static void main(String[] args) throws Exception {
		int max = 8;
		Koordinatensystem k = new Koordinatensystem(400, 400, max, 8);
		k.drawLine(0, 0, 8, 8);
		Thread.sleep(1000);
		// clear();
		k.drawLine(0, 0, 8, 4);
		k.repaint();
	}

	private class Grafikpanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private int width, height;
		private int xmax, ymax;
		private double scalex, scaley;
		private AffineTransform transform;
		private BufferedImage offImg;
		private Graphics2D offGraph;

		private Grafikpanel(int width, int height, int xmax, int ymax) {
			this.width = width;
			this.height = height;
			this.xmax = xmax;
			this.ymax = ymax;
			this.scalex = width / 2.0 / xmax;
			this.scaley = height / 2.0 / ymax;

			int xshift = width / 2;
			int yshift = height / 2;
			int xmult = 1;
			int ymult = -1;

			transform = new AffineTransform(xmult, 0, 0, ymult, xshift, yshift);

			offImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			offGraph = offImg.createGraphics();

			clear();
		}

		public void clear() {
			offGraph.setTransform(new AffineTransform());
			offGraph.setColor(Color.WHITE);
			offGraph.fillRect(0, 0, width, height);
			drawCross(offGraph);
			repaint();
		}

		public Dimension getMinimumSize() {
			return new Dimension(width, height);
		}

		public Dimension getPreferredSize() {
			return new Dimension(width, height);
		}

		private void drawLine(double x1, double y1, double x2, double y2) {
			offGraph.drawLine((int) (x1 * scalex), (int) (y1 * scaley), (int) (x2 * scalex), (int) (y2 * scaley));
		}

		private void drawDot(double x, double y, double width, double height) {
			offGraph.drawOval((int) (x * scalex), (int) (y * scaley), (int) (width * scalex), (int) (height * scaley));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(offImg, 0, 0, this);
		}

		private void drawCross(Graphics2D g) {
			int w = this.width / 2;
			int h = this.height / 2;
			int pad = 0;

			g.setTransform(new AffineTransform());
			g.setColor(Color.black);
			g.drawString("x", 2 * w - 20, h - 10);
			g.drawString("y", w + 10, 20);

			g.drawString("" + (ymax / 2), w - 20, (h / 2) + 5);
			g.drawString("" + (xmax / 2), (3 * w / 2) - 5, h + 20);

			// ----- transform --------
			g.setTransform(transform);

			g.drawLine(0, -h, 0, h); // y
			g.drawLine(-w, 0, w, 0); // x

			g.drawLine(-5, h - 10, 0, h); // y-Pfeil
			g.drawLine(5, h - 10, 0, h);
			g.drawLine(w - 10, 5, w, 0); // x-Pfeil
			g.drawLine(w - 10, -5, w, 0);

			int tics, len;
			g.setColor(Color.gray);
			tics = 10;
			len = 4;
			for (int i = -w + tics; i <= w - tics; i += tics) // y-Unterteilung
				g.drawLine(-len, i, len, i);
			for (int i = -w + tics; i <= w - tics; i += tics) // x-Unterteilung
				g.drawLine(i, -len, i, len);
			g.setColor(Color.black);
			tics = 50;
			len = 8;
			for (int i = -w + tics; i <= w - tics; i += tics) // y-Unterteilung
				g.drawLine(-len, i, len, i);
			for (int i = -w + tics; i <= w - tics; i += tics) // x-Unterteilung
				g.drawLine(i, -len, i, len);
		}
	}
}
