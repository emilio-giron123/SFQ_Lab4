package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */

public class App {
	//===================================================================================================
	//function to calculate Mean Squared Error
	public static double MSE (List<String[]> allData) {
		double sum=0.0, difference=0.0;
		int n= allData.size();

		//sum of all actual and predicted differences
		for (int i=0; i<n ; i++) {
			double actual = Double.parseDouble(allData.get(i)[0]); //extract actual value from 1st column
			double predicted = Double.parseDouble(allData.get(i)[1]); //extract predicted value from 2nd column

			difference = actual - predicted;
			sum += Math.pow (difference, 2);
		}

		double mse = (sum/n);

		return mse;
	}

	//==============================================================================================
	//function to calculate Mean Absolute Error
	public static double MAE (List<String[]> allData) {
		//initialize values
		double sum=0.0, difference = 0.0;
		int n=allData.size();

		//sum of all actual and predicted differences
		for (int i=0; i<n ; i++) {
			double actual = Double.parseDouble(allData.get(i)[0]); //extract actual value from 1st column
			double predicted = Double.parseDouble(allData.get(i)[1]); //extract predicted value from 2nd column

			difference = actual - predicted;
			sum += Math.abs (difference);
		}

		double mae = (sum/n);
		return mae;
	}

	//==============================================================================================
	//function to calculate Mean Absolute Error
	public static double MARE (List<String[]> allData) {
		//initialize values
		double sum=0.0, difference = 0.0;
		int n=allData.size();


		//sum of all actual and predicted differences
		for (int i=0; i<n ; i++) {
			double actual = Double.parseDouble(allData.get(i)[0]); //extract actual value from 1st column
			double predicted = Double.parseDouble(allData.get(i)[1]); //extract predicted value from 2nd column

			//avoid a 0 being the divisor
			if (actual != 0) {
				difference = (actual - predicted) / actual;
				sum += Math.abs(difference);
			}
		}

		double mare = (sum/n);
		return mare;
	}

	//================================================================================================
	//main function
    public static void main( String[] args )
    {
		//array with all file paths
		String[] filePaths= {"model_1.csv", "model_2.csv", "model_3.csv"};

		//variables for finding the best MSE, MAE & MARE
		double lowestMSE = Double.MAX_VALUE, lowestMAE = Double.MAX_VALUE, lowestMARE = Double.MAX_VALUE;
		String bestMSE="", bestMAE="", bestMARE="";

		//loop through each file
		for (int i=0; i <filePaths.length; i++){
			FileReader filereader;
			List<String[]> allData;
			try {
				filereader = new FileReader(filePaths[i]);
				CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
				allData = csvReader.readAll();
			} catch (Exception e) {
				System.out.println("Error reading the CSV file: "+filePaths[i]);
				return;
			}

			//OUTPUT for "each file
			double mse = MSE(allData);
			double mae = MAE(allData);
			double mare = MARE(allData);
			System.out.println("For " +filePaths[i]+":");
			System.out.println("\t MSE = " + mse);
			System.out.println("\t MAE = " + mae);
			System.out.println("\t MARE = " + mare);

			//save and the lowest value for MSE, MAE & MARE
			if (mse<lowestMSE) {
				lowestMSE = mse;
				bestMSE = filePaths[i];
			}

			if (mae<lowestMAE) {
				lowestMAE = mae;
				bestMAE = filePaths[i];
			}
			if (mare<lowestMARE) {
				lowestMARE = mare;
				bestMARE = filePaths[i];
			}


		}
		//output the lowest MSE, MAE & MARE back to user
		System.out.println ("According to MSE, the best model is "+bestMSE);
		System.out.println ("According to MAE, the best model is "+bestMAE);
		System.out.println ("According to MARE, the best model is "+bestMARE);

	}//end of MAIN
}//end of public class
