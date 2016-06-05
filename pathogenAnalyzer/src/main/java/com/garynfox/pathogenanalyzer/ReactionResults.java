package com.garynfox.pathogenanalyzer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVReader;

public class ReactionResults extends Activity {
	
	// Value pairs needed to pass bundle along
	public final static String STANDARD_CURVE_INFO = "com.garynfox.pathogenanalyzer.STANDARD_CURVE_INFO";	
	public final static String REACTION_INFO = "com.garynfox.pathogenanalyzer.REACTION_INFO";
	public final static String REFERENCE_INFO = "com.garynfox.pathogenanalyzer.REFERENCE_INFO";
	public final static String RECORDING_TYPE = "com.garynfox.pathogenanalyzer.RECORDING_TYPE";
	public final static int STANDARD_CURVE = 1;
	public final static int SAMPLE = 2;
	public final static String TIMECOUNT = "com.garynfox.pathogenanalyzer.TIMECOUNT";
	public final static String DATA_POINTS = "com.garynfox.pathogenanalyzer.DATA_POINTS";
	// public final static int MINUTES = 4;
	
	// Stuff being packed into the bundle
	static String[] standardCurveInfo = new String[3];
	static String[] sampleInfo = new String[3];
	static String[] referenceInfo = new String[3];
	static int recordingType = 999;
	static int timeCount = 0;
	static int dataPoints = 300;
	static int[] sampleTimes = new int[12];
	
	// Strings
	String standardCurveName;			// 0
	String standardCurvePath;			// 1
	String standardCurveNameAndPath;	// 2
	String sampleName;					// 0
	String samplePath;					// 1
	String sampleNameAndPath;			// 2
	String referenceName;				// 0
	String referencePath;				// 1
	String referenceNameAndPath;		// 2
	String savedResults; // to write to disk
	
	String dataPath = "dummy";

	boolean negControl = true;
	boolean posControl = true;

	boolean negPassStdCurve = false;
	boolean negPassSample = false;
	boolean posPassSample = false;
	boolean posPassStdCurve = false;
	
	Button reactionResultsButtonHome;
	
	// On-screen results
	TextView reactionResultsTextViewDisplayIntensity00;
	TextView reactionResultsTextViewDisplayIntensity01;
	TextView reactionResultsTextViewDisplayIntensity02;
	TextView reactionResultsTextViewDisplayIntensity03;
	TextView reactionResultsTextViewDisplayIntensity10;
	TextView reactionResultsTextViewDisplayIntensity11;
	TextView reactionResultsTextViewDisplayIntensity12;
	TextView reactionResultsTextViewDisplayIntensity13;
	TextView reactionResultsTextViewDisplayIntensity20;
	TextView reactionResultsTextViewDisplayIntensity21;
	TextView reactionResultsTextViewDisplayIntensity22;
	TextView reactionResultsTextViewDisplayIntensity23;
	TextView reactionResultsTextViewRsq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Intent previousScreen = getIntent();
		Bundle bundleReceived = previousScreen.getExtras();
				
		standardCurveInfo = bundleReceived.getStringArray(RecordingReaction.STANDARD_CURVE_INFO);
		sampleInfo = bundleReceived.getStringArray(RecordingReaction.REACTION_INFO);
		referenceInfo = bundleReceived.getStringArray(RecordingReaction.REFERENCE_INFO);
		recordingType = bundleReceived.getInt(RecordingReaction.RECORDING_TYPE);
		timeCount = bundleReceived.getInt(RecordingReaction.TIMECOUNT);
		dataPoints = bundleReceived.getInt(RecordingReaction.DATA_POINTS);
		
		if(recordingType == STANDARD_CURVE){
			dataPath = standardCurveInfo[2];
		} else if(recordingType == SAMPLE){
			dataPath = sampleInfo[2];
		}



