package edu.ktu.screenshotanalyser;

import edu.ktu.screenshotanalyser.ImageContoursProvider.ImageContoursProviderRequest;
import edu.ktu.screenshotanalyser.ImageContoursProvider.ImageContoursResponse;

public interface IImageContoursProvider
{
	ImageContoursResponse getContours(ImageContoursProviderRequest request);
}
