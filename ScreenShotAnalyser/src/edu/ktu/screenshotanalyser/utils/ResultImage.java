package edu.ktu.screenshotanalyser.utils;

import java.io.File;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ResultImage
{
	public ResultImage(File sourceImageFile)
	{
		this.image = Imgcodecs.imread(sourceImageFile.getAbsolutePath());
	}

	public void drawBounds(Rect bounds)
	{
		Imgproc.rectangle(this.image, bounds.br(), bounds.tl(), new Scalar(0, 255, 0), 2);
	}
	
	public void drawText(String text, Rect bounds)
	{
		if (text.length() > 0)
		{
			Imgproc.putText(this.image, text, bounds.tl(), Imgproc.FONT_ITALIC, 0.7, new Scalar(255));
		}
	}

	public void save(String fileName)
	{
		File resultFile = new File(fileName);

		Imgcodecs.imwrite(resultFile.getAbsolutePath(), this.image);
	}

	private Mat image;
}
