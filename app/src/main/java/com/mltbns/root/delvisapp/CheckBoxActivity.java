package com.mltbns.root.delvisapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class CheckBoxActivity extends AppCompatActivity {

    private CheckBox mCb1,mCb2,mCb3,mCb4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_box);

        mCb1 = (CheckBox) findViewById(R.id.cb_1);
        mCb2 = (CheckBox) findViewById(R.id.cb_2);
        mCb3 = (CheckBox) findViewById(R.id.cb_3);
        mCb4 = (CheckBox) findViewById(R.id.cb_4);

        mCb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(CheckBoxActivity.this, b?mCb1.getText()+" checked":mCb1.getText()+" unchecked", Toast.LENGTH_SHORT).show();
            }
        });
        mCb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(CheckBoxActivity.this, b?mCb2.getText()+" checked":mCb2.getText()+" unchecked", Toast.LENGTH_SHORT).show();
            }
        });
        mCb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(CheckBoxActivity.this, b?mCb3.getText()+" checked":mCb3.getText()+" unchecked", Toast.LENGTH_SHORT).show();
            }
        });
        mCb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(CheckBoxActivity.this, b?mCb4.getText()+" checked":mCb4.getText()+" unchecked", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
