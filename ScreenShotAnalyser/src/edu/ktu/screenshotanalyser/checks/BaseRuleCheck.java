package edu.ktu.screenshotanalyser.checks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseRuleCheck
{
	protected BaseRuleCheck(long id, String ruleCode)
	{
		this.id = id;
		this.ruleCode = ruleCode;
	}
	
	public void logMessage(String message)
	{
		message = message.trim() + "\n";
		
		try
		{
			Path logFile = Paths.get("e:/log/" + this.ruleCode + ".txt");
			
			Files.write(logFile, message.getBytes(Charset.forName("utf-8")), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException ex)
		{
			ex.printStackTrace(System.err);
		}
	}
	
	public String getRuleCode()
	{
		return this.ruleCode;
	}
	
	public long getId()
	{
		return this.id;
	}
	
	protected Set<String> loadLastRun(String fileName, String prefix)
	{
		HashSet<String> lastFunFiles = new HashSet<String>();

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName)))
		{
			String line = reader.readLine();

			while (line != null)
			{
				if (line.startsWith(prefix))
				{
					String file = line.substring(prefix.length());

					file = file.split(" ")[0];

					lastFunFiles.add(file);
				}

				line = reader.readLine();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return null;
		}

		if (lastFunFiles.size() > 0)
		{
			return lastFunFiles;
		}
		else
		{
			return null;
		}
	}
	
	private final String ruleCode;
	private final long id;
}
