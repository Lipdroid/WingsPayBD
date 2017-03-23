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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    String usernameText, passwordText;
    EditText username, password;
    String code;
     String URL="http://paperfly.com.bd/la.php";
    Button login;
    String loginApi, marchantApi, barcodeApi, barcodeType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

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

                new PostTask().execute();

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
            HttpPost httppost = new HttpPost("http://paperfly.com.bd/la.php");

            try {
                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", usernameText));
                nameValuePairs.add(new BasicNameValuePair("pass", passwordText));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity httpEntity = response.getEntity();
                String mJson = EntityUtils.toString(httpEntity);

                Log.e("Response", mJson.toString());

                JSONArray jsonarray = new JSONArray(mJson);


                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject obj = jsonarray.getJSONObject(i);

                    String name = obj.getString("usrname");
                    String pass = obj.getString("type");
                    String empName = obj.getString("empName");

                    System.out.println("saadname" + name);
                    System.out.println("saadpass" + pass);
                    System.out.println("empName" + pass);
                }


            } catch (ClientProtocolException e) {
                Log.e("Response", e.toString());
            } catch (IOException e) {
                Log.e("Response", e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }
    }


}



