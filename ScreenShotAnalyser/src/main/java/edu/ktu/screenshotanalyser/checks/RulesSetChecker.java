package edu.ktu.screenshotanalyser.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nonnull;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

public class RulesSetChecker
{
	public void addRule(BaseRuleCheck rule)
	{
		this.rulesCheckers.add(rule);
	}

	public void runStateChecks(State state, ExecutorService exec, IResultsCollector failures)
	{
//		if (!state.getImageFile().getAbsolutePath().contains("10.1_WXGA_Tablet_API_28\\com.tickledmedia.ParentTown\\states\\screen_2019-01-07_182440.png")) return;
		
		var checkResults = new StateCheckResults(state);
		var checksAvailable = this.rulesCheckers.stream().filter(IStateRuleChecker.class::isInstance).map(IStateRuleChecker.class::cast);
		var checksFutures = checksAvailable.map(check -> CompletableFuture.runAsync(() -> runStateCheck(check, state, checkResults), exec)).toList(); 
		var allChecksDoneFuture = CompletableFuture.allOf(checksFutures.toArray(new CompletableFuture[checksFutures.size()]));

		allChecksDoneFuture.thenRun(() -> finishStateChecks(state, failures, checkResults));
	}

	private void runStateCheck(@Nonnull IStateRuleChecker check, State state, StateCheckResults results)
	{
		try
		{
			check.analyze(state, results);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}		
	}
	
	private void finishStateChecks(State state, IResultsCollector failures, StateCheckResults results)
	{
		annotateResultImage(state, results);
		
		failures.finishedState(state, results);
		
		state.unloadData();
	}
	
	private void annotateResultImage(State state, StateCheckResults results)
	{
		var annotations = results.getAnnotations();

		if (!annotations.isEmpty())
		{
			var debugImage = state.getResultImage();
			
			annotations.forEach(p ->
			{
				debugImage.drawBounds(p.bounds());
				debugImage.drawText(p.message(), p.bounds());
			});
		}
	}

	public void runAppChecks(AppContext context, ExecutorService exec, IResultsCollector failures)
	{
		var checksAvailable = this.rulesCheckers.stream().filter(IAppRuleChecker.class::isInstance).map(IAppRuleChecker.class::cast).toList();
		
		checksAvailable.forEach(check -> exec.submit(() -> check.analyze(context, failures)));
	}
	
	public String buildRunName()
	{
		var names = this.rulesCheckers.stream().map(p -> p.getRuleCode());
		
		return String.join("-", names.toArray(String[]::new));
	}

	private final List<BaseRuleCheck> rulesCheckers = new ArrayList<>();
}
