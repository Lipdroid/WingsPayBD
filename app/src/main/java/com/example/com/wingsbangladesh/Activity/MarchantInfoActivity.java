package com.example.com.wingsbangladesh.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.com.wingsbangladesh.Model.CustomModel;
import com.example.com.wingsbangladesh.Interface.ItemClickListener;
import com.example.com.wingsbangladesh.Adapter.MarchantInfoAdapter;
import com.example.com.wingsbangladesh.Model.ModelMarchantInfo;
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

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by sabbir on 2/22/17.
 */

public class MarchantInfoActivity extends AppCompatActivity implements  RecyclerView.OnItemTouchListener {

    String  URL,name;
    ModelMarchantInfo m;
    ModelMarchantInfo marchant;
    String marchantID;
    Button logout;
    String pickUpId;
    ImageView settings;
    private List<ModelMarchantInfo> marchantList = new ArrayList<>();
   // private List<ModelPickUpSummary> modellIst = new ArrayList<>();
    private List<CustomModel> customList = new ArrayList<>();
    private List<String> stringist = new ArrayList<>();
    CustomModel custom;
    private RecyclerView recyclerView;
    private MarchantInfoAdapter mAdapter;
    LinearLayout lnrLayout;
    String username,password,usertype,userName;
    TextView user;

//
    String loginApi,marchantApi,barcodeApi,barcodeType;

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


        Intent intent=getIntent();

        username = intent.getStringExtra("usrname");
        password = intent.getStringExtra("pass");
        usertype = intent.getStringExtra("type");
        userName = intent.getStringExtra("empName");


        user=(TextView)findViewById(R.id.username);
        user.setText(username);


        logout=(Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settings=(ImageView)findViewById(R.id.setting);

        if(usertype.equals("Employee")){

          //  settings.setVisibility(View.GONE);
        }


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MarchantInfoActivity.this,SettingActivity.class);
              //  intent.putExtra("userid",userid);
                startActivity(intent);

            }
        });




        getSupportActionBar().hide();

  new PostTask().execute();
}

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


/* private  class GetData extends AsyncTask<Void, Void, Boolean> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new SweetAlertDialog(MarchantInfoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... uRls) {



            APICall();
            APICall2();

            return null;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            //pDialog.setVisibility(View.INVISIBLE);
            //   prog.setVisibility(View.GONE);


            //   prog.setVisibility(View.GONE);
            pDialog.dismiss();





        }

    }*/


   /* private  class GetData extends AsyncTask<Void, Void, Boolean> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new SweetAlertDialog(MarchantInfoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... uRls) {



            APICall();
            APICall2();

            return null;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            //pDialog.setVisibility(View.INVISIBLE);
            //   prog.setVisibility(View.GONE);


            //   prog.setVisibility(View.GONE);
            pDialog.dismiss();





        }

    }*/

    @Override
    public void onBackPressed()
    {
    }

