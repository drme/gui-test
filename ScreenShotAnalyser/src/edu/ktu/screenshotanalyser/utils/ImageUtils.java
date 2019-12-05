package edu.ktu.screenshotanalyser.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageUtils
{
	public static BufferedImage matToBufferedImage(Mat source)
	{
		try
		{
			MatOfByte bytes = new MatOfByte();

			Imgcodecs.imencode(".png", source, bytes);

			return ImageIO.read(new ByteArrayInputStream(bytes.toArray()));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			
			return null;
		}
	}
}
