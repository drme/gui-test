using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DefectsViewer
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


		//Misaligned controls
		public const String Unknown = "???";

	}

	class ImagesSorter : ImagesManager
	{

		public static Dictionary<string, string> defectTypes = new Dictionary<string, string>()
		{
			{"IC1", "InvisibleControl" },
			{"TSC", "TooSmallControl" },
			{"TSC2", "TooSmallControl" },


		};

		private readonly String badFile;
		private readonly String okFile;
		private readonly String invalidFile;
		private List<String> sortedImages = new List<String>();
		private readonly String laterFile;
		private Random r = new Random();

		public ImagesSorter(String rootFolder) : base(null)
		{
			this.root = new DirectoryInfo(rootFolder);

			this.badFile = Path.Combine(rootFolder, "bad.txt");
			this.okFile = Path.Combine(rootFolder, "ok.txt");
			this.invalidFile = Path.Combine(rootFolder, "invalid.txt");
			this.laterFile = Path.Combine(rootFolder, "later.txt");

			RemoveImagesFrom(this.badFile);
			RemoveImagesFrom(this.okFile);
			RemoveImagesFrom(this.invalidFile);
			RemoveImagesFrom(this.laterFile);

			ScanFolder(this.root);

			this.sortedImages = sortedImages.OrderBy(x => (r.Next())).ToList();
		}

		private void RemoveImagesFrom(string file0)
		{
			if (File.Exists(file0))
			{
				using (var file = new StreamReader(file0))
				{
					String fileName;

					while ((fileName = file.ReadLine()) != null)
					{
						string img = this.root.FullName + fileName.Split('|')[0];
						sortedImages.Add(img);
					}
				}
			}
		}

		private void ScanFolder(DirectoryInfo folder)
		{
			if (images.Count > 1000)
			{
				//return;
			}

			if ((folder.Name == "views") || (folder.Name == "temp"))
			{
				return;
			}

			foreach (var file in folder.GetFiles())
			{
				if ((file.Extension == ".jpg") || (file.Extension == ".png"))
				{
					if (this.sortedImages.Contains(file.FullName))
					{
						continue;
					}

					this.images.Add(file);
				}
			}

			foreach (var directory in folder.GetDirectories())
			{
				ScanFolder(directory);
			}
		}

		public override void MarkOk()
		{
			var fileName = this.ActiveImage.FullName;
			fileName = fileName.Substring(this.root.FullName.Length);

			File.AppendAllText(okFile, fileName + "\n");

			System.Diagnostics.Debug.WriteLine("Marked ok " + fileName);

			this.images.Remove(this.ActiveImage);
		}

		public override void MarkLater()
		{
			var fileName = this.ActiveImage.FullName;
			fileName = fileName.Substring(this.root.FullName.Length);

			System.Diagnostics.Debug.WriteLine("Marked later " + fileName);

			File.AppendAllText(laterFile, fileName + "\n");

			this.images.Remove(this.ActiveImage);
		}

		public override void MarkBad(params String[] type)
		{
			var fileName = this.ActiveImage.FullName;
			fileName = fileName.Substring(this.root.FullName.Length);

			System.Diagnostics.Debug.WriteLine("Marked bad " + fileName);

			File.AppendAllText(badFile, fileName + "|" + String.Join("|", type) + "\n");

			this.images.Remove(this.ActiveImage);
		}

		public override void MarkInvalid()
		{
			var fileName = this.ActiveImage.FullName;
			fileName = fileName.Substring(this.root.FullName.Length);

			System.Diagnostics.Debug.WriteLine("Marked invalid " + fileName);

			File.AppendAllText(invalidFile, fileName + "\n");

			this.images.Remove(this.ActiveImage);
		}
	}
}
