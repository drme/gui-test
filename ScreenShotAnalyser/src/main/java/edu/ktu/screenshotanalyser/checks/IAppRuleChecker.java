package edu.ktu.screenshotanalyser.checks;

import javax.annotation.Nonnull;
import edu.ktu.screenshotanalyser.context.AppContext;

/**
 * Checks all application for defects in one go.
 */
public interface IAppRuleChecker
{
	void analyze(@Nonnull AppContext appContext, @Nonnull AppCheckResults results);
}
