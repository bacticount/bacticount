package com.garynfox.pathogenanalyzer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class ChooseSource extends Activity{

    // Value pairs needed to pass bundle along
    public final static String SOURCE_TYPE = "com.garynfox.pathogenanalyzer.SOURCE_TYPE";

    Button chooseSourceButtonBlood;
    Button chooseSourceButtonUrine;
    Button chooseSourceButtonFeces;
    Button chooseSourceButtonOther;

    String sourceType;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Inflate the GUI
        setContentView(R.layout.activity_choose_source);

        chooseSourceButtonBlood = (Button) findViewById(R.id.chooseSourceButtonBlood);
        chooseSourceButtonUrine = (Button) findViewById(R.id.chooseSourceButtonUrine);
        chooseSourceButtonFeces = (Button) findViewById(R.id.chooseSourceButtonFeces);
        chooseSourceButtonOther = (Button) findViewById(R.id.chooseSourceButtonOther);

        setButtonOnClickListeners();

    }

    private void setButtonOnClickListeners(){
        final Intent intentChooseAction = new Intent(ChooseSource.this, ChooseAction.class);

        chooseSourceButtonBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                // prepare the bundle to send the correct source type for the rest of the system
                sourceType = "blood";
                Bundle b1 = new Bundle();
                b1.putString(SOURCE_TYPE, sourceType);
                intentChooseAction.putExtras(b1);
                startActivity(intentChooseAction);
            }
        });

        chooseSourceButtonUrine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                // prepare the bundle to send the correct source type for the rest of the system
                sourceType = "Urine";
                Bundle b1 = new Bundle();
                b1.putString(SOURCE_TYPE, sourceType);
                intentChooseAction.putExtras(b1);
                startActivity(intentChooseAction);
            }
        });

        chooseSourceButtonFeces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                // prepare the bundle to send the correct source type for the rest of the system
                sourceType = "feces";
                Bundle b1 = new Bundle();
                b1.putString(SOURCE_TYPE, sourceType);
                intentChooseAction.putExtras(b1);
                startActivity(intentChooseAction);
            }
        });

        chooseSourceButtonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                // prepare the bundle to send the correct source type for the rest of the system
                sourceType = "other";
                Bundle b1 = new Bundle();
                b1.putString(SOURCE_TYPE, sourceType);
                intentChooseAction.putExtras(b1);
                startActivity(intentChooseAction);
            }
        });

    }

}
