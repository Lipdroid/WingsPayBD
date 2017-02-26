package com.example.com.wingsbangladesh.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.com.wingsbangladesh.Model.ModelBarcode;
import com.example.com.wingsbangladesh.R;
import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.AndroidFont;
import com.onbarcode.barcode.android.Code128;
import com.onbarcode.barcode.android.IBarcode;
import com.onbarcode.barcode.android.ITF14;
import com.onbarcode.barcode.android.UPCA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mou on 2/25/2017.
 */

public class BarcodeActivity extends AppCompatActivity {


    String id;
    String URL;
    Button login;
    ModelBarcode m;
    private List<ModelBarcode> barcodeList = new ArrayList<>();
    TextView paperfy_order_id,marchent_ref,marchent_code,product_price,customer_phone,marchent_code2;
    String barcode_token;
    String UPCdata = "01011700062";
    //ITF24 Valid data length: 13 digits only, excluding the last checksum digit
    String ITFData = "0123456789012";
    String code128 = "112233445566";

    String paperfyorderid;
    String marchentref;
    String marchentcode;
    String productprice;
    String customerphone;
    String barcode;

    ImageView barcode_imageView = null;

    ImageView bmImage;
    LinearLayout view;
    Button print;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_print);

        Intent intent=getIntent();
        barcode_token = intent.getStringExtra("barcode_token");
        id = intent.getStringExtra("id");


        barcode_imageView = (ImageView) findViewById(R.id.barcode_img);

        getSupportActionBar().hide();


        findViewById();
        APICall();
        generateUPCACode(barcode_token);

    }

    public void APICall() {

        URL="http://paperfly.mybdweb.com/barcode_list.php/"+id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                //  for(int i = 0; i < jsonArray.length(); i++) {
                try {
                    //  JSONObject a = jsonObject.getJSONObject();
                    // mEntries.add(jsonObject.toString());


                     barcode=jsonObject.getString("barcode");
                     paperfyorderid=jsonObject.getString("paperfy_order_id");
                     marchentref=jsonObject.getString("marchent_ref");
                     marchentcode=jsonObject.getString("marchent_code");
                     productprice=jsonObject.getString("product_price");
                     customerphone=jsonObject.getString("customer_phone");


                    //TextView paperfy_order_id,marchent_ref,marchent_code,product_price,customer_phone,marchent_code2;

                    view = (LinearLayout)findViewById(R.id.barcodeid);
                    bmImage = (ImageView)findViewById(R.id.saad);

                    view.setDrawingCacheEnabled(true);
                    // this is the important code :)
                    // Without it the view will have a dimension of 0,0 and the bitmap will be null

                    paperfy_order_id.setText(paperfyorderid);
                    marchent_ref.setText(marchentref);
                    marchent_code.setText(marchentcode);
                    marchent_code2.setText(barcode);
                    product_price.setText(productprice);
                    customer_phone.setText(customerphone);

                    view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

                    view.buildDrawingCache(true);
                    Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
                    view.setDrawingCacheEnabled(false); // clear drawing cache

                    bmImage.setImageBitmap(b);

                    view.setVisibility(View.GONE);




                   /*
                    m=new ModelBarcode();
                    m.setBarcode(barcodes);
                    m.setPaperfy_order_id(paperfy_order_id);
                    m.setMarchent_ref(marchent_ref);
                    m.setMarchent_code(marchent_code);
                    m.setProduct_price(product_price);
                    m.setCustomer_phone(customer_phone);



                    barcodeList.add(m);
*/
                }
                catch(JSONException e) {
                    // mEntries.add("Error: " + e.getLocalizedMessage());
                }
            }

            // allDone();

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //   Toast.makeText(MarchantInfoActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // mEntries = new ArrayList<>();
        // requestQueue.add(request);

// Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(BarcodeActivity.this);
        requestQueue.add(request);
    }

    public  void findViewById(){



        //barcodes=(TextView)findViewById(R.id.barcodes);
        paperfy_order_id=(TextView)findViewById(R.id.paperfly_order_id);


        marchent_ref=(TextView)findViewById(R.id.marchant_ref);
        marchent_code2=(TextView)findViewById(R.id.marchant_code2);
        marchent_code=(TextView)findViewById(R.id.marchant_code);


        product_price=(TextView)findViewById(R.id.product_price);
        customer_phone=(TextView)findViewById(R.id.customer_phone);


    }

   private void generateCode128Code(String Data) {
        //Create an image for drawing the barcodes
        Bitmap barcode_image = Bitmap.createBitmap(230, 150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(barcode_image);

        Code128 barcodes = new Code128();


        barcodes.setData(Data);

        //  Set the processTilde property to true, if you want use the tilde character "~"
        //  to specify special characters in the input data. Default is false.
        //  1) All 128 ISO/IEC 646 characters, i.e. characters 0 to 127 inclusive, in accordance with ISO/IEC 646.
        //       NOTE This version consists of the G0 set of ISO/IEC 646 and the C0 set of ISO/IEC 6429 with values 28 - 31
        //       modified to FS, GS, RS and US respectively.
        //  2) Characters with byte values 128 to 255 may also be encoded.
        //  3) 4 non-data function characters.
        //  4) 4 code set selection characters.
        //  5) 3 Start characters.
        //  6) 1 Stop character.
        barcodes.setProcessTilde(false);

        // Unit of Measure, pixel, cm, or inch
        barcodes.setUom(IBarcode.UOM_PIXEL);
        // barcodes bar module width (X) in pixel
        barcodes.setX(1f);
        // barcodes bar module height (Y) in pixel
        barcodes.setY(75f);

        // barcodes image margins
        barcodes.setLeftMargin(10f);
        barcodes.setRightMargin(10f);
        barcodes.setTopMargin(10f);
        barcodes.setBottomMargin(10f);

        // barcodes image resolution in dpi
        barcodes.setResolution(72);

        // disply barcodes encoding data below the barcodes
        barcodes.setShowText(true);
        // barcodes encoding data font style
        barcodes.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 12));
        // space between barcodes and barcodes encoding data
        barcodes.setTextMargin(6);
        barcodes.setTextColor(AndroidColor.black);

        // barcodes bar color and background color in Android device
        barcodes.setForeColor(AndroidColor.black);
        barcodes.setBackColor(AndroidColor.white);


        RectF bounds = new RectF(30, 30, 0, 0);
        try {
            barcodes.drawBarcode(canvas, bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
       barcode_imageView.setImageBitmap(barcode_image);
    }

    private void generateITFCode(String Data) {
        //Create an image for drawing the barcodes
        Bitmap barcode_image = Bitmap.createBitmap(230, 150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(barcode_image);

        ITF14 barcode = new ITF14();
        barcode.setData(Data);

        // ITF-14 Wide Narrow bar Ratio
        // Valid value is from 2.0 to 3.0 inclusive.
        barcode.setN(3.0f);

        // ITF-14 bearer bar size vs bar module (X) size ratio
        // Valid values are 0-10 which are a multiple of X.
        barcode.setBearerBarHori(1);
        barcode.setBearerBarVert(1);

        // Unit of Measure, pixel, cm, or inch
        barcode.setUom(IBarcode.UOM_PIXEL);
        // barcodes bar module width (X) in pixel
        barcode.setX(1f);
        // barcodes bar module height (Y) in pixel
        barcode.setY(75f);

        // barcodes image margins
        barcode.setLeftMargin(10f);
        barcode.setRightMargin(10f);
        barcode.setTopMargin(10f);
        barcode.setBottomMargin(10f);

        // barcodes image resolution in dpi
        barcode.setResolution(72);

        // disply barcodes encoding data below the barcodes
        barcode.setShowText(true);
        // barcodes encoding data font style
        barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 12));
        // space between barcodes and barcodes encoding data
        barcode.setTextMargin(6);
        barcode.setTextColor(AndroidColor.black);

        // barcodes bar color and background color in Android device
        barcode.setForeColor(AndroidColor.black);
        barcode.setBackColor(AndroidColor.white);

        RectF bounds = new RectF(30, 30, 0, 0);
        try {
            barcode.drawBarcode(canvas, bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        barcode_imageView.setImageBitmap(barcode_image);
    }

    private void generateUPCACode(String data) {
        //Create an image for drawing the barcodes
        Bitmap barcode_image = Bitmap.createBitmap(230, 150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(barcode_image);

        UPCA barcode = new UPCA();

        barcode.setData(data);

        barcode.setSupData("12");
        // supplement bar height vs bar height ratio
        barcode.setSupHeight(0.8f);
        // space between barcodes and supplement barcodes (in pixel)
        barcode.setSupSpace(15);


        // Unit of Measure, pixel, cm, or inch
        barcode.setUom(IBarcode.UOM_PIXEL);
        // barcodes bar module width (X) in pixel
        barcode.setX(2f);
        // barcodes bar module width (Y) in pixel
        barcode.setY(100f);
        // barcodes image margins
        barcode.setLeftMargin(0f);
        barcode.setRightMargin(0f);
        barcode.setTopMargin(0f);
        barcode.setBottomMargin(1f);

        barcode.setBarAlignment(1);
        // barcodes image resolution in dpi
        barcode.setResolution(720);
        // disply barcodes encoding data below the barcodes
        barcode.setShowText(true);
        // barcodes encoding data font style
        barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 12));
        // space between barcodes and barcodes encoding data
        barcode.setTextMargin(6);
        barcode.setTextColor(AndroidColor.black);
        // barcodes bar color and background color in Android device
        barcode.setForeColor(AndroidColor.black);
        barcode.setBackColor(AndroidColor.white);

        RectF bounds = new RectF(0, 0, 0, 0);
        try {
            barcode.drawBarcode(canvas, bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        barcode_imageView.setImageBitmap(barcode_image);



        Paint paint = new Paint();

        //canvas.drawColor(Color.GREEN);

        Bitmap b = Bitmap.createBitmap(200, 200, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(b);
        c.drawRect(0, 0, 400, 400, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setTextSize(40);
        paint.setTextScaleX(1.f);
        paint.setAlpha(0);
        paint.setAntiAlias(true);
        // c.drawText("Your text2", 30, 40, paint);
        c.drawText("Your text", 200, 200, paint);

        paint.setColor(Color.RED);


        //  canvas.drawBitmap(b, 10,10, paint);

    }


}



