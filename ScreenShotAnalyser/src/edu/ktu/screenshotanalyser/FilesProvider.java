package edu.ktu.screenshotanalyser;

import java.io.File;
import java.io.FileFilter;

public class FilesProvider implements IFilesProvider {

	public File[] getFiles(String baseDir) {
		return new File(baseDir).listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !pathname.isDirectory();
			}
		});
	}

}
