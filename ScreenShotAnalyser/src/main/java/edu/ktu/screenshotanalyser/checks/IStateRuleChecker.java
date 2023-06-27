package edu.ktu.screenshotanalyser.checks;

import javax.annotation.Nonnull;
import edu.ktu.screenshotanalyser.context.State;

/**
 * Checks one application's screenshot for defects.
 */
public interface IStateRuleChecker
{
	/**
	 * Analyzes sate and adds defects to result.
	 * 
	 * @param state state to analyze.
	 * @param result results collector
	 */
	void analyze(@Nonnull State state, @Nonnull StateCheckResults result);
	/**
	 * @return defects identifier.
	 */
	long getId();
}
