using System;
using Microsoft.ML;
using Microsoft.ML.Runtime.Api;
using Microsoft.ML.Runtime.Data;
using Microsoft.ML.Runtime.Learners;
using Microsoft.ML.Transforms.Conversions;
using System;
namespace ImagesClasifier
{



		class Program
		{
			// STEP 1: Define your data structures
			// IrisData is used to provide training data, and as
			// input for prediction operations
			// - First 4 properties are inputs/features used to predict the label
			// - Label is what you are predicting, and is only set when training
			public class IrisData
			{
				public float SepalLength;
				public float SepalWidth;
				public float PetalLength;
				public float PetalWidth;
				public string Label;
			}

			// IrisPrediction is the result returned from prediction operations
			public class IrisPrediction
			{
				[ColumnName("PredictedLabel")]
				public string PredictedLabels;
			}

			static void Main(string[] args)
			{
				// STEP 2: Create a ML.NET environment  
				var mlContext = new MLContext();

				// If working in Visual Studio, make sure the 'Copy to Output Directory'
				// property of iris-data.txt is set to 'Copy always'
				string dataPath = "iris-data.txt";
				var reader = mlContext.Data.TextReader(new TextLoader.Arguments()
				{
					Separator = ",",
					HasHeader = true,
					Column = new[]
						{
										new TextLoader.Column("SepalLength", DataKind.R4, 0),
										new TextLoader.Column("SepalWidth", DataKind.R4, 1),
										new TextLoader.Column("PetalLength", DataKind.R4, 2),
										new TextLoader.Column("PetalWidth", DataKind.R4, 3),
										new TextLoader.Column("Label", DataKind.Text, 4)
								}
				});


				IDataView trainingDataView = reader.Read(new MultiFileSource(dataPath));

				// STEP 3: Transform your data and add a learner
				// Assign numeric values to text in the "Label" column, because only
				// numbers can be processed during model training.
				// Add a learning algorithm to the pipeline. e.g.(What type of iris is this?)
				// Convert the Label back into original text (after converting to number in step 3)
				var pipeline = mlContext.Transforms.Conversion.MapValueToKey("Label")
						.Append(mlContext.Transforms.Concatenate("Features", "SepalLength", "SepalWidth", "PetalLength", "PetalWidth"))
						.Append(mlContext.MulticlassClassification.Trainers.StochasticDualCoordinateAscent(labelColumn: "Label", featureColumn: "Features"))
						.Append(mlContext.Transforms.Conversion.MapKeyToValue("PredictedLabel"));

				// STEP 4: Train your model based on the data set  
				var model = pipeline.Fit(trainingDataView);

				// STEP 5: Use your model to make a prediction
				// You can change these numbers to test different predictions
				var prediction = model.MakePredictionFunction<IrisData, IrisPrediction>(mlContext).Predict(
						new IrisData()
						{
							SepalLength = 3.3f,
							SepalWidth = 1.6f,
							PetalLength = 0.2f,
							PetalWidth = 5.1f,
						});

				Console.WriteLine($"Predicted flower type is: {prediction.PredictedLabels}");
			}
		}
	}

