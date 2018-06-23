package edu.ktu.screenshotanalyser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoriesProvider implements IFilesProvider<File> {

	public File[] getFiles(String baseDir) {
		final List<File> items = new ArrayList<File>();
		for (File f : new File(baseDir).listFiles()) {
			if (f.isDirectory()) {
				items.add(f);
			}
		}
		return items.toArray(new File[0]);
	}

}
