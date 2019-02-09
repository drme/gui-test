using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DefectsViewer
{
	class ImagesManager
	{
		protected List<FileInfo> images = new List<FileInfo>();
		protected DirectoryInfo root;
		private int activeImage = 0;

		public ImagesManager(String rootFolder)
		{
			if (null != rootFolder)
			{
				this.root = new DirectoryInfo(rootFolder);

				var badFile = new FileInfo(rootFolder + "/bad.txt");

				if (badFile.Exists)
				{
					using (var file = new StreamReader(badFile.FullName))
					{
						String fileName;

						while ((fileName = file.ReadLine()) != null)
						{
							fileName = fileName.Split('|')[0];

							this.images.Add(new FileInfo(this.root.FullName + "/" + fileName));
						}
					}
				}
			}
		}

		public FileInfo ActiveImage
		{
			get
			{
				return this.images[this.activeImage];
			}
		}

		public String Status
		{
			get
			{
				return (this.activeImage + 1) + "/" + this.images.Count;
			}
		}

		public virtual void MarkOk()
		{
		}

		public virtual void MarkLater()
		{
		}

		public virtual void MarkBad(params string[] type)
		{
		}
        public virtual void MarkInvalid()
        {

        }

        public void NextImage()
		{
			this.activeImage++;
			this.activeImage %= this.images.Count;
		}
	}
}
