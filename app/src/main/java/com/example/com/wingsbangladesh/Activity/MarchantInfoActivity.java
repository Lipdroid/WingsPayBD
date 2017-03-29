package com.example.com.wingsbangladesh.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.com.wingsbangladesh.Adapter.MarchantInfoAdapter;
import com.example.com.wingsbangladesh.Model.MarchantModel;
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
import java.util.List;


public class MarchantInfoActivity extends AppCompatActivity  {

    String merchantCode;
    private String URL="http://paperfly.com.bd/merchantAPI.php";
    Button logout;
    ImageView settings;
    private List<MarchantModel> marchantList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MarchantInfoAdapter mAdapter;
    String username, password, usertype, employeeName;
    TextView user;
    String mJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_marchant_info);

        ActivityCompat.requestPermissions(MarchantInfoActivity.this,
                new String[]{Manifest.permission.CALL_PHONE},
                1);


        Intent intent = getIntent();

        username = intent.getStringExtra("usrname");
        password = intent.getStringExtra("pass");
        usertype = intent.getStringExtra("type");
        employeeName = intent.getStringExtra("empName");


        user = (TextView) findViewById(R.id.username);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        user.setText(employeeName);


        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settings = (ImageView) findViewById(R.id.setting);

        if (usertype.equals("Employee")) {

             settings.setVisibility(View.GONE);
        }


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MarchantInfoActivity.this, SettingActivity.class);
                //  intent.putExtra("userid",userid);
                startActivity(intent);

            }
        });


        getSupportActionBar().hide();

        new PostTask().execute();


    }




    private class PostTask extends AsyncTask<String[], String, String> {

        public PostTask() {
        }

        //
        @Override
        protected String doInBackground(String[]... data) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {

                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("pass", password));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity httpEntity = response.getEntity();
                 mJson = EntityUtils.toString(httpEntity);

                Log.e("MarchantResponse", mJson.toString());

                if (mJson.toString().length() == 0) {

                    Toast.makeText(MarchantInfoActivity.this, "APi Response Failed,Please try Again!",
                            Toast.LENGTH_LONG).show();


                }

                JSONArray jsonarray = new JSONArray(mJson);


                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject obj = jsonarray.getJSONObject(i);

                    merchantCode = obj.getString("merchantCode");
                    String merchantName = obj.getString("merchantName");
                    String address = obj.getString("address");
                    String companyPhone = obj.getString("companyPhone");
                    String totorder = obj.getString("totorder");
                    String large = obj.getString("large");
                    String express = obj.getString("express");


                    MarchantModel m = new MarchantModel();
                    m.setMerchantCode(merchantCode);
                    m.setMerchantName(merchantName);
                    m.setAddress(address);
                    m.setCompanyPhone(companyPhone);
                    m.setTotorder(totorder);
                    m.setLarge(large);
                    m.setExpress(express);

                    marchantList.add(m);


                    if(marchantList.size()==0){
                        Toast.makeText(MarchantInfoActivity.this, "APi Response Failed,Please try Again!",
                                Toast.LENGTH_LONG).show();


                    }

                }


            } catch (ClientProtocolException e) {

                Toast.makeText(MarchantInfoActivity.this, "APi Response Failed,Please try Again!",
                        Toast.LENGTH_LONG).show();
                Log.e("Response", e.toString());
            } catch (IOException e) {

                Toast.makeText(MarchantInfoActivity.this, "APi Response Failed,Please try Again!",
                        Toast.LENGTH_LONG).show();
                Log.e("Response", e.toString());
            } catch (JSONException e) {

                Toast.makeText(MarchantInfoActivity.this, "APi Response Failed,Please try Again!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (mJson .equals("[]") ) {

                Toast.makeText(MarchantInfoActivity.this, "Login Failed,Please try Again!",
                        Toast.LENGTH_LONG).show();


            }


            mAdapter = new MarchantInfoAdapter(marchantList, username, password, usertype, merchantCode, employeeName);
            recyclerView.setAdapter(mAdapter);


        }
    }


}




