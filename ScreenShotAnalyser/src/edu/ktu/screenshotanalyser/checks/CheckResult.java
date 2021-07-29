package edu.ktu.screenshotanalyser.checks;

import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;

public class CheckResult
{
	public CheckResult(State state, BaseRuleCheck ruleCheck, String message, long defectsCount)
	{
		this.message = message;
		this.state = state;
		this.ruleCheck = ruleCheck;
		this.appContext = state.getAppContext();
		this.defectsCount = defectsCount;
		
		this.ruleCheck.logMessage(this.ruleCheck.getRuleCode() + ": " + this.state.getImageFile().getAbsolutePath() + " " + this.message);
	}

	public CheckResult(AppContext appContext, BaseRuleCheck ruleCheck, String message)
	{
		this.state = null;
		this.message = message;
		this.ruleCheck = ruleCheck;
		this.appContext = appContext;
		this.defectsCount = -1;
		
		this.ruleCheck.logMessage(this.ruleCheck.getRuleCode() + ": " + this.appContext.getName() + " " + this.message);
	}	
	
	public BaseRuleCheck getRule()
	{
		return this.ruleCheck;
	}
	
	public long getDefectsCount()
	{
		return this.defectsCount;
	}
	
	public State getState()
	{
		return this.state;
	}
	
	public AppContext getAppContext()
	{
		return this.appContext;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public synchronized ResultImage getResultImage()
	{
		if (null == this.resultImage)
		{
			this.resultImage = new ResultImage(this.state.getImageFile());
		}
		
		return this.resultImage;
	}
	
	private final String message;
	private final State state;
	private final BaseRuleCheck ruleCheck;	
	private final AppContext appContext;
	private final long defectsCount;
	private ResultImage resultImage = null;
}
