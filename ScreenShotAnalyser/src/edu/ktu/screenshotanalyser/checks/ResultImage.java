package edu.ktu.screenshotanalyser.checks;

import java.awt.image.BufferedImage;
import java.io.File;
import org.opencv.core.CvType;
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

	public ResultImage(BufferedImage source)
	{
    byte[] data = ((java.awt.image.DataBufferByte) source.getRaster().getDataBuffer()).getData();
    Mat mat = new Mat(source.getHeight(), source.getWidth(), CvType.CV_8UC3);
    mat.put(0, 0, data);

    this.image = mat;
	}
	
	public void drawBounds(Rect bounds)
	{
		Imgproc.rectangle(this.image, bounds.br(), bounds.tl(), new Scalar(0, 255, 0), 2);
	}
	
	public void drawBounds(Rect bounds, int r, int g, int b)
	{
		Imgproc.rectangle(this.image, bounds.br(), bounds.tl(), new Scalar(r, g, b), 2);
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



/*


public void save(AppExtractInfo request) throws Throwable {
List<MatOfPoint> contours = request.getContours();
File file = request.getOriginalFile();
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

for (ExtractedText extractedText : request.getExtractedTexts()) {
	String text = extractedText.getText().trim();
	Rect bounds = extractedText.getArea();
	Imgproc.rectangle(original, bounds.br(), bounds.tl(), new Scalar(0, 255, 0), 2);

	if (text.length() > 0) {

		Imgproc.putText(original, text, bounds.tl(), Imgproc.FONT_ITALIC, 1.0, new Scalar(255));
	}
}
File outFile = new File(outDir, request.getOutFile() + ".png");

Imgcodecs.imwrite(outFile.getAbsolutePath(), original);
logger.info(String.format("Wrote png file to: %s", outFile));
}



*/