import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MessSorter
{
	public static void main(String[] args) throws IOException
	{
		//String device = "_2.7_QVGA_API_24";
		//String device = "_Galaxy_Nexus_API_24";
		String device = "_Nexus_One_API_25";
		 
		String root = "e:/_img_/2/1/";
		String destination = "e:/_results_/";

		for (String folderName : new File(root).list())
		{
			File app = new File(root + "/" + folderName);

			if (app.getAbsolutePath().endsWith(device))
			{
				moveResults(app, destination, device);
			}
		}		
	}

	private static void moveResults(File app, String destination, String device) throws IOException
	{
		String name = app.getName().replaceAll(device, "");
		name = name.replaceAll("[_]apkpure[.]com|[.]apk", "");
		
		System.out.println(name);
		
		File destinationFolder = new File(destination + "/" + device + "/" + name);
		
		Files.move(app.toPath(), destinationFolder.toPath());
	}
}
