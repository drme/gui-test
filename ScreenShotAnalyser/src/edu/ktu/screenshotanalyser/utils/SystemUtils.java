package edu.ktu.screenshotanalyser.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
