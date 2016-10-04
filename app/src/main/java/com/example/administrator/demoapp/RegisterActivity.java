package com.example.administrator.demoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.demoapp.helper.AppController;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUsername,edtPass,edtConfPass,edtEmail,edtFullName,edtPhone;
    Button btnRegister;
    String username,pass,confPass,email,fullname,phone;
    private ProgressDialog pDialog;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword,inputLayoutCfPass,inputLayoutFullName,inputLayoutPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = (EditText) findViewById(R.id.input_name);
        edtPass = (EditText) findViewById(R.id.input_password);
        edtEmail =(EditText) findViewById(R.id.input_email);
        edtConfPass = (EditText) findViewById(R.id.input_cfpassword);
        edtFullName = (EditText) findViewById(R.id.input_fullname);
        edtPhone =  (EditText) findViewById(R.id.input_phone);
        btnRegister = (Button) findViewById(R.id.btn_register);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutFullName = (TextInputLayout) findViewById(R.id.input_layout_fullname);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
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
        if(!validateFullName()){
            return;
        }
        if(!validatePhone()){
            return;
        }
        username = edtUsername.getText().toString().trim();
        pass = edtPass.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        fullname = edtFullName.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();
       checkReg(username,fullname,email,phone,pass);
    }
    private void checkReg(final String username,final String fullname,final String email,final String phone, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://demophp2.esy.es/user.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d("TAG", "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Toast.makeText(getBaseContext(),response,Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(i);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("txtEmail", email);
                params.put("txtPassword", password);
                params.put("txtUsername",username);
                params.put("txtPhone",phone);
                params.put("txtFullName",fullname);
                params.put("action","register");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
    private boolean validateFullName() {
        if (edtFullName.getText().toString().trim().isEmpty()) {
            inputLayoutFullName.setError(getString(R.string.err_msg_name));
            requestFocus(edtFullName);
            return false;
        }
        else if(edtFullName.getText().toString().trim().length()<6){
            inputLayoutFullName.setError(getString(R.string.err_username_lenght));
            requestFocus(edtFullName);
            return false;
        }
        else {
            inputLayoutFullName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        if (edtPhone.getText().toString().trim().isEmpty()) {
            inputLayoutPhone.setError(getString(R.string.err_msg_name));
            requestFocus(edtPhone);
            return false;
        }
        else if(edtPhone.getText().toString().trim().length()<10){
            inputLayoutPhone.setError(getString(R.string.err_username_lenght));
            requestFocus(edtPhone);
            return false;
        }
        else {
            inputLayoutPhone.setErrorEnabled(false);
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
                case R.id.input_fullname:
                    validateFullName();
                    break;
                case R.id.input_phone:
                    validatePhone();
                    break;
            }
        }
    }
}
