package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.DefectAnnotation;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.StateCheckResults;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.tools.TextExtractor;

public class ClippedTextCheck extends BaseTextRuleCheck implements IStateRuleChecker
{
	public ClippedTextCheck()
	{
		super(5, "TC2");
	}
	
	@Override
	public void analyze(@Nonnull State state, @Nonnull StateCheckResults result)
	{
		var language = state.predictLanguage();

		// if (false == language.equals("eng"))
		// {
		// return;
		// }

		var textsExtractor = new TextExtractor(0.65f, language);
		var defects = new ArrayList<DefectResult>();
		var textControls = state.getActualControls().stream().filter(p -> !shouldSkipControl(p, state));

		textControls.forEach(control ->
		{
			if ((control.isAd()) || ("Test Ad".equals(control.getText())))
			{
				// defects.add(new DefectResult(control.getParent().getBounds(), "", ""));
			}
			else
			{
				var bounds = control.getBounds();
				var expectedText = normalize(control.getText());

				textsExtractor.extract(state.getImage(), bounds, (x) -> findText(x, bounds, expectedText, control, defects));
			}
		});

		defects.forEach(defect -> result.addAnnotation(new DefectAnnotation(this, defect.bounds, defect.toString())));
	}
	
	private boolean findText(String actualText, Rect bounds, String expectedText, Control control, List<DefectResult> defects)
	{
		actualText = normalize(actualText);
		
		if (actualText.isBlank())
		{
			return false;
		}
		
		var done = checkForPartialText(defects, bounds, expectedText, actualText);
	
		if ((!done) && ((actualText.contains("0")) && (control.getText().contains("O"))))
		{
			done = checkForPartialText(defects, bounds, expectedText, actualText.replace('0', 'O'));
		}
	
		if ((!done) && (isUpperCase(actualText)))
		{
			done = checkForPartialText(defects, bounds, expectedText.toUpperCase(), actualText);
		}
		
		if ((!done) && (isUpperCase(expectedText)))
		{
			done = checkForPartialText(defects, bounds, expectedText, actualText.toUpperCase());
		}					
	
		if ((!done) && ((actualText.contains(" I")) && (control.getText().contains(" l"))))
		{
			done = checkForPartialText(defects, bounds, expectedText, actualText.replace(" I", " l"));
		}
		
		if (!done)
		{
			done = checkForPartialText(defects, bounds, fixWhiteSpaces(expectedText), fixWhiteSpaces(actualText));
		}
	
		if (!done)
		{
			done = checkForPartialText(defects, bounds, removeWhiteSpaces(expectedText), removeWhiteSpaces(actualText));
		}				
	
		return done;		
	}

	private boolean checkForPartialText(List<DefectResult> results, Rect bounds, String expectedText, String actualText)
	{
		boolean fullTextFound = hasFullText(expectedText, actualText);

		if (fullTextFound)
		{
			return true;
		}

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

	protected boolean shouldSkipControl(Control control, State state)
	{
		if (!control.isVisible())
		{
			return true;
		}

		if ((control.getText() == null) || (control.getText().length() <= 0))
		{
			return true;
		}

		if ("Test Ad".equals(control.getText()))
		{
			for (var c = control; c != null; c = c.getParent())
			{
				System.out.println(c.getSignature() + " " + c.getBounds().toString());
			}

			System.out.println("-----------------------------------------------------");

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

		if (hasMultiLine(control.getText()))
		{
			return true;
		}

		return false;
	}
	
	private boolean hasMultiLine(String message)
	{
		return message.contains("\n");
	}
	
	private boolean hasFullText(String source, String imageText)
	{
		return source.equals(imageText) || imageText.contains(source); //isSimillar(source.getText(), imageText) || (imageText.contains(source.getText()));
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
	
	private record DefectResult(Rect bounds, String expectedMessage, String actualMessage)
	{
		@Override
		public String toString()
		{
			return String.format("Expected: [%s], found: [%s]", this.expectedMessage, this.actualMessage); 
		}
	}
}
