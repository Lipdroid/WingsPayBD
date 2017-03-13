package com.example.com.wingsbangladesh.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.com.wingsbangladesh.Adapter.ConcernedPickUpAdapter;
import com.example.com.wingsbangladesh.Model.CustomModel;
import com.example.com.wingsbangladesh.Interface.ItemClickListener;
import com.example.com.wingsbangladesh.Model.ModelPrint;
import com.example.com.wingsbangladesh.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by sabbir on 2/22/17.
 */

public class ConcernedMarchantPickupActivity extends AppCompatActivity implements ItemClickListener {

    String URL, name, id;
    // ModelPrint m;
    ModelPrint print;
    private List<ModelPrint> modelPrintList = new ArrayList<>();
    // private List<ModelPickUpSummary> modellIst = new ArrayList<>();
    TextView user;
    CustomModel custom;
    private RecyclerView recyclerView;
    private ConcernedPickUpAdapter mAdapter;
    LinearLayout lnrLayout;
    Button logout;
    ImageView settings;
    String usertype, userid, barcodeApi, barcodeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.concerned_marchant_pickup_list);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        usertype = intent.getStringExtra("usertype");
        userid = intent.getStringExtra("userid");

        barcodeApi = intent.getStringExtra("barcodeApi");
        barcodeType = intent.getStringExtra("barcodeType");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        user = (TextView) findViewById(R.id.username);


        logout = (Button) findViewById(R.id.logout);
        settings = (ImageView) findViewById(R.id.setting);


        if (usertype.equals("1")) {

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
            }
        });

        getSupportActionBar().hide();


        user.setText(name);

        new GetData().execute();


        recyclerView.addOnItemTouchListener(new MarchantInfoActivity.RecyclerTouchListener(ConcernedMarchantPickupActivity.this, recyclerView, new MarchantInfoActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {


                print = new ModelPrint();
                print = modelPrintList.get(position);


                //Details
                Button btn = (Button) view.findViewById(R.id.button);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(ConcernedMarchantPickupActivity.this, BarcodeActivity.class);
                        intent.putExtra("id", print.getBarcodeId());
                        intent.putExtra("barcode_token", print.getBarCode());
                        intent.putExtra("barcodeApi", barcodeApi);
                        intent.putExtra("barcodeType", barcodeType);

                        startActivity(intent);


                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void restcall() {


        URL = barcodeApi;


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");

                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    String barcode;
                                    // mEntries.add(jsonObject.toString());

                                    if (barcodeType.equals("UPC-A Code")) {

                                        barcode = jsonObject.getString("barcode_upca");


                                    } else if (barcodeType.equals("CODE 128")) {
                                        barcode = jsonObject.getString("barcode_code128");

                                    } else {
                                        barcode = jsonObject.getString("barcode_itf");


                                    }


                                    String barcode_id = jsonObject.getString("barcode_id");
                                    String paperfy_order_id = jsonObject.getString("paperfy_order_id");

                                    String marchent_code = jsonObject.getString("marchent_code");

                                    System.out.println("marchent_code::" + marchent_code);

                                    ModelPrint m = new ModelPrint();
                                    m.setBarcodeId(barcode_id);
                                    m.setPaperFlyOrder(paperfy_order_id);
                                    m.setBarCode(barcode);
                                    m.setMarchentCode(marchent_code);


                                    System.out.println("barcode::" + barcode);

                                    modelPrintList.add(m);

                                    System.out.println("modelPrintList::" + modelPrintList);


                                    mAdapter = new ConcernedPickUpAdapter(modelPrintList);


                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);

                                    recyclerView.setAdapter(mAdapter);

                                } catch (JSONException e) {
                                    // mEntries.add("Error: " + e.getLocalizedMessage());
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
        RequestQueue requestQueue = Volley.newRequestQueue(ConcernedMarchantPickupActivity.this);
        requestQueue.add(jsonObjReq);

    }

    @Override
    public void onClick(View view, int position) {

    }


    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, ClickListener clickListener1) {
            clickListener = clickListener1;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private class GetData extends AsyncTask<Void, Void, Boolean> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new SweetAlertDialog(ConcernedMarchantPickupActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... uRls) {


            restcall();
            //   APICall();

            // uploadImage();

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

    }
}





