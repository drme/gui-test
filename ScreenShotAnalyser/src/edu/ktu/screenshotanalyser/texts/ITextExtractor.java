package edu.ktu.screenshotanalyser.texts;

import java.awt.image.BufferedImage;
import org.opencv.core.Rect;

public interface ITextExtractor
{
	public String extract(BufferedImage image, Rect area);
}
