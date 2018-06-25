package com.mltbns.root.delvisapp;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TextViewActivity extends AppCompatActivity {

    private TextView mText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        mText = findViewById(R.id.tv_1);
        mText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }
}
