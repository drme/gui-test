package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.RuleMatch;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;
import edu.ktu.screenshotanalyser.context.Control;
import edu.ktu.screenshotanalyser.context.LocalizedMessages;
import edu.ktu.screenshotanalyser.context.State;
import edu.ktu.screenshotanalyser.rules.checkers.CheckRequest;
import edu.ktu.screenshotanalyser.rules.checkers.CheckResult;

public class GrammarCheck extends BaseTextRuleCheck implements IStateRuleChecker, IAppRuleChecker
{
	public GrammarCheck()
	{
		super(2, "BadSpelling");
	}
	
	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		String errors = "";
		String mistypes = "";
	
		String allTexts = state.getActualControls().stream().map(this::getText).collect(Collectors.joining(". "));
	
		List<Language> languages = getLanguage(allTexts);
	
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
			//????
			failures.addFailure(new CheckResult(state, this, errors, errors.length()));
		}
	}
	


	@Override
	public void analyze(AppContext appContext, ResultsCollector failures)
	{
		LocalizedMessages messages = appContext.getMessages();
		
		if (null != messages)
		{
			Set<String> keys = messages.getKeys();
			String[] languages = messages.getLanguages().stream().sorted((x, y) -> x.length() - y.length()).collect(Collectors.toList()).toArray(new String[0]);
		
			for (String key : keys)
			{
				for (String language : languages)
				{
					String errors = "";
					String mistypes = "";
					
					String message = messages.getMessage(key, language);
					
					// remove placeholders
					message = message.replaceAll("%s", "");
					
					List<Language> l = getLanguageByCode(language);
					
					mistypes = isSpellingCorrect(appContext, mistypes, l, message);
					
					errors += " " + mistypes;
					errors = errors.trim();		
					
					
					if (errors.length() > 0)
					{
						failures.addFailure(new CheckResult(appContext, this, errors));
					}
				}
			}
		}
				
				
				
			

				
				
				
				
				
				
				
				
				
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/*
	
	
	
	
	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		final List<CheckResult> results = new ArrayList<>();
		for (Entry<String, List<ResourceText>> entrySet : context.getResources().entrySet()) {
			String langKey = entrySet.getKey();
			List<ResourceText> texts = entrySet.getValue();
			Language language = null;//getLang(langKey);
			if (language != null) {
				try {
					
					JLanguageTool langTool = new JLanguageTool(language);

					for (ResourceText resourceText : texts) {
						List<RuleMatch> matches = langTool.check(resourceText.getValue());
						for (RuleMatch match : matches) {

							System.out.println("Grammar check failed: " + match.getMessage()
							
							//+". Possible fixes: "+match.getSuggestedReplacements()
							
							+". Text was: "+resourceText.getValue());
							
							
					//		results.add(CheckResult.Nok(type, "Grammar check failed: " + match.getMessage()+". Possible fixes: "+match.getSuggestedReplacements()+". Text was: "+resourceText.getValue(),
			//					resourceText.getFile() + "@" + resourceText.getKey(), langKey));
						}
					}

				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else {
				//System.out.println("Skipping lang: " + langKey);
			}

		}
		return results.toArray(new CheckResult[0]);

	}
	
*/






	

}
