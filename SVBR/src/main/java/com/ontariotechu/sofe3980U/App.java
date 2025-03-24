package com.ontariotechu.sofe3980U;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
	//=======================================================================================
	public static double calcBCE (List<String[]> allData) {

		double bce=0.0;
		int n=allData.size();
		double epsilon = 1e-15;

		for (int i=0; i<n ; i++) {
			double actual = Double.parseDouble(allData.get(i)[0]);
			double predicted = Double.parseDouble(allData.get(i)[1]);

			predicted=Math.max(Math.min(predicted, 1-epsilon), epsilon);
			bce += actual * Math.log(predicted) + (1-actual) * Math.log(1-predicted);
		}
		return -bce/n;
	}

	//=================================================================================
	//function to calculate Confusion Matrix and return TP, FP, FN, TN
	public static int[] calcConfusionMatrix (List<String[]> allData, double threshold) {
		int TP=0, FP=0, FN=0, TN=0;
		int n=allData.size();

		for (int i=0; i<n ; i++) {
			double actual = Double.parseDouble(allData.get(i)[0]);
			double predicted = Double.parseDouble(allData.get(i)[1]);


			if(actual==1 && predicted>=threshold) {
				TP++;
			} else if(actual==1 && predicted<threshold) {
				FN++;
			} else if(actual==0 && predicted>=threshold) {
				FP++;
			} else if(actual==0 && predicted<threshold) {
				TN++;
			}

		}

		return new int[] {TP, FP, FN, TN};
	}

	//===========================================================================
	public static double calcAccuracy (int[] confusionMatrix) {
		return (double)(confusionMatrix[0]+confusionMatrix[3])/(confusionMatrix[0]+confusionMatrix[1]+confusionMatrix[2]+confusionMatrix[3]);
	}

	//===========================================================================
	public static double calcPrecision (int[] confusionMatrix) {
		return (double)(confusionMatrix[0])/(confusionMatrix[0]+confusionMatrix[1]);
	}

	//===========================================================================
	public static double calcRecall (int[] confusionMatrix) {
		return (double)(confusionMatrix[0])/(confusionMatrix[0]+confusionMatrix[2]);
	}
	//===========================================================================
	public static double calcF1Score (double precision, double recall) {
		return 2*(precision*recall)/(precision+recall);
	}
	//===========================================================================
	public static double calcAUC(List<String[]> allData) {
		double auc = 0.0;
		int n = allData.size();
		double[] actual = new double[n];
		double[] predicted = new double[n];

		// Read actual and predicted values into arrays
		for (int i = 0; i < n; i++) {
			actual[i] = Double.parseDouble(allData.get(i)[0]);
			predicted[i] = Double.parseDouble(allData.get(i)[1]);
		}

		// Arrays to store TPR and FPR values
		double[] tprList = new double[101]; // 101 thresholds from 0 to 1
		double[] fprList = new double[101];

		// Iterate over 100 thresholds from 0 to 1
		for (int i = 0; i <= 100; i++) {
			double threshold = i / 100.0;
			int TP = 0, FP = 0, FN = 0, TN = 0;

			for (int j = 0; j < n; j++) {
				if (actual[j] == 1 && predicted[j] >= threshold) {
					TP++;
				} else if (actual[j] == 1 && predicted[j] < threshold) {
					FN++;
				} else if (actual[j] == 0 && predicted[j] >= threshold) {
					FP++;
				} else if (actual[j] == 0 && predicted[j] < threshold) {
					TN++;
				}
			}

			// Compute and store TPR and FPR
			double TPR = (TP + FN == 0) ? 0 : (double) TP / (TP + FN); // Handle divide by zero
			tprList[i] = TPR;

			double FPR = (FP + TN == 0) ? 0 : (double) FP / (FP + TN); // Handle divide by zero
			fprList[i] = FPR;
		}

		// Calculate AUC
		for (int i = 1; i <= 100; i++) {
			auc+= (tprList[i]+tprList[i-1])*(Math.abs(fprList[i]-fprList[i-1]))/2;
		}

		return auc;
	}

	//======================================================================================
// main function
	public static void main(String[] args) {
		String[] filePath = {"model_1.csv", "model_2.csv", "model_3.csv"};
		FileReader filereader;
		List<String[]> allData;

		// Variables for finding the best model in each category
		double lowestBCE = Double.MAX_VALUE, highestAccuracy = -Double.MAX_VALUE, highestPrecision = -Double.MAX_VALUE;
		double highestRecall = -Double.MAX_VALUE, highestF1Score = -Double.MAX_VALUE, highestAUC = -Double.MAX_VALUE;
		String bestBCE = "", bestAccuracy = "", bestPrecision = "", bestRecall = "", bestF1Score = "", bestAUC = "";

		for (int i = 0; i < filePath.length; i++) {
			try {
				filereader = new FileReader(filePath[i]);
				CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
				allData = csvReader.readAll();
			} catch (Exception e) {
				System.out.println("Error reading the CSV file");
				return;
			}

			// Calculate the metrics for the model
			double bce = calcBCE(allData);
			int[] confusionMatrix = calcConfusionMatrix(allData, 0.5);
			double accuracy = calcAccuracy(confusionMatrix);
			double precision = calcPrecision(confusionMatrix);
			double recall = calcRecall(confusionMatrix);
			double f1score = calcF1Score(precision, recall);

			double auc = calcAUC(allData);

			// Output results for the model
			System.out.println("For: " + filePath[i]);
			System.out.println("\tBCE: " + bce);
			System.out.println("\tConfusion Matrix: \n\t\t\ty=1 \ty=0 \n\t\ty^=1 \t" + confusionMatrix[0] + "\t" + confusionMatrix[1] + "\n\t\ty^=0 \t" + confusionMatrix[2] + "\t\t" + confusionMatrix[3]);
			System.out.println("\tAccuracy = " + accuracy);
			System.out.println("\tPrecision = " + precision);
			System.out.println("\tRecall = " + recall);
			System.out.println("\tF1 Score = " + f1score);
			System.out.println("\tAUC ROC = " + auc);

			// Save the best model for each category (maximize for Accuracy, Precision, Recall, F1 Score, AUC; minimize for BCE)
			if (bce < lowestBCE) {
				lowestBCE = bce;
				bestBCE = filePath[i];
			}

			if (accuracy > highestAccuracy) {
				highestAccuracy = accuracy;
				bestAccuracy = filePath[i];
			}
			if (precision > highestPrecision) {
				highestPrecision = precision;
				bestPrecision = filePath[i];
			}
			if (recall > highestRecall) {
				highestRecall = recall;
				bestRecall = filePath[i];
			}

			if (f1score > highestF1Score) {
				highestF1Score = f1score;
				bestF1Score = filePath[i];
			}
			if (auc > highestAUC) {
				highestAUC = auc;
				bestAUC = filePath[i];
			}

		}

		// Output the best model for each category
		System.out.println("\nAccording to BCE, the best model is " + bestBCE);
		System.out.println("According to Accuracy, the best model is " + bestAccuracy);
		System.out.println("According to Precision, the best model is " + bestPrecision);
		System.out.println("According to Recall, the best model is " + bestRecall);
		System.out.println("According to F1 Score, the best model is " + bestF1Score);
		System.out.println("According to AUC, the best model is " + bestAUC);

	} // end of MAIN

}//end of CLASS
