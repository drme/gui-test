package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.Settings;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;
import edu.ktu.screenshotanalyser.texts.ITextExtractor;
import edu.ktu.screenshotanalyser.texts.TextExtractor;
import edu.ktu.screenshotanalyser.utils.ResultImage;
import edu.ktu.screenshotanalyser.utils.SystemUtils;
import jdk.nashorn.internal.runtime.ECMAErrors;

public class ClippedTextCheck extends BaseTextRuleCheck implements IStateRuleChecker, IAppRuleChecker
{
	public ClippedTextCheck()
	{
		super(5, "TC2");
	}
	
	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		List<DefectResult> results = new ArrayList<>();
		
		ResultImage resultImage = new ResultImage(state.getImageFile());
		
		long invalidControls = 0;
		String errors = "";
		
		for (Control control : state.getActualControls())
		{
			if (shouldSkipControl(control, state))
			{
				continue;
			}
			
			Rect bounds = control.getBounds();
			
			String language = state.predictLanguage();
			
			if (false == language.equals("eng"))
			{
				continue;
			}
			
			TextExtractor textsExtractor = new TextExtractor(0.65f, language);
			
			//System.out.println(language);
			
			//break;
			
			String expectedText = normalize(control.getText());
			
			final SearchResult textFound = new SearchResult();
			
			textsExtractor.extract(state.getImage(), bounds, (x) ->
			{
				try
				{
					x = normalize(x);
					
					boolean found = checkForPartialText(results, bounds, expectedText, x);
				
					if ((false == found) && ((x.contains("0")) && (control.getText().contains("O"))))
					{
						found = checkForPartialText(results, bounds, expectedText, x.replace('0', 'O'));
					}
				
					if ((found == false) && (true == SystemUtils.isUpperCase(x)))
					{
						found = checkForPartialText(results, bounds, expectedText.toUpperCase(), x);
					}
					
					if ((found == false) && (true == SystemUtils.isUpperCase(expectedText)))
					{
						found = checkForPartialText(results, bounds, expectedText, x.toUpperCase());
					}					
				
					if ((false == found) && ((x.contains(" I")) && (control.getText().contains(" l"))))
					{
						found = checkForPartialText(results, bounds, expectedText, x.replace(" I", " l"));
					}
					
					if (false == found)
					{
						String source = fixWhiteSpaces(expectedText);
						String target = fixWhiteSpaces(x);
					
						found = checkForPartialText(results, bounds, source, target);
					}
				
					if (false == found)
					{
						String source = removeWhiteSpaces(expectedText);
						String target = removeWhiteSpaces(x);
					
						found = checkForPartialText(results, bounds, source, target);
					}				
				
					if (found)
					{
						textFound.found = true;
					
						//	System.out.println(control.getText() + "\n" + x + "\n\n");
					}
					else
					{
						textFound.recognizedTexts += " [" + x + "]";
					}
				
					return found;
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					
					return false;
				}
			});
			
			if (false == textFound.found)
			{
				resultImage.drawBounds(bounds);
	
				resultImage.drawText(textFound.recognizedTexts + " | " + control.getText(), bounds);
				
				invalidControls++;
				
				System.out.println("1: " + control.getText() + "\n2: " + textFound.recognizedTexts + "\n");
				
				errors += "Expected: [" + control.getText()+ "] found " + textFound.recognizedTexts +"\n";
			}
		}
				
		logDefects(state, failures, resultImage, invalidControls, errors);
	}

	private boolean checkForPartialText(List<DefectResult> results, Rect bounds, String expectedText, String x)
	{
		boolean mesagefound = hasFullText(expectedText, x);
		
		if (false == mesagefound)
		{
			if (x.endsWith("..."))
			{
				x = x.substring(0, x.length() - 3);
			}
			else if (x.endsWith(".."))
			{
				x = x.substring(0, x.length() - 2);
			}
			
			if (expectedText.contains(x))
			{
				results.add(new DefectResult(bounds, expectedText, x));
							
				return true;
			}
			
			if (x.length() > 2)
			{
				x = x.substring(0, x.length() - 1); // removing last corrupted character
				
				if (expectedText.contains(x))
				{
					results.add(new DefectResult(bounds, expectedText, x));
								
					return true;
				}
			}
			
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public void analyze(AppContext appContext, ResultsCollector failures)
	{
	}
	
	private static class SearchResult
	{
		public boolean found = false;
		public String recognizedTexts = "";
	}
	
	private static class DefectResult
	{
		public DefectResult(Rect bounds, String expectedMessage, String actualMessage)
		{
			this.bounds = bounds;
			this.expectedMessage = expectedMessage;
			this.actualMessage = actualMessage;
		}
		
		public String expectedMessage;
		public Rect bounds;
		public String actualMessage;
	}
	
	private boolean shouldSkipControl(Control control, State state)
	{
		if (false == control.isVisible())
		{
			return true;
		}
		
		if ((control.getText() == null) || (control.getText().length() <= 0))
		{
			return true;
		}
		
		if (control.getType().equals("android.widget.Switch"))
		{
			return true;
		}
		
		if (control.getType().equals("android.widget.Image"))
		{
			return true;
		}
		
		if (control.getType().equals("android.webkit.WebView"))
		{
			return true;
		}

		
		
		
		if ((control.getBounds().width <= 0) || (control.getBounds().height <= 0))
		{
			return true;
		}
		
		if ((control.getBounds().width <= 3) || (control.getBounds().height <= 3))
		{
			return true;
		}		
		
		if ((control.getBounds().x + control.getBounds().width >= state.getImageSize().width) || (control.getBounds().y + control.getBounds().height >= state.getImageSize().height))
		{
			return true;
		}
		
		return false;
	}
	
	private boolean hasFullText(String source, String imageText)
	{
		return source.equals(imageText) || imageText.contains(source);
				//isSimillar(source.getText(), imageText) || (imageText.contains(source.getText()));
	}
	
	private void logDefects(State state, ResultsCollector failures, ResultImage resultImage, long invalidControls, String errors)
	{
		if (invalidControls > 0)
		{
			resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
		}
		
		failures.addFailure(new CheckResult(state, this, errors, invalidControls));
		
		
			
//			if (errors.length() > 0)
	//		{
//		failures.addFailure(new CheckResult(state, this, errors));
		//	}

			//if (errors.length() > 0)
		//	{
	//			//state.dumpRecognitionDebug();
//			}
	}
	
	private String fixWhiteSpaces(String string)
	{
		String[] words = string.split("[\n\r \u00a0]");
		
		string = "";
		
		for (String word : words)
		{
			if (word.length() > 0)
			{
				string += " " + word;
			}
		}
		
		return string.trim();
	}
	
	private String removeWhiteSpaces(String string)
	{
		String[] words = string.split("[\n\r ]");
		
		string = "";
		
		for (String word : words)
		{
			string += word;
		}
		
		return string.trim();
	}
	
	private String normalize(String source)
	{
		source = source.replace('’', '\'');
		source = source.replace('\u00a0', ' ');
		
		return source;
	}
}
