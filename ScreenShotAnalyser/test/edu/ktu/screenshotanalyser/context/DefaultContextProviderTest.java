package edu.ktu.screenshotanalyser.context;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DefaultContextProviderTest {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@SuppressWarnings("deprecation")
	@Test
	public void test() throws IOException {

		folder.create();
		folder.newFolder("res", "values");
		File createdFile = folder.newFile("res/values/strings.xml");

		FileUtils.write(createdFile, "<resources><string name='test'>TESTSTRING</string></resources>");
		DefaultContextProvider sut = new DefaultContextProvider();

		AppContext ctx = sut.getContext(folder.getRoot().getAbsolutePath());
		Assert.assertTrue(ctx.getKeys().contains("test"));
		Assert.assertTrue(ctx.getResources().containsKey("default"));
		Assert.assertTrue(ctx.getResources().get("default").contains("test"));
		Assert.assertTrue(ctx.getResources().get("default").get(0).getValue().equals("TESTSTRING"));
	}

}
