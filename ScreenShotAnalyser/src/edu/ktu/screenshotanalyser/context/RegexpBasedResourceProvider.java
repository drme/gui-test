package edu.ktu.screenshotanalyser.context;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ktu.screenshotanalyser.context.DefaultContextProvider.ResourceDao;
import edu.ktu.screenshotanalyser.context.DefaultContextProvider.Resources;

public class RegexpBasedResourceProvider implements IResourcesProvider {

	@Override
	public Resources getResource(String contents) throws Throwable {
		String a = "\\s+name=\"([^\"]*)\\\"";
		String pattern = "<string\\b" + a + "[^>]*>(.*?)<\\/string>";
		Resources r = new Resources();
		Matcher m = Pattern.compile(pattern).matcher(contents);
		Pattern.compile(pattern, Pattern.MULTILINE);
		List<ResourceDao> dd = new ArrayList<ResourceDao>();
		while (m.find()) {
			ResourceDao dao = new ResourceDao();
			dao.setName(m.group(1));
			dao.setValue(m.group(2));
			dd.add(dao);
		}
		
		r.setString(dd.toArray(new ResourceDao[0]));
		return r;
	}

}
