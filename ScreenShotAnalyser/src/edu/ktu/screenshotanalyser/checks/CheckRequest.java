package edu.ktu.screenshotanalyser.checks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;

import edu.ktu.screenshotanalyser.TextExtractor.ExtractedText;

public class CheckRequest {

	final List<MatOfPoint> contours = new ArrayList<>();
	final List<ExtractedText> extractedTexts = new ArrayList<>();

	final File originalFile;

	public List<MatOfPoint> getContours() {
		return contours;
	}

	public List<ExtractedText> getExtractedTexts() {
		return extractedTexts;
	}

	public File getOriginalFile() {
		return originalFile;
	}

	public CheckRequest(File originalFile) {
		this.originalFile = originalFile;

	}
}
