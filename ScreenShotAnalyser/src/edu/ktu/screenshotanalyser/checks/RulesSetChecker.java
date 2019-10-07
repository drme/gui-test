package edu.ktu.screenshotanalyser.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

public class RulesSetChecker
{
	public void addRule(BaseRuleCheck rule)
	{
		this.rulesCheckers.add(rule);
	}

	public void runStateChecks(State state, ExecutorService exec, ResultsCollector failures)
	{
		for (BaseRuleCheck ruleCheck : this.rulesCheckers)
		{
			if (ruleCheck instanceof IStateRuleChecker)
			{
				IStateRuleChecker check = (IStateRuleChecker)ruleCheck;

				exec.submit(() -> check.analyze(state, failures));
			}
		}
	}

	public void runAppChecks(AppContext context, ExecutorService exec, ResultsCollector failures)
	{
		for (BaseRuleCheck ruleCheck : this.rulesCheckers)
		{
			if (ruleCheck instanceof IAppRuleChecker)
			{
				IAppRuleChecker check = (IAppRuleChecker)ruleCheck;

				exec.submit(() -> check.analyze(context, failures));
			}
		}
	}

	private final List<BaseRuleCheck> rulesCheckers = new ArrayList<>();
}
