package com.mltbns.root.delvisapp;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditViewActivity extends AppCompatActivity {

    private Button  mBtnLogin;
    private EditText mEtUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);

        mBtnLogin = (Button) findViewById(R.id.btn_login);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(EditViewActivity.this,"Login Successful.",Toast.LENGTH_SHORT);
                String username = mEtUsername.getText().toString();
                System.out.println("Get username :" + username);
                String passwd = mEtUsername.getText().toString();
                System.out.println("Get passwd : " + passwd);
            }
        });

        mEtUsername = (EditText)findViewById(R.id.et_1);

        mEtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("edit text",charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
