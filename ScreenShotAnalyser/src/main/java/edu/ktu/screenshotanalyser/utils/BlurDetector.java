package edu.ktu.screenshotanalyser.utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BlurDetector
{
	public static boolean isBlurry(String fileName)
	{
    var src = Imgcodecs.imread(fileName);
	    
    return getBlurryVariance(src) < 100.0; 
	}

	public static double getBlurryVariance(Mat source)
	{
		var gray = new Mat();
		Imgproc.cvtColor(source, gray, Imgproc.COLOR_BGR2GRAY);

		var destination = new Mat();
		Imgproc.Laplacian(gray, destination, 3);//CvType.CV_64F);
		
		var mean = new MatOfDouble();
		var standardDeviation = new MatOfDouble();
		Core.meanStdDev(destination, mean, standardDeviation);

		return Math.pow(standardDeviation.get(0,0)[0], 2);		
	}
}
