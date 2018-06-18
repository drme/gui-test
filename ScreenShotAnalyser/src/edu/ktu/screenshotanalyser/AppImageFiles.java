package edu.ktu.screenshotanalyser;

import java.io.File;
import java.util.Arrays;

public class AppImageFiles {
	@Override
	public String toString() {
		return "AppImageFiles [projectName=" + projectName + ", config=" + config + ", files=" + Arrays.toString(files)
				+ ", baseDir=" + baseDir + "]";
	}

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