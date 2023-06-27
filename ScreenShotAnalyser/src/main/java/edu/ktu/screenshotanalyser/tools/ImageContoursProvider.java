package edu.ktu.screenshotanalyser.tools;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
import org.opencv.features2d.MSER;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import marvin.MarvinDefinitions;
import marvin.image.MarvinImage;
import marvin.image.MarvinSegment;
import marvin.io.MarvinImageIO;

import static marvinplugins.MarvinPluginCollection.findTextRegions;

import org.opencv.core.MatOfRect;

//import marvin.MarvinPluginCollection.;

import org.opencv.core.MatOfPoint2f;

public class ImageContoursProvider
{
	public static boolean isOnlyImage(File imageFile)
	{
		var original = Imgcodecs.imread(imageFile.getAbsolutePath());
    var gray = new Mat(original.rows(), original.cols(), original.type());
    Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);
    var binary = new Mat(original.rows(), original.cols(), original.type(), new Scalar(0));
    Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY_INV);
    var contours = new ArrayList<MatOfPoint>();
    var hierarchy = new Mat();

    Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

    for (var contour : contours)
		{
			var bounds = Imgproc.boundingRect(contour);

			if ((bounds.height) >= 30 && (bounds.width >= 30))
			{
				return false;
			}
		}
		
		
		return true;
	}
	
	public List<Rect> getContours(File imageFile)
	{
		var result = new ArrayList<Rect>();

		
		//
		
	 //	var src = MarvinImageIO.loadImage("E:\\gui\\_analyzer_\\unpacked_apps\\com.trendind_mobile_apps.birthday_songs_2018-2.apk\\res\\drawable\\" + "new_bd_images1.png");
		
		
//		var image = MarvinImageIO.loadImage(imageFile.getAbsolutePath());
	//	var segments = marvinplugins.MarvinPluginCollection.findAllSubimages(src, image, 0.5);
		
		
//		return segments.stream().filter(p -> p.height >= 5).map(p -> new Rect(p.x1, p.y1, p.width, p.height)).toList();
		
		
		
		/*
		 * 
		 * 
		var original = Imgcodecs.imread(imageFile.getAbsolutePath());
		 * 
    Mat gray = new Mat(original.rows(), original.cols(), original.type());
    Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);
		
		

		int threshold = 50;

		Mat edges = new Mat();
		Imgproc.Canny(gray, edges , threshold, threshold*3);
		
		
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(edges, contours, hierarchy , Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		
		
		
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();

		for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
		    MatOfPoint contour = contours.get(idx);
		    Rect rect = Imgproc.boundingRect(contour);
		    double contourArea = Imgproc.contourArea(contour);
		    matOfPoint2f.fromList(contour.toList());
		    Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
		    long total = approxCurve.total();
		    if (total == 3) { // is triangle
		        // do things for triangle
		    }
		    if (total >= 4 && total <= 6) {
		        List<Double> cos = new ArrayList<>();
		        Point[] points = approxCurve.toArray();
		        for (int j = 2; j < total + 1; j++) {
		            cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
		        }
		        Collections.sort(cos);
		        Double minCos = cos.get(0);
		        Double maxCos = cos.get(cos.size() - 1);
		        boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
		        boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
		        if (isRect) {
		            double ratio = Math.abs(1 - (double) rect.width / rect.height);
		            drawText(rect.tl(), ratio <= 0.02 ? "SQU" : "RECT");
		            
		            result.add(rect);
		            
		        }
		        if (isPolygon) {
		            drawText(rect.tl(), "Polygon");
		            
		            result.add(rect);
		            
		        }
		    }
		}		
		
		return result.stream().filter(p -> p.width > 100 && p.height > 100).toList();
		
		
		*/
		
		
		var original = Imgcodecs.imread(imageFile.getAbsolutePath());
		 
		
	  Mat src = original;
    //Converting the source image to binary
    Mat gray = new Mat(src.rows(), src.cols(), src.type());
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
    Mat binary = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));
    Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY_INV);
    //Finding Contours
    List<MatOfPoint> contours = new ArrayList<>();
    Mat hierarchey = new Mat();
    Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_TREE,
    Imgproc.CHAIN_APPROX_SIMPLE);

    for (var contour : contours)
		{
			var bounds = Imgproc.boundingRect(contour);

			if ((bounds.height) >= 30 && (bounds.width >= 30))
			{
				result.add(bounds);
			}
		}
		
		
		return result;
		

		
		/*

		Imgproc.cvtColor(original, original, Imgproc.COLOR_RGB2GRAY);
		MSER mser = MSER.create();
		List<MatOfPoint> msers = new ArrayList<>();
		MatOfRect bboxes = new MatOfRect();
		mser.detectRegions(original, msers, bboxes);		
		
		
		
		return bboxes.toList();
		*/
		
	//	return getTextContours(imageFile);
		
		
		
		/*
		
		

		var original = Imgcodecs.imread(imageFile.getAbsolutePath());
		var gray = new Mat();
		Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-1.png").getAbsolutePath(), gray);
		
		var gradient = new Mat();
		var morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
		Imgproc.morphologyEx(gray, gradient, Imgproc.MORPH_GRADIENT, morphStructure);

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-2.png").getAbsolutePath(), gradient);
		
		var binary = new Mat();
		Imgproc.threshold(gradient, binary, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-3.png").getAbsolutePath(), binary);
		
		var closed = new Mat();
		morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 1));
		Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, morphStructure);

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-4.png").getAbsolutePath(), closed);
		
		//Mat mask = Mat.zeros(binary.size(), CvType.CV_8UC1);
		var contours = new ArrayList<MatOfPoint>();
		var hierarchy = new Mat();
		Imgproc.findContours(closed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

		for (var contour : contours)
		{
			var bounds = Imgproc.boundingRect(contour);

			if ((bounds.height) >= 10 && (bounds.width >= 10))
			{
				result.add(bounds);
			}
		}
		
		return result.stream().filter(p -> p.width > 100 && p.height > 100).toList();
		
		
		

		
		
		*/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		
		
		
		
		
		
		
		
		
		
		
		/////
		
		
		
		Mat mask = Mat.zeros(binary.size(), CvType.CV_8UC1);

		int countourIndex = 0;

		Imgproc.drawContours(mask, contours, countourIndex++, new Scalar(255, 255, 255), Core.FILLED);

