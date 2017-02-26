package com.example.com.wingsbangladesh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.com.wingsbangladesh.R;

public class LoginActivity extends AppCompatActivity {

    String usernameText,passwordText;
    EditText username,password;
    String code;
    String URL;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();


        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login) ;



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameText=username.getText().toString();
                passwordText=password.getText().toString();

                APICall();

            }
        });

    }

    @Override
    public void onBackPressed()
    {
    }

    public void APICall() {


      //  URL="http://paperfly.mybdweb.com/login.php/rajib/pass1234";

       URL="http://paperfly.mybdweb.com/login.php/"+usernameText+"/"+passwordText;


        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
              //  Log.d(TAG, response.toString());
               // pDialog.hide();

                System.out.println("response"+response);

                if(response.toString().equals("1")){

                    Toast.makeText(LoginActivity.this, "Login is successful",
                            Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(LoginActivity.this,MarchantInfoActivity.class);
                    intent.putExtra("name",usernameText);
                    startActivity(intent);
                }
                else if(response.toString().equals("0")){

                    Toast.makeText(LoginActivity.this, "Login is not successful",
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               // VolleyLog.d(TAG, "Error: " + error.getMessage());
                //pDialog.hide();
            }
        });

// Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(strReq);
    }

}