/*    public void APICall() {

        URL="http://paperfly.mybdweb.com/marchant_info.php";

        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                // mEntries.add(jsonObject.toString());


                                marchantID=jsonObject.getString("marchent_id");
                                name=jsonObject.getString("marchent_name");
                                String address=jsonObject.getString("marchent_address");
                                String phone=jsonObject.getString("marchent_phone1");



                                m=new ModelMarchantInfo();
                                m.setMarchent_id(marchantID);
                                m.setMarchent_name(name);
                                m.setMarchent_address(address);
                                m.setMarchent_phone1(phone);

                                marchantList.add(m);
                                stringist.add(name);

                            }
                            catch(JSONException e) {
                               // mEntries.add("Error: " + e.getLocalizedMessage());
                            }
                        }

                       // allDone();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MarchantInfoActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

       // mEntries = new ArrayList<>();
       // requestQueue.add(request);

// Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(MarchantInfoActivity.this);
        requestQueue.add(request);
    }


    public void APICall2() {

        URL="http://paperfly.mybdweb.com/pickup_summery_user.php";


        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                // mEntries.add(jsonObject.toString());

                                pickUpId=jsonObject.getString("pickup_id");

                                String total_order_count=jsonObject.getString("total_order_count");
                               String l_s_splus_order_count= jsonObject.getString("l_s_splus_order_count");
                               String express_order_count= jsonObject.getString("express_order_count");

                               *//* ModelPickUpSummary m=new ModelPickUpSummary();
                                m.setTotal_order_count(total_order_count);
                                m.setL_s_splus_order_count(l_s_splus_order_count);
                                m.setExpress_order_count(express_order_count);*//*


                               // System.out.println("marchantListPhone"+marchantList.get(i).getMarchent_phone1());


                                custom=new CustomModel();

                                if(stringist.size()!=0) {
                                    custom.setPickupId(pickUpId);
                                    custom.setMarchent_name(stringist.get(i));
                                    custom.setTotal_order_count(total_order_count);
                                    custom.setLs_order_count(l_s_splus_order_count);
                                    custom.setExpress_order_cont(express_order_count);
                                    customList.add(custom);
                                }


                                recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                                mAdapter = new MarchantInfoAdapter(customList,marchantList,usertype,userid,barcodeApi,barcodeType);





                                recyclerView.addOnItemTouchListener(
                                        new RecyclerItemClickListener(MarchantInfoActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                                            @Override public void onItemClick(View view, int position) {
                                                // TODO Handle item click



                                                marchant = new ModelMarchantInfo();
                                                marchant = marchantList.get(position);


                                                custom=new CustomModel();
                                                custom = customList.get(position);



*//*

                                                //Details
                                                lnrLayout = (LinearLayout) view.findViewById(R.id.name);

                                                lnrLayout.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        // Intent intent = new Intent(this, DetailLessonActivity.class);
                                                        //intent.putExtra("id", lesson.getId());
                                                        //      startActivity(intent);


                                                        final Dialog dialog = new Dialog(MarchantInfoActivity.this);
                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog.setContentView(R.layout.custom_dialog);
                                                        dialog.setTitle("Marchant Info");
                                                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


                                                        TextView    name = (TextView) dialog.findViewById(R.id.text1);
                                                        TextView  address = (TextView) dialog.findViewById(R.id.text2);
                                                        TextView   phone = (TextView) dialog.findViewById(R.id.text3);

                                                        String tempString=marchant.getMarchent_phone1();
                                                        //TextView text=(TextView)findViewById(R.id.text);
                                                        SpannableString spanString = new SpannableString(tempString);
                                                        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
                                                        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                                                        spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
                                                        phone.setText(spanString);

                                                        name.setText(marchant.getMarchent_name());
                                                        address.setText(marchant.getMarchent_address());
                                                        phone.setText(marchant.getMarchent_phone1());


                                                        phone.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {



                                                                try {


                                                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                                    //    callIntent.setPackage("com.android.server.telecom");


                                                                    callIntent.setData(Uri.parse("tel:" + marchant.getMarchent_phone1()));
                                                                    // callIntent.setData(Uri.parse(print.getMarchent_phone1()));

                                                                    if (ActivityCompat.checkSelfPermission(MarchantInfoActivity.this,
                                                                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                                                                        MarchantInfoActivity.this.startActivity(callIntent);
                                                                        // return;
                                                                    }
                                                                    else{

                                                                        //  Toast.makeText(getApplicationContext(), "Permission Required For calling", Toast.LENGTH_SHORT).show();

                                                                    }


                                                                } catch (Exception e) {
                                                                    // no activity to handle intent. show error dialog/toast whatever
                                                                }




                                                            }
                                                        });

                                                        ImageView add = (ImageView) dialog.findViewById(R.id.close);



                                                        add.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                dialog.dismiss();

                                                                //    dailyTourPlanlist.addAll(sharedPreferencesData.getSharedPrefDailyTourPlan(Daily_Visit_Plan));
                                                            }
                                                        });


                                                        dialog.show();


                                                    }
                                                });
*//*


                                                //send to prcatice


*//*   LinearLayout send = (LinearLayout) view.findViewById(R.id.next);

                                                send.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        Intent intent = new Intent(MarchantInfoActivity.this, ConcernedMarchantPickupActivity.class);
                                                        intent.putExtra("id",custom.getPickupId());
                                                        intent.putExtra("name",custom.getMarchent_name());
                                                        intent.putExtra("usertype",usertype);
                                                        intent.putExtra("userid",userid);
                                                        intent.putExtra("barcodeApi",barcodeApi);
                                                        intent.putExtra("barcodeType",barcodeType);


                                                        //  print.getMarchent_id();
                                                        startActivity(intent);


                                                    }
                                                });*//*
                                            }
                                        })
                                );

                                //////////////////////////

                           *//*

                                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(MarchantInfoActivity.this,new RecyclerItemClickListener.OnItemClickListener() { {
                                    @Override
                                    public void onItemClick(View view, int position) {



                                        marchant = new ModelMarchantInfo();
                                        marchant = marchantList.get(position);


                                        custom=new CustomModel();
                                        custom = customList.get(position);




                                        //Details
                                        lnrLayout = (LinearLayout) view.findViewById(R.id.name);

                                        lnrLayout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // Intent intent = new Intent(this, DetailLessonActivity.class);
                                                //intent.putExtra("id", lesson.getId());
                                                //      startActivity(intent);


                                                final Dialog dialog = new Dialog(MarchantInfoActivity.this);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.custom_dialog);
                                                dialog.setTitle("Marchant Info");
                                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


                                                TextView    name = (TextView) dialog.findViewById(R.id.text1);
                                                TextView  address = (TextView) dialog.findViewById(R.id.text2);
                                                TextView   phone = (TextView) dialog.findViewById(R.id.text3);

                                                String tempString=marchant.getMarchent_phone1();
                                                //TextView text=(TextView)findViewById(R.id.text);
                                                SpannableString spanString = new SpannableString(tempString);
                                                spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
                                                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                                                spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
                                                phone.setText(spanString);

                                                name.setText(marchant.getMarchent_name());
                                                address.setText(marchant.getMarchent_address());
                                                phone.setText(marchant.getMarchent_phone1());


                                                phone.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {



                                                        try {


                                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                        //    callIntent.setPackage("com.android.server.telecom");


                                                            callIntent.setData(Uri.parse("tel:" + marchant.getMarchent_phone1()));
                                                           // callIntent.setData(Uri.parse(print.getMarchent_phone1()));

                                                            if (ActivityCompat.checkSelfPermission(MarchantInfoActivity.this,
                                                                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                                                                MarchantInfoActivity.this.startActivity(callIntent);
                                                               // return;
                                                            }
                                                            else{

                                                              //  Toast.makeText(getApplicationContext(), "Permission Required For calling", Toast.LENGTH_SHORT).show();

                                                            }


                                                        } catch (Exception e) {
                                                            // no activity to handle intent. show error dialog/toast whatever
                                                        }




                                                    }
                                                });

                                                ImageView add = (ImageView) dialog.findViewById(R.id.close);



                                                        add.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                dialog.dismiss();

                                                                //    dailyTourPlanlist.addAll(sharedPreferencesData.getSharedPrefDailyTourPlan(Daily_Visit_Plan));
                                                            }
                                                        });


                                                dialog.show();


                                            }
                                        });


            //send to prcatice

                LinearLayout send = (LinearLayout) view.findViewById(R.id.next);

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(MarchantInfoActivity.this, ConcernedMarchantPickupActivity.class);
                        intent.putExtra("id",custom.getPickupId());
                        intent.putExtra("name",custom.getMarchent_name());
                        intent.putExtra("usertype",usertype);
                        intent.putExtra("userid",userid);
                        intent.putExtra("barcodeApi",barcodeApi);
                        intent.putExtra("barcodeType",barcodeType);


                      //  print.getMarchent_id();
                        startActivity(intent);


                    }
                });



                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {

                                    }
                                }));
*//*

                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(mLayoutManager);

                                recyclerView.setAdapter(mAdapter);


                            }
                            catch(JSONException e) {
                                // mEntries.add("Error: " + e.getLocalizedMessage());
                            }
                        }



                        // allDone();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MarchantInfoActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // mEntries = new ArrayList<>();
        // requestQueue.add(request);

// Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(MarchantInfoActivity.this);
        requestQueue.add(request);
    }*/


   /* public void urlCall(){


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
        RequestQueue requestQueue = Volley.newRequestQueue(MarchantInfoActivity.this);
        requestQueue.add(jsonObjReq);

    }*/




    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;


    private class PostTask extends AsyncTask<String[], String, String> {

        public PostTask() {
        }

        //
        @Override
        protected String doInBackground(String[]... data) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://paperfly.com.bd/merchantAPI.php");

            try {

                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("pass", password));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity httpEntity = response.getEntity();
                String mJson = EntityUtils.toString(httpEntity);

                Log.e("MarchantResponse", mJson.toString());

                JSONArray jsonarray = new JSONArray(mJson);


             /*   for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject obj = jsonarray.getJSONObject(i);

                    String name = obj.getString("usrname");
                    String type = obj.getString("type");
                    String empName = obj.getString("empName");


                    Intent intent=new Intent();
                    intent.putExtra("usrname",name);
                    intent.putExtra("type",type);
                    intent.putExtra("empName",empName);
                    startActivity(intent);

                }*/


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




