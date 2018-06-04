package sdfdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;

public class ScreenShotAnalyser
{
	public static void main(String[] args) throws IOException
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		for (final File fileEntry : new File("e:/2").listFiles())
		{
			if (false == fileEntry.isDirectory())
			{
				parseImage(fileEntry);
			}
		}
	};

	private static void parseImage(File file) throws IOException
	{
		System.out.println(file.getAbsolutePath());
		
		Mat original = Imgcodecs.imread(file.getAbsolutePath());

//	removeSystemElements(original);

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
		Imgproc.findContours(closed, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

		extractTexts(file, original, mask, contours);

		Imgcodecs.imwrite("e:/ff/" + file.getName(), original);
	};

	private static void removeSystemElements(Mat original)
	{
		Rect statusBarBounds = new Rect(0, 0, original.width(), 25);
		
		Imgproc.rectangle(original, statusBarBounds.br(), statusBarBounds.tl(), new Scalar(255, 0, 255), 25);
	};

	private static void extractTexts(File file, Mat original, Mat mask, List<MatOfPoint> contours) throws IOException
	{
		BufferedImage image = ImageIO.read(file);

		int countourIndex = 0;

		for (MatOfPoint contour : contours)
		{
			Rect bounds = Imgproc.boundingRect(contour);

			if ((bounds.height < 10 || bounds.width < 10))
			{
				continue;
			}

			//Mat maskROI = new Mat(mask, bounds);

			Imgproc.drawContours(mask, contours, countourIndex++, new Scalar(255, 255, 255), Core.FILLED);

			//double fillRatio = (double) Core.countNonZero(maskROI) / (bounds.width * bounds.height);

			//if (/* (rect.width / rect.height > 1) && */ (fillRatio > 0.45))
			{
				Imgproc.rectangle(original, bounds.br(), bounds.tl(), new Scalar(0, 255, 0), 2);

				String text = getTextFromScreenShot(image, bounds).trim();

				if (text.length() > 0)
				{
					 System.out.println("[" + text + "]");

					Imgproc.putText(original, text, bounds.tl(), Core.FONT_ITALIC, 1.0, new Scalar(255));
				}
			}
		//	else
			{
				//Imgproc.rectangle(original, bounds.br(), bounds.tl(), new Scalar(255, 255, 0), 2);
			}
		}
	};
	
	private static String getTextFromScreenShot(BufferedImage image, Rect area)
	{
		ITesseract instance = new Tesseract();
		instance.setLanguage("nor");

		try
		{
			List<Word> words = instance.getWords(image.getSubimage(area.x, area.y, area.width, area.height), 0);

			String result = "";

			for (Word word : words)
			{
				System.out.println("" + word.getConfidence() + " -> " + word.getText());

				if (word.getConfidence() > 85.0f)
				{
					result += " " + word.getText();
				}
			}

			return result.trim();
		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			
			return "";
		}
	};
};
