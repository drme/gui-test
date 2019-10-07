package edu.ktu.screenshotanalyser.rules.checkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import edu.ktu.screenshotanalyser.checks.experiments.GrammarCheck;
import edu.ktu.screenshotanalyser.checks.experiments.MissingTranslationCheck;
import edu.ktu.screenshotanalyser.checks.semantic.SD1_SynonymsUsage;
import edu.ktu.screenshotanalyser.checks.semantic.SS4_UntactfullMessagesCheck;
import edu.ktu.screenshotanalyser.checks.semantic.SU2_TooHardToUnderstandCheck;
import edu.ktu.screenshotanalyser.context.AppContext;

public class MultiChecker implements IRuleChecker {

	private final IRuleChecker[] checks;

	public MultiChecker() {
		this(new IRuleChecker[0]);
	}

	public MultiChecker(IRuleChecker... checks) {
		this.checks = checks;
	}

	@Override
	public CheckResult[] analyze(final CheckRequest request, final AppContext context) {
		final List<CheckResult> results = new ArrayList<>();

		for (final IRuleChecker check : checks) {
			results.addAll(Arrays.asList(check.analyze(request, context)));
		}
		return results.stream().distinct().collect(Collectors.toList()).toArray(new CheckResult[0]);
	}

	public static IRuleChecker getAppContextChecks()
	{
		List<IRuleChecker> checkers = new ArrayList<>();

		//checkers.add(new SU2_TooHardToUnderstandCheck());
		//checkers.add(new SS1_GrammarCheck());
		//checkers.add(new SD1_SynonymsUsage());
		checkers.add(new SS4_UntactfullMessagesCheck());
		
		return new MultiChecker(checkers.toArray(new IRuleChecker[0]));
	}

	public static IRuleChecker getImageBasedChecks() {
		return new MultiChecker(/*new SL1_MissingTranslationCheck() */) ;
	}

}
