package edu.ktu.screenshotanalyser.checks;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
			Path logFile = Paths.get("e:/log" + this.ruleCode + ".txt");
			
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
	
	private final String ruleCode;
	private final long id;
}
