package edu.ktu.screenshotanalyser.tools;

import java.awt.Color;
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
import marvin.MarvinDefinitions;
import marvin.image.MarvinImage;
import marvin.image.MarvinSegment;
import marvin.io.MarvinImageIO;
import static marvin.MarvinPluginCollection.*;

public class ImageContoursProvider
{
	static
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// strange...
    MarvinDefinitions.setImagePluginPath("E:\\Projects\\eclipse\\prj346\\gui-test\\ScreenShotAnalyser\\lib\\marvin\\marvin\\plugins\\image\\");
	}

	
	public List<Rect> getContours(File imageFile)
	{
		List<Rect> result = new ArrayList<>();

		
		MarvinImage image = MarvinImageIO.loadImage(imageFile.getAbsolutePath());


		List<MarvinSegment> segments = findText(image, 15, 8, 30, 150);
		
		for(MarvinSegment s:segments){
			if(s.height >= 5){
				
				result.add(new Rect(s.x1, s.y1, s.width, s.height));
				
				s.y1-=5;
				s.y2+=5;
				image.drawRect(s.x1, s.y1, s.x2-s.x1, s.y2-s.y1, Color.red);
				image.drawRect(s.x1+1, s.y1+1, (s.x2-s.x1)-2, (s.y2-s.y1)-2, Color.red);
			}
		}
	
		
		
		MarvinImageIO.saveImage(image, "d:/debug/" + imageFile.getName() + "-9.png");
		

		
		
		
		
		/*
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Mat original = Imgcodecs.imread(imageFile.getAbsolutePath());

		Mat gray = new Mat();
		Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-1.png").getAbsolutePath(), gray);
		
		
		Mat gradient = new Mat();
		Mat morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
		Imgproc.morphologyEx(gray, gradient, Imgproc.MORPH_GRADIENT, morphStructure);

	Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-2.png").getAbsolutePath(), gradient);
		
		Mat binary = new Mat();
		Imgproc.threshold(gradient, binary, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-3.png").getAbsolutePath(), binary);
		
		Mat closed = new Mat();
		morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 1));
		Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, morphStructure);

		Imgcodecs.imwrite(new File("d:/debug/", imageFile.getName() + "-4.png").getAbsolutePath(), closed);
		
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
		
		
		
		
		
		
		
		
		
		
		
		
		return result;		
	}
	
	
	
	public List<MarvinSegment> findText(MarvinImage image, int maxWhiteSpace, int maxFontLineWidth, int minTextWidth, int grayScaleThreshold){
		List<MarvinSegment> segments = findTextRegions(image, maxWhiteSpace, maxFontLineWidth, minTextWidth, grayScaleThreshold);
		
		return segments;
	}
	
	
	
	
	
	
	
	
	
	
}




/*



package edu.ktu.screenshotanalyser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageContoursProvider implements IImageContoursProvider {
	private static final Logger logger = Logger.getGlobal();

	public ImageContoursResponse getContours(ImageContoursProviderRequest request) {

		File file = request.getFile();
		Mat original = Imgcodecs.imread(file.getAbsolutePath());
		Mat gray = new Mat();
		Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);

		Mat gradient = new Mat();
		Mat morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
		Imgproc.morphologyEx(gray, gradient, Imgproc.MORPH_GRADIENT, morphStructure);

		Mat binary = new Mat();
		Imgproc.threshold(gradient, binary, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

		Mat closed = new Mat();
		morphStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 1));
		Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, morphStructure);

		Mat mask = Mat.zeros(binary.size(), CvType.CV_8UC1);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(closed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE,
				new Point(0, 0));
		final ImageContoursResponse response = new ImageContoursResponse();
		response.getContours().addAll(contours);
		return response;
	}

	public static class ImageContoursResponse {
		private final List<MatOfPoint> contours = new ArrayList<>();

		public List<MatOfPoint> getContours() {
			return contours;
		}

		public List<Rect> getBounds() {
			final List<Rect> rects = new ArrayList<>();
			for (final MatOfPoint contour : this.contours) {
				Rect bounds = Imgproc.boundingRect(contour);

				if ((bounds.height < 10 || bounds.width < 10)) {
					continue;
				}
				rects.add(bounds);
			}
			return rects;
		}
	}

	public static class ImageContoursProviderRequest {
		private final File file;

		public ImageContoursProviderRequest(final File file) {
			this.file = file;
		}

		public File getFile() {
			return file;
		}
	}
}



*/
