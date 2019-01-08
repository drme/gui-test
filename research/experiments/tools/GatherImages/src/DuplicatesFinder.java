import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class DuplicatesFinder
{
	public static void main(String args[]) throws IOException
	{
		File tempFolder = new File("E:/_img_/h/");
		
		if (tempFolder.exists())
		{
			for (String sourceFileName : tempFolder.list())
			{
				File sourceFile = new File(tempFolder.getAbsolutePath() + "/" + sourceFileName);
				
				if (sourceFile.exists())
				{
					File original = findOriginal(sourceFile, new File("d:/_results_/"));
					
					if (null != original)
					{
						System.out.println(sourceFile.getAbsolutePath() + " = " + original.getAbsolutePath());
						
//						targetFile.delete();
	
					}
				}
			}
		}
	}
	
	private static File findOriginal(File copyFile, File startFolder) throws IOException
	{
		for (String originalFileName : startFolder.list())
		{
			File originalFile = new File(startFolder.getAbsolutePath() + "/" + originalFileName);

			if (originalFile.isDirectory())
			{
				File result = findOriginal(copyFile, originalFile);
				
				if (null != result)
				{
					return result;
				}
			}
			else
			{
				if (true == areTheSame(copyFile, originalFile))
				{
					return originalFile; 
				}
			}
		}
		
		return null;
	}

	private static void removeDuplicatesFromResults() throws IOException
	{
		String root = "d:/_results_/_2.7_QVGA_API_24/";

		for (String folderName : new File(root).list())
		{
			File folder = new File(root + "/" + folderName);

			if (folder.isDirectory())
			{
				removeDuplicates(folder);
			}
		}			
	}
	
	private static void removeDuplicates(File folder) throws IOException
	{
		File tempFolder = new File(folder.getAbsolutePath() + "/temp/");
		
		if (tempFolder.exists())
		{
			for (String sourceFileName : tempFolder.list())
			{
				File sourceFile = new File(tempFolder.getAbsolutePath() + "/" + sourceFileName);
				
				if (sourceFile.exists())
				{
					for (String targetFileName : tempFolder.list())
					{
						File targetFile = new File(tempFolder.getAbsolutePath() + "/" + targetFileName);
						
						if (targetFile.exists())
						{
							if (true == areTheSame(sourceFile, targetFile))
							{
								System.out.println(sourceFile.getAbsolutePath() + " = " + targetFile.getAbsolutePath());
								
								targetFile.delete();
							}
						}
					}
				}
			}
			


			
			
			File statesFolder = new File(folder.getAbsolutePath() + "/states/");
			
			
			if (statesFolder.exists())
			{
				for (String sourceFileName : statesFolder.list())
				{
					File sourceFile = new File(statesFolder.getAbsolutePath() + "/" + sourceFileName);
					
					if (sourceFile.exists())
					{
						for (String targetFileName : tempFolder.list())
						{
							File targetFile = new File(tempFolder.getAbsolutePath() + "/" + targetFileName);
							
							if (targetFile.exists())
							{
								if (true == areTheSame(sourceFile, targetFile))
								{
									System.out.println(sourceFile.getAbsolutePath() + " = " + targetFile.getAbsolutePath());
									
									targetFile.delete();
								}
							}
						}
					}
				}				
			}		
			
			
			
			
			
			
		}
		

		
	}
	
	private static boolean areTheSame(File source, File target) throws IOException
	{
		//System.out.println("Compare " + source.getName() + " -> " + target.getName());
		
		if (false == source.equals(target))
		{
			if (source.length() != target.length())
			{
				return false;
			}
			
			byte[] f1 = Files.readAllBytes(source.toPath());
			byte[] f2 = Files.readAllBytes(target.toPath());

			return Arrays.equals(f1, f2);
		}
		
		return false;
	}
}
