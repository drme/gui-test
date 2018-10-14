package edu.ktu.screenshotanalyser.contours;

import edu.ktu.screenshotanalyser.contours.ImageContoursProvider.ImageContoursProviderRequest;
import edu.ktu.screenshotanalyser.contours.ImageContoursProvider.ImageContoursResponse;

public interface IImageContoursProvider
{
	ImageContoursResponse getContours(ImageContoursProviderRequest request);
}
