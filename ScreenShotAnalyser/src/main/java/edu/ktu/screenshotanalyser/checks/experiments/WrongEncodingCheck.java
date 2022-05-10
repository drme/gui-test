package edu.ktu.screenshotanalyser.checks.experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.text.html.HTMLDocument.HTMLReader.CharacterAction;
import org.opencv.core.Rect;
//import com.lowagie.text.pdf.PatternColor;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.Settings;
import edu.ktu.screenshotanalyser.tools.TextExtractor;

public class WrongEncodingCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public WrongEncodingCheck()
	{
		super(27, "WrongEncodingCheck");
	}
	
	
	public static void main(String[] args)
	{
		System.out.println(ee("L\\'foo"));
	}
	
	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		try
		{
			var s1 = state;//new State("", state.getAppContext(), new File("E:\\gui\\_r\\s4\\lt.sisp.itero.ticket.client\\states\\screen_2019-02-01_161209.jpg"), new File("E:\\gui\\_r\\s4\\lt.sisp.itero.ticket.client\\states\\state_2019-02-01_161209.json"), null);

		
		
//		var language = state.predictLanguage();
		
//		if (false == language.equals("eng"))
		//{
	//		return;
//		}
				
	//	var textsExtractor = new TextExtractor(0.65f, language);
//		var defects = new ArrayList<DefectResult>();
		var textControls = s1.getActualControls().stream().filter(p -> !shouldSkipControl(p, s1)).collect(Collectors.toList());
		
		for (var control : textControls)
		{
			if ((isAd(control)) || ("Test Ad".equals(control.getText())))
			{
				//defects.add(new DefectResult(control.getParent().getBounds(), "", ""));
				
			}
			else
			{
	//			var bounds = control.getBounds();
		//		var expectedText = normalize(control.getText());
			
			//	textsExtractor.extract(state.getImage(), bounds, (x) -> findText(x, bounds, expectedText, control, defects));
				
				
				//\nn non lating? + latin 
				
				var t = control.getText();
				
				if (null == t)
				{
					t = control.getContentDescription();
				}
				
				
				
				var text = t.trim(); // \'

		//		if (!text.contains("Saviva"))
			//	{
				//	return;
//				}
				
				var symbols = new String[] { "\\'", "\\u", "\\\"", "\\n", "\\r" };
				
				for (var s : symbols)
				{
					if (text.contains(s))
					{
						System.out.println(text + state.getName());
						
						failures.addFailure(new CheckResult(state, this, text, 1));
						
						return;
						
					}
				}
				/*
				if (((text.contains("?") && !text.endsWith("?") && !text.contains(" ?") && !text.contains("? ") && !text.contains("http"))) || text.contains("\\'"))
				{
					System.out.println(text + state.getName());
				}
				*/
				var words = text.split("[ \n\r\u00a0]");
				
				for (var w : words)
				{
					w = w.replaceAll("[:,.?!0-9��'/@�{}()’%“&…=<>´״”`：！°►，™—‘׳;®]|[-()�\"]|[+]|[_]|[–]|[*¡#]|[|]|[—]|[™]|[―]", "");
					
					if (w.length() > 2 && w.length() < 20 && !w.contains("http") && !w.contains("―"))
					{
						int latin = 0;
						for (int j = 0; j < w.length(); j++)
						{
							char cc = w.charAt(j);
							
							if ((cc >= 'A' && cc <= 'Z') || (cc >= 'a' && cc <= 'z'))
							{
								latin++;
							}							
						}
						
						if (latin == 0)
						{
							continue;
						}
						
						
						if (Character.isAlphabetic(w.charAt(0)) && Character.isAlphabetic(w.charAt(w.length() - 1)))
						{
							for (int i = 1; i < w.length() - 1; i++)
							{
								char c = w.charAt(i);
								
								if (!Character.isAlphabetic(c))
								{
									System.out.println("---- [" + w + "] "   + state.getName());
									
									failures.addFailure(new CheckResult(state, this, "[" + w + "] "+ text, 1));
									
									return;
								}
							}
						}
						
						
					}
					
					/*
					int c = 0;
					
					for (int i = 0; i < w.length(); i++)
					{
						var cc = w.charAt(i);
						
						if (Character.isAlphabetic(cc))
						//if ((cc >= 'A' && cc <= 'Z') || (cc >= 'a' && cc <= 'z'))
						{
							c++;
						}
					}
					
					if (c > 1 && c != w.length() && (w.length() - c) % 2 == 0)
					{
						System.out.println("---- [" + w + "] "   + state.getName());
						
						return;
					}*/
				}
				
			}
		}
				
//		logDefects(state, failures, defects);
		
		
		}
		catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	
	
	private boolean findText(String actualText, Rect bounds, String expectedText, Control control, List<DefectResult> defects)
	{
		actualText = normalize(actualText);
		
		if (actualText.isBlank())
		{
			return false;
		}
		
		boolean done = checkForPartialText(defects, bounds, expectedText, actualText);
	
		if ((false == done) && ((actualText.contains("0")) && (control.getText().contains("O"))))
		{
			done = checkForPartialText(defects, bounds, expectedText, actualText.replace('0', 'O'));
		}
	
		if ((done == false) && (true == isUpperCase(actualText)))
		{
			done = checkForPartialText(defects, bounds, expectedText.toUpperCase(), actualText);
		}
		
		if ((done == false) && (true == isUpperCase(expectedText)))
		{
			done = checkForPartialText(defects, bounds, expectedText, actualText.toUpperCase());
		}					
	
		if ((false == done) && ((actualText.contains(" I")) && (control.getText().contains(" l"))))
		{
			done = checkForPartialText(defects, bounds, expectedText, actualText.replace(" I", " l"));
		}
		
		if (false == done)
		{
			done = checkForPartialText(defects, bounds, fixWhiteSpaces(expectedText), fixWhiteSpaces(actualText));
		}
	
		if (false == done)
		{
			done = checkForPartialText(defects, bounds, removeWhiteSpaces(expectedText), removeWhiteSpaces(actualText));
		}				
	
		return done;		
	}

	
	
	private boolean checkForPartialText(List<DefectResult> results, Rect bounds, String expectedText, String actualText)
	{
		boolean fullTextFound = hasFullText(expectedText, actualText);
		
		if (true == fullTextFound)
		{
			return true;
		}
		
		if (false == fullTextFound)
		{
			if (actualText.endsWith("..."))
			{
				actualText = actualText.substring(0, actualText.length() - 3);
			}
			else if (actualText.endsWith(".."))
			{
				actualText = actualText.substring(0, actualText.length() - 2);
			}
			
			if (expectedText.contains(actualText))
			{
				results.add(new DefectResult(bounds, expectedText, actualText));
							
				return true;
			}
			
			if (actualText.length() > 2)
			{
				actualText = actualText.substring(0, actualText.length() - 1); // removing last corrupted character
				
				if (expectedText.contains(actualText))
				{
					results.add(new DefectResult(bounds, expectedText, actualText));
								
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
	
	
	*/
	
	private static boolean ee(String e)
	{
		System.out.println(e);
		
		return e.contains("\\'");
	}
	
	private boolean shouldSkipControl(Control control, State state)
	{
		if (false == control.isVisible())
		{
			return true;
		}
		
		var t = control.getText();
		
		if (null == t)
		{
			t = control.getContentDescription();
		}
		
		if ((t == null) || (t.length() <= 0))
		{
			return true;
		}
		
		if ("Test Ad".equals(control.getText()))
				{
			
			for (var c = control; c != null; c = c.getParent())
			{
	//			System.out.println(c.getSignature() + " " + c.getBounds().toString());
			}
			
		//	System.out.println("-----------------------------------------------------");
			
			return false;
				}
		
		if (control.getType() == null)
		{
			return false;
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
		/*
		if (hasMultiLine(control.getText()))
		{
			return true;
		}
		*/
		return false;
	}
	
	/*
	
	private boolean hasMultiLine(String message)
	{
		return message.contains("\n");
	}
	
	private boolean hasFullText(String source, String imageText)
	{
		return source.equals(imageText) || imageText.contains(source); //isSimillar(source.getText(), imageText) || (imageText.contains(source.getText()));
	}
	
	private void logDefects(State state, ResultsCollector failures, List<DefectResult> defects)
	{
		if (defects.isEmpty())
		{
			return;
		}
		
		var result = new CheckResult(state, this, defects.stream().map(p -> p.toString()).collect(Collectors.joining()), defects.size());
		var debugImage = result.getResultImage();
				
		defects.forEach(defect -> 
		{
			debugImage.drawBounds(defect.bounds);
			debugImage.drawText(defect.toString(), defect.bounds);
		});
		
		failures.addFailure(result);

		debugImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
	}
	
	private String fixWhiteSpaces(String string)
	{
		return Arrays.stream(string.split("[\n\r \u00a0]")).filter(p -> p.length() > 0).collect(Collectors.joining(" ")).trim();
	}
	
	private String removeWhiteSpaces(String string)
	{
		return String.join("", string.split("[\n\r ]")).trim();
	}
	
	private String normalize(String source)
	{
		return source.replace('�', '\'').replace('\u00a0', ' ');
	}
	
	private static class DefectResult
	{
		public DefectResult(Rect bounds, String expectedMessage, String actualMessage)
		{
			this.bounds = bounds;
			this.expectedMessage = expectedMessage;
			this.actualMessage = actualMessage;
		}
		
		@Override
		public String toString()
		{
			return String.format("Expected: [%s], found: [%s]", this.expectedMessage, this.actualMessage); 
		}
		
		public String expectedMessage;
		public Rect bounds;
		public String actualMessage;
	}	*/
}
