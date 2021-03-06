package edu.ktu.screenshotanalyser.rules.checkers;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;

import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;

public class CheckRequest {

	final List<ExtractedText> extractedTexts = new ArrayList<>();
	final List<String> actualTexts = new ArrayList<>();

	public List<String> getActualTexts() {
		return actualTexts;
	}

	final String originalFile;
	final String device;
	private double w;
	private double h;

	public double getW() {
		return w;
	}

	public double getH() {
		return h;
	}

	public String getDevice() {
		return device;
	}

	public List<ExtractedText> getExtractedTexts() {
		return extractedTexts;
	}

	public String getOriginalFile() {
		return originalFile;
	}

	public CheckRequest(String originalFile) {
		this.originalFile = originalFile;
		this.device = null;
	}

	public CheckRequest(String originalFile, String device, double w, double h) {
		this.originalFile = originalFile;
		this.device = device;
		this.w = w;
		this.h = h;

	}
}
