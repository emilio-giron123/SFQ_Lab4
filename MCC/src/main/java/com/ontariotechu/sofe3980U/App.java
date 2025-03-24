package com.ontariotechu.sofe3980U;


import java.io.FileReader;
import java.sql.Array;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{

	public static double calcCE(List<String[]> allData) {
		double ce=0.0;
		int n= allData.size();
		int trueClass=0;
		double val=0;

		for (int i=0; i<n; i++) {
			trueClass = Integer.parseInt(allData.get(i)[0]); //get the true class from first column
			val=Math.log(Double.parseDouble(allData.get(i)[trueClass])); //only choose the value from the row of the true class

			ce += val;
		}

		return -ce/n;
	}

	public static int[][] calcConfusionMatrix(List<String[]> allData, int column) {
		int[][] confusionMatrix = new int [5][5];
		int trueClass, predictedClass;
		int n=allData.size();

		for (int i=0; i<n; i++) {
			trueClass= Integer.parseInt(allData.get(i)[0]) -1; //column

			predictedClass = findMax(allData.get(i)) - 1; //row

			confusionMatrix[predictedClass][trueClass]++;

		}

		return confusionMatrix;
	}

	public static int findMax (String[] row) {
		//start at first index of predicted values
		int indexOfMax=1;
		double maxVal= Double.parseDouble(row[1]);

		for (int i=2; i<=5; i++) {
			double val= Double.parseDouble(row[i]);//value to be tested
			if (val > maxVal) {
				maxVal = val; //tested value becomes new max
				indexOfMax = i;
			}
		}
		return indexOfMax; //return the index that has the max value
	}

    public static void main( String[] args )
    {
		String filePath="model.csv";
		FileReader filereader;
		List<String[]> allData;
		try{
			filereader = new FileReader(filePath); 
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}

		//print out the CE
		double ce = calcCE(allData);
		System.out.println("CE = "+ce);


		//calculate confusion matrix
		int[][] table = calcConfusionMatrix(allData, 5);

		//output confusion matrix
		System.out.println("Confusion Matrix\n");
		System.out.println("\t\t\ty=1 \ty=2 \ty=3 \ty=4 \ty=5");
		for (int i=0; i<5; i++) {
			System.out.print("\n\ty^="+i+"\t\t");

			for (int j=0; j<5; j++) {
				System.out.print(table[i][j]+"     ");
			}
			System.out.println();
		}

	}
	
}
