package edu.ktu.screenshotanalyser.rules.checkers;

import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
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
	
	
	
	
	
	
	
	
	
	
	
	

	





	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "CheckResult [type=" + ", message=" + message + ", file=" + ", isOK=" + isOK + "]";
	}



	public boolean isOK() {
		return isOK;
	}

	private  boolean isOK;
	private  String lang;

	public CheckResult(String message, String file, boolean isOK, String lang) {

		this.appContext = null;
		this.ruleCheck = null;
		this.state = null;
		this.message = message;
		this.isOK = isOK;
		this.lang = lang;

		this.defectsCount = -1;
	}

	public static CheckResult Ok(String type, String file) {
		return new CheckResult("", file, true, null);
	}
	public static CheckResult Nok(String type, String message, String file, String lang) {
		return new CheckResult(message, file, false, lang);
	}
	
	
	
	private final String message;
	private final State state;
	private final BaseRuleCheck ruleCheck;	
	private final AppContext appContext;
	private final long defectsCount;
}
