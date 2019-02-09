using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DefectsViewer
{
	struct DefectTypes
	{
		public const String TooSmallText = "TS2";
		public const String UntranslatedText = "SL2";
		public const String WastedSpace = "WS1";
		public const String ClippedText = "TC2";
		public const String BadColors = "BC1";
	}

	class ImagesSorter : ImagesManager
	{
		public ImagesSorter(String rootFolder) : base(null)
		{
			this.root = new DirectoryInfo(rootFolder);

			ScanFolder(this.root);

			var okFile = new FileInfo(rootFolder + "/ok.txt");

			if (okFile.Exists)
			{
				using (var file = new StreamReader(okFile.FullName))
				{
					String fileName;

					while ((fileName = file.ReadLine()) != null)
					{
						RemoveImageFromList(fileName);
					}
				}
			}

			var badFile = new FileInfo(rootFolder + "/bad.txt");

			if (badFile.Exists)
			{
				using (var file = new StreamReader(badFile.FullName))
				{
					String fileName;

					while ((fileName = file.ReadLine()) != null)
					{
						RemoveImageFromList(fileName.Split('|')[0]);
					}
				}
			}

			var laterFile = new FileInfo(rootFolder + "/later.txt");

			if (laterFile.Exists)
			{
				using (var file = new StreamReader(laterFile.FullName))
				{
					String fileName;

					while ((fileName = file.ReadLine()) != null)
					{
						RemoveImageFromList(fileName);
					}
				}
			}
		}

		private void ScanFolder(DirectoryInfo folder)
		{
			if ((folder.Name == "views") || (folder.Name == "temp"))
			{
				return;
			}

			foreach (var file in folder.GetFiles())
			{
				if ((file.Extension == ".jpg") || (file.Extension == ".png"))
				{
					this.images.Add(file);
				}
			}

			foreach (var directory in folder.GetDirectories())
			{
				ScanFolder(directory);
			}
		}

		private void RemoveImageFromList(String fileName)
		{
			var sortedFile = new FileInfo(this.root.FullName + "/" + fileName);

			foreach (var f in this.images)
			{
				if (f.FullName == sortedFile.FullName)
				{
					Debug.WriteLine("Already sorted out: " + fileName);

					this.images.Remove(f);

					break;
				}
			}
		}

		public override void MarkOk()
		{
			var fileName = this.ActiveImage.FullName;
			fileName = fileName.Substring(this.root.FullName.Length);

			File.AppendAllText(this.root.FullName + "/ok.txt", fileName + "\n");

			this.images.Remove(this.ActiveImage);
		}

		public override void MarkLater()
		{
			var fileName = this.ActiveImage.FullName;
			fileName = fileName.Substring(this.root.FullName.Length);

			File.AppendAllText(this.root.FullName + "/later.txt", fileName + "\n");

			this.images.Remove(this.ActiveImage);
		}

		public override void MarkBad(String type)
		{
			var fileName = this.ActiveImage.FullName;
			fileName = fileName.Substring(this.root.FullName.Length);

			File.AppendAllText(this.root.FullName + "/bad.txt", fileName + "|" + type + "\n");

			this.images.Remove(this.ActiveImage);
		}
	}
}
