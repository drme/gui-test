package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.languagetool.Language;
import edu.ktu.screenshotanalyser.checks.BaseTextRuleCheck;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.IAppRuleChecker;
import edu.ktu.screenshotanalyser.checks.IStateRuleChecker;
import edu.ktu.screenshotanalyser.checks.ResultsCollector;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

public class GrammarCheck extends BaseTextRuleCheck implements IStateRuleChecker, IAppRuleChecker
{
	public GrammarCheck()
	{
		super(2, "BadSpelling");
	}
	
	@Override
	public void analyze(State state, ResultsCollector failures)
	{
		var allTexts = state.getActualControls().stream().map(this::getText).collect(Collectors.joining(". "));
		var ll = determineLanguageAll(allTexts);

		if (ll.isEmpty())
		{
			return;
		}

		for (var language : ll)
		{
			if (language.equals("lt"))
			{
				return;
			}
		}

		var languages = new ArrayList<Language>();

		for (var q : ll)
		{
			languages.addAll(getLanguageByCode(q));
		}

		if (languages.isEmpty())
		{
			return;
		}

		// var messages = new ArrayList<String>();

		var mistypes = "";
		var errors = "";

		for (var control : state.getActualControls())
		{
			if (null != control.getText() && control.getText().trim().length() > 0)
			{
				// messages.add(control.getText());

				mistypes = isSpellingCorrect(state.getAppContext(), mistypes, languages, control.getText().trim(), false);

				if ((mistypes != null) && (mistypes.length() > 0))
				{
					failures.addFailure(new CheckResult(state, this, mistypes, 1));

					return;
				}
			}

			if (null != control.getContentDescription())
			{
				// messages.add(control.getContentDescription());

				if (null != control.getContentDescription() && control.getContentDescription().trim().length() > 0)
				{
					mistypes = isSpellingCorrect(state.getAppContext(), mistypes, languages, control.getContentDescription().trim(), false);

					if ((mistypes != null) && (mistypes.length() > 0))
					{
						failures.addFailure(new CheckResult(state, this, mistypes, 1));

						return;
					}
				}
			}
		}

		// for (var expected : messages)
		// {
		// mistypes = isSpellingCorrect(state.getAppContext(), mistypes, languages, expected);
		//
		// errors += " " + mistypes;
		// errors = errors.trim();
		// }
		//
		// if (errors.length() > 0)
		// {
		// failures.addFailure(new CheckResult(state, this, errors, errors.length()));
		// }
	}

	@Override
	public void analyze(AppContext appContext, ResultsCollector failures)
	{
		var messages = appContext.getMessages();
		
		if (null != messages)
		{
			var languages = messages.getLanguages().stream().sorted((x, y) -> x.length() - y.length()).collect(Collectors.toList()).toArray(new String[0]);
		
			for (var key : messages.getKeys())
			{
				for (var language : languages)
				{
					var errors = "";
					var message = messages.getMessage(key, language).replace("%s", "");
					var mistypes = isSpellingCorrect(appContext, "", getLanguageByCode(language), message, false);
					
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
}
