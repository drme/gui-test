package edu.ktu.screenshotanalyser.utils;

public class BaseLogger
{
	private BaseLogger()
	{
	}
	
	public static void logException(String message, Throwable ex)
	{
		System.err.println(message + " :" + ex.getMessage());
	}

	public static void logInfo(String message)
	{
		System.out.println(message);
	}
}
