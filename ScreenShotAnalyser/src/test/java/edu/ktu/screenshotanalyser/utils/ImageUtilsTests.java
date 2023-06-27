package edu.ktu.screenshotanalyser.utils;

import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import junit.framework.Assert;

public class ImageUtilsTests
{
	@BeforeClass
	void setUp()
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	@Test
	void testBufferedImageToMat2x2Black() throws IOException
	{
		var sourceImage = ImageIO.read(getClass().getResourceAsStream("black2x2.png"));
		
		var result = ImageUtils.bufferedImageToMat(sourceImage);
		
		Assert.assertEquals(2, result.width());
		Assert.assertEquals(2, result.height());
		
		Assert.assertEquals(new Double[] {0d, 0d, 0d, 0d}, result.get(0, 0));
	}
}
