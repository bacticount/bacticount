package com.garynfox.pathogenanalyzer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.bytecode.opencsv.CSVReader;

// the new version!

public class ReactionResultsSimpleActivity extends Activity {

    public final static String NEG_TEST_C6 = "NEG_TEST_C6";
    public final static String NEG_TEST_D6 = "NEG_TEST_D6";
    public final static String CURVE_R2 = "CURVE_R2";
    public final static String SOURCE_TYPE_STR = "SOURCE_TYPE";
    public final static String RESULT_DATA = "RESULT_DATA";


    public final static int STANDARD_CURVE = 1;
    public final static int SAMPLE = 2;

    public final static int ARRAY_SIZE = 36;
    public final static int timeCount = 300;


    public final static String[] columnNames = new String[]{"A", "B", "C", "D", "E", "F"};

    static String[] standardCurveInfo = new String[3];
    static String[] sampleInfo = new String[3];
    static String[] referenceInfo = new String[3];
    static int recordingType = 999;
    static int dataPoints = 300;
    static double[] offsetTime = new double[dataPoints];
    int inclusionCount = 0;

    static String stdCurveNegTest_C6, stdCurveNegTest_D6, stdCurveRsqString;

    static int[] sampleTimes;
    static double[] ampCheckArr;
    int[] inclusionArr;

    String dataPath = "";
    String sourceType = "";
    String savedResults = "";
    double stdCurveIntercept;
    double stdCurveSlope;
    double stdCurveRsq;
    double[] stdCurveKnownValuesAdjustedSave;
    int[] stdCurveTimesAdjustedSave;

    ArrayList<ReactionResultsEntry> resultEntries;

