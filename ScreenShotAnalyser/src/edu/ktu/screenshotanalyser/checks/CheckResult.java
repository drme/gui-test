package edu.ktu.screenshotanalyser.checks;

public class CheckResult {
	private final String type;
	private final String message;
	private final String file;

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

	private final boolean isOK;

	public CheckResult(String type, String message, String file, boolean isOK) {
		this.type = type;
		this.message = message;
		this.file = file;
		this.isOK = isOK;

	}

	public static CheckResult Ok(String type, String file) {
		return new CheckResult(type, "", file, true);
	}
	public static CheckResult Nok(String type, String message, String file) {
		return new CheckResult(type, message, file, false);
	}
}
