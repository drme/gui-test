package edu.ktu.screenshotanalyser;

import java.io.File;
import java.util.List;

import org.opencv.core.MatOfPoint;

import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;

public class Screen {
	public File originalFile;
	public String config;
	public String appName;
	public String shortFileName;
	public String[] actualTexts;
	public List<ExtractedText> extractedTexts;
	public List<MatOfPoint> contours;
	public double width;
	public double height;
	public String device;
}