		timeCount = 300;

		
		// Inflate the GUI
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reaction_results);
		// set up button that sends user back home
		reactionResultsButtonHome = (Button) findViewById(R.id.reactionResultsButtonHome);
		setButtonOnClickListeners();

		// display the text view that shows intensity
		reactionResultsTextViewDisplayIntensity00 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity00);
		reactionResultsTextViewDisplayIntensity01 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity01);
		reactionResultsTextViewDisplayIntensity02 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity02);
		reactionResultsTextViewDisplayIntensity03 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity03);
		reactionResultsTextViewDisplayIntensity10 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity10);
		reactionResultsTextViewDisplayIntensity11 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity11);
		reactionResultsTextViewDisplayIntensity12 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity12);
		reactionResultsTextViewDisplayIntensity13 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity13);
		reactionResultsTextViewDisplayIntensity20 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity20);
		reactionResultsTextViewDisplayIntensity21 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity21);
		reactionResultsTextViewDisplayIntensity22 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity22);
		reactionResultsTextViewDisplayIntensity23 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayIntensity23);
		reactionResultsTextViewRsq = (TextView) findViewById(R.id.reactionResultsTextViewRsq);


		// analyzing and preparing the standard curve information
		// "/storage/emulated/0/Pictures/bacterialdilutions1/bacterialdilutions1.pasc"
		analyzeSet(standardCurveInfo[1]);
		if(negControl == true) {
			Log.d("Standard Curve negative control passed");
			negPassStdCurve = true;
		} else {
			Log.d("Standard Curve negative control failed");
		}

		if(posControl == true) {
			Log.d("Standard Curve negative control passed");
			posPassStdCurve = true;
		} else {
			Log.d("Standard Curve negative control failed");
		}

		int[] stdCurveKnownValues = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
		int[] stdCurveTimes = sampleTimes.clone();
		int[] stdCurveKnownTimes = new int[8];
		for(int i = 4; i < 12; i++){
			stdCurveKnownTimes[i - 4] = stdCurveTimes[i];
		}

		SimpleRegression stdCurveRegression = new SimpleRegression();
		for(int i = 0; i < 8; i++){
			stdCurveRegression.addData(stdCurveKnownTimes[i], stdCurveKnownValues[i]);
		}
		double stdCurveIntercept = stdCurveRegression.getIntercept();
		double stdCurveSlope = stdCurveRegression.getSlope();
		double stdCurveRsq = stdCurveRegression.getRSquare();
		android.util.Log.d("r sq", String.valueOf(stdCurveRsq));
		android.util.Log.d("intercept", String.valueOf(stdCurveIntercept));
		android.util.Log.d("slope", String.valueOf(stdCurveSlope));

		BigDecimal rsqRounded = new BigDecimal(stdCurveRsq);
		// rsqRounded = new BigDecimal(String.valueOf(stdCurveRsq));
		rsqRounded = rsqRounded.setScale(3, BigDecimal.ROUND_HALF_UP);

		// subtract the positive control threshold time from all sample runs by comparison
		// with the positive control time from the std curve

		analyzeSet(sampleInfo[1]);
		if(negControl == true) {
			Log.d("Sample negative control passed");
			negPassSample = true;
		} else {
			Log.d("Sample Curve negative control failed");
		}

		if(posControl == true) {
			Log.d("Sample negative control passed");
			posPassSample = true;
		} else {
			Log.d("Sample negative control failed");
		}



		int calibrationTime;
		calibrationTime = sampleTimes[1] - stdCurveTimes[1];
		for(int i = 0; i < 12; i++){
			sampleTimes[i] = sampleTimes[i] - calibrationTime;
		}
		double[] results = new double[12];
		BigDecimal[] resultsRounded = new BigDecimal[12];
		for(int i = 0; i < 12; i++){
			results[i] = (sampleTimes[i] * stdCurveSlope) + stdCurveIntercept;
			resultsRounded[i] = new BigDecimal(String.valueOf(results[i]));
			resultsRounded[i] = resultsRounded[i].setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		Log.d("1e0 " + results[4]);
		Log.d("1e1 " + results[5]);
		Log.d("1e2 " + results[6]);
		Log.d("1e3 " + results[7]);
		Log.d("1e4 " + results[8]);
		Log.d("1e5 " + results[9]);
		Log.d("1e6 " + results[10]);
		Log.d("1e7 " + results[11]);

		Log.d("1e0 rounded " + resultsRounded[4]);
		Log.d("1e1 rounded " + resultsRounded[5]);
		Log.d("1e2 rounded " + resultsRounded[6]);
		Log.d("1e3 rounded " + resultsRounded[7]);
		Log.d("1e4 rounded " + resultsRounded[8]);
		Log.d("1e5 rounded " + resultsRounded[9]);
		Log.d("1e6 rounded " + resultsRounded[10]);
		Log.d("1e7 rounded " + resultsRounded[11]);

		// logic checking for pos and neg control on both curves
		if(posPassSample == true && posPassStdCurve == true){
			reactionResultsTextViewDisplayIntensity01.setText("YES");
		} else {
			reactionResultsTextViewDisplayIntensity01.setText("NO");
			reactionResultsTextViewDisplayIntensity01.setTextColor(Color.RED);
		}
		if(negPassSample == true && negPassStdCurve == true){
			reactionResultsTextViewDisplayIntensity02.setText("YES");
		} else {
			reactionResultsTextViewDisplayIntensity02.setText("NO");
			reactionResultsTextViewDisplayIntensity02.setTextColor(Color.RED);
		}

		reactionResultsTextViewDisplayIntensity00.setText(resultsRounded[0] + "");
		reactionResultsTextViewDisplayIntensity03.setText(resultsRounded[3] + "");
		reactionResultsTextViewDisplayIntensity10.setText(resultsRounded[4] + "");
		reactionResultsTextViewDisplayIntensity11.setText(resultsRounded[5] + "");
		reactionResultsTextViewDisplayIntensity12.setText(resultsRounded[6] + "");
		reactionResultsTextViewDisplayIntensity13.setText(resultsRounded[7] + "");
		reactionResultsTextViewDisplayIntensity20.setText(resultsRounded[8] + "");
		reactionResultsTextViewDisplayIntensity21.setText(resultsRounded[9] + "");
		reactionResultsTextViewDisplayIntensity22.setText(resultsRounded[10] + "");
		reactionResultsTextViewDisplayIntensity23.setText(resultsRounded[11] + "");
		reactionResultsTextViewRsq.setText("R^2 = " + rsqRounded);
		

		for(int i = 0; i < 12; i++){
			Log.d("sample " + i + " " + sampleTimes[i]);
		}
		
		Log.d("Sample info 0 " + sampleInfo[0]);
		Log.d("Sample info 1 " + sampleInfo[1]);
		Log.d("Sample info 2 " + sampleInfo[2]);

		String fullSampleNameEraseParr = removeParr(sampleInfo[1]);
		Log.d("getting rid of .parr " + fullSampleNameEraseParr);

		savedResults = fullSampleNameEraseParr + "_analysis" + ".txt";
		Log.d("Results analysis name path etc " + savedResults);

		try {
			FileWriter textWriter = new FileWriter(savedResults);
			textWriter.write("sample file analyzed, " + sampleInfo[0] + "\n");
			textWriter.write("standard curve used, " + standardCurveInfo[0] + "\n");
			textWriter.write("standard curve r_squared, " + stdCurveRsq + "\n");
			textWriter.write("standard curve positive control, " + String.valueOf(posPassStdCurve) + "\n");
			textWriter.write("standard curve negative control, " + String.valueOf(negPassStdCurve) + "\n");
			textWriter.write("sample initial concentration vial 00, 1e" + results[0] + "\n");
			textWriter.write("sample initial concentration vial 03, 1e" + results[3] + "\n");
			textWriter.write("sample initial concentration vial 10, 1e" + results[4] + "\n");
			textWriter.write("sample initial concentration vial 11, 1e" + results[5] + "\n");
			textWriter.write("sample initial concentration vial 12, 1e" + results[6] + "\n");
			textWriter.write("sample initial concentration vial 13, 1e" + results[7] + "\n");
			textWriter.write("sample initial concentration vial 20, 1e" + results[8] + "\n");
			textWriter.write("sample initial concentration vial 21, 1e" + results[9] + "\n");
			textWriter.write("sample initial concentration vial 22, 1e" + results[10] + "\n");
			textWriter.write("sample initial concentration vial 23, 1e" + results[11] + "\n");
			textWriter.write("sample positive control, " + String.valueOf(posPassSample) + "\n");
			textWriter.write("sample negative control, " + String.valueOf(negPassSample) + "\n");
			textWriter.flush();
			textWriter.close();
		} catch (IOException ioe) {
			Log.d("text writing didn't work... bummer!");
		}

	}

	private String removeParr(String path){
		path = path.substring(0, path.length() - 5);
		return path;
	}

	private void analyzeSet(String path){

		int coarseDer = 22;

		double[] sample00Intensity = new double[timeCount + coarseDer]; // NOTHING
		double[] sample01Intensity = new double[timeCount + coarseDer]; // positive: genomic dna
		double[] sample02Intensity = new double[timeCount + coarseDer]; // negative
		double[] sample03Intensity = new double[timeCount + coarseDer]; // NOTHING
		double[] sample10Intensity = new double[timeCount + coarseDer]; // 1e0
		double[] sample11Intensity = new double[timeCount + coarseDer]; // 1e1
		double[] sample12Intensity = new double[timeCount + coarseDer]; // 1e2
		double[] sample13Intensity = new double[timeCount + coarseDer]; // 1e3
		double[] sample20Intensity = new double[timeCount + coarseDer]; // 1e4
		double[] sample21Intensity = new double[timeCount + coarseDer]; // 1e5
		double[] sample22Intensity = new double[timeCount + coarseDer]; // 1e6
		double[] sample23Intensity = new double[timeCount + coarseDer]; // 1e7

		double[] sample00Smeared = new double[timeCount + coarseDer]; // NOTHING
		double[] sample01Smeared = new double[timeCount + coarseDer]; // positive: genomic dna
		double[] sample02Smeared = new double[timeCount + coarseDer]; // negative
		double[] sample03Smeared = new double[timeCount + coarseDer]; // NOTHING
		double[] sample10Smeared = new double[timeCount + coarseDer]; // 1e0
		double[] sample11Smeared = new double[timeCount + coarseDer]; // 1e1
		double[] sample12Smeared = new double[timeCount + coarseDer]; // 1e2
		double[] sample13Smeared = new double[timeCount + coarseDer]; // 1e3
		double[] sample20Smeared = new double[timeCount + coarseDer]; // 1e4
		double[] sample21Smeared = new double[timeCount + coarseDer]; // 1e5
		double[] sample22Smeared = new double[timeCount + coarseDer]; // 1e6
		double[] sample23Smeared = new double[timeCount + coarseDer]; // 1e7

		double[] sample00CoarseDer = new double[timeCount]; // NOTHING
		double[] sample01CoarseDer = new double[timeCount]; // positive: genomic dna
		double[] sample02CoarseDer = new double[timeCount]; // negative
		double[] sample03CoarseDer = new double[timeCount]; // NOTHING
		double[] sample10CoarseDer = new double[timeCount]; // 1e0
		double[] sample11CoarseDer = new double[timeCount]; // 1e1
		double[] sample12CoarseDer = new double[timeCount]; // 1e2
		double[] sample13CoarseDer = new double[timeCount]; // 1e3
		double[] sample20CoarseDer = new double[timeCount]; // 1e4
		double[] sample21CoarseDer = new double[timeCount]; // 1e5
		double[] sample22CoarseDer = new double[timeCount]; // 1e6
		double[] sample23CoarseDer = new double[timeCount]; // 1e7

		double max00 = 0;
		double max01 = 0;
		double max02 = 0;
		double max03 = 0;
		double max10 = 0;
		double max11 = 0;
		double max12 = 0;
		double max13 = 0;
		double max20 = 0;
		double max21 = 0;
		double max22 = 0;
		double max23 = 0;

		int sample00IntensityTime = 0;
		int sample01IntensityTime = 0;
		int sample02IntensityTime = 0;
		int sample03IntensityTime = 0;
		int sample10IntensityTime = 0;
		int sample11IntensityTime = 0;
		int sample12IntensityTime = 0;
		int sample13IntensityTime = 0;
		int sample20IntensityTime = 0;
		int sample21IntensityTime = 0;
		int sample22IntensityTime = 0;
		int sample23IntensityTime = 0;

		double[] nextLineDouble = new double[13];

		String[] nextLine = new String[13];

		CSVReader reader = null;

		try {
			reader = new CSVReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			nextLine = reader.readNext();
			for(int i = 0; i < timeCount; i++){
				nextLine = reader.readNext();
				// Log.d(Arrays.toString(nextLine));
				for(int j = 0; j < 12; j++){
					nextLineDouble[j] = Double.parseDouble(nextLine[j]);
				}
				sample00Intensity[i + (coarseDer / 2)] = nextLineDouble[0];
				sample01Intensity[i + (coarseDer / 2)] = nextLineDouble[1];
				sample02Intensity[i + (coarseDer / 2)] = nextLineDouble[2];
				sample03Intensity[i + (coarseDer / 2)] = nextLineDouble[3];
				sample10Intensity[i + (coarseDer / 2)] = nextLineDouble[4];
				sample11Intensity[i + (coarseDer / 2)] = nextLineDouble[5];
				sample12Intensity[i + (coarseDer / 2)] = nextLineDouble[6];
				sample13Intensity[i + (coarseDer / 2)] = nextLineDouble[7];
				sample20Intensity[i + (coarseDer / 2)] = nextLineDouble[8];
				sample21Intensity[i + (coarseDer / 2)] = nextLineDouble[9];
				sample22Intensity[i + (coarseDer / 2)] = nextLineDouble[10];
				sample23Intensity[i + (coarseDer / 2)] = nextLineDouble[11];
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* for(int i = 0; i < 442; i++){
		 	Log.d("sample 11 " + i + " " + sample11Intensity[i]);
		} */

		Log.d("smearing...");

		// Padding the samples for smoothing
		for(int i = 0; i < (coarseDer / 2); i++){
			sample00Intensity[i] =                                    sample00Intensity[(coarseDer / 2)];
			sample00Intensity[(timeCount + (coarseDer /2) + i )] = sample00Intensity[(coarseDer / 2) + timeCount - 1];
			sample01Intensity[i] =                                    sample01Intensity[(coarseDer / 2)];
			sample01Intensity[(timeCount + (coarseDer /2) + i )] = sample01Intensity[(coarseDer / 2) + timeCount - 1];
			sample02Intensity[i] =                                    sample02Intensity[(coarseDer / 2)];
			sample02Intensity[(timeCount + (coarseDer /2) + i )] = sample02Intensity[(coarseDer / 2) + timeCount - 1];
			sample03Intensity[i] =                                    sample03Intensity[(coarseDer / 2)];
			sample03Intensity[(timeCount + (coarseDer /2) + i )] = sample03Intensity[(coarseDer / 2) + timeCount - 1];
			sample10Intensity[i] =                                    sample10Intensity[(coarseDer / 2)];
			sample10Intensity[(timeCount + (coarseDer /2) + i )] = sample10Intensity[(coarseDer / 2) + timeCount - 1];
			sample11Intensity[i] =                                    sample11Intensity[(coarseDer / 2)];
			sample11Intensity[(timeCount + (coarseDer /2) + i )] = sample11Intensity[(coarseDer / 2) + timeCount - 1];
			sample12Intensity[i] =                                    sample12Intensity[(coarseDer / 2)];
			sample12Intensity[(timeCount + (coarseDer /2) + i )] = sample12Intensity[(coarseDer / 2) + timeCount - 1];
			sample13Intensity[i] =                                    sample13Intensity[(coarseDer / 2)];
			sample13Intensity[(timeCount + (coarseDer /2) + i )] = sample13Intensity[(coarseDer / 2) + timeCount - 1];
			sample20Intensity[i] =                                    sample20Intensity[(coarseDer / 2)];
			sample20Intensity[(timeCount + (coarseDer /2) + i )] = sample20Intensity[(coarseDer / 2) + timeCount - 1];
			sample21Intensity[i] =                                    sample21Intensity[(coarseDer / 2)];
			sample21Intensity[(timeCount + (coarseDer /2) + i )] = sample21Intensity[(coarseDer / 2) + timeCount - 1];
			sample22Intensity[i] =                                    sample22Intensity[(coarseDer / 2)];
			sample22Intensity[(timeCount + (coarseDer /2) + i )] = sample22Intensity[(coarseDer / 2) + timeCount - 1];
			sample23Intensity[i] =                                    sample23Intensity[(coarseDer / 2)];
			sample23Intensity[(timeCount + (coarseDer /2) + i )] = sample23Intensity[(coarseDer / 2) + timeCount - 1];
		}

		for(int i = 0; i < (coarseDer / 2); i++){
			sample00Smeared[i] = sample00Intensity[i];
			sample01Smeared[i] = sample01Intensity[i];
			sample02Smeared[i] = sample02Intensity[i];
			sample03Smeared[i] = sample03Intensity[i];
			sample10Smeared[i] = sample10Intensity[i];
			sample11Smeared[i] = sample11Intensity[i];
			sample12Smeared[i] = sample12Intensity[i];
			sample13Smeared[i] = sample13Intensity[i];
			sample20Smeared[i] = sample20Intensity[i];
			sample21Smeared[i] = sample21Intensity[i];
			sample22Smeared[i] = sample22Intensity[i];
			sample23Smeared[i] = sample23Intensity[i];
		}

		for(int i = timeCount + (coarseDer / 2); i < timeCount + coarseDer; i++){
			sample00Smeared[i] = sample00Intensity[i];
			sample01Smeared[i] = sample01Intensity[i];
			sample02Smeared[i] = sample02Intensity[i];
			sample03Smeared[i] = sample03Intensity[i];
			sample10Smeared[i] = sample10Intensity[i];
			sample11Smeared[i] = sample11Intensity[i];
			sample12Smeared[i] = sample12Intensity[i];
			sample13Smeared[i] = sample13Intensity[i];
			sample20Smeared[i] = sample20Intensity[i];
			sample21Smeared[i] = sample21Intensity[i];
			sample22Smeared[i] = sample22Intensity[i];
			sample23Smeared[i] = sample23Intensity[i];
		}

		for(int i = 5; i < timeCount + 15; i++){
			sample00Smeared[i] = 0;
			sample01Smeared[i] = 0;
			sample02Smeared[i] = 0;
			sample03Smeared[i] = 0;
			sample10Smeared[i] = 0;
			sample11Smeared[i] = 0;
			sample12Smeared[i] = 0;
			sample13Smeared[i] = 0;
			sample20Smeared[i] = 0;
			sample21Smeared[i] = 0;
			sample22Smeared[i] = 0;
			sample23Smeared[i] = 0;
			for(int k = -5; k < 6; k++){
				sample00Smeared[i] = sample00Smeared[i] + ((sample00Intensity[i + k]) / (coarseDer / 2));
				sample01Smeared[i] = sample01Smeared[i] + ((sample01Intensity[i + k]) / (coarseDer / 2));
				sample02Smeared[i] = sample02Smeared[i] + ((sample02Intensity[i + k]) / (coarseDer / 2));
				sample03Smeared[i] = sample03Smeared[i] + ((sample03Intensity[i + k]) / (coarseDer / 2));
				sample10Smeared[i] = sample10Smeared[i] + ((sample10Intensity[i + k]) / (coarseDer / 2));
				sample11Smeared[i] = sample11Smeared[i] + ((sample11Intensity[i + k]) / (coarseDer / 2));
				sample12Smeared[i] = sample12Smeared[i] + ((sample12Intensity[i + k]) / (coarseDer / 2));
				sample13Smeared[i] = sample13Smeared[i] + ((sample13Intensity[i + k]) / (coarseDer / 2));
				sample20Smeared[i] = sample20Smeared[i] + ((sample20Intensity[i + k]) / (coarseDer / 2));
				sample21Smeared[i] = sample21Smeared[i] + ((sample21Intensity[i + k]) / (coarseDer / 2));
				sample22Smeared[i] = sample22Smeared[i] + ((sample22Intensity[i + k]) / (coarseDer / 2));
				sample23Smeared[i] = sample23Smeared[i] + ((sample23Intensity[i + k]) / (coarseDer / 2));
			}
		}

		for(int i = 0; i < timeCount; i++){
			sample00CoarseDer[i] = sample00Smeared[i + coarseDer] - sample00Smeared[i];
			sample01CoarseDer[i] = sample01Smeared[i + coarseDer] - sample01Smeared[i];
			sample02CoarseDer[i] = sample02Smeared[i + coarseDer] - sample02Smeared[i];
			sample03CoarseDer[i] = sample03Smeared[i + coarseDer] - sample03Smeared[i];
			sample10CoarseDer[i] = sample10Smeared[i + coarseDer] - sample10Smeared[i];
			sample11CoarseDer[i] = sample11Smeared[i + coarseDer] - sample11Smeared[i];
			sample12CoarseDer[i] = sample12Smeared[i + coarseDer] - sample12Smeared[i];
			sample13CoarseDer[i] = sample13Smeared[i + coarseDer] - sample13Smeared[i];
			sample20CoarseDer[i] = sample20Smeared[i + coarseDer] - sample20Smeared[i];
			sample21CoarseDer[i] = sample21Smeared[i + coarseDer] - sample21Smeared[i];
			sample22CoarseDer[i] = sample22Smeared[i + coarseDer] - sample22Smeared[i];
			sample23CoarseDer[i] = sample23Smeared[i + coarseDer] - sample23Smeared[i];
			if(sample00CoarseDer[i] > max00){
				max00 = sample00CoarseDer[i];
				sample00IntensityTime = i;
			}
			if(sample01CoarseDer[i] > max01){
				max01 = sample01CoarseDer[i];
				sample01IntensityTime = i;
			}
			if(sample02CoarseDer[i] > max02){
				max02 = sample02CoarseDer[i];
				sample02IntensityTime = i;
			}
			if(sample03CoarseDer[i] > max03){
				max03 = sample03CoarseDer[i];
				sample03IntensityTime = i;
			}
			if(sample10CoarseDer[i] > max10){
				max10 = sample10CoarseDer[i];
				sample10IntensityTime = i;
			}
			if(sample11CoarseDer[i] > max11){
				max11 = sample11CoarseDer[i];
				sample11IntensityTime = i;
			}
			if(sample12CoarseDer[i] > max12){
				max12 = sample12CoarseDer[i];
				sample12IntensityTime = i;
			}
			if(sample13CoarseDer[i] > max13){
				max13 = sample13CoarseDer[i];
				sample13IntensityTime = i;
			}
			if(sample20CoarseDer[i] > max20){
				max20 = sample20CoarseDer[i];
				sample20IntensityTime = i;
			}
			if(sample21CoarseDer[i] > max21){
				max21 = sample21CoarseDer[i];
				sample21IntensityTime = i;
			}
			if(sample22CoarseDer[i] > max22){
				max22 = sample22CoarseDer[i];
				sample22IntensityTime = i;
			}
			if(sample23CoarseDer[i] > max23){
				max23 = sample23CoarseDer[i];
				sample23IntensityTime = i;
			}
		}

		Log.d("Max coarse derivative is  " + max00);
		Log.d("Max coarse derivative is  " + max01);
		Log.d("Max coarse derivative is  " + max02);
		Log.d("Max coarse derivative is  " + max03);
		Log.d("Max coarse derivative is  " + max10);
		Log.d("Max coarse derivative is  " + max11);
		Log.d("Max coarse derivative is  " + max12);
		Log.d("Max coarse derivative is  " + max13);
		Log.d("Max coarse derivative is  " + max20);
		Log.d("Max coarse derivative is  " + max21);
		Log.d("Max coarse derivative is  " + max22);
		Log.d("Max coarse derivative is  " + max23);

		if( max02 > max10 && max02 > max11 && max02 > max12 && max02 > max13 && max02 > max20 && max02 > max21 && max02 > max22 && max02 > max23 ){
			negControl = false;
		}

		if( max01 < max10 && max01 < max11 && max01 < max12 && max01 < max13 && max01 < max20 && max01 < max21 && max01 < max22 && max01 < max23 ){
			posControl = false;
		}



		//Log.d("max 11 time " + sample11IntensityTime);

		/* for(int i = 0; i < timeCount; i++){
			Log.d(" sample 11 " + i + " " + sample11CoarseDer[i]);
			// Log.d("original 11 " + i + " " + sample11Intensity[i]);
		} */


		sampleTimes[0] =  sample00IntensityTime;
		sampleTimes[1] =  sample01IntensityTime;
		sampleTimes[2] =  sample02IntensityTime;
		sampleTimes[3] =  sample03IntensityTime;
		sampleTimes[4] =  sample10IntensityTime;
		sampleTimes[5] =  sample11IntensityTime;
		sampleTimes[6] =  sample12IntensityTime;
		sampleTimes[7] =  sample13IntensityTime;
		sampleTimes[8] =  sample20IntensityTime;
		sampleTimes[9] =  sample21IntensityTime;
		sampleTimes[10] = sample22IntensityTime;
		sampleTimes[11] = sample23IntensityTime;


	}

	private void setButtonOnClickListeners(){
		
		// Go back home after seeing results
		reactionResultsButtonHome.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// Launch recording reaction screen
				Intent i1 = new Intent(ReactionResults.this, HomeScreen.class);
				startActivity(i1);	
			}		
		});			
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reaction_results, menu);
		return true;
	}

}
