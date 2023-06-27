package edu.ktu.screenshotanalyser.checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.database.DataBase;
import edu.ktu.screenshotanalyser.utils.LazyObject;

public abstract class BaseRuleCheck
{
	protected BaseRuleCheck(long id, String ruleCode)
	{
		this.id = id;
		this.ruleCode = ruleCode;
	}
	
	public void analyze(State state, StateCheckResults results)
	{
		// temp..
	}
	
	
	public void logMessage(String message)
	{
		message = message.trim() + "\n";
		
		try
		{
			var logFile = Paths.get("e:/log/" + this.ruleCode + ".txt");
			
			Files.write(logFile, message.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
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
		var lastFunFiles = new HashSet<String>();

		try (var reader = new BufferedReader(new FileReader(fileName)))
		{
			var line = reader.readLine();

			while (line != null)
			{
				if (line.startsWith(prefix))
				{
					var file = line.substring(prefix.length());

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

		if (!lastFunFiles.isEmpty())
		{
			return lastFunFiles;
		}
		else
		{
			return null;
		}
	}
	
	protected static String executeShellCommand(String... command)
	{
		var result = new StringBuilder("");

		try
		{
			var process = Runtime.getRuntime().exec(command, new String[] { "PYTHONIOENCODING=utf8" }, null);

			try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
			{
				String line = null;

				while ((line = reader.readLine()) != null)
				{
					result.append(line);
				}
			}
			
			try (var reader = new BufferedReader(new InputStreamReader(process.getErrorStream())))
			{
				String line = null;

				while ((line = reader.readLine()) != null)
				{
					result.append(line);
				}
			}			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

//		System.out.println("[" + result + "]");
		
		return result.toString();
	}
	
	protected boolean shouldSkipControl(Control control, State state)
	{
		if (!control.isVisible())
		{
			return true;
		}

		if (control.isAd())
		{
			return true;
		}

		if (control.getSignature().contains("Layout"))
		{
			return true;
		}

		var bounds = control.getBounds();

		if ((bounds.width <= 3) || (bounds.height <= 5))
		{
			return true;
		}		

		return control.isOffScreen();

		// if ((control.getBounds().x + control.getBounds().width >= state.getImageSize().width) || (control.getBounds().y + control.getBounds().height >= state.getImageSize().height))
		// {
		// return true;
		// }
		//return false;
	}	
	
	
	protected static boolean isCutByScrollView(Control control)
	{
		var expectedType = "ScrollView";
		
		if (control.getParent() == null)
		{
			return false;
		}
		
		if (control.getBounds().y + control.getBounds().height >= control.getParent().getBounds().y + control.getParent().getBounds().height)
		{
			for (var p = control.getParent(); p != null; p = p.getParent())
			{
				if ((p.getType() != null) && (p.getType().contains(expectedType)))
				{
					return p.getBounds().y - control.getBounds().y > 100;
				}
			}
		}
		
		return false;
	}		
	
	protected static boolean isVisibleImage(Control control)
	{
		if (control.getResourceId() == null)
		{
			return false;
		}
		
		if (control.isOffScreen())
		{
			return false;
		}
		
		if (control.isAd())
		{
			return false;
		}

		return true;
	}	
	
	protected boolean isReference(File file)
	{
		for (var referenceImage : this.referenceImages.instance())
		{
			if (file.getAbsolutePath().endsWith(referenceImage))
			{
				return true;
			}
		}
		
		return false;
	}
	
	protected final LazyObject<List<String>> referenceImages = new LazyObject<>(() -> new DataBase().getList(rs -> rs.getString(1), "SELECT s.FileName FROM ScreenShot s LEFT JOIN Defect d ON d.ScreenshotId = s.Id WHERE d.DefectTypeId = ?", getId()));
	private final String ruleCode;
	private final long id;
}
