package edu.ktu.screenshotanalyser.checks;

public class CheckResult {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + (isOK ? 1231 : 1237);
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckResult other = (CheckResult) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (isOK != other.isOK)
			return false;
		if (lang == null) {
			if (other.lang != null)
				return false;
		} else if (!lang.equals(other.lang))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

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
	private final String lang;

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
}
