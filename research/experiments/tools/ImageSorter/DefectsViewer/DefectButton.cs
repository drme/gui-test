using System;
using System.Windows.Controls;

namespace DefectsViewer
{
	public class DefectButton : Button
	{
		private String defect;

		public String Defect
		{
			get
			{
				return this.defect;
			}
			set
			{
				if (this.Content == null)
				{
					this.Content = value;
				}

				this.defect = value;
			}
		}
	}
}
