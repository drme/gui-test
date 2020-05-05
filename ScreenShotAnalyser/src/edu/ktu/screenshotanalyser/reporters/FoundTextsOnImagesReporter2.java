package edu.ktu.screenshotanalyser.reporters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import edu.ktu.screenshotanalyser.Screen;
import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;

public class FoundTextsOnImagesReporter2 {
	private static final Logger logger = Logger.getGlobal();

	public void save(Screen screen, String outFile) throws Throwable {
		List<MatOfPoint> contours = screen.contours;
		File file = screen.originalFile;
		Mat original = Imgcodecs.imread(file.getAbsolutePath());

		// removeSystemElements(original);

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

		int countourIndex = 0;

		Imgproc.drawContours(mask, contours, countourIndex++, new Scalar(255, 255, 255), Core.FILLED);

		for (ExtractedText extractedText : screen.extractedTexts) {
			String text = extractedText.getText().trim();
			Rect bounds = extractedText.getArea();
			Imgproc.rectangle(original, bounds.br(), bounds.tl(), new Scalar(0, 255, 0), 2);

			if (text.length() > 0) {

				Imgproc.putText(original, text, bounds.tl(), Imgproc.FONT_ITALIC, 1.0, new Scalar(255));
			}
		}
		// File outFile = new File(outDir, request.getOutFile() + ".png");

		Imgcodecs.imwrite(outFile, original);
		logger.info(String.format("Wrote png file to: %s", outFile));
	}

	public static class AppExtractInfo {
		final transient List<MatOfPoint> contours = new ArrayList<>();
		final List<ExtractedText> extractedTexts = new ArrayList<>();
		final transient File originalFile;
		private final String configName;
		private final String appName;

		public String getConfigName() {
			return configName;
		}

		public String getAppName() {
			return appName;
		}

		public String getShortFileName() {
			return shortFileName;
		}

		public String getOutFile() {
			return this.baseDir + "//" + org.apache.commons.io.FilenameUtils.removeExtension(this.shortFileName);
		}

		private final String shortFileName;
		private String baseDir;

		public String getBaseName() {
			return baseDir;
		}

		public List<MatOfPoint> getContours() {
			return contours;
		}

		public List<ExtractedText> getExtractedTexts() {
			return extractedTexts;
		}

		public File getOriginalFile() {
			return originalFile;
		}

		public AppExtractInfo(final File originalFile, final String baseName, final String appName,
				final String configName) {
			this.originalFile = originalFile;
			this.baseDir = baseName;
			this.shortFileName = originalFile.getName();
			this.appName = appName;
			this.configName = configName;
		}

	}
}
