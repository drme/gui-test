using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace DefectsViewer
{
	/// <summary>
	/// Logique d'interaction pour MainWindow.xaml
	/// </summary>
	public partial class MainWindow : Window
	{
        private static string baseFolder = @"E:\darbai\sh\OneDrive_1_2-8-2019\app-screenshots\_results_\nokia3.1\ab.damumed";
          //  @"E:\darbai\sh\OneDrive_1_2-8-2019\app-screenshots\_results_\";

        private ImagesManager sorter = new ImagesSorter(baseFolder);

		public MainWindow()
		{
			InitializeComponent();

			ShowActiveImage();
		}

		private void NextImageClick(object sender, RoutedEventArgs e)
		{
			this.sorter.NextImage();

			ShowActiveImage();
		}

		private void MarkOkClick(object sender, RoutedEventArgs e)
		{
			this.sorter.MarkOk();

			ShowActiveImage();
		}

		private void MarkLaterClick(object sender, RoutedEventArgs e)
		{
			this.sorter.MarkLater();

			ShowActiveImage();
		}

		private void ShowActiveImage()
		{
			var image = new BitmapImage(new Uri(this.sorter.ActiveImage.FullName));

			this.ImageView.Source = image;

			var p = PresentationSource.FromVisual(this);

			if (null != p)
			{
				Matrix m = p.CompositionTarget.TransformToDevice;
				double dx = m.M11;
				double dy = m.M22;

				double h = this.ImageView.Height;



				this.Title = this.sorter.Status + " " + h + " " + dy;
			}
		}

		private void MarkDefect(String defect)
		{
			this.sorter.MarkBad(defect);
			ShowActiveImage();
		}

        private void MarkInvalid(object sender, RoutedEventArgs e)
        {
            this.sorter.MarkInvalid();
            ShowActiveImage();
        }

        private void UnreadableClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.UnreadableText);
		}

        private void MarkUnknown(object sender, RoutedEventArgs e)
        {
            MarkDefect(DefectTypes.Unknown);
        }

        private void UntranslatedClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.UntranslatedText);
		}

		private void DefectsClick(object sender, RoutedEventArgs e)
		{
			this.sorter = new ImagesManager(baseFolder);
			ShowActiveImage();
		}

		private void WindowPreviewKeyDown(object sender, KeyEventArgs e)
		{
			if (e.Key == Key.Space)
			{
				NextImageClick(sender, null);
			}
			else if (e.Key == Key.Enter)
			{
				MarkOkClick(sender, null);
			}
			else if (e.Key == Key.Escape)
			{
				MarkLaterClick(sender, null);
			}

			e.Handled = true;
		}

		private void WindowSizeChanged(object sender, SizeChangedEventArgs e)
		{
			ShowActiveImage();
		}

		private void WastedSpaceClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.WastedSpace);
		}

		private void ClippedTextClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.ClippedText);
		}

		private void BadColorsClick(object sender, RoutedEventArgs e)
		{
			MarkDefect(DefectTypes.BadColors);
		}

        private void ImageView_MouseUp(object sender, MouseButtonEventArgs e)
        {

        }

        private void ImageView_MouseDown(object sender, MouseButtonEventArgs e)
        {

        }

        private void ImageView_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {

        }
    }
}
