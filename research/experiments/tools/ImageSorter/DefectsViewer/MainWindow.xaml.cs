using System;
using System.Collections.Generic;
using System.Drawing;
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
using Brushes = System.Windows.Media.Brushes;


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
        // True when we're selecting a rectangle.
        private bool IsSelecting = false;

        // The area we are selecting.
      

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

            ShowImage(image);
        }

        private void ShowImage(BitmapImage src)
        {





            ImageSource s = src;
         
            System.Diagnostics.Debug.WriteLine(X0 + " " + Y0 + "  -  " + X1 + " " + Y1);
            var h = Math.Abs(Y1 - Y0);
            var w = Math.Abs(X1 - X0);
           if (X0 > 0 && Y0 > 0 && h > 0 && w > 0 && this.H > 0 && this.W > 0)
            {
                var dh = src.PixelHeight / this.H;
                var dw = src.PixelWidth / this.W;
                 var r = new Rect(X0 * dw, Y0 * dh, w * dw, h * dh);
                 System.Diagnostics.Debug.WriteLine(r);
                 DrawingVisual dv = new DrawingVisual();
                 using (DrawingContext dc = dv.RenderOpen())
                 {
                     dc.DrawImage(src, new Rect(0, 0, src.PixelWidth, src.PixelHeight));
                     dc.DrawRectangle(null, new Pen(new SolidColorBrush(Color.FromRgb(255,0,0)), 1.5), r);
                 }

                 RenderTargetBitmap rtb = new RenderTargetBitmap(src.PixelWidth, src.PixelHeight, 96, 96, PixelFormats.Pbgra32);
                 rtb.Render(dv);
                 s = rtb;
                 System.Diagnostics.Debug.WriteLine("Updated");
            }

          


            this.ImageView.Source = s;
            var p = PresentationSource.FromVisual(this);

            if (null != p)
            {
                Matrix m = p.CompositionTarget.TransformToDevice;
                var dx = m.M11;
                var dy = m.M22;

                double hh = this.ImageView.Height;



                this.Title = this.sorter.Status + " " + hh + " " + dy;
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


            

        private void ImageView_MouseRightButtonDown(object sender, MouseButtonEventArgs e)
        {
            System.Windows.Point p = e.GetPosition(this.ImageView);
            this.X1 = p.X;
            this.Y1 = p.Y;
            this.H = this.ImageView.ActualHeight;
            this.W = this.ImageView.ActualWidth;
            ShowActiveImage();
        }

        private void ImageView_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            System.Windows.Point p = e.GetPosition(this.ImageView);
            this.X0 = p.X;
            this.Y0 = p.Y;
            this.H = this.ImageView.ActualHeight;
            this.W = this.ImageView.ActualWidth;
            ShowActiveImage();
        }
        private double X0, X1, Y0, Y1 = -1;
        private double H = -1;
        private double W = -1;
    }
}
