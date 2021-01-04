package edu.ktu.screenshotanalyser.checks.experiments;

import java.awt.Image;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.Settings;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;
import edu.ktu.screenshotanalyser.texts.TextExtractor;
import edu.ktu.screenshotanalyser.utils.ResultImage;
import edu.ktu.screenshotanalyser.utils.SystemUtils;

public class MissingTextCheck extends BaseRuleCheck implements IStateRuleChecker
{
	private Set<String> lastRun = null;
	
	public MissingTextCheck()
	{
		super(26, "TM1");

		this.lastRun = loadLastRun("E:\\e1\\2\\p2.txt", "TM1: ");
	}

	/*
	
	private static List<String> pass2Files;

	private static synchronized boolean canSkip(State state)
	{
		String ss = state.getImageFile().getAbsolutePath();

		boolean c = !pass2Files.contains(ss);

		if (c)
		{
			// System.out.println("not skip " + ss);
		}

		return false;//c;
	}
	
	*/
	
	
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

		if ((control.getText() == null) || (control.getText().length() <= 1)) // skip kind of icons from fonts?
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
	
	
	private static class SearchResult
	{
		public boolean found = false;
		public String recognizedTexts = "";
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
	
	
	private void logDefects(State state, ResultsCollector failures, ResultImage resultImage, long invalidControls, String errors)
	{
		if (invalidControls > 0)
		{
			//resultImage.save(Settings.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
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
	
	
	private boolean hasFullText(String source, String imageText)
	{
		source = source.toLowerCase();
		imageText = imageText.toLowerCase();
		
		
		return source.equals(imageText) || imageText.contains(source);
				//isSimillar(source.getText(), imageText) || (imageText.contains(source.getText()));
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
				//results.add(new DefectResult(bounds, expectedText, x));
							
				return true;
			}
			
			if (x.length() > 2)
			{
				x = x.substring(0, x.length() - 1); // removing last corrupted character
				
				if (expectedText.contains(x))
				{
					//results.add(new DefectResult(bounds, expectedText, x));
								
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
	public void analyze(State state, ResultsCollector failures)
	{
		if (null != this.lastRun)
		{
			if (false == this.lastRun.contains(state.getImageFile().getAbsolutePath()))
			{
				return;
			}
		}
		
		
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
			
		if (errors.length() > 0)
		{
		
		logDefects(state, failures, resultImage, invalidControls, errors);		
		
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		
		
		
		try
		{
			if (false == state.getImageFile().getAbsolutePath().equals("d:\\_r\\10.1_WXGA_Tablet_API_28\\a2dp.Vol_137\\states\\screen_2019-01-06_230357.png"))
			{
				return;
			}			
			
			if (true == state.isLauncherScreen())
			{
//				logSuccess(state.getImageFile().getAbsolutePath() + "ex: " + "launcher");
				
				return;
			}
			
			Rect screenSize = getScreenSize(state);

			if (isTooSmallScreen(screenSize))
			{
			//	logSuccess(state.getImageFile().getAbsolutePath() + "ex: " + "too small");
				
				return;
			}			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			

			
		//	if (canSkip(state))
//			{
	//			return;
		//	}
			/*
			 * else if (1 == 1) { failures.addFailure(new CheckResult(state, this, "-")); return; }
			 *00/



			
			String recognizedTexts = state.getImageTexts();
			String recognizedTextsFromFragments = null;

			if ((null == recognizedTexts) || (recognizedTexts.trim().length() == 0))
			{
//				logSuccess(state.getImageFile().getAbsolutePath() + " no recognized texts, ignore image");

				return;
			}

			String faulty = "";
			
			recognizedTexts = recognizedTexts.toLowerCase().replaceAll("\n", " ").replaceAll("  ", " ").trim();

			for (Control expected : state.getActualControls())
			{
				if (hasVisibleWord(expected, screenSize.width, screenSize.height))
				{
					String expectedMessage = expected.getText().toLowerCase().replaceAll("  ", " ").trim();

					if (expectedMessage.length() > 0)
					{
						if (false == containsMessage(recognizedTexts, expectedMessage))
						{
							
							
							
							if (null == recognizedTextsFromFragments)
							{
								StringBuilder allMessages22 = new StringBuilder();
								state.getImageControls().stream().filter(p -> p.getText() != null).forEach(p -> allMessages22.append(" " + p.getText()));

								recognizedTextsFromFragments = allMessages22.toString().toLowerCase();
							}

							if (false == containsMessage(recognizedTextsFromFragments, expectedMessage))
							{
								if (faulty.length() == 0)
								{
									faulty = "TM1: " + state.getImageFile().getAbsolutePath() + " ";
								}
									 
								
								faulty += (" " + ("missing: [" + expectedMessage + "] in [" + recognizedTexts + recognizedTextsFromFragments + "] " + state.getImageFile().getName())).replace('\n', ' ').replace('\r', ' ');
							}
							else
							{
								//System.out.println("found2: " + expected.getText() + " in " + recognizedTextsFromFragments);
							}
						}
						else
						{
							//System.out.println("found1: " + expected.getText() + " in " + recognizedTexts);
						}
					}
				}
			}

			if (faulty.length() > 0)
			{
				System.out.println(state.getImageFile().getAbsolutePath() + " " + faulty);
				
		//		logMessage(faulty);
				
				// ????
				failures.addFailure(new CheckResult(state, this, faulty, faulty.length()));
			}
			else
			{
				//logSuccess(state.getImageFile().getAbsolutePath() + "all found :");
			}
		}
		catch (Throwable e)
		{
//			logSuccess(state.getImageFile().getAbsolutePath() + "ex: " + e.getMessage() + " " + failures);
		} */
	}
	
	private boolean isTooSmallScreen(Rect screenSize)
	{
		if ((screenSize.width < 400) || (screenSize.height < 400))
		{
			return true;
		}
		
		return false;
	}
	
	private Rect getScreenSize(State state) throws IOException
	{
		int screenWidth = state.getTestDevice().screenWidth;
		int screenHeight = state.getTestDevice().screenHeight;

		Image image = ImageIO.read(state.getImageFile());

		int imageHeight = image.getHeight(null);
		int imageWidth = image.getWidth(null);

		if (imageHeight != screenHeight)
		{
			if ((imageHeight == screenWidth) && (imageWidth == screenHeight))
			{
				screenWidth = imageWidth;
				screenHeight = imageHeight;
			}
		}
		
		return new Rect(0, 0, screenWidth, screenHeight);
	}
	
	private boolean hasVisibleWord(Control expected, int screenWidth, int screenHeight)
	{
		if ((null == expected.getText()) || (expected.getText().trim().length() == 0))
		{
			return false;
		}
		
		if (expected.getBounds().width <= 0)
		{
			return false;
		}
		
		if (expected.getBounds().height <= 0)
		{
			return false;
		}

		if (expected.getBounds().x >= screenWidth)
		{
			return false;
		}

		if (expected.getBounds().y >= screenHeight)
		{
			return false;
		}		

		return true;
	}

	private void logSuccess(String message)
	{
		message = message.trim() + "\n";

		try
		{
			Path logFile = Paths.get("e:/log" + "--OK" + ".txt");

			Files.write(logFile, message.getBytes(Charset.forName("utf-8")), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch (IOException ex)
		{
			ex.printStackTrace(System.err);
		}
	}

	private boolean isWord(String word)
	{
		for (int i = 0; i < word.length(); i++)
		{
			if (false == Character.isAlphabetic(word.charAt(i)))
			{
				return false;
			}
		}

		return true;
	}

	private boolean containsMessage(String targetMessage, String searchMessage)
	{
		if (null == targetMessage)
		{
			targetMessage = "";
		}
		
		if (null == searchMessage)
		{
			searchMessage = "";
		}
		
		if (targetMessage.contains(searchMessage))
		{
			return true;
		}
		
		searchMessage = searchMessage.replace(' ', ' ').replace('\n', ' ').replace('\r', ' ');

		String[] words = searchMessage.split("[ ]");
		String[] targetWords = targetMessage.split("[ ]");

		for (String word : words)
		{
			if (false == targetMessage.contains(word))
			{
				if (word.startsWith("http://") || word.startsWith("https://"))
				{
					continue;
				}

				if (word.length() == 1)
				{
					continue;
				}

				if (word.endsWith(".") || word.endsWith("!") || word.endsWith(",") || word.endsWith("?"))
				{
					word = word.substring(0, word.length() - 1);

					if (false == targetMessage.contains(word))
					{
						if (isWord(word))
						{
							if (!hasSimillar(word, targetWords))
							{
								return false;
							}
						}
					}
				}
				else
				{
					if (isWord(word))
					{
						if (!hasSimillar(word, targetWords))
						{
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private boolean hasSimillar(String word, String[] targetWords)
	{
		for (String a : targetWords)
		{
			int d = LevenshteinDistance.getDefaultInstance().apply(word, a);

			int m_len = Math.max(a.length(), word.length());

			float d1 = 0;

			if (m_len == 0)
			{
				d1 = 0;
			}
			else
			{
				d1 = (float) d / (float) m_len;
			}

			if (d1 <= 0.3)
			{
				return true;
			}
		}

		return false;
	}

	/*
	 * @Override public CheckResult[] analyze(CheckRequest request, AppContext context) { List<CheckResult> results = new ArrayList<>(); for (String actualText : request.getActualTexts()) { boolean textRecognized = request.getExtractedTexts().stream() .anyMatch(eText -> actualText.equalsIgnoreCase(eText.getText())); if (!textRecognized) { results.add(CheckResult.Nok(type, String.format("[%s] device: Actual text '%s' was not recognized", request.getDevice(), actualText), request.getOriginalFile(), "-")); } } return results.toArray(new CheckResult[0]); }
	 */

}
