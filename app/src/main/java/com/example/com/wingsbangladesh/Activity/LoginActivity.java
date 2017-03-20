package com.example.com.wingsbangladesh.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.com.wingsbangladesh.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    String usernameText,passwordText;
    EditText username,password;
    String code;
    String URL;
    Button login;
    String loginApi,marchantApi,barcodeApi,barcodeType;


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


        new GetUrlData().execute();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameText=username.getText().toString();
                passwordText=password.getText().toString();
                usernameText=usernameText.trim();
                passwordText=passwordText.trim();
                if(usernameText!=""&&passwordText!=""&&loginApi!=null) {

                    restcall();
                }
                else{

                    Toast.makeText(LoginActivity.this, "Login Failed,Try again",
                            Toast.LENGTH_LONG).show();
                }

              //  APICall();

            }
        });

    }

    @Override
    public void onBackPressed()
    {
    }

    public void restcall(){


        URL=loginApi+usernameText+"/"+passwordText;


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

//
                        try {
                            int success=  response.getInt("success");
                            String message=  response.getString("message");


                            if(success==1){

                                JSONObject result=  response.getJSONObject("results");

                                String userid=  result.getString("user_id");
                                String username= result.getString("username");
                                String usertype= result.getString("usertype");

                                Toast.makeText(LoginActivity.this, message,
                                        Toast.LENGTH_LONG).show();

                                Intent intent=new Intent(LoginActivity.this,MarchantInfoActivity.class);
                                intent.putExtra("name",username);
                                intent.putExtra("usertype",usertype);
                                intent.putExtra("userid",userid);
                                intent.putExtra("marchantApi",marchantApi);
                                intent.putExtra("barcodeApi",barcodeApi);
                                intent.putExtra("barcodeType",barcodeType);

                                startActivity(intent);
                            }
                            else if(success==0){

                                Toast.makeText(LoginActivity.this,  "Login Failed,Try again",
                                        Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(LoginActivity.this, "Login Failed,Try again",
                        Toast.LENGTH_LONG).show();

            }
        });

// Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(jsonObjReq);

    }

   public void urlCall(){


        URL="http://paperfly.mybdweb.com/get_settings.php";


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            int success=  response.getInt("success");
                            String message=  response.getString("message");
                            JSONArray arr=  response.getJSONArray("results");

                            for(int i=0;i<arr.length();i++) {

                                JSONObject jsonob=arr.getJSONObject(i);

                                loginApi = jsonob.getString("login_api");
                                marchantApi = jsonob.getString("merchant_api");
                                barcodeApi = jsonob.getString("barcode_api");
                                barcodeType = jsonob.getString("barcode_type");

                                // System.out.println("SAAD::"+logins);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

// Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(jsonObjReq);

    }


    private  class GetUrlData extends AsyncTask<Void, Void, Boolean> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... uRls) {

            urlCall();

            return null;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            pDialog.dismiss();

        }

    }

}



