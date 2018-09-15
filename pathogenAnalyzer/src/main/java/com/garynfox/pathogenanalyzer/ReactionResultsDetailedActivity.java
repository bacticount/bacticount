package com.garynfox.pathogenanalyzer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;

public class ReactionResultsDetailedActivity extends Activity {
    ArrayList<ReactionResultsEntry> resultEntries = new ArrayList<>();
    String negTest1Result = "";
    String negTest2Result = "";
    String curveResult = "";
    String sourceType = "";
    Button reactionResultsDetailedHome;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_view_detailed);

        reactionResultsDetailedHome = (Button) findViewById(R.id.reactionResultsDeatiledButtonHome);

        // get stuff from bundle!
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
        {
            Bundle bundle = intent.getExtras();
            negTest1Result = bundle.getString(ReactionResultsSimpleActivity.NEG_TEST_C6);
            negTest2Result = bundle.getString(ReactionResultsSimpleActivity.NEG_TEST_D6);
            curveResult = bundle.getString(ReactionResultsSimpleActivity.CURVE_R2);
            sourceType = bundle.getString(ReactionResultsSimpleActivity.SOURCE_TYPE_STR);
            resultEntries = bundle.getParcelableArrayList(ReactionResultsSimpleActivity.RESULT_DATA);
        }

        GridView gridview = (GridView) findViewById(R.id.pathogenResultsGridView);
        gridview.setAdapter(new ReactionResultsImageAdapter(this, resultEntries, true));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                ReactionResultsEntry entry = resultEntries.get(position);

                new AlertDialog.Builder(ReactionResultsDetailedActivity.this)
                        .setTitle("Information about "+entry.key)
                        .setMessage("Result Value: "+ entry.getStringValueLong() +"\nPathogen Detected: " + (entry.isPathogenDetected? "Yes" : "No" ))
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

        TextView curveValue = (TextView) findViewById(R.id.curveValue);
        curveValue.setText(curveResult);

        TextView replicate1Value = (TextView) findViewById(R.id.replicate1Value);
        replicate1Value.setText(negTest1Result);

        TextView replicate2Value = (TextView) findViewById(R.id.replicate2Value);
        replicate2Value.setText(negTest2Result);

        TextView sourceTypeValue = (TextView) findViewById(R.id.sourceType);
        sourceTypeValue.setText(sourceType);

        TextView notes = (TextView) findViewById(R.id.notes);

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        String boldStr = getString(R.string.notes_detailed_bold);
        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(boldStr);
        text.append("\n"+getString(R.string.notes_detailed_example));
        text.setSpan(bss, 0, boldStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        notes.setText(text);

        setButtonOnClickListeners();

    }

    private void setButtonOnClickListeners(){
        reactionResultsDetailedHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                Intent i1 = new Intent(ReactionResultsDetailedActivity.this, HomeScreen.class);
                startActivity(i1);
            }
        });
    }
}
