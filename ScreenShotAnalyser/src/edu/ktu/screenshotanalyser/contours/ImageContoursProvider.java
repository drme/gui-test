package edu.ktu.screenshotanalyser.contours;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
//import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageContoursProvider implements IImageContoursProvider
{
	static
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public List<Rect> getContours(File imageFile)
	{
		Mat original = Imgcodecs.imread(imageFile.getAbsolutePath());

		Mat gray = new Mat();
		Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);

		//Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-1.png").getAbsolutePath(), gray);
		
		
		Mat gradient = new Mat();
		Mat morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
		Imgproc.morphologyEx(gray, gradient, Imgproc.MORPH_GRADIENT, morphStructure);

	//	Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-2.png").getAbsolutePath(), gradient);
		
		Mat binary = new Mat();
		Imgproc.threshold(gradient, binary, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

		//Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-3.png").getAbsolutePath(), binary);
		
		Mat closed = new Mat();
		morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 1));
		Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, morphStructure);

	//	Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-4.png").getAbsolutePath(), closed);
		
		//Mat mask = Mat.zeros(binary.size(), CvType.CV_8UC1);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(closed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

		List<Rect> result = new ArrayList<>();
		
		for (MatOfPoint contour : contours)
		{
			Rect bounds = Imgproc.boundingRect(contour);

			if ((bounds.height) >= 10 && (bounds.width >= 10))
			{
				result.add(bounds);
			}
		}
		
		return result;		
	}
}
