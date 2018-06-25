package com.mltbns.root.delvisapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RadioButtonActivity extends AppCompatActivity {


    private RadioGroup  mRg1;
    private RadioGroup  mRg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_radio_button);
        mRg1 = findViewById(R.id.rg_1);
        mRg2 = findViewById(R.id.rg_2);


        mRg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                Toast.makeText(RadioButtonActivity.this, "press:" + radioButton.getText(),Toast.LENGTH_SHORT).show();
            }
        });

        mRg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = ( RadioButton ) radioGroup.findViewById(i);
                Toast.makeText(RadioButtonActivity.this, "press:"+radioButton.getText(),Toast.LENGTH_SHORT).show();
            }
        });

    }


}
