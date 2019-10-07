package edu.ktu.screenshotanalyser.contours;

import java.io.File;
import java.util.List;
import org.opencv.core.Rect;

public interface IImageContoursProvider
{
	List<Rect> getContours(File imageFile);
}
