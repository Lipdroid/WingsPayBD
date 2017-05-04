package com.example.com.wingsbangladesh.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
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
import com.example.com.wingsbangladesh.Adapter.MarchantInfoAdapter;
import com.example.com.wingsbangladesh.R;
import com.example.com.wingsbangladesh.util.ConnectionDetector;
import com.example.com.wingsbangladesh.util.ConstantURLs;
import com.example.com.wingsbangladesh.util.GlobalUtils;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    String usernameText, passwordText;
    EditText username, password;
    String URL = "http://paperfly.com.bd/la.php";
    Button login;
    String mJson;
    private SweetAlertDialog pDialog = null;
    private ConnectionDetector cd = null;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        GlobalUtils.prev_connected_device = null;

        cd = new ConnectionDetector(this);
         prefs = getSharedPreferences(ConstantURLs.PREF_NAME, Context.MODE_PRIVATE);

        getSupportActionBar().hide();


        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameText = username.getText().toString();
                passwordText = password.getText().toString();
                usernameText = usernameText.trim();
                passwordText = passwordText.trim();


                if (cd.isConnectingToInternet()) {

//                    String loginApi_txt = prefs.getString(ConstantURLs.LOGIN_API_KEY, ConstantURLs.LOGIN_URL);
//                    String marchantApi_txt = prefs.getString(ConstantURLs.MERCHANT_API_KEY, ConstantURLs.MERCHANT_INFO_URL);
//                    String barcodeApi_txt = prefs.getString(ConstantURLs.BARCODE_LIST_API_KEY, ConstantURLs.BARCODE_LIST_URL);
//                    String barcode_type_txt = prefs.getString(ConstantURLs.BARCODE_TYPE_KEY, "UPC-A");
//
//
//                    if (loginApi_txt.equals(ConstantURLs.LOGIN_URL) && marchantApi_txt.equals(ConstantURLs.MERCHANT_INFO_URL) && barcodeApi_txt.equals(ConstantURLs.BARCODE_LIST_URL) && barcode_type_txt.equals("UPC-A"))
//                    {
                        new PostTask().execute();
//                    }else{
//                        Toast.makeText(LoginActivity.this, "Api url chaged by admin",
//                                Toast.LENGTH_LONG).show();
//                    }
                } else {

                    Toast.makeText(LoginActivity.this, "No Internet Connection!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
    }


    private class PostTask extends AsyncTask<String[], String, String> {

        public PostTask() {
        }

        //
        @Override
        protected String doInBackground(String[]... data) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            URL=prefs.getString(ConstantURLs.LOGIN_API_KEY, ConstantURLs.LOGIN_URL);
            HttpPost httppost = new HttpPost(URL);

            try {
                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", usernameText));
                nameValuePairs.add(new BasicNameValuePair("pass", passwordText));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //execute http post
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                mJson = EntityUtils.toString(httpEntity);

                System.out.println("sss" + response);

                Log.e("Response", mJson.toString());


                JSONArray jsonarray = new JSONArray(mJson);


                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject obj = jsonarray.getJSONObject(i);

                    String name = obj.getString("usrname");
                    String type = obj.getString("type");
                    String empName = obj.getString("empName");


                    Intent intent = new Intent(LoginActivity.this, MarchantInfoActivity.class);
                    intent.putExtra("usrname", name);
                    intent.putExtra("pass", passwordText);
                    intent.putExtra("type", type);
                    intent.putExtra("empName", empName);
                    startActivity(intent);

                }
                return "success";

            } catch (ClientProtocolException e) {
                pDialog.dismiss();
                return "Authentication Failed!";


            } catch (IOException e) {
                pDialog.dismiss();

                return "Authentication Failed!";
            } catch (JSONException e) {
                pDialog.dismiss();
                return "Authentication Failed!";
            }

        }

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            if (mJson.equals("[]")) {

                Toast.makeText(LoginActivity.this, "Authentication Failed!",
                        Toast.LENGTH_LONG).show();


            }

            if (result.equals("Authentication Failed!")) {

                Toast.makeText(LoginActivity.this, result,
                        Toast.LENGTH_LONG).show();

            }

        }
    }


}



