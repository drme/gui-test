package edu.ktu.screenshotanalyser.checks.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.English;
import org.languagetool.language.Russian;
import org.languagetool.rules.RuleMatch;

import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.AppContext.ResourceText;

public class SS1_GrammarCheck implements ICheck {
	private final String type = "SS1";

	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		final List<CheckResult> results = new ArrayList<>();
		for (Entry<String, List<ResourceText>> entrySet : context.getResources().entrySet()) {
			String langKey = entrySet.getKey();
			List<ResourceText> texts = entrySet.getValue();
			Language language = getLang(langKey);
			if (language != null) {
				try {
					
					JLanguageTool langTool = new JLanguageTool(language);

					for (ResourceText resourceText : texts) {
						List<RuleMatch> matches = langTool.check(resourceText.getValue());
						for (RuleMatch match : matches) {
							
							results.add(CheckResult.Nok(type, "Grammar check failed: " + match.getMessage()+". Possible fixes: "+match.getSuggestedReplacements()+". Text was: "+resourceText.getValue(),
									resourceText.getFile() + "@" + resourceText.getKey(), langKey));
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
	
	private static Language getLang(String lang) {
		if ("default".equals(lang)) {
			return new AmericanEnglish();
		}
		if ("us".equals(lang)) {
			return new AmericanEnglish();
		}
		if ("uk".equals(lang)) {
			return new BritishEnglish();
		}
		if ("ru".equals(lang)) {
			return new Russian();
		}
		return null;
	}
}
