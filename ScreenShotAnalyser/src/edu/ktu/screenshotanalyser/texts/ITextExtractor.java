package edu.ktu.screenshotanalyser.texts;

import java.awt.image.BufferedImage;
import java.io.File;
import org.opencv.core.Rect;
import net.sourceforge.tess4j.TesseractException;

public interface ITextExtractor
{
	public String extract(File imageFile);
	public String extract(BufferedImage image, Rect bounds);
	public String extract(File imageFile, Rect area);
}
