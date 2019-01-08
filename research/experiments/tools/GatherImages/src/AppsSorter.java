import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AppsSorter
{
	public static void main(String[] args) throws IOException
	{
		String root = "e:/_img_/2/1/";
		String destination = "e:/_apps/";

		for (String apkName : new File(root).list())
		{
			File app = new File(root + "/" + apkName);

			if (app.getAbsolutePath().endsWith(".apk"))
			{
				moveApp(app, destination);
			}
		}		
	}

	private static void moveApp(File app, String destination) throws IOException
	{
		String name = app.getName().replaceAll("[_]apkpure[.]com|[.]apk", "");
		
		File destinationFolder = new File(destination + "/" + name);
		
		destinationFolder.mkdirs();
		
		Files.move(app.toPath(), new File(destinationFolder.getAbsolutePath() + "/" + name + ".apk").toPath());
	}
}
