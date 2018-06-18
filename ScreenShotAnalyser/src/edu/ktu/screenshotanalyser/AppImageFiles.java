package edu.ktu.screenshotanalyser;

import java.io.File;

public class AppImageFiles {
	public String getBaseDirName() {
		return baseDir;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getConfig() {
		return config;
	}

	public File[] getFiles() {
		return files;
	}

	private String projectName;
	private String config;
	private File[] files;
	private String baseDir;

	public AppImageFiles(String baseDir, String appName, String configName, File... files) {
		this.baseDir = baseDir;
		this.projectName = appName;
		this.config = configName;
		this.files = files;
	}
}