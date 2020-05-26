package edu.ktu.screenshotanalyser.utils;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.opencv.core.Rect;

public class SystemUtils
{
	public static void executeCommand(String command) throws IOException
	{
		final Process process = Runtime.getRuntime().exec(command);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(30000);
					
					if (process.isAlive())
					{
						process.destroyForcibly();
					}
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream())))
		{
			try (BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream())))
			{
				String s;
				
				while ((s = stdInput.readLine()) != null)
				{
					System.out.println(s);
				}

				while ((s = stdError.readLine()) != null)
				{
					System.err.println(s);
				}
			}
		}
	}
	
	public static void delete(File file)
	{
		if (file.exists())
		{
			if (file.isDirectory())
			{
				for (File childFile : file.listFiles())
				{
					delete(childFile);
				}
				
				file.delete();
			}
			else
			{
				file.delete();
			}
		}
	}
	
	public static void logMessage(String fileName, String message)
	{
		message = message.trim() + "\n";
		
		try
		{
			Path logFile = Paths.get(fileName);
			
			Files.write(logFile, message.getBytes(Charset.forName("utf-8")), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException ex)
		{
			ex.printStackTrace(System.err);
		}
	}

	public static Rectangle toRectangle(Rect bounds)
	{
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public static boolean isUpperCase(String string)
	{
		char[] chars = string.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			if (Character.isLetter(chars[i]))
			{
				if (!Character.isUpperCase(chars[i]))
				{
					return false;
				}
			}
		}

		return true;
	}
}
