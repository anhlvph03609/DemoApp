package com.example.administrator.demoapp;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUsername,edtPass,edtConfPass,edtEmail;
    Button btnRegister;
    String username,pass,confPass,email;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword,inputLayoutCfPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = (EditText) findViewById(R.id.input_name);
        edtPass = (EditText) findViewById(R.id.input_password);
        edtEmail =(EditText) findViewById(R.id.input_email);
        edtConfPass = (EditText) findViewById(R.id.input_cfpassword);
        btnRegister = (Button) findViewById(R.id.btn_register);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutCfPass = (TextInputLayout) findViewById(R.id.input_layout_cfpassword) ;
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();

        }
        });
    }


    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        if(!validateCfPass()){
            return;
        }
        username = edtUsername.getText().toString().trim();
        pass = edtPass.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        HashMap postData = new HashMap();
        postData.put("txtUsername", username);
        postData.put("txtPass", pass);
        postData.put("txtEmail",email);
        PostResponseAsyncTask task1 = new PostResponseAsyncTask(RegisterActivity.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                if (s.equals("success")) {
                    Toast.makeText(RegisterActivity.this, "Register Successfully !", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else if(s.equals("exists")){
                    Toast.makeText(RegisterActivity.this, "Username already exists!", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this,s, Toast.LENGTH_LONG).show();
                }
            }
        });
        task1.execute("http://192.168.1.15:8080/customer/register.php");
    }

    private boolean validateName() {
        if (edtUsername.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(edtUsername);
            return false;
        }
        else if(edtUsername.getText().toString().trim().length()<6){
            inputLayoutName.setError(getString(R.string.err_username_lenght));
            requestFocus(edtUsername);
            return false;
        }
        else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(edtEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateCfPass(){
        if(!edtConfPass.getText().toString().trim().equals(edtPass.getText().toString().trim())){
            inputLayoutCfPass.setError(getString(R.string.err_msg_pass_not_match));
            requestFocus(edtConfPass);
            return false;
        }
        else {
            inputLayoutCfPass.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePassword() {
        if (edtPass.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(edtPass);
            return false;
        }
        else if(edtPass.getText().toString().length()<6){
            inputLayoutPassword.setError(getString(R.string.err_pass_lenght));
            requestFocus(edtPass);
            return false;
        }

        else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_cfpassword:
                    validateCfPass();
                    break;
            }
        }
    }
}
