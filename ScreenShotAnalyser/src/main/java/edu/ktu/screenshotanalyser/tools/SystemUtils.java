package edu.ktu.screenshotanalyser.tools;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Rect;

public class SystemUtils
{
	private SystemUtils()
	{
	}
	
	public static void executeCommand(String command) throws IOException
	{
		final Process process = Runtime.getRuntime().exec(command);

		new Thread(() ->
		{
			try
			{
				Thread.sleep(30000);

				if (process.isAlive())
				{
					process.destroyForcibly();
				}
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
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
			var logFile = Paths.get(fileName);
			
			Files.write(logFile, message.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
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
	
	public static List<String> readAllLines(String fileName, String prefixFilter) throws IOException
	{
		var result = new ArrayList<String>();
		
		try (var file = new RandomAccessFile(fileName, "r"))
		{
			String str;
		
			while ((str = file.readLine()) != null)
			{
				if (null == prefixFilter || str.startsWith(prefixFilter))
				{
					result.add(str);
				}
			}
		}

		return result;
	}	
}
