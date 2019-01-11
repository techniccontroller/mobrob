package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ImageProcessing {
	public ImageProcessing() {
		
	}
	
	public static BufferedImage process(BufferedImage image) {
		Mat frame = matify(image);
		
		//Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
		

		return Utils.matToBufferedImage(frame);
	}
	
	public static Mat matify(BufferedImage im) {
	    // Convert INT to BYTE
	    //im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
	    // Convert bufferedimage to byte array
	    byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer())
	            .getData();

	    // Create a Matrix the same size of image
	    Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
	    // Fill Matrix with image values
	    image.put(0, 0, pixels);

	    return image;
	}
}
