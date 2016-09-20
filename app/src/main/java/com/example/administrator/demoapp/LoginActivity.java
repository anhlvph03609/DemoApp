package com.example.administrator.demoapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.*;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    final String s = "LoginActivity";
    EditText edtUsername,edtPassword;
    Button btnLogin;
    Button btnReg;
    String username,passs;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        edtUsername = (EditText) findViewById(R.id.input_username);
        edtPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReg = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = edtUsername.getText().toString();
                passs = edtPassword.getText().toString();
                if (username.equals("") || passs.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter Username and Password", Toast.LENGTH_LONG).show();
                } else {
                    HashMap postData = new HashMap();
                    postData.put("txtUsername", username);
                    postData.put("txtPassword", passs);

                    PostResponseAsyncTask task1 = new PostResponseAsyncTask(LoginActivity.this, postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if (!s.equals("error")) {
                                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                               // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                //intent.putExtra("username",username);
                                //startActivity(intent);
                            }
                            else{
                                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    task1.execute("http://192.168.1.15:8080/customer/");
                }
            }
        });


    }




}
