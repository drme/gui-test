package edu.ktu.screenshotanalyser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class DroidBotFilesProvider implements IFilesProvider<AppImageFiles> {

	public AppImageFiles[] getFiles(String baseDir) {
		final List<AppImageFiles> items = new ArrayList<AppImageFiles>();
		for (File f : new File(baseDir).listFiles()) {
			String[] temp = f.getName().split("\\.apk_");
			String appName = temp[0];
			String configName = temp.length > 1 ? temp[1] : "Unknown";
			if (f.isDirectory()) {
				File[] files = new File(f, "states").listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return pathname.getName().endsWith(".png");
					}
				});
				items.add(new AppImageFiles(f.getName(), appName, configName, files));
			}
		}
		return items.toArray(new AppImageFiles[0]);
	}

}