    // Value pairs needed to pass bundle along

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_view_simple);

        resultEntries =  new ArrayList<>();
        sampleTimes = new int[ARRAY_SIZE];
        ampCheckArr = new double[ARRAY_SIZE];
        inclusionArr = new int[ARRAY_SIZE];

        setupData(getIntent());

        GridView gridview = (GridView) findViewById(R.id.pathogenResultsGridView);
        gridview.setAdapter(new ReactionResultsImageAdapter(this, resultEntries, false));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                ReactionResultsEntry entry = resultEntries.get(position);

                new AlertDialog.Builder(ReactionResultsSimpleActivity.this)
                        .setTitle("Information about "+entry.key)
                        .setMessage("Result Value: "+ entry.getStringValue() +"\nPathogen Detected: " + (entry.isPathogenDetected? "Yes" : "No" ))
                        .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });


        TextView sampleNameTextView = (TextView) findViewById(R.id.sampleNameTextView);
        sampleNameTextView.setText(sampleInfo[0]);
        
        Button goToDetailView = (Button) findViewById(R.id.detailViewButton);
        goToDetailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(ReactionResultsSimpleActivity.this, ReactionResultsDetailedActivity.class);
                Bundle b1 = new Bundle();
                b1.putString(NEG_TEST_C6, stdCurveNegTest_C6);
                b1.putString(NEG_TEST_D6, stdCurveNegTest_D6);
                b1.putString(CURVE_R2, stdCurveRsqString);
                b1.putString(SOURCE_TYPE_STR, sourceType);
                b1.putParcelableArrayList(RESULT_DATA, resultEntries);
                i.putExtras(b1);
                startActivity(i);
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String analysisTime  = dateFormat.format(new Date());

        String fullSampleNameEraseParr = removeParr(sampleInfo[1]);
        Log.d("getting rid of .parr " + fullSampleNameEraseParr);

        savedResults = fullSampleNameEraseParr + "_analysis";
        Log.d("Results analysis name path etc " + savedResults);

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
            textWriter.write("standard curve negative control test, well C6, " + stdCurveNegTest_C6 + "\n");
            textWriter.write("standard curve negative control test, well D6, " + stdCurveNegTest_D6 + "\n");
            textWriter.write(" " + "\n");
            textWriter.write("standard curve data points"  + "\n");
            for(int i = 0; i < inclusionCount; i++){
                textWriter.write("point: " + i + ", log10 [k]: " + stdCurveKnownValuesAdjustedSave[i] + ", t_T: " + stdCurveTimesAdjustedSave[i]+  "\n");
            }
            textWriter.write("" + "\n");
            textWriter.write("unknown specimen results and information about the results " +"\n");
            textWriter.write("Convention: first value is well, second is bacterial concentration [k], third amp result" + "\n");
            for(ReactionResultsEntry e: resultEntries){
                textWriter.write(e.getKey() + ", " + e.getValue() + ", ");
                textWriter.write(e.isPathogenDetected? "Amplified!":"no amp");
                textWriter.write("" + "\n");
            }

            textWriter.flush();
            textWriter.close();
        } catch (IOException ioe) {
            Log.d("text writing didn't work... bummer!");
        }

    }

    public void setupData(Intent previousScreenIntent) {
        if (previousScreenIntent != null && previousScreenIntent.getExtras() != null) {
            Bundle extrasBundle = previousScreenIntent.getExtras();
            standardCurveInfo = extrasBundle.getStringArray(RecordingReaction.STANDARD_CURVE_INFO);
            sampleInfo = extrasBundle.getStringArray(RecordingReaction.REACTION_INFO);
            referenceInfo = extrasBundle.getStringArray(RecordingReaction.REFERENCE_INFO);
            recordingType = extrasBundle.getInt(RecordingReaction.RECORDING_TYPE);
            dataPoints = extrasBundle.getInt(RecordingReaction.DATA_POINTS);
            sourceType = extrasBundle.getString(RecordingReaction.SOURCE_TYPE);
            android.util.Log.d("source type: ", " " + sourceType);
            android.util.Log.d("source type: ", "" + standardCurveInfo[2]);

            if (recordingType == STANDARD_CURVE) {
                dataPath = standardCurveInfo[2];
            } else if (recordingType == SAMPLE) {
                dataPath = sampleInfo[2];
            }

            int thresholdValue = 250;
            boolean isSourceTypeBlood = true;
            if((sourceType.equals("feces")) || (sourceType.equals("urine")) || (sourceType.equals("other"))) {
                isSourceTypeBlood = false;
                thresholdValue = 500;
            }

            analyzeDataSet(standardCurveInfo[1]);
            double[] standardResults = calculateStandardCurveInfo(thresholdValue, isSourceTypeBlood);

            // results are always stdCurveNegTest_C6_result, stdCurveNegTest_D6_result, stdCurveRsq, stdCurveSlope, stdCurveIntercept in that order

            stdCurveNegTest_C6 = standardResults[0] == 1? "Pass" : "Fail";
            stdCurveNegTest_D6 = standardResults[1] == 1? "Pass" : "Fail";

            BigDecimal rsqRounded = new BigDecimal(standardResults[2]);
            rsqRounded = rsqRounded.setScale(3, BigDecimal.ROUND_HALF_UP);

            stdCurveRsqString = rsqRounded.toString();

            analyzeDataSet(sampleInfo[1]);

            double[] results = calculateSampleInfo(thresholdValue, standardResults[3], standardResults[4]);

            // now that we have the data, let's make the array for the GridView!
            for(int i = 0; i < results.length; i++) {
                int column = (int) Math.floor(i/6);
                String columnName = columnNames[column]+((i%6)+1);
                resultEntries.add(new ReactionResultsEntry(columnName, results[i], (inclusionArr[i] == 1)));
            }
        }
    }

    public void analyzeDataSet(String dataPath) {
        int coarseDer = 22;

        ArrayList<Double[]> sampleIntensityList = new ArrayList<Double[]>();
        ArrayList<Double[]> sampleSmearedList = new ArrayList<Double[]>();
        ArrayList<Double[]> sampleCoarseDerList = new ArrayList<Double[]>();

        double[] maxForSample = new double[ARRAY_SIZE];
        double[] sampleAmpCheck = new double[ARRAY_SIZE];
        int[] sampleIntensityTime = new int[ARRAY_SIZE];

        // initialization
        for (int i = 0; i < ARRAY_SIZE; i++) {
            sampleIntensityList.add(new Double[timeCount + coarseDer]);
            sampleSmearedList.add(new Double[timeCount + coarseDer]);
            sampleCoarseDerList.add(new Double[timeCount]);
            maxForSample[i] = 0;
            sampleAmpCheck[i] = 0;
            sampleIntensityTime[i] = 0;
        }

        ArrayList<Double> nextLineDouble = new ArrayList();
        String[] nextLine;

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(dataPath));
            nextLine = reader.readNext(); // skip the column labels (A1, A2...F5, F6 etc)

            for(int i = 0; i < timeCount; i++) {
                nextLineDouble.clear();
                nextLine = reader.readNext();
                for (int j = 0; j < 37; j++) {
                    nextLineDouble.add(Double.parseDouble(nextLine[j]));
                }

                // do you ever use this?
                offsetTime[i] = nextLineDouble.get(36);

                for (int k = 0; k < sampleIntensityList.size(); k++) // size of sample intensity is 36
                {
                    Double[] sampleIntensityCol = sampleIntensityList.get(k); //ex: 0 = A1, 1 = A2 etc
                    sampleIntensityCol[i + (coarseDer / 2)] = nextLineDouble.get(k); //ex: 0th val of nextLineDouble.
                }
            }
            reader.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("smearing...");

        for (int sampleNum = 0; sampleNum < sampleIntensityList.size(); sampleNum++) {
            Double[] sampleIntensity = sampleIntensityList.get(sampleNum);
            for (int i = 0; i < (coarseDer / 2); i++) {
                sampleIntensity[i] = sampleIntensity[(coarseDer / 2)];
                sampleIntensity[(timeCount + (coarseDer / 2) + i)] = sampleIntensity[(coarseDer / 2) + timeCount - 1];
            }
        }

        // Can we collapse this into the for loop above?
        for (int sampleNum = 0; sampleNum < sampleIntensityList.size(); sampleNum++) {
            Double[] individualSampleSmeared = sampleSmearedList.get(sampleNum);
            Double[] individualSampleIntensity = sampleIntensityList.get(sampleNum);
            for (int i = 0; i < (coarseDer / 2); i++) {
                individualSampleSmeared[i] = individualSampleIntensity[i];
            }

            for (int i = timeCount + (coarseDer / 2); i < timeCount + coarseDer; i++) {
                individualSampleSmeared[i] = individualSampleIntensity[i];
            }
        }

        for (int sampleNum = 0; sampleNum < sampleIntensityList.size(); sampleNum++) {
            Double[] individualSampleSmeared = sampleSmearedList.get(sampleNum);
            Double[] individualSampleIntensity = sampleIntensityList.get(sampleNum);

            for (int i = 5; i < timeCount + 15; i++) {
                individualSampleSmeared[i] = 0.;
                for (int k = -5; k < 6; k++) {
                    individualSampleSmeared[i] += ((individualSampleIntensity[i + k]) / (coarseDer / 2)); //
                }
            }
        }

        for (int sampleNum = 0; sampleNum < sampleIntensityList.size(); sampleNum++) {
            Double[] individualSampleCourseDer = sampleCoarseDerList.get(sampleNum);
            Double[] individualSampleSmeared = sampleSmearedList.get(sampleNum);

            for (int i = 0; i < timeCount; i++) {
                individualSampleCourseDer[i] = (individualSampleSmeared[i + coarseDer] - individualSampleSmeared[i]);

                if (individualSampleCourseDer[i] > maxForSample[sampleNum]) {
                    maxForSample[sampleNum] = individualSampleCourseDer[i];
                    sampleIntensityTime[sampleNum] = (i + 1) * 10;
                }

                if (individualSampleCourseDer[i] >= 0) {
                    sampleAmpCheck[sampleNum] += individualSampleCourseDer[i];
                }
            }
        }

        for (int i = 0; i < ARRAY_SIZE; i++) {
            ampCheckArr[i] = sampleAmpCheck[i];
            sampleTimes[i] = sampleIntensityTime[i];
        }
    }

    public double[] calculateStandardCurveInfo(int thresholdValue, boolean isSourceTypeBlood)
    {

        double stdCurveOffsetTime = offsetTime[0];
        android.util.Log.d("offset time std curve", String.valueOf(stdCurveOffsetTime));
        Long L = Math.round(stdCurveOffsetTime);
        int stdCurveOffsetTimeInt = Integer.valueOf(L.intValue());

        double[] stdCurveKnownValues = new double[]{50000000, 5000000, 500000, 50000, 5000, 50000000, 5000000, 500000, 50000, 5000};

        if(!isSourceTypeBlood){
            for(int i = 0; i < stdCurveKnownValues.length; i++){
                stdCurveKnownValues[i] = stdCurveKnownValues[i] / 1;
            }
        }

        for(int i = 0; i < stdCurveKnownValues.length; i++){
            stdCurveKnownValues[i] = Math.log10(stdCurveKnownValues[i]);
            android.util.Log.d("log10 test", String.valueOf(stdCurveKnownValues[i]));
        }
        int[] stdCurveTimes = sampleTimes.clone();
        double[] stdCurveAmpCheck = ampCheckArr.clone();

        for(int i = 0; i < stdCurveTimes.length; i++){
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
        int stdCurveNegTest_C6_result = (stdCurveNegTest_C6 > thresholdValue? 0 : 1);
        int stdCurveNegTest_D6_result= (stdCurveNegTest_D6 > thresholdValue? 0 : 1);


        // special inclusionArr for stdCurve as it is only looking at 10 values
        int[] inclusionArrStdCurve = new int[10];

        for(int i = 0; i < inclusionArrStdCurve.length; i++){
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

        stdCurveKnownValuesAdjustedSave = stdCurveKnownValuesAdjusted;
        stdCurveTimesAdjustedSave = stdCurveTimesAdjusted;

        android.util.Log.d("inclusion count: ", "" + inclusionCount);

        SimpleRegression stdCurveRegression = new SimpleRegression();
        for(int i = 0; i < inclusionCount; i++){
            android.util.Log.d("log of known [k]", String.valueOf(stdCurveKnownValuesAdjusted[i]));
            android.util.Log.d("t_T", String.valueOf(stdCurveTimesAdjusted[i]));
            stdCurveRegression.addData(stdCurveTimesAdjusted[i], stdCurveKnownValuesAdjusted[i]);
        }
        stdCurveIntercept = stdCurveRegression.getIntercept();
        stdCurveSlope = stdCurveRegression.getSlope();
        stdCurveRsq = stdCurveRegression.getRSquare();
        android.util.Log.d("r sq", String.valueOf(stdCurveRsq));
        android.util.Log.d("intercept", String.valueOf(stdCurveIntercept));
        android.util.Log.d("slope", String.valueOf(stdCurveSlope));



        // subtract the positive control threshold time from all sample runs by comparison
        // with the positive control time from the std curve

        return new double[]{ stdCurveNegTest_C6_result, stdCurveNegTest_D6_result, stdCurveRsq, stdCurveSlope, stdCurveIntercept };
    }

    public double[] calculateSampleInfo(int thresholdValue, double stdCurveSlope, double stdCurveIntercept)
    {
        for(int i = 0; i < inclusionArr.length ; i++) {
            inclusionArr[i] = (ampCheckArr[i] >= thresholdValue? 1 : 0);
            android.util.Log.d("amp test result, sample", String.valueOf(inclusionArr[i]));
        }

        double sampleOffsetTime = offsetTime[0];
        Long L2 = Math.round(sampleOffsetTime);
        int sampleOffsetTimeInt = Integer.valueOf(L2.intValue());

        for(int i = 0; i < sampleTimes.length; i++){
            sampleTimes[i] = sampleTimes[i] + sampleOffsetTimeInt;
        }

        double[] results = new double[sampleTimes.length];
        for (int i = 0 ; i <results.length ; i++) {
            results[i] = Math.pow(10, ((sampleTimes[i] * stdCurveSlope) + stdCurveIntercept));
        }

        return results;
    }

    private String removeParr(String path){
        path = path.substring(0, path.length() - 5);
        return path;
    }

}