*/
/*
		for (ExtractedText extractedText : request.getExtractedTexts()) {
			String text = extractedText.getText().trim();
			Rect bounds = extractedText.getArea();
			Imgproc.rectangle(original, bounds.br(), bounds.tl(), new Scalar(0, 255, 0), 2);

			if (text.length() > 0) {

				Imgproc.putText(original, text, bounds.tl(), Core.FONT_ITALIC, 1.0, new Scalar(255));
			}
		}*/
		
		/*
		
		File outFile = new File("d:/debug/", imageFile.getName() + "-5.png");

		Imgcodecs.imwrite(outFile.getAbsolutePath(), mask);

		*/
		
		
		/////
		
		
		
		
		
		
		
		
		
		
		
		
	//	return result;		
	}
	
	
	
	
	private double angle(Point pt1, Point pt2, Point pt0) {
    double dx1 = pt1.x - pt0.x;
    double dy1 = pt1.y - pt0.y;
    double dx2 = pt2.x - pt0.x;
    double dy2 = pt2.y - pt0.y;
    return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
}

private void drawText(Point ofs, String text) {
 //   Imgproc.putText(colorImage, text, ofs, Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255,255,25));
}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<Rect> getTextContours(File imageFile)
	{
		var image = MarvinImageIO.loadImage(imageFile.getAbsolutePath());
		var segments = findTextRegions(image, 15, 8, 30, 150);

		return segments.stream().filter(p -> p.height >= 5).map(p -> new Rect(p.x1, p.y1, p.width, p.height)).toList();
	}
}
