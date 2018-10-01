package edu.ktu.screenshotanalyser.checks;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;

import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;

public class CheckRequest {

	final List<MatOfPoint> contours = new ArrayList<>();
	final List<ExtractedText> extractedTexts = new ArrayList<>();

	final String originalFile;

	public List<MatOfPoint> getContours() {
		return contours;
	}

	public List<ExtractedText> getExtractedTexts() {
		return extractedTexts;
	}

	public String getOriginalFile() {
		return originalFile;
	}

	public CheckRequest(String originalFile) {
		this.originalFile = originalFile;

	}
}
