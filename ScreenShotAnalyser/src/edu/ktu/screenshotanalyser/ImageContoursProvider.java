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
