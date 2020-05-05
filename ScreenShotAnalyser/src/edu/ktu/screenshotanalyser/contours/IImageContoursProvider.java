package edu.ktu.screenshotanalyser.contours;

import java.io.File;
import java.util.List;
import org.opencv.core.Rect;

public interface IImageContoursProvider
{
	List<Rect> getContours(File imageFile);
}


/*


package edu.ktu.screenshotanalyser;

import edu.ktu.screenshotanalyser.ImageContoursProvider.ImageContoursProviderRequest;
import edu.ktu.screenshotanalyser.ImageContoursProvider.ImageContoursResponse;

public interface IImageContoursProvider {
	ImageContoursResponse getContours(ImageContoursProviderRequest request);
}



*/
