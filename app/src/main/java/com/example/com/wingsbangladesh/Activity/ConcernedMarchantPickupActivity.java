package com.example.com.wingsbangladesh.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.com.wingsbangladesh.Adapter.ConcernedPickUpAdapter;
import com.example.com.wingsbangladesh.Model.ModelBarcodeList;
import com.example.com.wingsbangladesh.R;
import com.example.com.wingsbangladesh.util.ConnectionDetector;
import com.example.com.wingsbangladesh.util.ConstantURLs;

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


public class ConcernedMarchantPickupActivity extends AppCompatActivity {

    private List<ModelBarcodeList> modelBarcodeList = new ArrayList<>();
    TextView user;
    private RecyclerView recyclerView;
    private ConcernedPickUpAdapter mAdapter;
    LinearLayout lnrLayout;
    Button logout;
    ImageView settings;
    String mJson;
    String usertype, userid, marchantcode, username, password, employeeName;
    private ConnectionDetector cd = null;
    String URL= "http://paperfly.com.bd/barcodeList.php";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.concerned_marchant_pickup_list);

        cd = new ConnectionDetector(this);
        prefs = getSharedPreferences(ConstantURLs.PREF_NAME, Context.MODE_PRIVATE);

        Intent intent = getIntent();

        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        usertype = intent.getStringExtra("usertype");
        marchantcode = intent.getStringExtra("marchantcode");
        employeeName = intent.getStringExtra("employeeName");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        user = (TextView) findViewById(R.id.username);


        logout = (Button) findViewById(R.id.logout);
        settings = (ImageView) findViewById(R.id.setting);


        if (usertype.equals("Employee")) {

            settings.setVisibility(View.GONE);
        }


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConcernedMarchantPickupActivity.this, SettingActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);


            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ConcernedMarchantPickupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().hide();

        if(employeeName!=null) {
            user.setText(employeeName);
        }
        if (cd.isConnectingToInternet()) {

            new PostTask().execute();
        } else {

            Toast.makeText(ConcernedMarchantPickupActivity.this, "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private class PostTask extends AsyncTask<String[], String, String> {

        public PostTask() {
        }

        //
        @Override
        protected String doInBackground(String[]... data) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();

            URL = prefs.getString(ConstantURLs.BARCODE_LIST_API_KEY, ConstantURLs.BARCODE_LIST_URL);

            HttpPost httppost = new HttpPost(URL);

            try {

                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("pass", password));
                nameValuePairs.add(new BasicNameValuePair("merchantCode", marchantcode));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity httpEntity = response.getEntity();
                mJson = EntityUtils.toString(httpEntity);

                Log.e("saadResponse", mJson.toString());


                JSONArray jsonarray = new JSONArray(mJson);


                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject obj = jsonarray.getJSONObject(i);


                    String orderid = obj.getString("orderid");
                    String barcode = obj.getString("barcode");
                    String merchantCode = obj.getString("merchantCode");
                    String merOrderRef = obj.getString("merOrderRef");
                    String packagePrice = obj.getString("packagePrice");
                    String phone = obj.getString("phone");


                    ModelBarcodeList m = new ModelBarcodeList();
                    //  m.setBarcodeId(barcode_id);
                    m.setOrderid(orderid);
                    m.setBarcode(barcode);
                    m.setMerchantCode(merchantCode);
                    m.setMerOrderRef(merOrderRef);
                    m.setPackagePrice(packagePrice);
                    m.setPhone(phone);


                    modelBarcodeList.add(m);


                }
                return "success";


            } catch (ClientProtocolException e) {
                return "No data!";
            } catch (IOException e) {
                return  "No data!";
            } catch (JSONException e) {
                return  "No data!";
            }

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (mJson.equals("[]")) {

                Toast.makeText(ConcernedMarchantPickupActivity.this, "No data!",
                        Toast.LENGTH_LONG).show();


            }
            if(result.equals("success")) {

                mAdapter = new ConcernedPickUpAdapter(modelBarcodeList);
                Log.e("saadtest", modelBarcodeList.toString());
                recyclerView.setAdapter(mAdapter);
            }
            else {
                Toast.makeText(ConcernedMarchantPickupActivity.this, result,
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}





