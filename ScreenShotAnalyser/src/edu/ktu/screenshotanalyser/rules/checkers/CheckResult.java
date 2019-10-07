package edu.ktu.screenshotanalyser.rules.checkers;

import edu.ktu.screenshotanalyser.checks.BaseRuleCheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.context.State;
import edu.stanford.nlp.io.EncodingPrintWriter.err;

public class CheckResult
{
	public CheckResult(State state, BaseRuleCheck ruleCheck, String message)
	{
		this.message = message;
		this.state = state;
		this.ruleCheck = ruleCheck;
		this.appContext = state.getAppContext();
		
		this.ruleCheck.logMessage(this.ruleCheck.getRuleCode() + ": " + this.state.getImageFile().getAbsolutePath() + " " + this.message);
	}

	public CheckResult(AppContext appContext, BaseRuleCheck ruleCheck, String message)
	{
		this.message = message;
		this.state = null;
		this.ruleCheck = ruleCheck;
		this.appContext = appContext;
		
		this.ruleCheck.logMessage(this.ruleCheck.getRuleCode() + ": " + this.appContext.getAppName() + " " + this.message);
	}	
	

	
	private  String type;

	private  String file;

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "CheckResult [type=" + type + ", message=" + message + ", file=" + file + ", isOK=" + isOK + "]";
	}

	public String getFile() {
		return file;
	}

	public boolean isOK() {
		return isOK;
	}

	private  boolean isOK;
	private  String lang;

	public CheckResult(String type, String message, String file, boolean isOK, String lang) {
		this.type = type;
		this.message = message;
		this.file = file;
		this.isOK = isOK;
		this.lang = lang;

	}

	public static CheckResult Ok(String type, String file) {
		return new CheckResult(type, "", file, true, null);
	}
	public static CheckResult Nok(String type, String message, String file, String lang) {
		return new CheckResult(type, message, file, false, lang);
	}
	
	
	
	private final String message;
	private State state;
	private BaseRuleCheck ruleCheck;	
	private AppContext appContext;
}
