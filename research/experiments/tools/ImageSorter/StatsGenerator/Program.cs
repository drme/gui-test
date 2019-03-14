using System;
using System.Collections.Generic;
using System.IO;

namespace StatsGenerator
{






	public struct DefectTypes
	{
		public const string TextPlacement = "TP1";
		public const string FontSizes = "TS1";
		public const string UnreadableText = "TS2";
		public const string ClashingBackground = "TB1";
		public const string PartialText = "TC1";
		public const string ClippedText = "TC2";
		public const string WrongEncoding = "TE1";
		public const string MissingText = "TM1";

		public const string NotEnoughSpace = "NS1";

		public const String UntranslatedText = "SL2";
		public const String WastedSpace = "WS1";
		public const String BadColors = "BC1";
		public const String BadScaling = "BS1";
		public const String InvisibleControl = "IC1";
		public const String MisalignedControl = "MC1";
		public const String BadSpelling = "BadSpelling";
		public const String TechnicalJargon = "TechnicalJargon";
		public const String LowResImage = "LowResImage";
		public const String ClippedControl = "ClippedControl";
		public const String NoMargins = "NoMargins";
		public const String Uncentered = "Uncentered";
		public const String UnfilledPlaceholder = "UnfilledPlaceholder";
		public const String BadMargins = "BadMargins";
		public const String ObscuredControl = "ObscuredControl";
		public const String NoAntiAliasing = "NoAntiAliasing";
		public const String EmptyView = "EmptyView";
		public const String UnalignedControlls = "UnalignedControls";
		public const String CrowdedControlls = "CrowdedControlls";
		public const String ObscuredText = "ObscuredText";
		public const String UnlabeledEntry = "UnlabeledEntryField";


		//Misaligned controls
		public const String Unknown = "???";

	}

	























		class Program
	{













		static void Main(string[] args)
		{
			String rootFolder = "D:/_r/";


		String badFile;
		 String okFile;
		 String invalidFile;
		 List<String> sortedImages = new List<String>();
		String laterFile;
		 Random r = new Random();




			DirectoryInfo root = new DirectoryInfo(rootFolder);

			badFile = Path.Combine(rootFolder, "bad.txt");
			okFile = Path.Combine(rootFolder, "ok.txt");
			invalidFile = Path.Combine(rootFolder, "invalid.txt");
			laterFile = Path.Combine(rootFolder, "later.txt");


			long totalImages = 0;


			var defects = new Dictionary<String, long>();


			if (File.Exists(badFile))
			{
				using (var file = new StreamReader(badFile))
				{
					String fileName;

					while ((fileName = file.ReadLine()) != null)
					{
						string[] img = fileName.Split('|');

						String source = img[0];

						for (int i = 1; i < img.Length; i++)
						{
							var d = img[i].Split('@')[0];

							if (false == defects.ContainsKey(d))
							{
								defects.Add(d, 1);
							}
							else
							{
								defects[d] += 1;
							}

						}


						totalImages += 1;
					}
				}
			}

			System.Console.WriteLine("defects: " + totalImages);


			foreach (var dd in defects.Keys)
			{
				System.Console.WriteLine(dd + ": " + defects[dd]);
			}


			if (File.Exists(okFile))
			{ 
				using (var file = new StreamReader(okFile))
				{
					String fileName;

					while ((fileName = file.ReadLine()) != null)
					{
						totalImages += 1;
					}
				}
			}


			if (File.Exists(okFile))
			{
				using (var file = new StreamReader(okFile))
				{
					String fileName;

					while ((fileName = file.ReadLine()) != null)
					{
						totalImages += 1;
					}
				}
			}


			System.Console.WriteLine("totall: " + totalImages);

		}
	}
}
