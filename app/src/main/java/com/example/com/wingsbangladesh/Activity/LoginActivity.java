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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.com.wingsbangladesh.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


     //   new GetUrlData().execute();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameText=username.getText().toString();
                passwordText=password.getText().toString();
                usernameText=usernameText.trim();
                passwordText=passwordText.trim();
             //   if(usernameText!=""&&passwordText!=""&&loginApi!=null) {

                  //  test();
               // }
                //else{

                //}

              //  APICall();

               // APIPost();

                String[] data = {"testa","testa$"};
                new PostTask().execute(data);

            }
        });

    }

    @Override
    public void onBackPressed()
    {
    }




    public void APIPost() {


        URL="http://paperfly.com.bd/la.php";
        Map<String, String> jsonParams = new HashMap<String, String>();


        // jsonParams.put("Authorization", token);
        jsonParams.put("username", usernameText);
        jsonParams.put("pass", passwordText);



        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                new JSONObject(jsonParams),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        // result= response.getJSONObject("results");

                        //token = response.getString("token");


                        //  System.out.println("token:::" + token+"   :::");*//*
                        //   verificationSuccess(response);

                        System.out.println("responseSAAD:::" + response + "   :::");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  verificationFailed(error);
                        Log.e("errorsaad",String.valueOf(error));
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();


                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(myRequest);
        //  MyApplication.getInstance().addToRequestQueue(myRequest, "tag");

    }

    public void test(){

        URL="http://paperfly.com.bd/la.php";


        // Creating the JsonArrayRequest class called arrayreq, passing the required parameters
        //JsonURL is the URL to be fetched from
        JsonArrayRequest myRequest = new JsonArrayRequest(URL,
                // The second parameter Listener overrides the method onResponse() and passes
                //JSONArray as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Retrieves first JSON object in outer array
                            JSONObject data = response.getJSONObject(0);

                          String usrname=  data.getString("usrname");
                            String type=  data.getString("type");
                            String empName=  data.getString("empName");


                            System.out.println("saad222"+usrname);

                        }
                        // Try and catch are included to handle any errors due to JSON
                        catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                        }
                    }
                },
                // The final parameter overrides the method onErrorResponse() and passes VolleyError
                //as a parameter
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  verificationFailed(error);
                    }
                })  {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("username", usernameText);
                headers.put("pass", passwordText);
                return headers;
            }


        };


        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(myRequest);

        // Adds the JSON array request "arrayreq" to the request queue
      //  requestQueue.add(arrayreq);
    }

    public void restcall(){


       // URL=loginApi+usernameText+"/"+passwordText;

        URL="http://paperfly.com.bd/la.php";


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


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


    private class PostTask extends AsyncTask<String[], String, String> {

        public PostTask() {
        }

        @Override
        protected String  doInBackground(String[]... data) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://paperfly.com.bd/la.php");

            try {
                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", "testa"));
                nameValuePairs.add(new BasicNameValuePair("pass", "testa$"));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity httpEntity = response.getEntity();
                String mJson = EntityUtils.toString(httpEntity);

                Log.e("Response",response.toString());

            } catch (ClientProtocolException e) {
                Log.e("Response",e.toString());
            } catch (IOException e) {
                Log.e("Response",e.toString());
            }
            return "";
        }
    }

}



