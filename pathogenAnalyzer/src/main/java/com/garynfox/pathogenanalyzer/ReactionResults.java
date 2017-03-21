package com.garynfox.pathogenanalyzer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.DecimalFormat;

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
	public final static String SOURCE_TYPE = "com.garynfox.pathogenanalyzer.SOURCE_TYPE";
	// public final static int MINUTES = 4;
	public final static int BLOOD = 1;
	public final static int URINE = 2;
	public final static int FECES = 3;
	public final static int OTHER = 4;
	
	// Stuff being packed into the bundle
	static String[] standardCurveInfo = new String[3];
	static String[] sampleInfo = new String[3];
	static String[] referenceInfo = new String[3];
	static int recordingType = 999;
	static int timeCount = 0;
	static int dataPoints = 300;
	static double[] offsetTime = new double[dataPoints];
	static int[] sampleTimes = new int[36];
	static double[] ampCheckArr = new double[36];

	int[] inclusionArr = new int[36];
	int inclusionCount = 0;
	
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
	String sourceType = "dummy";

	boolean negControl = true;
	boolean posControl = true;

	boolean negPassStdCurve = false;
	boolean negPassSample = false;
	boolean posPassSample = false;
	boolean posPassStdCurve = false;
	
	Button reactionResultsButtonHome;
	
	// On-screen results

	TextView reactionResultsTextViewRsq;
	TextView reactionResultsTextViewDisplayStdCurveC1result;
	TextView reactionResultsTextViewDisplayStdCurveD1result;
	TextView reactionResultsTextViewDisplayConcentrationA1; //
	TextView reactionResultsTextViewDisplayConcentrationA2; //
	TextView reactionResultsTextViewDisplayConcentrationA3; //
	TextView reactionResultsTextViewDisplayConcentrationA4; //
	TextView reactionResultsTextViewDisplayConcentrationA5; //
	TextView reactionResultsTextViewDisplayConcentrationA6; //
	TextView reactionResultsTextViewDisplayConcentrationB1; //
	TextView reactionResultsTextViewDisplayConcentrationB2; //
	TextView reactionResultsTextViewDisplayConcentrationB3; //
	TextView reactionResultsTextViewDisplayConcentrationB4; //
	TextView reactionResultsTextViewDisplayConcentrationB5; //
	TextView reactionResultsTextViewDisplayConcentrationB6; //
	TextView reactionResultsTextViewDisplayConcentrationC1; //
	TextView reactionResultsTextViewDisplayConcentrationC2; //
	TextView reactionResultsTextViewDisplayConcentrationC3; //
	TextView reactionResultsTextViewDisplayConcentrationC4; //
	TextView reactionResultsTextViewDisplayConcentrationC5; //
	TextView reactionResultsTextViewDisplayConcentrationC6; //
	TextView reactionResultsTextViewDisplayConcentrationD1; //
	TextView reactionResultsTextViewDisplayConcentrationD2; //
	TextView reactionResultsTextViewDisplayConcentrationD3; //
	TextView reactionResultsTextViewDisplayConcentrationD4; //
	TextView reactionResultsTextViewDisplayConcentrationD5; //
	TextView reactionResultsTextViewDisplayConcentrationD6; //
	TextView reactionResultsTextViewDisplayConcentrationE1; //
	TextView reactionResultsTextViewDisplayConcentrationE2; //
	TextView reactionResultsTextViewDisplayConcentrationE3; //
	TextView reactionResultsTextViewDisplayConcentrationE4; //
	TextView reactionResultsTextViewDisplayConcentrationE5; //
	TextView reactionResultsTextViewDisplayConcentrationE6; //
	TextView reactionResultsTextViewDisplayConcentrationF1; //
	TextView reactionResultsTextViewDisplayConcentrationF2; //
	TextView reactionResultsTextViewDisplayConcentrationF3; //
	TextView reactionResultsTextViewDisplayConcentrationF4; //
	TextView reactionResultsTextViewDisplayConcentrationF5; //
	TextView reactionResultsTextViewDisplayConcentrationF6; //
	
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
		sourceType = bundleReceived.getString(RecordingReaction.SOURCE_TYPE);
		android.util.Log.d("source type: ", " " + sourceType );
		android.util.Log.d("source type: ", "" + standardCurveInfo[2]);
		
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
		reactionResultsTextViewRsq = (TextView) findViewById(R.id.reactionResultsTextViewRsq);
		reactionResultsTextViewDisplayStdCurveC1result = (TextView) findViewById(R.id.reactionResultsTextViewDisplayStdCurveC1result);
		reactionResultsTextViewDisplayStdCurveD1result = (TextView) findViewById(R.id.reactionResultsTextViewDisplayStdCurveD1result);
		reactionResultsTextViewDisplayConcentrationA1 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationA1); //
		reactionResultsTextViewDisplayConcentrationA2 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationA2); //
		reactionResultsTextViewDisplayConcentrationA3 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationA3); //
		reactionResultsTextViewDisplayConcentrationA4 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationA4); //
		reactionResultsTextViewDisplayConcentrationA5 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationA5); //
		reactionResultsTextViewDisplayConcentrationA6 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationA6); //
		reactionResultsTextViewDisplayConcentrationB1 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationB1); //
		reactionResultsTextViewDisplayConcentrationB2 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationB2); //
		reactionResultsTextViewDisplayConcentrationB3 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationB3); //
		reactionResultsTextViewDisplayConcentrationB4 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationB4); //
		reactionResultsTextViewDisplayConcentrationB5 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationB5); //
		reactionResultsTextViewDisplayConcentrationB6 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationB6); //
		reactionResultsTextViewDisplayConcentrationC1 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationC1); //
		reactionResultsTextViewDisplayConcentrationC2 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationC2); //
		reactionResultsTextViewDisplayConcentrationC3 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationC3); //
		reactionResultsTextViewDisplayConcentrationC4 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationC4); //
		reactionResultsTextViewDisplayConcentrationC5 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationC5); //
		reactionResultsTextViewDisplayConcentrationC6 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationC6); //
		reactionResultsTextViewDisplayConcentrationD1 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationD1); //
		reactionResultsTextViewDisplayConcentrationD2 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationD2); //
		reactionResultsTextViewDisplayConcentrationD3 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationD3); //
		reactionResultsTextViewDisplayConcentrationD4 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationD4); //
		reactionResultsTextViewDisplayConcentrationD5 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationD5); //
		reactionResultsTextViewDisplayConcentrationD6 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationD6); //
		reactionResultsTextViewDisplayConcentrationE1 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationE1); //
		reactionResultsTextViewDisplayConcentrationE2 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationE2); //
		reactionResultsTextViewDisplayConcentrationE3 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationE3); //
		reactionResultsTextViewDisplayConcentrationE4 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationE4); //
		reactionResultsTextViewDisplayConcentrationE5 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationE5); //
		reactionResultsTextViewDisplayConcentrationE6 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationE6); //
		reactionResultsTextViewDisplayConcentrationF1 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationF1); //
		reactionResultsTextViewDisplayConcentrationF2 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationF2); //
		reactionResultsTextViewDisplayConcentrationF3 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationF3); //
		reactionResultsTextViewDisplayConcentrationF4 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationF4); //
		reactionResultsTextViewDisplayConcentrationF5 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationF5); //
		reactionResultsTextViewDisplayConcentrationF6 = (TextView) findViewById(R.id.reactionResultsTextViewDisplayConcentrationF6); //


		// analyzing and preparing the standard curve information
		// "/storage/emulated/0/Pictures/bacterialdilutions1/bacterialdilutions1.pasc"

		analyzeSet(standardCurveInfo[1]);

		double stdCurveOffsetTime = offsetTime[0];
		android.util.Log.d("offset time std curve", String.valueOf(stdCurveOffsetTime));
		Long L = Math.round(stdCurveOffsetTime);
		int stdCurveOffsetTimeInt = Integer.valueOf(L.intValue());

		/*
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
		*/

		double[] stdCurveKnownValues = new double[]{50000000, 5000000, 500000, 50000, 5000, 50000000, 5000000, 500000, 50000, 5000};

		int thresholdValue = 250;

		if((sourceType.equals("feces")) || (sourceType.equals("urine")) || (sourceType.equals("other"))){

			thresholdValue = 500;

			for(int i = 0; i < 10; i++){

				stdCurveKnownValues[i] = stdCurveKnownValues[i] / 2;
			}
		}

		for(int i = 0; i < 10; i++){
			stdCurveKnownValues[i] = Math.log10(stdCurveKnownValues[i]);
			android.util.Log.d("log10 test", String.valueOf(stdCurveKnownValues[i]));
		}
		int[] stdCurveTimes = sampleTimes.clone();
		double[] stdCurveAmpCheck = ampCheckArr.clone();

		for(int i = 0; i < 36; i++){
			stdCurveTimes[i] = stdCurveTimes[i] + stdCurveOffsetTimeInt;
			android.util.Log.d("amp test computation", String.valueOf(stdCurveAmpCheck[i]));
			android.util.Log.d("std curve times", String.valueOf(stdCurveTimes[i]));
			// android.util.Log.d("amp test result", String.valueOf(inclusionArr[i]));
		}

		// ignoring C6 and D6 for the std curve check, and times once computed for offset
		int[] stdCurveTimesExControl = new int[10];
		double[] stdCurveAmpCheckExControl = new double[10];

		stdCurveAmpCheckExControl[0] = stdCurveAmpCheck[12];
		stdCurveAmpCheckExControl[1] = stdCurveAmpCheck[13];
		stdCurveAmpCheckExControl[2] = stdCurveAmpCheck[14];
		stdCurveAmpCheckExControl[3] = stdCurveAmpCheck[15];
		stdCurveAmpCheckExControl[4] = stdCurveAmpCheck[16];
		// skip C6 NEG
		stdCurveAmpCheckExControl[5] = stdCurveAmpCheck[18];
		stdCurveAmpCheckExControl[6] = stdCurveAmpCheck[19];
		stdCurveAmpCheckExControl[7] = stdCurveAmpCheck[20];
		stdCurveAmpCheckExControl[8] = stdCurveAmpCheck[21];
		stdCurveAmpCheckExControl[9] = stdCurveAmpCheck[22];
		// skip D6 NEG

		stdCurveTimesExControl[0] = stdCurveTimes[12]; // 1e7
		stdCurveTimesExControl[1] = stdCurveTimes[13]; // 1e6
		stdCurveTimesExControl[2] = stdCurveTimes[14]; // 1e5
		stdCurveTimesExControl[3] = stdCurveTimes[15]; // 1e4
		stdCurveTimesExControl[4] = stdCurveTimes[16]; // 1e3
		// skip C6
		stdCurveTimesExControl[5] = stdCurveTimes[18]; // 1e7
		stdCurveTimesExControl[6] = stdCurveTimes[19]; // 1e6
		stdCurveTimesExControl[7] = stdCurveTimes[20]; // 1e5
		stdCurveTimesExControl[8] = stdCurveTimes[21]; // 1e4
		stdCurveTimesExControl[9] = stdCurveTimes[22]; // 1e3
		// skip D6



		// standard curve negative control test



		double stdCurveNegTest_C6 = stdCurveAmpCheck[17];
		double stdCurveNegTest_D6 = stdCurveAmpCheck[23];
		int stdCurveNegTest_C6_result;
		int stdCurveNegTest_D6_result;
		if(stdCurveNegTest_C6 > thresholdValue){
			stdCurveNegTest_C6_result = 0;
		} else {
			stdCurveNegTest_C6_result = 1;
		}
		if(stdCurveNegTest_D6 > thresholdValue){
			stdCurveNegTest_D6_result = 0;
		} else {
			stdCurveNegTest_D6_result = 1;
		}

		String stdCurveNegTest_C6_result_word;
		String stdCurveNegTest_D6_result_word;

		if(stdCurveNegTest_C6_result == 1){
			stdCurveNegTest_C6_result_word = "pass";
		} else {
			stdCurveNegTest_C6_result_word = "FAIL";
		}

		if(stdCurveNegTest_D6_result == 1){
			stdCurveNegTest_D6_result_word = "pass";
		} else {
			stdCurveNegTest_D6_result_word = "FAIL";
		}


		// special inclusionArr for stdCurve as it is only looking at 10 values
		int[] inclusionArrStdCurve = new int[10];

		for(int i = 0; i < 10; i++){
			if(stdCurveAmpCheckExControl[i] >= thresholdValue){
				inclusionArrStdCurve[i] = 1;
				inclusionCount = inclusionCount + 1;
			} else {
				inclusionArrStdCurve[i] = 0;
			}
			android.util.Log.d("amp test result", String.valueOf(stdCurveAmpCheckExControl[i]));
			android.util.Log.d("amp test result", String.valueOf(inclusionArrStdCurve[i]));

		}

		double[] stdCurveKnownValuesAdjusted = new double[inclusionCount];
		int[] stdCurveTimesAdjusted = new int[inclusionCount];



		int indexCountAdjusted = 0;
		for(int i = 0; i < 10; i++){
			if(inclusionArrStdCurve[i] == 1){
				stdCurveKnownValuesAdjusted[indexCountAdjusted] = stdCurveKnownValues[i];
				stdCurveTimesAdjusted[indexCountAdjusted] = stdCurveTimesExControl[i];
				indexCountAdjusted = indexCountAdjusted + 1;
			}
		}

		android.util.Log.d("inclusion count: ", "" + inclusionCount);

		SimpleRegression stdCurveRegression = new SimpleRegression();
		for(int i = 0; i < inclusionCount; i++){
			android.util.Log.d("log of known [k]", String.valueOf(stdCurveKnownValuesAdjusted[i]));
			android.util.Log.d("t_T", String.valueOf(stdCurveTimesAdjusted[i]));
			stdCurveRegression.addData(stdCurveTimesAdjusted[i], stdCurveKnownValuesAdjusted[i]);
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

		for(int i = 0; i < 36; i++){
			if(ampCheckArr[i] >= thresholdValue){
				inclusionArr[i] = 1;
			} else {
				inclusionArr[i] = 0;
			}
			android.util.Log.d("amp test result, sample", String.valueOf(inclusionArr[i]));
		}

		double sampleOffsetTime = offsetTime[0];
		Long L2 = Math.round(sampleOffsetTime);
		int sampleOffsetTimeInt = Integer.valueOf(L2.intValue());

		for(int i = 0; i < 36; i++){
			sampleTimes[i] = sampleTimes[i] + sampleOffsetTimeInt;
		}

		/*
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
		*/

		DecimalFormat df = new DecimalFormat("0.0000000000E00");
		DecimalFormat dfR = new DecimalFormat("0.00E00");

		double[] results = new double[36];
		String[] resultsSn = new String[36];
		String[] resultsSnR = new String[36];
		BigDecimal[] resultsRounded = new BigDecimal[36];
		for(int i = 0; i < 36; i++){
			results[i] = Math.pow(10, ((sampleTimes[i] * stdCurveSlope) + stdCurveIntercept));
			resultsSn[i] = df.format(results[i]);
			resultsSnR[i] = dfR.format(results[i]);
			// resultsRounded[i] = new BigDecimal(String.valueOf(results[i]));
			// resultsRounded[i] = resultsRounded[i].setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		Log.d("A1 " + resultsSnR[0]);
		Log.d("A2 " + resultsSnR[1]);
		Log.d("A3 " + resultsSnR[2]);
		Log.d("A4 " + resultsSn[3]);
		Log.d("A5 " + resultsSn[4]);
		Log.d("A6 " + resultsSn[5]);
		Log.d("B1 " + resultsSn[6]);
		Log.d("B2 " + resultsSn[7]);
		Log.d("B3 " + resultsSn[8]);
		Log.d("B4 " + resultsSn[9]);
		Log.d("B5 " + resultsSn[10]);
		Log.d("B6 " + resultsSn[11]);
		Log.d("C1 " + resultsSn[12]);
		Log.d("C2 " + resultsSn[13]);
		Log.d("C3 " + resultsSn[14]);
		Log.d("C4 " + resultsSn[15]);
		Log.d("C5 " + resultsSn[16]);
		Log.d("C6 " + resultsSn[17]);
		Log.d("D1 " + resultsSn[18]);
		Log.d("D2 " + resultsSn[19]);
		Log.d("D3 " + resultsSn[20]);
		Log.d("D4 " + resultsSn[21]);
		Log.d("D5 " + resultsSn[22]);
		Log.d("D6 " + resultsSn[23]);
		Log.d("E1 " + resultsSn[24]);
		Log.d("E2 " + resultsSn[25]);
		Log.d("E3 " + resultsSn[26]);
		Log.d("E4 " + resultsSn[27]);
		Log.d("E5 " + resultsSn[28]);
		Log.d("E6 " + resultsSn[29]);
		Log.d("F1 " + resultsSn[30]);
		Log.d("F2 " + resultsSn[31]);
		Log.d("F3 " + resultsSn[32]);
		Log.d("F4 " + resultsSn[33]);
		Log.d("F5 " + resultsSn[34]);
		Log.d("F6 " + resultsSn[35]);

		Log.d("A1 rounded " + resultsRounded[0]);
		Log.d("A2 rounded " + resultsRounded[1]);
		Log.d("A3 rounded " + resultsRounded[2]);
		Log.d("A4 rounded " + resultsRounded[3]);
		Log.d("A5 rounded " + resultsRounded[4]);
		Log.d("A6 rounded " + resultsRounded[5]);
		Log.d("B1 rounded " + resultsRounded[6]);
		Log.d("B2 rounded " + resultsRounded[7]);
		Log.d("B3 rounded " + resultsRounded[8]);
		Log.d("B4 rounded " + resultsRounded[9]);
		Log.d("B5 rounded " + resultsRounded[10]);
		Log.d("B6 rounded " + resultsRounded[11]);
		Log.d("C1 rounded " + resultsRounded[12]);
		Log.d("C2 rounded " + resultsRounded[13]);
		Log.d("C3 rounded " + resultsRounded[14]);
		Log.d("C4 rounded " + resultsRounded[15]);
		Log.d("C5 rounded " + resultsRounded[16]);
		Log.d("C6 rounded " + resultsRounded[17]);
		Log.d("D1 rounded " + resultsRounded[18]);
		Log.d("D2 rounded " + resultsRounded[19]);
		Log.d("D3 rounded " + resultsRounded[20]);
		Log.d("D4 rounded " + resultsRounded[21]);
		Log.d("D5 rounded " + resultsRounded[22]);
		Log.d("D6 rounded " + resultsRounded[23]);
		Log.d("E1 rounded " + resultsRounded[24]);
		Log.d("E2 rounded " + resultsRounded[25]);
		Log.d("E3 rounded " + resultsRounded[26]);
		Log.d("E4 rounded " + resultsRounded[27]);
		Log.d("E5 rounded " + resultsRounded[28]);
		Log.d("E6 rounded " + resultsRounded[29]);
		Log.d("F1 rounded " + resultsRounded[30]);
		Log.d("F2 rounded " + resultsRounded[31]);
		Log.d("F3 rounded " + resultsRounded[32]);
		Log.d("F4 rounded " + resultsRounded[33]);
		Log.d("F5 rounded " + resultsRounded[34]);
		Log.d("F6 rounded " + resultsRounded[35]);

		// logic checking for pos and neg control on both curves
		/*
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
		}*/

		String[] inclusionArrMsg = new String[36];

		for(int i = 0; i < 36; i++){
			Log.d("sample " + i + " " + sampleTimes[i]);
			if(inclusionArr[i] == 1){
				inclusionArrMsg[i] = "AMP";
			} else if(inclusionArr[i] == 0){
				inclusionArrMsg[i] = "no";
			}
		}

		reactionResultsTextViewDisplayStdCurveC1result.setText("std crv C1 (-): " + stdCurveNegTest_C6_result_word);
		reactionResultsTextViewDisplayStdCurveD1result.setText("std crv D1 (-): " + stdCurveNegTest_D6_result_word);
		reactionResultsTextViewDisplayConcentrationA1.setText("A1: " + resultsSnR[0] + ", amp? : " + inclusionArrMsg[0] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationA2.setText("A2: " + resultsSnR[1] + ", amp? : " + inclusionArrMsg[1] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationA3.setText("A3: " + resultsSnR[2] + ", amp? : " + inclusionArrMsg[2] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationA4.setText("A4: " + resultsSnR[3] + ", amp? : " + inclusionArrMsg[3] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationA5.setText("A5: " + resultsSnR[4] + ", amp? : " + inclusionArrMsg[4] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationA6.setText("A6: " + resultsSnR[5] + ", amp? : " + inclusionArrMsg[5] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationB1.setText("B1: " + resultsSnR[6] + ", amp? : " + inclusionArrMsg[6] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationB2.setText("B2: " + resultsSnR[7] + ", amp? : " + inclusionArrMsg[7] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationB3.setText("B3: " + resultsSnR[8] + ", amp? : " + inclusionArrMsg[8] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationB4.setText("B4: " + resultsSnR[9] + ", amp? : " + inclusionArrMsg[9] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationB5.setText("B5: " + resultsSnR[10] + ", amp? : " + inclusionArrMsg[10] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationB6.setText("B6: " + resultsSnR[11] + ", amp? : " + inclusionArrMsg[11] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationC1.setText("C1: " + resultsSnR[12] + ", amp? : " + inclusionArrMsg[12] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationC2.setText("C2: " + resultsSnR[13] + ", amp? : " + inclusionArrMsg[13] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationC3.setText("C3: " + resultsSnR[14] + ", amp? : " + inclusionArrMsg[14] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationC4.setText("C4: " + resultsSnR[15] + ", amp? : " + inclusionArrMsg[15] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationC5.setText("C5: " + resultsSnR[16] + ", amp? : " + inclusionArrMsg[16] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationC6.setText("C6: " + resultsSnR[17] + ", amp? : " + inclusionArrMsg[17] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationD1.setText("D1: " + resultsSnR[18] + ", amp? : " + inclusionArrMsg[18] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationD2.setText("D2: " + resultsSnR[19] + ", amp? : " + inclusionArrMsg[19] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationD3.setText("D3: " + resultsSnR[20] + ", amp? : " + inclusionArrMsg[20] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationD4.setText("D4: " + resultsSnR[21] + ", amp? : " + inclusionArrMsg[21] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationD5.setText("D5: " + resultsSnR[22] + ", amp? : " + inclusionArrMsg[22] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationD6.setText("D6: " + resultsSnR[23] + ", amp? : " + inclusionArrMsg[23] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationE1.setText("E1: " + resultsSnR[24] + ", amp? : " + inclusionArrMsg[24] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationE2.setText("E2: " + resultsSnR[25] + ", amp? : " + inclusionArrMsg[25] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationE3.setText("E3: " + resultsSnR[26] + ", amp? : " + inclusionArrMsg[26] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationE4.setText("E4: " + resultsSnR[27] + ", amp? : " + inclusionArrMsg[27] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationE5.setText("E5: " + resultsSnR[28] + ", amp? : " + inclusionArrMsg[28] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationE6.setText("E6: " + resultsSnR[29] + ", amp? : " + inclusionArrMsg[29] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationF1.setText("F1: " + resultsSnR[30] + ", amp? : " + inclusionArrMsg[30] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationF2.setText("F2: " + resultsSnR[31] + ", amp? : " + inclusionArrMsg[31] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationF3.setText("F3: " + resultsSnR[32] + ", amp? : " + inclusionArrMsg[32] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationF4.setText("F4: " + resultsSnR[33] + ", amp? : " + inclusionArrMsg[33] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationF5.setText("F5: " + resultsSnR[34] + ", amp? : " + inclusionArrMsg[34] + "  |  "); //
		reactionResultsTextViewDisplayConcentrationF6.setText("F6: " + resultsSnR[35] + ", amp? : " + inclusionArrMsg[35] + "  |  "); //
		reactionResultsTextViewRsq.setText("R^2 = " + rsqRounded + ", " + sourceType);
		
		Log.d("Sample info 0 " + sampleInfo[0]);
		Log.d("Sample info 1 " + sampleInfo[1]);
		Log.d("Sample info 2 " + sampleInfo[2]);

		String fullSampleNameEraseParr = removeParr(sampleInfo[1]);
		Log.d("getting rid of .parr " + fullSampleNameEraseParr);

		savedResults = fullSampleNameEraseParr + "_analysis";
		Log.d("Results analysis name path etc " + savedResults);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String analysisTime  = dateFormat.format(new Date());

		try {
			FileWriter textWriter = new FileWriter(savedResults + "_" + analysisTime + ".txt");
			textWriter.write("sample file analyzed, " + sampleInfo[0] + "\n");
			textWriter.write("standard curve used, " + standardCurveInfo[0] + "\n");
			textWriter.write("pathogenic standard curve and unknown sample medium (or source), " + sourceType + "\n");
			textWriter.write("NOTE: 'blood' and 'other use concentration 5e7, 5e6, etc. for curve fitting. 'urine' and 'feces' 2.5e7, 2.5e6, etc. " + "\n");
			textWriter.write(" " + "\n");
			textWriter.write("standard curve intercept, " + stdCurveIntercept + "\n");
			textWriter.write("standard curve slope, " + stdCurveSlope + "\n");
			textWriter.write("standard curve r_squared, " + stdCurveRsq + "\n");
			textWriter.write("standard curve negative control test, well C6, (pass = 1, fail = 0), " + stdCurveNegTest_C6_result + "\n");
			textWriter.write("standard curve negative control test, well D6, (pass = 1, fail = 0), " + stdCurveNegTest_D6_result + "\n");
			textWriter.write(" " + "\n");
			textWriter.write("standard curve data points"  + "\n");
			for(int i = 0; i < inclusionCount; i++){
				textWriter.write("point: " + i + ", log [k]: " + stdCurveKnownValuesAdjusted[i] + ", t_T: " + stdCurveTimesAdjusted[i]+  "\n");
			}
			textWriter.write(" " +"\n");
			textWriter.write("unknown specimen results and information about the results " +"\n");
			textWriter.write("1 = AMP, 0 = amplification failed" + "\n");
			textWriter.write("Convention: first value is [k], second amp check algo, third amp result" + "\n");
			textWriter.write("A1  " + resultsSn[0] + ", " + ampCheckArr[0] + ", " + inclusionArrMsg[0]+ "\n");
			textWriter.write("A2  " + resultsSn[1] + ", " + ampCheckArr[1] + ", " + inclusionArrMsg[1]+ "\n");
			textWriter.write("A3  " + resultsSn[2] + ", " + ampCheckArr[2] + ", " + inclusionArrMsg[2]+ "\n");
			textWriter.write("A4  " + resultsSn[3] + ", " + ampCheckArr[3] + ", " + inclusionArrMsg[3]+ "\n");
			textWriter.write("A5  " + resultsSn[4] + ", " + ampCheckArr[4] + ", " + inclusionArrMsg[4]+ "\n");
			textWriter.write("A6  " + resultsSn[5] + ", " + ampCheckArr[5] + ", " + inclusionArrMsg[5]+ "\n");
			textWriter.write("B1  " + resultsSn[6] + ", " + ampCheckArr[6] + ", " + inclusionArrMsg[6]+ "\n");
			textWriter.write("B2  " + resultsSn[7] + ", " + ampCheckArr[7] + ", " + inclusionArrMsg[7]+ "\n");
			textWriter.write("B3  " + resultsSn[8] + ", " + ampCheckArr[8] + ", " + inclusionArrMsg[8]+ "\n");
			textWriter.write("B4  " + resultsSn[9] + ", " + ampCheckArr[9] + ", " + inclusionArrMsg[9]+ "\n");
			textWriter.write("B5  " + resultsSn[10] + ", " + ampCheckArr[10] + ", " + inclusionArrMsg[10]+ "\n");
			textWriter.write("B6  " + resultsSn[11] + ", " + ampCheckArr[11] + ", " + inclusionArrMsg[11]+ "\n");
			textWriter.write("C1  " + resultsSn[12] + ", " + ampCheckArr[12] + ", " + inclusionArrMsg[12]+ "\n");
			textWriter.write("C2  " + resultsSn[13] + ", " + ampCheckArr[13] + ", " + inclusionArrMsg[13]+ "\n");
			textWriter.write("C3  " + resultsSn[14] + ", " + ampCheckArr[14] + ", " + inclusionArrMsg[14]+ "\n");
			textWriter.write("C4  " + resultsSn[15] + ", " + ampCheckArr[15] + ", " + inclusionArrMsg[15]+ "\n");
			textWriter.write("C5  " + resultsSn[16] + ", " + ampCheckArr[16] + ", " + inclusionArrMsg[16]+ "\n");
			textWriter.write("C6  " + resultsSn[17] + ", " + ampCheckArr[17] + ", " + inclusionArrMsg[17]+ "\n");
			textWriter.write("D1  " + resultsSn[18] + ", " + ampCheckArr[18] + ", " + inclusionArrMsg[18]+ "\n");
			textWriter.write("D2  " + resultsSn[19] + ", " + ampCheckArr[19] + ", " + inclusionArrMsg[19]+ "\n");
			textWriter.write("D3  " + resultsSn[20] + ", " + ampCheckArr[20] + ", " + inclusionArrMsg[20]+ "\n");
			textWriter.write("D4  " + resultsSn[21] + ", " + ampCheckArr[21] + ", " + inclusionArrMsg[21]+ "\n");
			textWriter.write("D5  " + resultsSn[22] + ", " + ampCheckArr[22] + ", " + inclusionArrMsg[22]+ "\n");
			textWriter.write("D6  " + resultsSn[23] + ", " + ampCheckArr[23] + ", " + inclusionArrMsg[23]+ "\n");
			textWriter.write("E1  " + resultsSn[24] + ", " + ampCheckArr[24] + ", " + inclusionArrMsg[24]+ "\n");
			textWriter.write("E2  " + resultsSn[25] + ", " + ampCheckArr[25] + ", " + inclusionArrMsg[25]+ "\n");
			textWriter.write("E3  " + resultsSn[26] + ", " + ampCheckArr[26] + ", " + inclusionArrMsg[26]+ "\n");
			textWriter.write("E4  " + resultsSn[27] + ", " + ampCheckArr[27] + ", " + inclusionArrMsg[27]+ "\n");
			textWriter.write("E5  " + resultsSn[28] + ", " + ampCheckArr[28] + ", " + inclusionArrMsg[28]+ "\n");
			textWriter.write("E6  " + resultsSn[29] + ", " + ampCheckArr[29] + ", " + inclusionArrMsg[29]+ "\n");
			textWriter.write("F1  " + resultsSn[30] + ", " + ampCheckArr[30] + ", " + inclusionArrMsg[30]+ "\n");
			textWriter.write("F2  " + resultsSn[31] + ", " + ampCheckArr[31] + ", " + inclusionArrMsg[31]+ "\n");
			textWriter.write("F3  " + resultsSn[32] + ", " + ampCheckArr[32] + ", " + inclusionArrMsg[32]+ "\n");
			textWriter.write("F4  " + resultsSn[33] + ", " + ampCheckArr[33] + ", " + inclusionArrMsg[33]+ "\n");
			textWriter.write("F5  " + resultsSn[34] + ", " + ampCheckArr[34] + ", " + inclusionArrMsg[34]+ "\n");
			textWriter.write("F6  " + resultsSn[35] + ", " + ampCheckArr[35] + ", " + inclusionArrMsg[35]+ "\n");
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

		/* double[] sample00Intensity = new double[timeCount + coarseDer]; // NOTHING
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
		*/

		double[] sampleA1Intensity = new double[timeCount + coarseDer]; //
		double[] sampleA2Intensity = new double[timeCount + coarseDer]; //
		double[] sampleA3Intensity = new double[timeCount + coarseDer]; //
		double[] sampleA4Intensity = new double[timeCount + coarseDer]; //
		double[] sampleA5Intensity = new double[timeCount + coarseDer]; //
		double[] sampleA6Intensity = new double[timeCount + coarseDer]; //
		double[] sampleB1Intensity = new double[timeCount + coarseDer]; //
		double[] sampleB2Intensity = new double[timeCount + coarseDer]; //
		double[] sampleB3Intensity = new double[timeCount + coarseDer]; //
		double[] sampleB4Intensity = new double[timeCount + coarseDer]; //
		double[] sampleB5Intensity = new double[timeCount + coarseDer]; //
		double[] sampleB6Intensity = new double[timeCount + coarseDer]; //
		double[] sampleC1Intensity = new double[timeCount + coarseDer]; // 1e7
		double[] sampleC2Intensity = new double[timeCount + coarseDer]; // 1e6
		double[] sampleC3Intensity = new double[timeCount + coarseDer]; // 1e5
		double[] sampleC4Intensity = new double[timeCount + coarseDer]; // 1e4
		double[] sampleC5Intensity = new double[timeCount + coarseDer]; // 1e3
		double[] sampleC6Intensity = new double[timeCount + coarseDer]; // NEG
		double[] sampleD1Intensity = new double[timeCount + coarseDer]; // 1e7
		double[] sampleD2Intensity = new double[timeCount + coarseDer]; // 1e6
		double[] sampleD3Intensity = new double[timeCount + coarseDer]; // 1e5
		double[] sampleD4Intensity = new double[timeCount + coarseDer]; // 1e4
		double[] sampleD5Intensity = new double[timeCount + coarseDer]; // 1e3
		double[] sampleD6Intensity = new double[timeCount + coarseDer]; // NEG
		double[] sampleE1Intensity = new double[timeCount + coarseDer]; //
		double[] sampleE2Intensity = new double[timeCount + coarseDer]; //
		double[] sampleE3Intensity = new double[timeCount + coarseDer]; //
		double[] sampleE4Intensity = new double[timeCount + coarseDer]; //
		double[] sampleE5Intensity = new double[timeCount + coarseDer]; //
		double[] sampleE6Intensity = new double[timeCount + coarseDer]; //
		double[] sampleF1Intensity = new double[timeCount + coarseDer]; //
		double[] sampleF2Intensity = new double[timeCount + coarseDer]; //
		double[] sampleF3Intensity = new double[timeCount + coarseDer]; //
		double[] sampleF4Intensity = new double[timeCount + coarseDer]; //
		double[] sampleF5Intensity = new double[timeCount + coarseDer]; //
		double[] sampleF6Intensity = new double[timeCount + coarseDer]; //

		/*
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
		*/

		double[] sampleA1Smeared = new double[timeCount + coarseDer]; //
		double[] sampleA2Smeared = new double[timeCount + coarseDer]; //
		double[] sampleA3Smeared = new double[timeCount + coarseDer]; //
		double[] sampleA4Smeared = new double[timeCount + coarseDer]; //
		double[] sampleA5Smeared = new double[timeCount + coarseDer]; //
		double[] sampleA6Smeared = new double[timeCount + coarseDer]; //
		double[] sampleB1Smeared = new double[timeCount + coarseDer]; //
		double[] sampleB2Smeared = new double[timeCount + coarseDer]; //
		double[] sampleB3Smeared = new double[timeCount + coarseDer]; //
		double[] sampleB4Smeared = new double[timeCount + coarseDer]; //
		double[] sampleB5Smeared = new double[timeCount + coarseDer]; //
		double[] sampleB6Smeared = new double[timeCount + coarseDer]; //
		double[] sampleC1Smeared = new double[timeCount + coarseDer]; // 1e7
		double[] sampleC2Smeared = new double[timeCount + coarseDer]; // 1e6
		double[] sampleC3Smeared = new double[timeCount + coarseDer]; // 1e5
		double[] sampleC4Smeared = new double[timeCount + coarseDer]; // 1e4
		double[] sampleC5Smeared = new double[timeCount + coarseDer]; // 1e3
		double[] sampleC6Smeared = new double[timeCount + coarseDer]; // NEG
		double[] sampleD1Smeared = new double[timeCount + coarseDer]; // 1e7
		double[] sampleD2Smeared = new double[timeCount + coarseDer]; // 1e6
		double[] sampleD3Smeared = new double[timeCount + coarseDer]; // 1e5
		double[] sampleD4Smeared = new double[timeCount + coarseDer]; // 1e4
		double[] sampleD5Smeared = new double[timeCount + coarseDer]; // 1e3
		double[] sampleD6Smeared = new double[timeCount + coarseDer]; // NEG
		double[] sampleE1Smeared = new double[timeCount + coarseDer]; //
		double[] sampleE2Smeared = new double[timeCount + coarseDer]; //
		double[] sampleE3Smeared = new double[timeCount + coarseDer]; //
		double[] sampleE4Smeared = new double[timeCount + coarseDer]; //
		double[] sampleE5Smeared = new double[timeCount + coarseDer]; //
		double[] sampleE6Smeared = new double[timeCount + coarseDer]; //
		double[] sampleF1Smeared = new double[timeCount + coarseDer]; //
		double[] sampleF2Smeared = new double[timeCount + coarseDer]; //
		double[] sampleF3Smeared = new double[timeCount + coarseDer]; //
		double[] sampleF4Smeared = new double[timeCount + coarseDer]; //
		double[] sampleF5Smeared = new double[timeCount + coarseDer]; //
		double[] sampleF6Smeared = new double[timeCount + coarseDer]; //

		/*
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
		*/

		double[] sampleA1CoarseDer = new double[timeCount]; //
		double[] sampleA2CoarseDer = new double[timeCount]; //
		double[] sampleA3CoarseDer = new double[timeCount]; //
		double[] sampleA4CoarseDer = new double[timeCount]; //
		double[] sampleA5CoarseDer = new double[timeCount]; //
		double[] sampleA6CoarseDer = new double[timeCount]; //
		double[] sampleB1CoarseDer = new double[timeCount]; //
		double[] sampleB2CoarseDer = new double[timeCount]; //
		double[] sampleB3CoarseDer = new double[timeCount]; //
		double[] sampleB4CoarseDer = new double[timeCount]; //
		double[] sampleB5CoarseDer = new double[timeCount]; //
		double[] sampleB6CoarseDer = new double[timeCount]; //
		double[] sampleC1CoarseDer = new double[timeCount]; // 1e7
		double[] sampleC2CoarseDer = new double[timeCount]; // 1e6
		double[] sampleC3CoarseDer = new double[timeCount]; // 1e5
		double[] sampleC4CoarseDer = new double[timeCount]; // 1e4
		double[] sampleC5CoarseDer = new double[timeCount]; // 1e3
		double[] sampleC6CoarseDer = new double[timeCount]; // NEG
		double[] sampleD1CoarseDer = new double[timeCount]; // 1e7
		double[] sampleD2CoarseDer = new double[timeCount]; // 1e6
		double[] sampleD3CoarseDer = new double[timeCount]; // 1e5
		double[] sampleD4CoarseDer = new double[timeCount]; // 1e4
		double[] sampleD5CoarseDer = new double[timeCount]; // 1e3
		double[] sampleD6CoarseDer = new double[timeCount]; // NEG
		double[] sampleE1CoarseDer = new double[timeCount]; //
		double[] sampleE2CoarseDer = new double[timeCount]; //
		double[] sampleE3CoarseDer = new double[timeCount]; //
		double[] sampleE4CoarseDer = new double[timeCount]; //
		double[] sampleE5CoarseDer = new double[timeCount]; //
		double[] sampleE6CoarseDer = new double[timeCount]; //
		double[] sampleF1CoarseDer = new double[timeCount]; //
		double[] sampleF2CoarseDer = new double[timeCount]; //
		double[] sampleF3CoarseDer = new double[timeCount]; //
		double[] sampleF4CoarseDer = new double[timeCount]; //
		double[] sampleF5CoarseDer = new double[timeCount]; //
		double[] sampleF6CoarseDer = new double[timeCount]; //


		/*
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
		*/

		double maxA1 = 0; //
		double maxA2 = 0; //
		double maxA3 = 0; //
		double maxA4 = 0; //
		double maxA5 = 0; //
		double maxA6 = 0; //
		double maxB1 = 0; //
		double maxB2 = 0; //
		double maxB3 = 0; //
		double maxB4 = 0; //
		double maxB5 = 0; //
		double maxB6 = 0; //
		double maxC1 = 0; // 1e7
		double maxC2 = 0; // 1e6
		double maxC3 = 0; // 1e5
		double maxC4 = 0; // 1e4
		double maxC5 = 0; // 1e3
		double maxC6 = 0; // NEG
		double maxD1 = 0; // 1e7
		double maxD2 = 0; // 1e6
		double maxD3 = 0; // 1e5
		double maxD4 = 0; // 1e4
		double maxD5 = 0; // 1e3
		double maxD6 = 0; // NEG
		double maxE1 = 0; //
		double maxE2 = 0; //
		double maxE3 = 0; //
		double maxE4 = 0; //
		double maxE5 = 0; //
		double maxE6 = 0; //
		double maxF1 = 0; //
		double maxF2 = 0; //
		double maxF3 = 0; //
		double maxF4 = 0; //
		double maxF5 = 0; //
		double maxF6 = 0; //

		double sampleA1ampCheck = 0; //
		double sampleA2ampCheck = 0; //
		double sampleA3ampCheck = 0; //
		double sampleA4ampCheck = 0; //
		double sampleA5ampCheck = 0; //
		double sampleA6ampCheck = 0; //
		double sampleB1ampCheck = 0; //
		double sampleB2ampCheck = 0; //
		double sampleB3ampCheck = 0; //
		double sampleB4ampCheck = 0; //
		double sampleB5ampCheck = 0; //
		double sampleB6ampCheck = 0; //
		double sampleC1ampCheck = 0; // 1e7
		double sampleC2ampCheck = 0; // 1e6
		double sampleC3ampCheck = 0; // 1e5
		double sampleC4ampCheck = 0; // 1e4
		double sampleC5ampCheck = 0; // 1e3
		double sampleC6ampCheck = 0; // NEG
		double sampleD1ampCheck = 0; // 1e7
		double sampleD2ampCheck = 0; // 1e6
		double sampleD3ampCheck = 0; // 1e5
		double sampleD4ampCheck = 0; // 1e4
		double sampleD5ampCheck = 0; // 1e3
		double sampleD6ampCheck = 0; // NEG
		double sampleE1ampCheck = 0; //
		double sampleE2ampCheck = 0; //
		double sampleE3ampCheck = 0; //
		double sampleE4ampCheck = 0; //
		double sampleE5ampCheck = 0; //
		double sampleE6ampCheck = 0; //
		double sampleF1ampCheck = 0; //
		double sampleF2ampCheck = 0; //
		double sampleF3ampCheck = 0; //
		double sampleF4ampCheck = 0; //
		double sampleF5ampCheck = 0; //
		double sampleF6ampCheck = 0; //

		/*
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
		*/

		int sampleA1IntensityTime = 0; //
		int sampleA2IntensityTime = 0; //
		int sampleA3IntensityTime = 0; //
		int sampleA4IntensityTime = 0; //
		int sampleA5IntensityTime = 0; //
		int sampleA6IntensityTime = 0; //
		int sampleB1IntensityTime = 0; //
		int sampleB2IntensityTime = 0; //
		int sampleB3IntensityTime = 0; //
		int sampleB4IntensityTime = 0; //
		int sampleB5IntensityTime = 0; //
		int sampleB6IntensityTime = 0; //
		int sampleC1IntensityTime = 0; // 1e7
		int sampleC2IntensityTime = 0; // 1e6
		int sampleC3IntensityTime = 0; // 1e5
		int sampleC4IntensityTime = 0; // 1e4
		int sampleC5IntensityTime = 0; // 1e3
		int sampleC6IntensityTime = 0; // NEG
		int sampleD1IntensityTime = 0; // 1e7
		int sampleD2IntensityTime = 0; // 1e6
		int sampleD3IntensityTime = 0; // 1e5
		int sampleD4IntensityTime = 0; // 1e4
		int sampleD5IntensityTime = 0; // 1e3
		int sampleD6IntensityTime = 0; // NEG
		int sampleE1IntensityTime = 0; //
		int sampleE2IntensityTime = 0; //
		int sampleE3IntensityTime = 0; //
		int sampleE4IntensityTime = 0; //
		int sampleE5IntensityTime = 0; //
		int sampleE6IntensityTime = 0; //
		int sampleF1IntensityTime = 0; //
		int sampleF2IntensityTime = 0; //
		int sampleF3IntensityTime = 0; //
		int sampleF4IntensityTime = 0; //
		int sampleF5IntensityTime = 0; //
		int sampleF6IntensityTime = 0; //

		double[] nextLineDouble = new double[39];

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
			// offsetTime = Integer.parseInt(nextLine[36]);
			for(int i = 0; i < timeCount; i++){
				nextLine = reader.readNext();
				// Log.d(Arrays.toString(nextLine));
				for(int j = 0; j < 37; j++){
					nextLineDouble[j] = Double.parseDouble(nextLine[j]);
				}
				/*
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
				*/
				offsetTime[i] = nextLineDouble[36];
				sampleA1Intensity[i + (coarseDer / 2)] = nextLineDouble[0]; //
				sampleA2Intensity[i + (coarseDer / 2)] = nextLineDouble[1]; //
				sampleA3Intensity[i + (coarseDer / 2)] = nextLineDouble[2]; //
				sampleA4Intensity[i + (coarseDer / 2)] = nextLineDouble[3]; //
				sampleA5Intensity[i + (coarseDer / 2)] = nextLineDouble[4]; //
				sampleA6Intensity[i + (coarseDer / 2)] = nextLineDouble[5]; //
				sampleB1Intensity[i + (coarseDer / 2)] = nextLineDouble[6]; //
				sampleB2Intensity[i + (coarseDer / 2)] = nextLineDouble[7]; //
				sampleB3Intensity[i + (coarseDer / 2)] = nextLineDouble[8]; //
				sampleB4Intensity[i + (coarseDer / 2)] = nextLineDouble[9]; //
				sampleB5Intensity[i + (coarseDer / 2)] = nextLineDouble[10]; //
				sampleB6Intensity[i + (coarseDer / 2)] = nextLineDouble[11]; //
				sampleC1Intensity[i + (coarseDer / 2)] = nextLineDouble[12]; // 1e7
				sampleC2Intensity[i + (coarseDer / 2)] = nextLineDouble[13]; // 1e6
				sampleC3Intensity[i + (coarseDer / 2)] = nextLineDouble[14]; // 1e5
				sampleC4Intensity[i + (coarseDer / 2)] = nextLineDouble[15]; // 1e4
				sampleC5Intensity[i + (coarseDer / 2)] = nextLineDouble[16]; // 1e3
				sampleC6Intensity[i + (coarseDer / 2)] = nextLineDouble[17]; // NEG
				sampleD1Intensity[i + (coarseDer / 2)] = nextLineDouble[18]; // 1e7
				sampleD2Intensity[i + (coarseDer / 2)] = nextLineDouble[19]; // 1e6
				sampleD3Intensity[i + (coarseDer / 2)] = nextLineDouble[20]; // 1e5
				sampleD4Intensity[i + (coarseDer / 2)] = nextLineDouble[21]; // 1e4
				sampleD5Intensity[i + (coarseDer / 2)] = nextLineDouble[22]; // 1e3
				sampleD6Intensity[i + (coarseDer / 2)] = nextLineDouble[23]; // NEG
				sampleE1Intensity[i + (coarseDer / 2)] = nextLineDouble[24]; //
				sampleE2Intensity[i + (coarseDer / 2)] = nextLineDouble[25]; //
				sampleE3Intensity[i + (coarseDer / 2)] = nextLineDouble[26]; //
				sampleE4Intensity[i + (coarseDer / 2)] = nextLineDouble[27]; //
				sampleE5Intensity[i + (coarseDer / 2)] = nextLineDouble[28]; //
				sampleE6Intensity[i + (coarseDer / 2)] = nextLineDouble[29]; //
				sampleF1Intensity[i + (coarseDer / 2)] = nextLineDouble[30]; //
				sampleF2Intensity[i + (coarseDer / 2)] = nextLineDouble[31]; //
				sampleF3Intensity[i + (coarseDer / 2)] = nextLineDouble[32]; //
				sampleF4Intensity[i + (coarseDer / 2)] = nextLineDouble[33]; //
				sampleF5Intensity[i + (coarseDer / 2)] = nextLineDouble[34]; //
				sampleF6Intensity[i + (coarseDer / 2)] = nextLineDouble[35]; //
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

			/*
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
			*/

			sampleA1Intensity[i] =                                    sampleA1Intensity[(coarseDer / 2)];
			sampleA1Intensity[(timeCount + (coarseDer /2) + i )] = sampleA1Intensity[(coarseDer / 2) + timeCount - 1];
			sampleA2Intensity[i] =                                    sampleA2Intensity[(coarseDer / 2)];
			sampleA2Intensity[(timeCount + (coarseDer /2) + i )] = sampleA2Intensity[(coarseDer / 2) + timeCount - 1];
			sampleA3Intensity[i] =                                    sampleA3Intensity[(coarseDer / 2)];
			sampleA3Intensity[(timeCount + (coarseDer /2) + i )] = sampleA3Intensity[(coarseDer / 2) + timeCount - 1];
			sampleA4Intensity[i] =                                    sampleA4Intensity[(coarseDer / 2)];
			sampleA4Intensity[(timeCount + (coarseDer /2) + i )] = sampleA4Intensity[(coarseDer / 2) + timeCount - 1];
			sampleA5Intensity[i] =                                    sampleA5Intensity[(coarseDer / 2)];
			sampleA5Intensity[(timeCount + (coarseDer /2) + i )] = sampleA5Intensity[(coarseDer / 2) + timeCount - 1];
			sampleA6Intensity[i] =                                    sampleA6Intensity[(coarseDer / 2)];
			sampleA6Intensity[(timeCount + (coarseDer /2) + i )] = sampleA6Intensity[(coarseDer / 2) + timeCount - 1];
			sampleB1Intensity[i] =                                    sampleB1Intensity[(coarseDer / 2)];
			sampleB1Intensity[(timeCount + (coarseDer /2) + i )] = sampleB1Intensity[(coarseDer / 2) + timeCount - 1];
			sampleB2Intensity[i] =                                    sampleB2Intensity[(coarseDer / 2)];
			sampleB2Intensity[(timeCount + (coarseDer /2) + i )] = sampleB2Intensity[(coarseDer / 2) + timeCount - 1];
			sampleB3Intensity[i] =                                    sampleB3Intensity[(coarseDer / 2)];
			sampleB3Intensity[(timeCount + (coarseDer /2) + i )] = sampleB3Intensity[(coarseDer / 2) + timeCount - 1];
			sampleB4Intensity[i] =                                    sampleB4Intensity[(coarseDer / 2)];
			sampleB4Intensity[(timeCount + (coarseDer /2) + i )] = sampleB4Intensity[(coarseDer / 2) + timeCount - 1];
			sampleB5Intensity[i] =                                    sampleB5Intensity[(coarseDer / 2)];
			sampleB5Intensity[(timeCount + (coarseDer /2) + i )] = sampleB5Intensity[(coarseDer / 2) + timeCount - 1];
			sampleB6Intensity[i] =                                    sampleB6Intensity[(coarseDer / 2)];
			sampleB6Intensity[(timeCount + (coarseDer /2) + i )] = sampleB6Intensity[(coarseDer / 2) + timeCount - 1];
			sampleC1Intensity[i] =                                    sampleC1Intensity[(coarseDer / 2)];
			sampleC1Intensity[(timeCount + (coarseDer /2) + i )] = sampleC1Intensity[(coarseDer / 2) + timeCount - 1];
			sampleC2Intensity[i] =                                    sampleC2Intensity[(coarseDer / 2)];
			sampleC2Intensity[(timeCount + (coarseDer /2) + i )] = sampleC2Intensity[(coarseDer / 2) + timeCount - 1];
			sampleC3Intensity[i] =                                    sampleC3Intensity[(coarseDer / 2)];
			sampleC3Intensity[(timeCount + (coarseDer /2) + i )] = sampleC3Intensity[(coarseDer / 2) + timeCount - 1];
			sampleC4Intensity[i] =                                    sampleC4Intensity[(coarseDer / 2)];
			sampleC4Intensity[(timeCount + (coarseDer /2) + i )] = sampleC4Intensity[(coarseDer / 2) + timeCount - 1];
			sampleC5Intensity[i] =                                    sampleC5Intensity[(coarseDer / 2)];
			sampleC5Intensity[(timeCount + (coarseDer /2) + i )] = sampleC5Intensity[(coarseDer / 2) + timeCount - 1];
			sampleC6Intensity[i] =                                    sampleC6Intensity[(coarseDer / 2)];
			sampleC6Intensity[(timeCount + (coarseDer /2) + i )] = sampleC6Intensity[(coarseDer / 2) + timeCount - 1];
			sampleD1Intensity[i] =                                    sampleD1Intensity[(coarseDer / 2)];
			sampleD1Intensity[(timeCount + (coarseDer /2) + i )] = sampleD1Intensity[(coarseDer / 2) + timeCount - 1];
			sampleD2Intensity[i] =                                    sampleD2Intensity[(coarseDer / 2)];
			sampleD2Intensity[(timeCount + (coarseDer /2) + i )] = sampleD2Intensity[(coarseDer / 2) + timeCount - 1];
			sampleD3Intensity[i] =                                    sampleD3Intensity[(coarseDer / 2)];
			sampleD3Intensity[(timeCount + (coarseDer /2) + i )] = sampleD3Intensity[(coarseDer / 2) + timeCount - 1];
			sampleD4Intensity[i] =                                    sampleD4Intensity[(coarseDer / 2)];
			sampleD4Intensity[(timeCount + (coarseDer /2) + i )] = sampleD4Intensity[(coarseDer / 2) + timeCount - 1];
			sampleD5Intensity[i] =                                    sampleD5Intensity[(coarseDer / 2)];
			sampleD5Intensity[(timeCount + (coarseDer /2) + i )] = sampleD5Intensity[(coarseDer / 2) + timeCount - 1];
			sampleD6Intensity[i] =                                    sampleD6Intensity[(coarseDer / 2)];
			sampleD6Intensity[(timeCount + (coarseDer /2) + i )] = sampleD6Intensity[(coarseDer / 2) + timeCount - 1];
			sampleE1Intensity[i] =                                    sampleE1Intensity[(coarseDer / 2)];
			sampleE1Intensity[(timeCount + (coarseDer /2) + i )] = sampleE1Intensity[(coarseDer / 2) + timeCount - 1];
			sampleE2Intensity[i] =                                    sampleE2Intensity[(coarseDer / 2)];
			sampleE2Intensity[(timeCount + (coarseDer /2) + i )] = sampleE2Intensity[(coarseDer / 2) + timeCount - 1];
			sampleE3Intensity[i] =                                    sampleE3Intensity[(coarseDer / 2)];
			sampleE3Intensity[(timeCount + (coarseDer /2) + i )] = sampleE3Intensity[(coarseDer / 2) + timeCount - 1];
			sampleE4Intensity[i] =                                    sampleE4Intensity[(coarseDer / 2)];
			sampleE4Intensity[(timeCount + (coarseDer /2) + i )] = sampleE4Intensity[(coarseDer / 2) + timeCount - 1];
			sampleE5Intensity[i] =                                    sampleE5Intensity[(coarseDer / 2)];
			sampleE5Intensity[(timeCount + (coarseDer /2) + i )] = sampleE5Intensity[(coarseDer / 2) + timeCount - 1];
			sampleE6Intensity[i] =                                    sampleE6Intensity[(coarseDer / 2)];
			sampleE6Intensity[(timeCount + (coarseDer /2) + i )] = sampleE6Intensity[(coarseDer / 2) + timeCount - 1];
			sampleF1Intensity[i] =                                    sampleF1Intensity[(coarseDer / 2)];
			sampleF1Intensity[(timeCount + (coarseDer /2) + i )] = sampleF1Intensity[(coarseDer / 2) + timeCount - 1];
			sampleF2Intensity[i] =                                    sampleF2Intensity[(coarseDer / 2)];
			sampleF2Intensity[(timeCount + (coarseDer /2) + i )] = sampleF2Intensity[(coarseDer / 2) + timeCount - 1];
			sampleF3Intensity[i] =                                    sampleF3Intensity[(coarseDer / 2)];
			sampleF3Intensity[(timeCount + (coarseDer /2) + i )] = sampleF3Intensity[(coarseDer / 2) + timeCount - 1];
			sampleF4Intensity[i] =                                    sampleF4Intensity[(coarseDer / 2)];
			sampleF4Intensity[(timeCount + (coarseDer /2) + i )] = sampleF4Intensity[(coarseDer / 2) + timeCount - 1];
			sampleF5Intensity[i] =                                    sampleF5Intensity[(coarseDer / 2)];
			sampleF5Intensity[(timeCount + (coarseDer /2) + i )] = sampleF5Intensity[(coarseDer / 2) + timeCount - 1];
			sampleF6Intensity[i] =                                    sampleF6Intensity[(coarseDer / 2)];
			sampleF6Intensity[(timeCount + (coarseDer /2) + i )] = sampleF6Intensity[(coarseDer / 2) + timeCount - 1];




		}

		for(int i = 0; i < (coarseDer / 2); i++){

			/*
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
			*/

			sampleA1Smeared[i] = sampleA1Intensity[i]; //
			sampleA2Smeared[i] = sampleA2Intensity[i]; //
			sampleA3Smeared[i] = sampleA3Intensity[i]; //
			sampleA4Smeared[i] = sampleA4Intensity[i]; //
			sampleA5Smeared[i] = sampleA5Intensity[i]; //
			sampleA6Smeared[i] = sampleA6Intensity[i]; //
			sampleB1Smeared[i] = sampleB1Intensity[i]; //
			sampleB2Smeared[i] = sampleB2Intensity[i]; //
			sampleB3Smeared[i] = sampleB3Intensity[i]; //
			sampleB4Smeared[i] = sampleB4Intensity[i]; //
			sampleB5Smeared[i] = sampleB5Intensity[i]; //
			sampleB6Smeared[i] = sampleB6Intensity[i]; //
			sampleC1Smeared[i] = sampleC1Intensity[i]; // 1e7
			sampleC2Smeared[i] = sampleC2Intensity[i]; // 1e6
			sampleC3Smeared[i] = sampleC3Intensity[i]; // 1e5
			sampleC4Smeared[i] = sampleC4Intensity[i]; // 1e4
			sampleC5Smeared[i] = sampleC5Intensity[i]; // 1e3
			sampleC6Smeared[i] = sampleC6Intensity[i]; // NEG
			sampleD1Smeared[i] = sampleD1Intensity[i]; // 1e7
			sampleD2Smeared[i] = sampleD2Intensity[i]; // 1e6
			sampleD3Smeared[i] = sampleD3Intensity[i]; // 1e5
			sampleD4Smeared[i] = sampleD4Intensity[i]; // 1e4
			sampleD5Smeared[i] = sampleD5Intensity[i]; // 1e3
			sampleD6Smeared[i] = sampleD6Intensity[i]; // NEG
			sampleE1Smeared[i] = sampleE1Intensity[i]; //
			sampleE2Smeared[i] = sampleE2Intensity[i]; //
			sampleE3Smeared[i] = sampleE3Intensity[i]; //
			sampleE4Smeared[i] = sampleE4Intensity[i]; //
			sampleE5Smeared[i] = sampleE5Intensity[i]; //
			sampleE6Smeared[i] = sampleE6Intensity[i]; //
			sampleF1Smeared[i] = sampleF1Intensity[i]; //
			sampleF2Smeared[i] = sampleF2Intensity[i]; //
			sampleF3Smeared[i] = sampleF3Intensity[i]; //
			sampleF4Smeared[i] = sampleF4Intensity[i]; //
			sampleF5Smeared[i] = sampleF5Intensity[i]; //
			sampleF6Smeared[i] = sampleF6Intensity[i]; //

		}

		for(int i = timeCount + (coarseDer / 2); i < timeCount + coarseDer; i++){

			/*
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
			*/

			sampleA1Smeared[i] = sampleA1Intensity[i]; //
			sampleA2Smeared[i] = sampleA2Intensity[i]; //
			sampleA3Smeared[i] = sampleA3Intensity[i]; //
			sampleA4Smeared[i] = sampleA4Intensity[i]; //
			sampleA5Smeared[i] = sampleA5Intensity[i]; //
			sampleA6Smeared[i] = sampleA6Intensity[i]; //
			sampleB1Smeared[i] = sampleB1Intensity[i]; //
			sampleB2Smeared[i] = sampleB2Intensity[i]; //
			sampleB3Smeared[i] = sampleB3Intensity[i]; //
			sampleB4Smeared[i] = sampleB4Intensity[i]; //
			sampleB5Smeared[i] = sampleB5Intensity[i]; //
			sampleB6Smeared[i] = sampleB6Intensity[i]; //
			sampleC1Smeared[i] = sampleC1Intensity[i]; // 1e7
			sampleC2Smeared[i] = sampleC2Intensity[i]; // 1e6
			sampleC3Smeared[i] = sampleC3Intensity[i]; // 1e5
			sampleC4Smeared[i] = sampleC4Intensity[i]; // 1e4
			sampleC5Smeared[i] = sampleC5Intensity[i]; // 1e3
			sampleC6Smeared[i] = sampleC6Intensity[i]; // NEG
			sampleD1Smeared[i] = sampleD1Intensity[i]; // 1e7
			sampleD2Smeared[i] = sampleD2Intensity[i]; // 1e6
			sampleD3Smeared[i] = sampleD3Intensity[i]; // 1e5
			sampleD4Smeared[i] = sampleD4Intensity[i]; // 1e4
			sampleD5Smeared[i] = sampleD5Intensity[i]; // 1e3
			sampleD6Smeared[i] = sampleD6Intensity[i]; // NEG
			sampleE1Smeared[i] = sampleE1Intensity[i]; //
			sampleE2Smeared[i] = sampleE2Intensity[i]; //
			sampleE3Smeared[i] = sampleE3Intensity[i]; //
			sampleE4Smeared[i] = sampleE4Intensity[i]; //
			sampleE5Smeared[i] = sampleE5Intensity[i]; //
			sampleE6Smeared[i] = sampleE6Intensity[i]; //
			sampleF1Smeared[i] = sampleF1Intensity[i]; //
			sampleF2Smeared[i] = sampleF2Intensity[i]; //
			sampleF3Smeared[i] = sampleF3Intensity[i]; //
			sampleF4Smeared[i] = sampleF4Intensity[i]; //
			sampleF5Smeared[i] = sampleF5Intensity[i]; //
			sampleF6Smeared[i] = sampleF6Intensity[i]; //
		}

		for(int i = 5; i < timeCount + 15; i++){

			/*
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
			*/

			sampleA1Smeared[i] = 0; //
			sampleA2Smeared[i] = 0; //
			sampleA3Smeared[i] = 0; //
			sampleA4Smeared[i] = 0; //
			sampleA5Smeared[i] = 0; //
			sampleA6Smeared[i] = 0; //
			sampleB1Smeared[i] = 0; //
			sampleB2Smeared[i] = 0; //
			sampleB3Smeared[i] = 0; //
			sampleB4Smeared[i] = 0; //
			sampleB5Smeared[i] = 0; //
			sampleB6Smeared[i] = 0; //
			sampleC1Smeared[i] = 0; // 1e7
			sampleC2Smeared[i] = 0; // 1e6
			sampleC3Smeared[i] = 0; // 1e5
			sampleC4Smeared[i] = 0; // 1e4
			sampleC5Smeared[i] = 0; // 1e3
			sampleC6Smeared[i] = 0; // NEG
			sampleD1Smeared[i] = 0; // 1e7
			sampleD2Smeared[i] = 0; // 1e6
			sampleD3Smeared[i] = 0; // 1e5
			sampleD4Smeared[i] = 0; // 1e4
			sampleD5Smeared[i] = 0; // 1e3
			sampleD6Smeared[i] = 0; // NEG
			sampleE1Smeared[i] = 0; //
			sampleE2Smeared[i] = 0; //
			sampleE3Smeared[i] = 0; //
			sampleE4Smeared[i] = 0; //
			sampleE5Smeared[i] = 0; //
			sampleE6Smeared[i] = 0; //
			sampleF1Smeared[i] = 0; //
			sampleF2Smeared[i] = 0; //
			sampleF3Smeared[i] = 0; //
			sampleF4Smeared[i] = 0; //
			sampleF5Smeared[i] = 0; //
			sampleF6Smeared[i] = 0; //

			for(int k = -5; k < 6; k++){

				/*
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
				*/

				sampleA1Smeared[i] = sampleA1Smeared[i] + ((sampleA1Intensity[i + k]) / (coarseDer / 2)); //
				sampleA2Smeared[i] = sampleA2Smeared[i] + ((sampleA2Intensity[i + k]) / (coarseDer / 2)); //
				sampleA3Smeared[i] = sampleA3Smeared[i] + ((sampleA3Intensity[i + k]) / (coarseDer / 2)); //
				sampleA4Smeared[i] = sampleA4Smeared[i] + ((sampleA4Intensity[i + k]) / (coarseDer / 2)); //
				sampleA5Smeared[i] = sampleA5Smeared[i] + ((sampleA5Intensity[i + k]) / (coarseDer / 2)); //
				sampleA6Smeared[i] = sampleA6Smeared[i] + ((sampleA6Intensity[i + k]) / (coarseDer / 2)); //
				sampleB1Smeared[i] = sampleB1Smeared[i] + ((sampleB1Intensity[i + k]) / (coarseDer / 2)); //
				sampleB2Smeared[i] = sampleB2Smeared[i] + ((sampleB2Intensity[i + k]) / (coarseDer / 2)); //
				sampleB3Smeared[i] = sampleB3Smeared[i] + ((sampleB3Intensity[i + k]) / (coarseDer / 2)); //
				sampleB4Smeared[i] = sampleB4Smeared[i] + ((sampleB4Intensity[i + k]) / (coarseDer / 2)); //
				sampleB5Smeared[i] = sampleB5Smeared[i] + ((sampleB5Intensity[i + k]) / (coarseDer / 2)); //
				sampleB6Smeared[i] = sampleB6Smeared[i] + ((sampleB6Intensity[i + k]) / (coarseDer / 2)); //
				sampleC1Smeared[i] = sampleC1Smeared[i] + ((sampleC1Intensity[i + k]) / (coarseDer / 2)); // 1e7
				sampleC2Smeared[i] = sampleC2Smeared[i] + ((sampleC2Intensity[i + k]) / (coarseDer / 2)); // 1e6
				sampleC3Smeared[i] = sampleC3Smeared[i] + ((sampleC3Intensity[i + k]) / (coarseDer / 2)); // 1e5
				sampleC4Smeared[i] = sampleC4Smeared[i] + ((sampleC4Intensity[i + k]) / (coarseDer / 2)); // 1e4
				sampleC5Smeared[i] = sampleC5Smeared[i] + ((sampleC5Intensity[i + k]) / (coarseDer / 2)); // 1e3
				sampleC6Smeared[i] = sampleC6Smeared[i] + ((sampleC6Intensity[i + k]) / (coarseDer / 2)); // NEG
				sampleD1Smeared[i] = sampleD1Smeared[i] + ((sampleD1Intensity[i + k]) / (coarseDer / 2)); // 1e7
				sampleD2Smeared[i] = sampleD2Smeared[i] + ((sampleD2Intensity[i + k]) / (coarseDer / 2)); // 1e6
				sampleD3Smeared[i] = sampleD3Smeared[i] + ((sampleD3Intensity[i + k]) / (coarseDer / 2)); // 1e5
				sampleD4Smeared[i] = sampleD4Smeared[i] + ((sampleD4Intensity[i + k]) / (coarseDer / 2)); // 1e4
				sampleD5Smeared[i] = sampleD5Smeared[i] + ((sampleD5Intensity[i + k]) / (coarseDer / 2)); // 1e3
				sampleD6Smeared[i] = sampleD6Smeared[i] + ((sampleD6Intensity[i + k]) / (coarseDer / 2)); // NEG
				sampleE1Smeared[i] = sampleE1Smeared[i] + ((sampleE1Intensity[i + k]) / (coarseDer / 2)); //
				sampleE2Smeared[i] = sampleE2Smeared[i] + ((sampleE2Intensity[i + k]) / (coarseDer / 2)); //
				sampleE3Smeared[i] = sampleE3Smeared[i] + ((sampleE3Intensity[i + k]) / (coarseDer / 2)); //
				sampleE4Smeared[i] = sampleE4Smeared[i] + ((sampleE4Intensity[i + k]) / (coarseDer / 2)); //
				sampleE5Smeared[i] = sampleE5Smeared[i] + ((sampleE5Intensity[i + k]) / (coarseDer / 2)); //
				sampleE6Smeared[i] = sampleE6Smeared[i] + ((sampleE6Intensity[i + k]) / (coarseDer / 2)); //
				sampleF1Smeared[i] = sampleF1Smeared[i] + ((sampleF1Intensity[i + k]) / (coarseDer / 2)); //
				sampleF2Smeared[i] = sampleF2Smeared[i] + ((sampleF2Intensity[i + k]) / (coarseDer / 2)); //
				sampleF3Smeared[i] = sampleF3Smeared[i] + ((sampleF3Intensity[i + k]) / (coarseDer / 2)); //
				sampleF4Smeared[i] = sampleF4Smeared[i] + ((sampleF4Intensity[i + k]) / (coarseDer / 2)); //
				sampleF5Smeared[i] = sampleF5Smeared[i] + ((sampleF5Intensity[i + k]) / (coarseDer / 2)); //
				sampleF6Smeared[i] = sampleF6Smeared[i] + ((sampleF6Intensity[i + k]) / (coarseDer / 2)); //

			}
		}

		for(int i = 0; i < timeCount; i++){
			/*
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
			*/

			sampleA1CoarseDer[i] = sampleA1Smeared[i + coarseDer] - sampleA1Smeared[i]; //
			sampleA2CoarseDer[i] = sampleA2Smeared[i + coarseDer] - sampleA2Smeared[i]; //
			sampleA3CoarseDer[i] = sampleA3Smeared[i + coarseDer] - sampleA3Smeared[i]; //
			sampleA4CoarseDer[i] = sampleA4Smeared[i + coarseDer] - sampleA4Smeared[i]; //
			sampleA5CoarseDer[i] = sampleA5Smeared[i + coarseDer] - sampleA5Smeared[i]; //
			sampleA6CoarseDer[i] = sampleA6Smeared[i + coarseDer] - sampleA6Smeared[i]; //
			sampleB1CoarseDer[i] = sampleB1Smeared[i + coarseDer] - sampleB1Smeared[i]; //
			sampleB2CoarseDer[i] = sampleB2Smeared[i + coarseDer] - sampleB2Smeared[i]; //
			sampleB3CoarseDer[i] = sampleB3Smeared[i + coarseDer] - sampleB3Smeared[i]; //
			sampleB4CoarseDer[i] = sampleB4Smeared[i + coarseDer] - sampleB4Smeared[i]; //
			sampleB5CoarseDer[i] = sampleB5Smeared[i + coarseDer] - sampleB5Smeared[i]; //
			sampleB6CoarseDer[i] = sampleB6Smeared[i + coarseDer] - sampleB6Smeared[i]; //
			sampleC1CoarseDer[i] = sampleC1Smeared[i + coarseDer] - sampleC1Smeared[i]; // 1e7
			sampleC2CoarseDer[i] = sampleC2Smeared[i + coarseDer] - sampleC2Smeared[i]; // 1e6
			sampleC3CoarseDer[i] = sampleC3Smeared[i + coarseDer] - sampleC3Smeared[i]; // 1e5
			sampleC4CoarseDer[i] = sampleC4Smeared[i + coarseDer] - sampleC4Smeared[i]; // 1e4
			sampleC5CoarseDer[i] = sampleC5Smeared[i + coarseDer] - sampleC5Smeared[i]; // 1e3
			sampleC6CoarseDer[i] = sampleC6Smeared[i + coarseDer] - sampleC6Smeared[i]; // NEG
			sampleD1CoarseDer[i] = sampleD1Smeared[i + coarseDer] - sampleD1Smeared[i]; // 1e7
			sampleD2CoarseDer[i] = sampleD2Smeared[i + coarseDer] - sampleD2Smeared[i]; // 1e6
			sampleD3CoarseDer[i] = sampleD3Smeared[i + coarseDer] - sampleD3Smeared[i]; // 1e5
			sampleD4CoarseDer[i] = sampleD4Smeared[i + coarseDer] - sampleD4Smeared[i]; // 1e4
			sampleD5CoarseDer[i] = sampleD5Smeared[i + coarseDer] - sampleD5Smeared[i]; // 1e3
			sampleD6CoarseDer[i] = sampleD6Smeared[i + coarseDer] - sampleD6Smeared[i]; // NEG
			sampleE1CoarseDer[i] = sampleE1Smeared[i + coarseDer] - sampleE1Smeared[i]; //
			sampleE2CoarseDer[i] = sampleE2Smeared[i + coarseDer] - sampleE2Smeared[i]; //
			sampleE3CoarseDer[i] = sampleE3Smeared[i + coarseDer] - sampleE3Smeared[i]; //
			sampleE4CoarseDer[i] = sampleE4Smeared[i + coarseDer] - sampleE4Smeared[i]; //
			sampleE5CoarseDer[i] = sampleE5Smeared[i + coarseDer] - sampleE5Smeared[i]; //
			sampleE6CoarseDer[i] = sampleE6Smeared[i + coarseDer] - sampleE6Smeared[i]; //
			sampleF1CoarseDer[i] = sampleF1Smeared[i + coarseDer] - sampleF1Smeared[i]; //
			sampleF2CoarseDer[i] = sampleF2Smeared[i + coarseDer] - sampleF2Smeared[i]; //
			sampleF3CoarseDer[i] = sampleF3Smeared[i + coarseDer] - sampleF3Smeared[i]; //
			sampleF4CoarseDer[i] = sampleF4Smeared[i + coarseDer] - sampleF4Smeared[i]; //
			sampleF5CoarseDer[i] = sampleF5Smeared[i + coarseDer] - sampleF5Smeared[i]; //
			sampleF6CoarseDer[i] = sampleF6Smeared[i + coarseDer] - sampleF6Smeared[i]; //

			/*
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
			*/

			if(sampleA1CoarseDer[i] > maxA1){
				maxA1 = sampleA1CoarseDer[i];
				sampleA1IntensityTime = (i + 1) * 10;
			}
			if(sampleA2CoarseDer[i] > maxA2){
				maxA2 = sampleA2CoarseDer[i];
				sampleA2IntensityTime = (i + 1) * 10;
			}
			if(sampleA3CoarseDer[i] > maxA3){
				maxA3 = sampleA3CoarseDer[i];
				sampleA3IntensityTime = (i + 1) * 10;
			}
			if(sampleA4CoarseDer[i] > maxA4){
				maxA4 = sampleA4CoarseDer[i];
				sampleA4IntensityTime = (i + 1) * 10;
			}
			if(sampleA5CoarseDer[i] > maxA5){
				maxA5 = sampleA5CoarseDer[i];
				sampleA5IntensityTime = (i + 1) * 10;
			}
			if(sampleA6CoarseDer[i] > maxA6) {
				maxA6 = sampleA6CoarseDer[i];
				sampleA6IntensityTime = (i + 1) * 10;
			}
			if(sampleB1CoarseDer[i] > maxB1){
				maxB1 = sampleB1CoarseDer[i];
				sampleB1IntensityTime = (i + 1) * 10;
			}
			if(sampleB2CoarseDer[i] > maxB2){
				maxB2 = sampleB2CoarseDer[i];
				sampleB2IntensityTime = (i + 1) * 10;
			}
			if(sampleB3CoarseDer[i] > maxB3){
				maxB3 = sampleB3CoarseDer[i];
				sampleB3IntensityTime = (i + 1) * 10;
			}
			if(sampleB4CoarseDer[i] > maxB4){
				maxB4 = sampleB4CoarseDer[i];
				sampleB4IntensityTime = (i + 1) * 10;
			}
			if(sampleB5CoarseDer[i] > maxB5){
				maxB5 = sampleB5CoarseDer[i];
				sampleB5IntensityTime = (i + 1) * 10;
			}
			if(sampleB6CoarseDer[i] > maxB6){
				maxB6 = sampleB6CoarseDer[i];
				sampleB6IntensityTime = (i + 1) * 10;
			}
			if(sampleC1CoarseDer[i] > maxC1){
				maxC1 = sampleC1CoarseDer[i];
				sampleC1IntensityTime = (i + 1) * 10;
			}
			if(sampleC2CoarseDer[i] > maxC2){
				maxC2 = sampleC2CoarseDer[i];
				sampleC2IntensityTime = (i + 1) * 10;
			}
			if(sampleC3CoarseDer[i] > maxC3){
				maxC3 = sampleC3CoarseDer[i];
				sampleC3IntensityTime = (i + 1) * 10;
			}
			if(sampleC4CoarseDer[i] > maxC4){
				maxC4 = sampleC4CoarseDer[i];
				sampleC4IntensityTime = (i + 1) * 10;
			}
			if(sampleC5CoarseDer[i] > maxC5){
				maxC5 = sampleC5CoarseDer[i];
				sampleC5IntensityTime = (i + 1) * 10;
			}
			if(sampleC6CoarseDer[i] > maxC6){
				maxC6 = sampleC6CoarseDer[i];
				sampleC6IntensityTime = (i + 1) * 10;
			}
			if(sampleD1CoarseDer[i] > maxD1){
				maxD1 = sampleD1CoarseDer[i];
				sampleD1IntensityTime = (i + 1) * 10;
			}
			if(sampleD2CoarseDer[i] > maxD2){
				maxD2 = sampleD2CoarseDer[i];
				sampleD2IntensityTime = (i + 1) * 10;
			}
			if(sampleD3CoarseDer[i] > maxD3){
				maxD3 = sampleD3CoarseDer[i];
				sampleD3IntensityTime = (i + 1) * 10;
			}
			if(sampleD4CoarseDer[i] > maxD4){
				maxD4 = sampleD4CoarseDer[i];
				sampleD4IntensityTime = (i + 1) * 10;
			}
			if(sampleD5CoarseDer[i] > maxD5){
				maxD5 = sampleD5CoarseDer[i];
				sampleD5IntensityTime = (i + 1) * 10;
			}
			if(sampleD6CoarseDer[i] > maxD6){
				maxD6 = sampleD6CoarseDer[i];
				sampleD6IntensityTime = (i + 1) * 10;
			}
			if(sampleF1CoarseDer[i] > maxF1){
				maxF1 = sampleF1CoarseDer[i];
				sampleF1IntensityTime = (i + 1) * 10;
			}
			if(sampleF2CoarseDer[i] > maxF2){
				maxF2 = sampleF2CoarseDer[i];
				sampleF2IntensityTime = (i + 1) * 10;
			}
			if(sampleF3CoarseDer[i] > maxF3){
				maxF3 = sampleF3CoarseDer[i];
				sampleF3IntensityTime = (i + 1) * 10;
			}
			if(sampleF4CoarseDer[i] > maxF4){
				maxF4 = sampleF4CoarseDer[i];
				sampleF4IntensityTime = (i + 1) * 10;
			}
			if(sampleF5CoarseDer[i] > maxF5){
				maxF5 = sampleF5CoarseDer[i];
				sampleF5IntensityTime = (i + 1) * 10;
			}
			if(sampleF6CoarseDer[i] > maxF6){
				maxF6 = sampleF6CoarseDer[i];
				sampleF6IntensityTime = (i + 1) * 10;
			}
			if(sampleE1CoarseDer[i] > maxE1){
				maxE1 = sampleE1CoarseDer[i];
				sampleE1IntensityTime = (i + 1) * 10;
			}
			if(sampleE2CoarseDer[i] > maxE2){
				maxE2 = sampleE2CoarseDer[i];
				sampleE2IntensityTime = (i + 1) * 10;
			}
			if(sampleE3CoarseDer[i] > maxE3){
				maxE3 = sampleE3CoarseDer[i];
				sampleE3IntensityTime = (i + 1) * 10;
			}
			if(sampleE4CoarseDer[i] > maxE4){
				maxE4 = sampleE4CoarseDer[i];
				sampleE4IntensityTime = (i + 1) * 10;
			}
			if(sampleE5CoarseDer[i] > maxE5){
				maxE5 = sampleE5CoarseDer[i];
				sampleE5IntensityTime = (i + 1) * 10;
			}
			if(sampleE6CoarseDer[i] > maxE6){
				maxE6 = sampleE6CoarseDer[i];
				sampleE6IntensityTime = (i + 1) * 10;
			}

			if(sampleA1CoarseDer[i] >= 0){
				sampleA1ampCheck = sampleA1ampCheck + sampleA1CoarseDer[i];
			}
			if(sampleA2CoarseDer[i] >= 0){
				sampleA2ampCheck = sampleA2ampCheck + sampleA2CoarseDer[i];
			}
			if(sampleA3CoarseDer[i] >= 0){
				sampleA3ampCheck = sampleA3ampCheck + sampleA3CoarseDer[i];
			}
			if(sampleA4CoarseDer[i] >= 0){
				sampleA4ampCheck = sampleA4ampCheck + sampleA4CoarseDer[i];
			}
			if(sampleA5CoarseDer[i] >= 0){
				sampleA5ampCheck = sampleA5ampCheck + sampleA5CoarseDer[i];
			}
			if(sampleA6CoarseDer[i] >= 0){
				sampleA6ampCheck = sampleA6ampCheck + sampleA6CoarseDer[i];
			}
			if(sampleB1CoarseDer[i] >= 0){
				sampleB1ampCheck = sampleB1ampCheck + sampleB1CoarseDer[i];
			}
			if(sampleB2CoarseDer[i] >= 0){
				sampleB2ampCheck = sampleB2ampCheck + sampleB2CoarseDer[i];
			}
			if(sampleB3CoarseDer[i] >= 0){
				sampleB3ampCheck = sampleB3ampCheck + sampleB3CoarseDer[i];
			}
			if(sampleB4CoarseDer[i] >= 0){
				sampleB4ampCheck = sampleB4ampCheck + sampleB4CoarseDer[i];
			}
			if(sampleB5CoarseDer[i] >= 0){
				sampleB5ampCheck = sampleB5ampCheck + sampleB5CoarseDer[i];
			}
			if(sampleB6CoarseDer[i] >= 0){
				sampleB6ampCheck = sampleB6ampCheck + sampleB6CoarseDer[i];
			}
			if(sampleC1CoarseDer[i] >= 0){
				sampleC1ampCheck = sampleC1ampCheck + sampleC1CoarseDer[i];
			}
			if(sampleC2CoarseDer[i] >= 0){
				sampleC2ampCheck = sampleC2ampCheck + sampleC2CoarseDer[i];
			}
			if(sampleC3CoarseDer[i] >= 0){
				sampleC3ampCheck = sampleC3ampCheck + sampleC3CoarseDer[i];
			}
			if(sampleC4CoarseDer[i] >= 0){
				sampleC4ampCheck = sampleC4ampCheck + sampleC4CoarseDer[i];
			}
			if(sampleC5CoarseDer[i] >= 0){
				sampleC5ampCheck = sampleC5ampCheck + sampleC5CoarseDer[i];
			}
			if(sampleC6CoarseDer[i] >= 0){
				sampleC6ampCheck = sampleC6ampCheck + sampleC6CoarseDer[i];
			}
			if(sampleD1CoarseDer[i] >= 0){
				sampleD1ampCheck = sampleD1ampCheck + sampleD1CoarseDer[i];
			}
			if(sampleD2CoarseDer[i] >= 0){
				sampleD2ampCheck = sampleD2ampCheck + sampleD2CoarseDer[i];
			}
			if(sampleD3CoarseDer[i] >= 0){
				sampleD3ampCheck = sampleD3ampCheck + sampleD3CoarseDer[i];
			}
			if(sampleD4CoarseDer[i] >= 0){
				sampleD4ampCheck = sampleD4ampCheck + sampleD4CoarseDer[i];
			}
			if(sampleD5CoarseDer[i] >= 0){
				sampleD5ampCheck = sampleD5ampCheck + sampleD5CoarseDer[i];
			}
			if(sampleD6CoarseDer[i] >= 0){
				sampleD6ampCheck = sampleD6ampCheck + sampleD6CoarseDer[i];
			}
			if(sampleE1CoarseDer[i] >= 0){
				sampleE1ampCheck = sampleE1ampCheck + sampleE1CoarseDer[i];
			}
			if(sampleE2CoarseDer[i] >= 0){
				sampleE2ampCheck = sampleE2ampCheck + sampleE2CoarseDer[i];
			}
			if(sampleE3CoarseDer[i] >= 0){
				sampleE3ampCheck = sampleE3ampCheck + sampleE3CoarseDer[i];
			}
			if(sampleE4CoarseDer[i] >= 0){
				sampleE4ampCheck = sampleE4ampCheck + sampleE4CoarseDer[i];
			}
			if(sampleE5CoarseDer[i] >= 0){
				sampleE5ampCheck = sampleE5ampCheck + sampleE5CoarseDer[i];
			}
			if(sampleE6CoarseDer[i] >= 0){
				sampleE6ampCheck = sampleE6ampCheck + sampleE6CoarseDer[i];
			}
			if(sampleF1CoarseDer[i] >= 0){
				sampleF1ampCheck = sampleF1ampCheck + sampleF1CoarseDer[i];
			}
			if(sampleF2CoarseDer[i] >= 0){
				sampleF2ampCheck = sampleF2ampCheck + sampleF2CoarseDer[i];
			}
			if(sampleF3CoarseDer[i] >= 0){
				sampleF3ampCheck = sampleF3ampCheck + sampleF3CoarseDer[i];
			}
			if(sampleF4CoarseDer[i] >= 0){
				sampleF4ampCheck = sampleF4ampCheck + sampleF4CoarseDer[i];
			}
			if(sampleF5CoarseDer[i] >= 0){
				sampleF5ampCheck = sampleF5ampCheck + sampleF5CoarseDer[i];
			}
			if(sampleF6CoarseDer[i] >= 0){
				sampleF6ampCheck = sampleF6ampCheck + sampleF6CoarseDer[i];
			}
		}

		ampCheckArr[0] = sampleA1ampCheck;
		ampCheckArr[1] = sampleA2ampCheck;
		ampCheckArr[2] = sampleA3ampCheck;
		ampCheckArr[3] = sampleA4ampCheck;
		ampCheckArr[4] = sampleA5ampCheck;
		ampCheckArr[5] = sampleA6ampCheck;
		ampCheckArr[6] = sampleB1ampCheck;
		ampCheckArr[7] = sampleB2ampCheck;
		ampCheckArr[8] = sampleB3ampCheck;
		ampCheckArr[9] = sampleB4ampCheck;
		ampCheckArr[10] = sampleB5ampCheck;
		ampCheckArr[11] = sampleB6ampCheck;
		ampCheckArr[12] = sampleC1ampCheck;
		ampCheckArr[13] = sampleC2ampCheck;
		ampCheckArr[14] = sampleC3ampCheck;
		ampCheckArr[15] = sampleC4ampCheck;
		ampCheckArr[16] = sampleC5ampCheck;
		ampCheckArr[17] = sampleC6ampCheck;
		ampCheckArr[18] = sampleD1ampCheck;
		ampCheckArr[19] = sampleD2ampCheck;
		ampCheckArr[20] = sampleD3ampCheck;
		ampCheckArr[21] = sampleD4ampCheck;
		ampCheckArr[22] = sampleD5ampCheck;
		ampCheckArr[23] = sampleD6ampCheck;
		ampCheckArr[24] = sampleE1ampCheck;
		ampCheckArr[25] = sampleE2ampCheck;
		ampCheckArr[26] = sampleE3ampCheck;
		ampCheckArr[27] = sampleE4ampCheck;
		ampCheckArr[28] = sampleE5ampCheck;
		ampCheckArr[29] = sampleE6ampCheck;
		ampCheckArr[30] = sampleF1ampCheck;
		ampCheckArr[31] = sampleF2ampCheck;
		ampCheckArr[32] = sampleF3ampCheck;
		ampCheckArr[33] = sampleF4ampCheck;
		ampCheckArr[34] = sampleF5ampCheck;
		ampCheckArr[35] = sampleF6ampCheck;

		/*
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
		*/

		Log.d("Max coarse derivative is  " + maxA1); //
		Log.d("Max coarse derivative is  " + maxA2); //
		Log.d("Max coarse derivative is  " + maxA3); //
		Log.d("Max coarse derivative is  " + maxA4); //
		Log.d("Max coarse derivative is  " + maxA5); //
		Log.d("Max coarse derivative is  " + maxA6); //
		Log.d("Max coarse derivative is  " + maxB1); //
		Log.d("Max coarse derivative is  " + maxB2); //
		Log.d("Max coarse derivative is  " + maxB3); //
		Log.d("Max coarse derivative is  " + maxB4); //
		Log.d("Max coarse derivative is  " + maxB5); //
		Log.d("Max coarse derivative is  " + maxB6); //
		Log.d("Max coarse derivative is  " + maxC1); // 1e7
		Log.d("Max coarse derivative is  " + maxC2); // 1e6
		Log.d("Max coarse derivative is  " + maxC3); // 1e5
		Log.d("Max coarse derivative is  " + maxC4); // 1e4
		Log.d("Max coarse derivative is  " + maxC5); // 1e3
		Log.d("Max coarse derivative is  " + maxC6); // NEG
		Log.d("Max coarse derivative is  " + maxD1); // 1e7
		Log.d("Max coarse derivative is  " + maxD2); // 1e6
		Log.d("Max coarse derivative is  " + maxD3); // 1e5
		Log.d("Max coarse derivative is  " + maxD4); // 1e4
		Log.d("Max coarse derivative is  " + maxD5); // 1e3
		Log.d("Max coarse derivative is  " + maxD6); // NEG
		Log.d("Max coarse derivative is  " + maxE1); //
		Log.d("Max coarse derivative is  " + maxE2); //
		Log.d("Max coarse derivative is  " + maxE3); //
		Log.d("Max coarse derivative is  " + maxE4); //
		Log.d("Max coarse derivative is  " + maxE5); //
		Log.d("Max coarse derivative is  " + maxE6); //
		Log.d("Max coarse derivative is  " + maxF1); //
		Log.d("Max coarse derivative is  " + maxF2); //
		Log.d("Max coarse derivative is  " + maxF3); //
		Log.d("Max coarse derivative is  " + maxF4); //
		Log.d("Max coarse derivative is  " + maxF5); //
		Log.d("Max coarse derivative is  " + maxF6); //

		/*
		if( max02 > max10 && max02 > max11 && max02 > max12 && max02 > max13 && max02 > max20 && max02 > max21 && max02 > max22 && max02 > max23 ){
			negControl = false;
		}

		if( max01 < max10 && max01 < max11 && max01 < max12 && max01 < max13 && max01 < max20 && max01 < max21 && max01 < max22 && max01 < max23 ){
			posControl = false;
		}
		*/


		//Log.d("max 11 time " + sample11IntensityTime);

		/* for(int i = 0; i < timeCount; i++){
			Log.d(" sample 11 " + i + " " + sample11CoarseDer[i]);
			// Log.d("original 11 " + i + " " + sample11Intensity[i]);
		} */

		/*
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
		*/

		sampleTimes[0] = sampleA1IntensityTime; //
		sampleTimes[1] = sampleA2IntensityTime; //
		sampleTimes[2] = sampleA3IntensityTime; //
		sampleTimes[3] = sampleA4IntensityTime; //
		sampleTimes[4] = sampleA5IntensityTime; //
		sampleTimes[5] = sampleA6IntensityTime; //
		sampleTimes[6] = sampleB1IntensityTime; //
		sampleTimes[7] = sampleB2IntensityTime; //
		sampleTimes[8] = sampleB3IntensityTime; //
		sampleTimes[9] = sampleB4IntensityTime; //
		sampleTimes[10] = sampleB5IntensityTime; //
		sampleTimes[11] = sampleB6IntensityTime; //
		sampleTimes[12] = sampleC1IntensityTime; // 1e7
		sampleTimes[13] = sampleC2IntensityTime; // 1e6
		sampleTimes[14] = sampleC3IntensityTime; // 1e5
		sampleTimes[15] = sampleC4IntensityTime; // 1e4
		sampleTimes[16] = sampleC5IntensityTime; // 1e3
		sampleTimes[17] = sampleC6IntensityTime; // NEG
		sampleTimes[18] = sampleD1IntensityTime; // 1e7
		sampleTimes[19] = sampleD2IntensityTime; // 1e6
		sampleTimes[20] = sampleD3IntensityTime; // 1e5
		sampleTimes[21] = sampleD4IntensityTime; // 1e4
		sampleTimes[22] = sampleD5IntensityTime; // 1e3
		sampleTimes[23] = sampleD6IntensityTime; // NEG
		sampleTimes[24] = sampleE1IntensityTime; //
		sampleTimes[25] = sampleE2IntensityTime; //
		sampleTimes[26] = sampleE3IntensityTime; //
		sampleTimes[27] = sampleE4IntensityTime; //
		sampleTimes[28] = sampleE5IntensityTime; //
		sampleTimes[29] = sampleE6IntensityTime; //
		sampleTimes[30] = sampleF1IntensityTime; //
		sampleTimes[31] = sampleF2IntensityTime; //
		sampleTimes[32] = sampleF3IntensityTime; //
		sampleTimes[33] = sampleF4IntensityTime; //
		sampleTimes[34] = sampleF5IntensityTime; //
		sampleTimes[35] = sampleF6IntensityTime; //


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
