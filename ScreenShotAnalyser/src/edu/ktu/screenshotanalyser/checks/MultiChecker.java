package edu.ktu.screenshotanalyser.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ktu.screenshotanalyser.checks.semantic.SL1_MissingTranslationCheck;
import edu.ktu.screenshotanalyser.checks.semantic.SU2_TooHardToUnderstandCheck;
import edu.ktu.screenshotanalyser.context.AppContext;

public class MultiChecker implements ICheck {

	public MultiChecker() {
		this(new ICheck[0]);
	}

	public MultiChecker(ICheck... checks) {
		this.checks = checks;
	}

	private final ICheck[] checks;

	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		List<CheckResult> results = new ArrayList<>();

		for (ICheck check : checks) {
			results.addAll(Arrays.asList(check.analyze(request, context)));
		}
		return results.toArray(new CheckResult[0]);
	}

	public static ICheck getAppContextChecks() {
		return new MultiChecker(new SU2_TooHardToUnderstandCheck());
	}

	public static ICheck getImageBasedChecks() {
		return new MultiChecker(new SL1_MissingTranslationCheck());
	}

}
