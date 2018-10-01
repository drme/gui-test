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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((file == null) ? 0 : file.hashCode());
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			ResourceText other = (ResourceText) obj;
			if (file == null) {
				if (other.file != null)
					return false;
			} else if (!file.equals(other.file))
				return false;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
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
