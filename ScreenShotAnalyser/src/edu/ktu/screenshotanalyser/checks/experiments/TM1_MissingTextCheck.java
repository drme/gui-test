package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.List;

import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.context.AppContext;

public class TM1_MissingTextCheck implements ICheck {

	String type = "TM1";

	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		List<CheckResult> results = new ArrayList<>();

		for (String actualText : request.getActualTexts()) {
			boolean textRecognized = request.getExtractedTexts().stream()
					.anyMatch(eText -> actualText.equalsIgnoreCase(eText.getText()));
			if (!textRecognized) {
				results.add(CheckResult.Nok(type,
						String.format("[%s] device: Actual text '%s' was not recognized", request.getDevice(),
								actualText),

						request.getOriginalFile(), "-"));
			}
		}
		return results.toArray(new CheckResult[0]);
	}

}
