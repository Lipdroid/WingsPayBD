package com.example.com.wingsbangladesh.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.com.wingsbangladesh.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Mou on 2/26/2017.
 */


public class SettingActivity extends AppCompatActivity {

    String usernameText,passwordText,message,result;
    EditText loginApi,marchantApi,barcodeApi;
    String logins,marchant,barcode,barcodeType;
    String code,userid;
    String URL;
    Button update;
    Spinner s;
    int success;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();

        loginApi=(EditText)findViewById(R.id.login_api);
        marchantApi=(EditText)findViewById(R.id.marchant_api);
        barcodeApi=(EditText)findViewById(R.id.barcode_api);

        update=(Button) findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIPost();
            }
        });

        Intent intent=getIntent();
        userid = intent.getStringExtra("userid");


        String[] state = { "UPC-A", "ITF", "CODE 128" };

        s = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, state);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter_state);

        new GetData().execute();

    }

    private  class GetData extends AsyncTask<Void, Void, Boolean> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new SweetAlertDialog(SettingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... uRls) {

            restcall();

            return null;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            pDialog.dismiss();

        }

    }


    public void restcall(){


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

                                logins = jsonob.getString("login_api");
                                marchant = jsonob.getString("merchant_api");
                                barcode = jsonob.getString("barcode_api");
                                barcodeType = jsonob.getString("barcode_type");

                                System.out.println("SAAD::"+logins);

                                loginApi.setText(logins);
                                marchantApi.setText(marchant);
                                barcodeApi.setText(barcode);

                                if(barcodeType=="UPC-A"){
                                    s.setSelection(0);


                                }
                                else if(barcodeType=="ITF"){
                                    s.setSelection(1);

                                }

                                else if(barcodeType=="CODE 128"){

                                    s.setSelection(2);
                                }

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
        RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
        requestQueue.add(jsonObjReq);

    }


    public void APIPost() {
        Map<String, String> jsonParams = new HashMap<String, String>();

/*
        Api: http://analytico.co/madina/madina_project/madina_project/public/api/insert_dealer
        Keys: name,area,proprietor,address,mobile,general_com,target_com,
                other_com_package,security_status
*/


        // jsonParams.put("Authorization", token);
        jsonParams.put("userid", userid);
        jsonParams.put("login-api", loginApi.getText().toString());
        jsonParams.put("merchant_api", marchantApi.getText().toString());
        jsonParams.put("barcode_api", barcodeApi.getText().toString());
        jsonParams.put("barcode_type", s.getSelectedItem().toString());


        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.POST,
                "http://paperfly.mybdweb.com/update_settings.php",
                new JSONObject(jsonParams),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            success = response.getInt("success");

                               message = response.getString("message");

                           // result= response.getJSONObject("results");

                            //token = response.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if (success==1) {

                            Toast.makeText(SettingActivity.this, message,
                                    Toast.LENGTH_LONG).show();

                        }
                        else if(success==0){

                            Toast.makeText(SettingActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }


                        //  System.out.println("token:::" + token+"   :::");*/
                        //   verificationSuccess(response);

                        System.out.println("responseSAAD:::" + response + "   :::");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  verificationFailed(error);
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

        RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
        requestQueue.add(myRequest);
        //  MyApplication.getInstance().addToRequestQueue(myRequest, "tag");

    }


}






