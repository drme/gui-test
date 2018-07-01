package edu.ktu.screenshotanalyser.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.ktu.screenshotanalyser.checks.semantic.SD1_SynonymsUsage;
import edu.ktu.screenshotanalyser.checks.semantic.SL1_MissingTranslationCheck;
import edu.ktu.screenshotanalyser.checks.semantic.SS1_GrammarCheck;
import edu.ktu.screenshotanalyser.checks.semantic.SU2_TooHardToUnderstandCheck;
import edu.ktu.screenshotanalyser.context.AppContext;

public class MultiChecker implements ICheck {

	private final ICheck[] checks;

	public MultiChecker() {
		this(new ICheck[0]);
	}

	public MultiChecker(ICheck... checks) {
		this.checks = checks;
	}

	@Override
	public CheckResult[] analyze(final CheckRequest request, final AppContext context) {
		final List<CheckResult> results = new ArrayList<>();

		for (final ICheck check : checks) {
			results.addAll(Arrays.asList(check.analyze(request, context)));
		}
		return results.stream().distinct().collect(Collectors.toList()).toArray(new CheckResult[0]);
	}

	public static ICheck getAppContextChecks() {
		return new MultiChecker(new SU2_TooHardToUnderstandCheck(), new SS1_GrammarCheck(), new SD1_SynonymsUsage());
	}

	public static ICheck getImageBasedChecks() {
		return new MultiChecker(new SL1_MissingTranslationCheck());
	}

}
