package edu.ktu.screenshotanalyser.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppContext {

	private final Map<String, List<ResourceText>> resources = new HashMap<String, List<ResourceText>>();
	private final List<String> keys = new ArrayList<>();

	public Map<String, List<ResourceText>> getResources() {
		return resources;
	}

	public List<String> getKeys() {
		return keys;
	}

	public boolean isPlaceholder(String text) {
		if (text == null) {
			return false;
		}
		return this.keys.contains(text);
	}

	public static class ResourceText {
		private final String key;

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		private final String value;
		private final  String file;

		public String getFile() {
			return file;
		}

		public ResourceText(String key, String value, String file) {
			this.key = key;
			this.value = value;
			this.file = file;
		}
	}

}
