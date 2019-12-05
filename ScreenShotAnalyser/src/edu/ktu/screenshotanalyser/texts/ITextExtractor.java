package edu.ktu.screenshotanalyser.texts;

import java.awt.image.BufferedImage;
import java.io.File;
import org.opencv.core.Rect;

public interface ITextExtractor
{
	public String extract(File imageFile);
	//public String extract(BufferedImage image);
	public String extract(File imageFile, Rect area);
}
