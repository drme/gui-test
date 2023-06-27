package edu.ktu.screenshotanalyser.utils;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.opencv.core.Core;

public class BlurDetectorTests
{
	@BeforeAll
	public static void beforeTests()
	{
	}
	
	@Test
	public void testBlurry()
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);		
		
		var detector = new BlurDetector();
		
		//var result = detector.isBlurry("E:\\Projects\\eclipse\\prj346\\gui-test\\ScreenShotAnalyser\\src\\test\\resources\\1.png");
//		 result = detector.isBlurry("E:\\Projects\\eclipse\\prj346\\gui-test\\ScreenShotAnalyser\\src\\test\\resources\\2.png");

		 var result = detector.isBlurry("E:\\1.png");
		 
		 
		assertTrue(result);
	}
}
