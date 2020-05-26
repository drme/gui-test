package edu.ktu.screenshotanalyser.checks.experiments;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;

public class MissingTextCheck extends BaseRuleCheck implements IStateRuleChecker
{
	public MissingTextCheck()
	{
		super(26, "TM1");

		if (null == pass2Files)
		{
			List<String> qq = new ArrayList<String>();

			BufferedReader reader;
			try
			{
				reader = new BufferedReader(new FileReader("E:\\666\\logTM1.txt"));
				String line = reader.readLine();
				while (line != null)
				{

					if (line.startsWith("TM1:"))
					{

						String fileName = line.substring(0, line.indexOf("  missing: ["));

						fileName = fileName.substring(5);

						// System.out.println(line);
						// read next line

						qq.add(fileName);

					}

					line = reader.readLine();

					// System.out.println(fileName);
				}
				reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			pass2Files = qq;
		}

	}

	private static List<String> pass2Files;

	private static synchronized boolean canSkip(State state)
	{
		String ss = state.getImageFile().getAbsolutePath();

		boolean c = !pass2Files.contains(ss);

		if (c)
		{
			// System.out.println("not skip " + ss);
		}

		return c;
	}

	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		try
		{
			/*if (false == state.getImageFile().getAbsolutePath().equals("d:\\_r\\10.1_WXGA_Tablet_API_28\\a2dp.Vol_137\\states\\screen_2019-01-06_230724.png"))
			{
				return;
			}*/
			
			if (canSkip(state))
			{
				return;
			}
			/*
			 * else if (1 == 1) { failures.addFailure(new CheckResult(state, this, "-")); return; }
			 */

			if (true == state.isLauncherScreen())
			{
				logSuccess(state.getImageFile().getAbsolutePath() + "ex: " + "launcher");
				
				return;
			}

			Rect screenSize = getScreenSize(state);

			if (isTooSmallScreen(screenSize))
			{
				logSuccess(state.getImageFile().getAbsolutePath() + "ex: " + "too small");
				
				return;
			}
			
			String recognizedTexts = state.getImageTexts();
			String recognizedTextsFromFragments = null;

			if ((null == recognizedTexts) || (recognizedTexts.trim().length() == 0))
			{
				logSuccess(state.getImageFile().getAbsolutePath() + " no recognized texts, ignore image");

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
				// ????
				failures.addFailure(new CheckResult(state, this, faulty, faulty.length()));
			}
			else
			{
				logSuccess(state.getImageFile().getAbsolutePath() + "all found :");
			}
		}
		catch (Throwable e)
		{
			logSuccess(state.getImageFile().getAbsolutePath() + "ex: " + e.getMessage() + " " + failures);
		}
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
