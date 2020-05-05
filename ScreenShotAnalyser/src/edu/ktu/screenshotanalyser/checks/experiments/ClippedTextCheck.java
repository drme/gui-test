package edu.ktu.screenshotanalyser.checks.experiments;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.languagetool.Language;
import org.opencv.core.Rect;
import edu.ktu.screenshotanalyser.Config;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.LocalizedMessages;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;
import edu.ktu.screenshotanalyser.texts.ITextExtractor;
import edu.ktu.screenshotanalyser.texts.TextExtractor;
import edu.ktu.screenshotanalyser.utils.ResultImage;

public class ClippedTextCheck extends BaseTextRuleCheck implements IStateRuleChecker, IAppRuleChecker
{
	public ClippedTextCheck()
	{
		super(5, "TC2");
	}
	
	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		ResultImage resultImage = new ResultImage(state.getImageFile());
		
		boolean found = false;
		
		for (Control control : state.getActualControls())
		{
			if ((control.getBounds().width <= 0) || (control.getBounds().height <= 0))
			{
				continue;
			}
			
			if ((control.getBounds().x + control.getBounds().width >= state.getImageSize().width) || (control.getBounds().y + control.getBounds().height >= state.getImageSize().height))
			{
				continue;
			}			
			
			if (control.getText() != null && control.getText().length() > 0)
			{
			Rect bounds = control.getBounds();
			
			
			String language = state.predictLanguage();
			
			if (language.equals("eng"))
			{
			ITextExtractor textsExtractor = new TextExtractor(0.65f, language);
			
			//System.out.println(language);
			
			//break;
			
			String imageText="";
			try
			{
				imageText = textsExtractor.extract(ImageIO.read(state.getImageFile()), bounds);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					//extract(state.getImageFile(), bounds);
			
			if ((false == isSimillar(control.getText(), imageText)) && (false == imageText.contains(control.getText())))
			{
		
				String imageText2 = textsExtractor.extract(state.getImageFile(), bounds);
				
				if ((false == isSimillar(control.getText(), imageText2)) && (false == imageText2.contains(control.getText())))
				{
				
				resultImage.drawBounds(bounds);
	
				resultImage.drawText(imageText2 + " / " + imageText + " | " + control.getText(), bounds);
				
				found = true;
				
				System.out.println("1: " + control.getText());
				System.out.println("2: " + imageText + " | " + imageText2 + "\n");
				
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
		
			
			}
			}
			}
		}
		
		if (found)
		{
		resultImage.save(Config.debugFolder + "a_" + UUID.randomUUID().toString() + "1.png");
		}
		
		
		
	//	String errors = "";
	//	String mistypes = "";
	
//		String allTexts = state.getActualControls().stream().map(this::getText).collect(Collectors.joining(". "));
	
//		System.out.println(allTexts);
		
		
	/*	List<Language> languages = getLanguage(allTexts);
	
		if (0 == languages.size())
		{
			return;
		}
	
		List<String> messages = new ArrayList<>();
	
		for (Control control : state.getActualControls())
		{
			if (null != control.getText())
			{
				messages.add(control.getText());
			}
			
			if (null != control.getContentDescription())
			{
				messages.add(control.getContentDescription());
			}
		}
	
		for (String expected : messages)
		{
			mistypes = isSpellingCorrect(state.getAppContext(), mistypes, languages, expected);
			
			errors += " " + mistypes;
			errors = errors.trim();
		}
		
		if (errors.length() > 0)
		{
//			state.dumpRecognitionDebug();
		}
		
		if (errors.length() > 0)
		{
			failures.addFailure(new CheckResult(state, this, errors));
		} */
	}
	
	@Override
	public void analyze(AppContext appContext, ResultsCollector failures)
	{
	}
}
