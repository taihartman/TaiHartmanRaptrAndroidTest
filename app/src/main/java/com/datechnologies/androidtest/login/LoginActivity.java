package com.datechnologies.androidtest.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datechnologies.androidtest.MainActivity;
import com.datechnologies.androidtest.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A screen that displays a login prompt, allowing the user to login to the D & A Technologies Web Server.
 *
 */
public class LoginActivity extends AppCompatActivity {

    //==============================================================================================
    // Static Class Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        setTitle(R.string.login_title);
        // TODO: Make the UI look like it does in the mock-up. Allow for horizontal screen rotation.
        // TODO: Add a ripple effect when the buttons are clicked
        // TODO: Save screen state on screen rotation, inputted username and password should not disappear on screen rotation

        // TODO: Send 'email' and 'password' to http://dev.rapptrlabs.com/Tests/scripts/login.php
        // TODO: as FormUrlEncoded parameters.

        // TODO: When you receive a response from the login endpoint, display an AlertDialog.
        // TODO: The AlertDialog should display the 'code' and 'message' that was returned by the endpoint.
        // TODO: The AlertDialog should also display how long the API call took in milliseconds.
        // TODO: When a login is successful, tapping 'OK' on the AlertDialog should bring us back to the MainActivity

        // TODO: The only valid login credentials are:
        // TODO: email: info@rapptrlabs.com
        // TODO: password: Test123
        // TODO: so please use those to test the login.
        Button loginButton = findViewById(R.id.loginButton);
        EditText emailEditText = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);

        loginButton.setOnClickListener(view -> {
            loginTask(emailEditText.getText().toString(),passwordEditText.getText().toString());
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    //Log in and display alert dialog
    public void loginTask(String email, String password){
        //creating request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://dev.rapptrlabs.com/Tests/scripts/login.php";
        //setting up post request and checking how long the request took
        long requestStartTime = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> { //handle response
                    // response
                    Log.d("Response", response);
                    try {
                        //calculating how long the request took
                        long requestTime = System.currentTimeMillis() - requestStartTime;
                        //converting response to json to get values
                        JSONObject obj = new JSONObject(response);
                        //creating and showing alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Stats");
                        builder.setMessage("Code: "+obj.getString("code") +"\n"+"Message: "+obj.getString("message")+"\n"+ "Request time: " + requestTime+"ms" );
                        builder.setPositiveButton("OK", (dialogInterface, i) -> {
                            onBackPressed();
                        });
                        builder.show();

                    } catch (Throwable t) {
                        Log.e("Response", "Could not parse malformed JSON");
                    }
                },
                error -> {
                    // error

                    //calculating how long the request took
                    long requestTime = System.currentTimeMillis() - requestStartTime;
                    //creating and showing alert dialog
                    Log.d("Error.Response", error.toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("Authentication Error\n" + "Request time: " + requestTime +"ms" );
                    builder.setPositiveButton("OK", (dialogInterface, i) -> {

                    });
                    builder.show();
                }
        ) {
            //setting parameters
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
            //setting FormUrlEncoded params
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        //starting request
        queue.add(postRequest);
    }
}
