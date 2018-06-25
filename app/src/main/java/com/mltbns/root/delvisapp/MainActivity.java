package com.mltbns.root.delvisapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private Button mBtnTextView;
    private Button mBtnEditView;
    private Button mBtnRadioView;
    private Button mBtnCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnTextView = findViewById(R.id.btn_textview);

        mBtnEditView = findViewById(R.id.btn_editview);
        mBtnRadioView = findViewById(R.id.btn_radioview);
        mBtnCheckBox    =   findViewById(R.id.btn_checkbox);
        setListeners();
    }

    private void setListeners( ) {
        OnClick onClick = new OnClick();
        mBtnTextView.setOnClickListener(onClick);
        mBtnEditView.setOnClickListener(onClick);
        mBtnRadioView.setOnClickListener(onClick);
        mBtnCheckBox.setOnClickListener(onClick);
    }
    private class OnClick implements View.OnClickListener{

        @Override
        public void onClick( View v ) {
            Intent intent = null;
            switch( v.getId() ) {

                case R.id.btn_editview:
                    intent = new Intent( MainActivity.this, EditViewActivity.class );
                    startActivity(intent);
                    break;

                case R.id.btn_textview:
                    intent = new Intent( MainActivity.this, TextViewActivity.class );
                    startActivity(intent);
                    break;

                case R.id.btn_radioview:
                    intent = new Intent( MainActivity.this, RadioButtonActivity.class  );
                    startActivity(intent);

                case R.id.btn_checkbox:
                    intent = new Intent( MainActivity.this, CheckBoxActivity.class );
                    startActivity(intent);

            }

        }

    }

}



