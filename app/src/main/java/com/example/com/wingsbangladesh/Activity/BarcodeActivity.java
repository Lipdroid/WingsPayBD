package com.example.com.wingsbangladesh.Activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.com.wingsbangladesh.Model.ModelBarcode;
import com.example.com.wingsbangladesh.util.P25ConnectionException;
import com.example.com.wingsbangladesh.util.P25Connector;
import com.example.com.wingsbangladesh.R;
import com.example.com.wingsbangladesh.pockdata.PocketPos;
import com.example.com.wingsbangladesh.util.FontDefine;
import com.example.com.wingsbangladesh.util.PrintTools_58mm;
import com.example.com.wingsbangladesh.util.Printer;
import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.AndroidFont;
import com.onbarcode.barcode.android.Code128;
import com.onbarcode.barcode.android.IBarcode;
import com.onbarcode.barcode.android.ITF14;
import com.onbarcode.barcode.android.UPCA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    Bitmap targetImage;

    ImageView barcode_imageView = null;

    ImageView bmImage;
    LinearLayout view;
    private Button mConnectBtn;
    private Button mEnableBtn;
    private Button mPrintBarcodeBtn;
    private Spinner mDeviceSp;
    String barcodeApi;


    private ProgressDialog mProgressDlg;
    private ProgressDialog mConnectingDlg;

    private BluetoothAdapter mBluetoothAdapter;

    private P25Connector mConnector;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();


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
        barcodeApi = intent.getStringExtra("barcodeApi");


        barcode_imageView = (ImageView) findViewById(R.id.barcode_img);

        getSupportActionBar().hide();


        findViewById();
        restcall();
        //APICall();
        generateUPCACode(UPCdata);

        ///////////



        mConnectBtn			= (Button) findViewById(R.id.btn_connect);
        mEnableBtn			= (Button) findViewById(R.id.btn_enable);
        mPrintBarcodeBtn 	= (Button) findViewById(R.id.btn_print_barcode);
        mDeviceSp 			= (Spinner) findViewById(R.id.sp_device);

        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showDisabled();
            } else {
                showEnabled();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    mDeviceList.addAll(pairedDevices);

                    updateDeviceList();
                }
            }

            mProgressDlg 	= new ProgressDialog(this);

            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg 	= new ProgressDialog(this);

            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            mConnector 		= new P25Connector(new P25Connector.P25ConnectionListener() {

                @Override
                public void onStartConnecting() {
                    mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    mConnectingDlg.dismiss();

                    showConnected();
                }

                @Override
                public void onConnectionFailed(String error) {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onConnectionCancelled() {
                    mConnectingDlg.dismiss();
                }

                @Override
                public void onDisconnected() {
                    showDisonnected();
                }
            });

            //enable bluetooth
            mEnableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    startActivityForResult(intent, 1000);
                }
            });

            //connect/disconnect
            mConnectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    connect();
                }
            });

            //print demo text


            //print barcode 1D or 2D
            mPrintBarcodeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    printBarcode();
                }
            });

        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, filter);


    }




    public void restcall(){


        URL=barcodeApi+id;


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            int success=  response.getInt("success");
                            String message=  response.getString("message");

                            JSONArray jsonArray=response.getJSONArray("results");
                            for(int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    //  JSONObject a = jsonObject.getJSONObject();
                                    // mEntries.add(jsonObject.toString());

                                    JSONObject jsonObject=jsonArray.getJSONObject(i);

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
                                     targetImage = Bitmap.createBitmap(view.getDrawingCache());
                                    view.setDrawingCacheEnabled(false); // clear drawing cache

                                    bmImage.setImageBitmap(targetImage);

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
        RequestQueue requestQueue = Volley.newRequestQueue(BarcodeActivity.this);
        requestQueue.add(jsonObjReq);

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            mBluetoothAdapter.startDiscovery();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        if (mConnector != null) {
            try {
                mConnector.disconnect();
            } catch (P25ConnectionException e) {
                e.printStackTrace();
            }
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size	= data.size();
        list		= new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, getArray(mDeviceList));

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        mDeviceSp.setAdapter(adapter);
        mDeviceSp.setSelection(0);
    }

    private void showDisabled() {
        showToast("Bluetooth disabled");

        mEnableBtn.setVisibility(View.VISIBLE);
        mConnectBtn.setVisibility(View.GONE);
        mDeviceSp.setVisibility(View.GONE);
    }

    private void showEnabled() {
        showToast("Bluetooth enabled");

        mEnableBtn.setVisibility(View.GONE);
        mConnectBtn.setVisibility(View.VISIBLE);
        mDeviceSp.setVisibility(View.VISIBLE);
    }

    private void showUnsupported() {
        showToast("Bluetooth is unsupported by this device");

        mConnectBtn.setEnabled(false);
        mPrintBarcodeBtn.setEnabled(false);
        mDeviceSp.setEnabled(false);
    }

    private void showConnected() {
        showToast("Connected");

        mConnectBtn.setText("Disconnect");

        mPrintBarcodeBtn.setEnabled(true);

        mDeviceSp.setEnabled(false);
    }

    private void showDisonnected() {
        showToast("Disconnected");

        mConnectBtn.setText("Connect");

        mPrintBarcodeBtn.setEnabled(false);

        mDeviceSp.setEnabled(true);
    }

    private void connect() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = mDeviceList.get(mDeviceSp.getSelectedItemPosition());

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(device);
            } catch (Exception e) {
                showToast("Failed to pair device");

                return;
            }
        }

        try {
            if (!mConnector.isConnected()) {
                mConnector.connect(device);
            } else {
                mConnector.disconnect();

                showDisonnected();
            }
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }

    private void createBond(BluetoothDevice device) throws Exception {

        try {
            Class<?> cl 	= Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par 	= {};

            Method method 	= cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    private void sendData(byte[] bytes) {
        try {
            mConnector.sendData(bytes);
        } catch (P25ConnectionException e) {
            e.printStackTrace();
        }
    }


    private void print1DBarcode() {
        String content	= UPCdata;

        //1D barcode format (hex): 1d 6b 02 0d + barcode data

        byte[] formats	= {(byte) 0x1d, (byte) 0x6b, (byte) 0x02, (byte) 0x0d};
        byte[] contents	= content.getBytes();

        byte[] bytes	= new byte[formats.length + contents.length];

        System.arraycopy(formats, 0, bytes, 0, formats.length);
        System.arraycopy(contents, 0, bytes, formats.length, contents.length);

        sendData(bytes);

        byte[] newline 	= Printer.printfont("\n\n", FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A, PocketPos.LANGUAGE_ENGLISH);

        sendData(newline);
    }



    private void printBarcode() {

       // print1DBarcode();

        printImage();

    }



    private void printImage() {
        try {
  /*          //image must be in monochrome bitmap
            //format: 1B 58 31 0B 30 + image data
            //where = 1B 58 31 = image format
            //   0B 30 = width x height (tes.bmp: 84x48 pixel)
            //   0B = image width/8 -> 84/8 = 11 (in decimal) -> 0B (in hex)
            //   30 = image height = 48 (in decimal) -> 30 in hexa
            //see: http://bluebamboo.helpserve.com/index.php?/Knowledgebase/Article/View/48

            //byte[] formats	= {(byte) 0x1B, (byte) 0x58, (byte) 0x34, (byte) 0x35, (byte) 0x118	};
            //byte[] image 	= FileOperation.getBytesFromAssets(MainActivity.this, "tes.bmp");
            //byte[] image	= StringUtil.hexStringToBytes("00 00 00 00 FF FF FF 00 FF FF FF FF FE FF FF FF FF FF FF FF FF 80 00 00 00 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 01 01 0F FF FF FE FE FF FE FE FE FE FF FF FE FF FE FE FF F0 00 01 00 FF FE FE FF FF FE FF FE FF FE FE FE FF 81 01 00 00 7F FE FF FF FE FF FE FF FE FE FE FF FF FE FE FF 80 01 1E FE FF FF 7F FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 00 01 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 00 07 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 00 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 03 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC 01 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF F8 00 07 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 03 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF F8 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C7 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF E3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FE 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FE 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FE 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FE 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF C7 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF C7 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF C3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF E3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF F1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF F1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 87 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 87 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 87 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 9F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 9F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF BF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF CF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF EF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E7 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F9 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FD FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 07 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 07 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 03 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 01 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF F9 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 01 FF FF FF FF FF FF FF FF FF FF FF FF FF FF F1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 00 0F FF FF FF FF FF FF FF FF FF FF FF FF FF E1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 01 FF FF FF FF FF FF FF FF FF FF FF FF C1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF 81 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 00 00 00 00 01 FF FF FF FF FF FF FF FF FF FE 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 00 00 00 00 00 3F FF FF FF F1 FF FF FF FF FC 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 00 00 00 00 00 03 FF FF FF 80 7F FF FF FF FC 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 7F FF FE 00 3F FF FF FF F8 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 0F FF FC 00 3F FF FF FF F0 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 01 FF E0 00 1F FF FF FF E0 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 1F 00 00 0F FF FF FF C0 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF 80 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 07 FF FF FF 80 00 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FE 00 00 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 01 FF FF FE 00 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 01 FF FF FC 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 FF FF F8 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 7F FF E0 00 00 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF C0 00 00 07 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF 80 00 00 03 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FE 00 00 00 01 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 00 00 07 F0 00 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 03 FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 0F FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 1F FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 7F FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 7F FF FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F0 07 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 01 FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 FE 07 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF F8 3F 80 00 00 00 00 00 00 00 01 FC 1F FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF F0 7F C0 00 00 00 00 00 00 00 03 F8 07 FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF F1 FF E0 00 00 00 00 00 00 00 07 F0 03 FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF E3 FF F0 00 00 00 00 00 00 00 07 E0 01 FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF C7 FF F0 00 00 00 00 00 00 00 0F E0 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 8F FF F8 00 00 00 00 00 00 00 0F C0 00 FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 0F FF F8 00 00 00 00 00 00 00 1F C0 00 7F FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FE 1F FF FC 00 00 00 00 00 00 00 3F C0 00 7F FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FF C7 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FC 7F FF FE 00 00 00 00 00 00 00 7F E0 00 7F FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FE 03 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF F8 FF FF FE 00 00 00 00 00 00 00 7F FE 01 FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F FC 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF F1 FF FF FF 00 00 00 00 00 00 01 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F F8 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 01 FF FF FF FF FF FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F F0 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 03 FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F E0 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 07 FF FF FF FF FF FF FF FF F0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F E0 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF C0 00 00 03 FF 80 0F FF FF FF FF FF FF FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F E0 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 93 FF FF C0 00 00 0F FF E0 1F FF FF FF FF FF FF FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F E0 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 3F FF E0 00 00 7F FF F0 3F FF FF FF FF FF FF FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F E0 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 0F FF E0 00 01 FF FF FC 3F FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F E0 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 07 FF E0 00 03 FF FF FE 7F FF FF FF FF FF FF FF FF FF 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3F E0 03 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 07 FF C0 00 07 FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F F0 03 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 07 FF 80 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1F F0 03 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 03 FF 00 00 7F FF FF FF FF FF FF FF FF FF 80 3F FF FF F8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F F0 03 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 03 FF 00 1F FF FF FF FF FF FF FF FF FF FF 00 1F FF FF FC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 F8 01 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 03 FE 00 7F FF FF FF FF FF FF FF FF FF FE 00 0F FF FF FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 F8 00 FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 01 FE 01 FF FF FF FF FF FF F7 FF FF FF FC 00 07 FF FF FF 00 00 00 00 00 00 7F FF FC 00 00 00 00 00 03 F8 00 7F FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 01 FC 03 FF FF FF FF FF FF E3 FF FF FF F8 00 07 FF FF FF 80 00 00 00 00 01 FF FF FF 80 00 00 00 00 03 FC 00 1F FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 03 FE 07 FF FF FF FF FF FF C1 FF FF FF F0 00 03 FF FF FF C0 00 00 00 00 03 FF FF FF F8 00 00 00 00 07 FC 00 0F FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 03 FF FF FF FF FF FF FF FF 81 FF FF FF E0 00 03 FF FF FF E0 00 00 00 00 0F FF FF FF FF 00 00 00 00 1F FC 00 03 FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 07 FF FF FF FF FF FF FF FF 80 FF FF FF E0 00 03 FF FF FF E0 00 00 00 00 1F FF FF FF FF E0 00 00 00 FF FC 00 00 FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 80 07 FF FF FF FF FF FF FF FF 80 FF FF FF C0 00 03 FF FF FF F0 00 00 00 00 3F FF FF FF FF FE 00 00 03 FF FC 00 00 7F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 80 0F FF FF FF FF FF FF F7 FF 00 7F FF FF C0 00 03 FF FF FF F0 00 00 3E 00 7F FF FF FF FF FF E0 00 0F FF F8 00 00 1F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 80 1F FF FF FF FF FF FF F3 FF 00 3F FF FF 80 00 03 FF FF FF F8 00 00 FE 00 FF FF FF FF FF FF FF 00 3F FF F8 00 00 1F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 80 3F FF FF FF FF FF FF E3 FF 00 3F FF FF 80 00 03 FF FF FF F8 00 01 FF 01 FF FF FF FF FF FF FF C0 3F FF F0 00 00 0F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 80 7F FF FF FF FF FF FF E3 FF 00 1F FF FF 00 00 03 FF FF FF F8 00 03 FF 03 FF FF FF FF FF FF FF E0 7F FF E0 00 00 0F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF C1 FF FF FF FF FF FF FF E7 FF 00 1F FF FF 00 00 07 FF FF FF F8 00 0F FF 07 FF FF FF FF FF FF FF F0 FF FF C0 00 00 0F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF C1 FF FF FF FF FF FF FF E7 FF 00 1F FF FF 00 00 07 FF FF FF FC 00 0F FE 07 FF FF FF FF FF FF FF F0 FF FF 80 00 00 0F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF C3 FF FF FF FF FF FF FF E7 FF 00 0F FF FF 00 00 0F FF FF FF FC 00 3F FE 0F FF FF FF FF FF FF FF F0 FF FF 80 00 00 0F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF C7 FF FF FF FF FF FF FF EF FF 80 0F FF FF 00 00 0F FF FF FF FC 00 7F FE 1F FF FF FF FF FF FF FF F0 FF FF 00 00 00 0F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF C7 FF FF FF FF FF FF FF FF FF 80 0F FF FF 00 00 1F FF FF FF FC 00 7F FE 3F FF FF FF FF FF FF FF F0 FF FF 00 00 00 07 FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF 80 0F FF FF 00 00 3F FF FF FF FC 00 FF FC 7F FF FF FF FF FF FF FF F8 7F FF 00 00 00 07 FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 8F FF FF FF FF FF FF FF FF FF C0 0F FF FF 80 00 7F FF FF FF FC 01 FF FC 7F FF FF FF FF FF FF FF F8 7F FF C0 00 00 03 FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 03 FF FF FF FF FF FF FF FF FF C0 0F FF FF 80 00 FF FF FF FF FC 03 FF F8 FF FF FF FF FF FF FF FF F8 7F FF F0 00 00 03 FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 FF FF FF FF FF FF FF FF FF E0 0F FF FF C0 03 FF FF FF FF FC 03 FF F1 FF FF FF FF FF FF FF FF F8 3F FF FC 00 00 00 FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 FF FF EF FF FF FF FF FF FF F0 0F FF FF E0 0F FF FF FF FF FC 07 FF F1 FF FF FF FF FF FF FF FF FC 0F FF FF 80 00 00 7F FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 00 3F FF 8F FF FF FF FF FF FF F8 1F FF FF F0 1F FF FF FF FF FC 0F FF F3 FF FF FF FF FF FF FF FF FE 03 FF FF E0 00 00 0F FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 38 3F FE 1F FF FF FF FF F3 FF FC 1F FF FF F8 7F FF FF FF FF FE 0F FF E7 FF FF FF FF FF FF FF FF FF 00 7F FF FC 00 00 01 FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 38 3F FC 3F CF FF FF FF F1 FF FE 1F FF FF FF FF FF FF FF FF FE 1F FF EF FF FF FF FF FF FF FF FF FF 80 1F FF FF 00 00 00 FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 9F FF F8 7F 9F FF FF FF F1 FF FF BF FF FF FF FF FF FF FF FF FE 3F FF FF FF FF FF FF FF FF FF FF FF C0 03 FF FF E0 00 00 1F FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 9F FF F0 FE 3F FF FF FF F1 FF FF FF FF FF FF FF FF FF FF FF FE 7F FF FF FF FF FF FF FF FF FF FF FF F0 00 FF FF FE 00 00 0F FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF CF FF E1 FE 7F FF FF FF F9 FF FF FF FF FF FF FF FF FF FF FF FE FF FF FF FF FF FF FF FF FF FF FF FF FC 00 3F FF FF 80 00 07 FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF CF FF C3 F8 FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 03 FF FF FC 00 01 FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF C7 FF 87 F1 FF FF FF FF F8 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 00 7F FF FF 80 00 FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF E7 FF 0F E3 FF FF FF FF FC FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 0F FF FF E0 00 FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF E7 FE 1F C7 FF FF FF FF FC 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 80 03 FF FF FC 00 7F FF FF FF F0 00 00 00 FF FF FF FF FF FF FF CF FC 3F 9F FF FF FF FF FC 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 7F FF FF F0 3F FF FF FF F0 00 00 00 FF FF FF FF FF FF FF CF F8 7F 1F FF FF FF FF FE 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 0F FF FF FF 3F FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 9F F0 7E 7F FF FF FF FF FE 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 03 FF FF FF 9F FF FF FF F0 00 00 00 FF FF FF FF FF FF FF 3F E1 FE FF FF FF FF FF FF 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FE 00 3F FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FE 3F C3 F9 FF FF FF FF FF FF 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 03 FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FC 7F 83 F9 FF FF FF FF FF FF 9F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 01 FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF F8 FF 07 F7 FF FF FF FF FF FF CF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 3F FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF F1 FE 0F CF FF FF FF FF FF FF C7 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 07 FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF E3 FC 1F 9F FF FF FF FF FF FF F3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 07 FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF C7 F8 3F 3F FF FF FF FF FF FF FB FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 03 FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF CF E0 3E 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C1 FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF 9F C0 7C FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF 3F 80 F9 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 3F FF FF FF FF F0 00 00 00 FF FF FF FF FF FE 3F 81 F3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 03 FF FF FF FF F0 00 00 00 FF FF FF FF FF FE 7F 03 C7 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 03 FF FF FF FF F0 00 00 00 FF FF FF FF FF FC 7E 07 CF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FC 07 FF FF FF FF F0 00 00 00 FF FF FF FF FF F8 FC 0F 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF F0 FC 3E 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF F0 FC FC FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF E0 FD F9 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF E0 FF F1 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF E0 7F E3 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF E0 7F C7 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF F0 7F 87 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF F8 7F 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FC 3C 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FC 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FE 00 1F FF FF FF FF FF FF FF FF FF F1 FF FF FF FF FF FF FF E0 01 FF E7 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF 80 3F FF FF FF FF FF FF FF FF FF F0 FF FF FF FF FF FF FF C0 00 7F CF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF F0 FF FF FF FF FF FF FF FF FF FF E0 7F FF FF FF FF FF FF 80 00 1F 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 3F FF FF FF FF FF FF 80 00 00 0F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 1F FF FF FF FF FF FF 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 1F FF FF FF FF FF FF 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 0F FF FF FF FF FF FE 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 0F FF FF FF FF FF FF 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 07 FF FF FF FF FF FF 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 03 FF FF FF FF FF FF 80 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 03 FF FF FF FF FF FF 80 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 01 FF FF FF FF FF FF C0 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 01 FF FF FF FF FF FF C0 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 7F FF FF FF FF FF E0 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 7F FF FF FF FF FF E0 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 3F FF FF FF FF FF C0 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 1F FF FF FF FF FF 80 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 0F FF FF FF FF FE 00 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 03 FF FF FF FF FC 0F 00 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF C0 00 00 00 07 FF FF F0 7F C0 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 00 FF 00 FF FF C1 FF F0 00 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 07 FF F8 7F FF 87 FF F8 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF E0 00 1F FF FF FF FF 1F FF FC 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 7F FF FF FF FF FF FF FF 00 3F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 FF FF FF FF FF FF FF FF 80 7F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 03 FF FF FF FF FF FF FF FF E0 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F8 1F FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF F0 00 00 00");
            //byte[] bytes	= new byte[formats.length + image.length];

            //System.arraycopy(formats, 0, bytes, 0, formats.length);
            //System.arraycopy(image, 0, bytes, formats.length, image.length);

            //bluebamboo logo
            byte[] bytes = {(byte)0x1B,(byte)0x58,(byte)0x31,(byte)0x24,(byte)0x2D,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x1B,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x39,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x38,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7C,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7E,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x3F,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x37,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x9F,(byte)0x88,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x25,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x4F,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x27,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x1E,(byte)0x27,(byte)0xE6,(byte)0x00,(byte)0x03,(byte)0xFF,(byte)0xFC,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0xFF,(byte)0xF0,(byte)0x07,(byte)0xFF,(byte)0xF8,(byte)0x7F,(byte)0xFF,(byte)0x1E,(byte)0x00,(byte)0x7D,(byte)0xFF,(byte)0xFE,(byte)0x0F,(byte)0xFF,(byte)0xC1,(byte)0xFF,(byte)0xF8,(byte)0x25,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0x93,(byte)0xCD,(byte)0x00,(byte)0x03,(byte)0xFF,(byte)0xFE,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0xFF,(byte)0xF0,(byte)0x07,(byte)0xFF,(byte)0xFC,(byte)0x7F,(byte)0xFF,(byte)0x9F,(byte)0x00,(byte)0x7D,(byte)0xFF,(byte)0xFF,(byte)0x1F,(byte)0xFF,(byte)0xE3,(byte)0xFF,(byte)0xFC,(byte)0x10,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0xC9,(byte)0x98,(byte)0x80,(byte)0x03,(byte)0xFF,(byte)0xFF,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0xFF,(byte)0xF0,(byte)0x07,(byte)0xFF,(byte)0xFC,(byte)0xFF,(byte)0xFF,(byte)0x9F,(byte)0x00,(byte)0xFD,(byte)0xFF,(byte)0xFF,(byte)0x3F,(byte)0xFF,(byte)0xE3,(byte)0xFF,(byte)0xFE,(byte)0x0F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xCF,(byte)0xE4,(byte)0x3C,(byte)0x60,(byte)0x03,(byte)0xC0,(byte)0x0F,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xFC,(byte)0xFF,(byte)0xFF,(byte)0x9F,(byte)0x80,(byte)0xFD,(byte)0xFF,(byte)0xFF,(byte)0xBF,(byte)0xFF,(byte)0xF7,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xA7,(byte)0xF2,(byte)0x3F,(byte)0x30,(byte)0x03,(byte)0x80,(byte)0x07,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x1C,(byte)0xE0,(byte)0x03,(byte)0x9F,(byte)0x81,(byte)0xFD,(byte)0xC0,(byte)0x07,(byte)0xB8,(byte)0x00,(byte)0xF7,(byte)0x80,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x93,(byte)0xFC,(byte)0x3F,(byte)0x98,(byte)0x03,(byte)0x80,(byte)0x07,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x1C,(byte)0xE0,(byte)0x03,(byte)0x9F,(byte)0xC3,(byte)0xFD,(byte)0xC0,(byte)0x07,(byte)0xB8,(byte)0x00,(byte)0x77,(byte)0x80,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xC9,(byte)0xF9,(byte)0x9F,(byte)0xCC,(byte)0x03,(byte)0xFF,(byte)0xFE,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0xFF,(byte)0xE0,(byte)0x07,(byte)0xFF,(byte)0xFC,(byte)0xE0,(byte)0x03,(byte)0x9F,(byte)0xC3,(byte)0xFD,(byte)0xFF,(byte)0xFF,(byte)0x38,(byte)0x00,(byte)0x77,(byte)0x80,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xE4,(byte)0x73,(byte)0x4F,(byte)0xE4,(byte)0x03,(byte)0xFF,(byte)0xFE,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0xFF,(byte)0xE0,(byte)0x07,(byte)0xFF,(byte)0xF8,(byte)0xE7,(byte)0xFF,(byte)0x9D,(byte)0xE7,(byte)0xBD,(byte)0xFF,(byte)0xFF,(byte)0x38,(byte)0x00,(byte)0x77,(byte)0x80,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xE2,(byte)0x72,(byte)0x27,(byte)0xFC,(byte)0x03,(byte)0xFF,(byte)0xFE,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0xFF,(byte)0xE0,(byte)0x07,(byte)0xFF,(byte)0xF8,(byte)0xE7,(byte)0xFF,(byte)0x9D,(byte)0xE7,(byte)0xBD,(byte)0xFF,(byte)0xFF,(byte)0x38,(byte)0x00,(byte)0x77,(byte)0x80,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xF1,(byte)0x07,(byte)0x13,(byte)0xF8,(byte)0x03,(byte)0xFF,(byte)0xFF,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0xFF,(byte)0xE0,(byte)0x07,(byte)0xFF,(byte)0xFC,(byte)0xE7,(byte)0xFF,(byte)0x9C,(byte)0xFF,(byte)0x3D,(byte)0xFF,(byte)0xFF,(byte)0x38,(byte)0x00,(byte)0x77,(byte)0x80,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xF9,(byte)0x8F,(byte)0x89,(byte)0xF0,(byte)0x03,(byte)0xC0,(byte)0x07,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x1E,(byte)0xE7,(byte)0xFF,(byte)0x9C,(byte)0xFF,(byte)0x3D,(byte)0xC0,(byte)0x07,(byte)0xB8,(byte)0x00,(byte)0x77,(byte)0x80,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFF,(byte)0x8F,(byte)0xC4,(byte)0xE0,(byte)0x03,(byte)0x80,(byte)0x07,(byte)0x78,(byte)0x00,(byte)0x70,(byte)0x00,(byte)0xEF,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x1E,(byte)0xE0,(byte)0x03,(byte)0x9C,(byte)0x7E,(byte)0x3D,(byte)0xC0,(byte)0x03,(byte)0xB8,(byte)0x00,(byte)0x77,(byte)0x80,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x7E,(byte)0x27,(byte)0xE2,(byte)0x00,(byte)0x03,(byte)0xC0,(byte)0x07,(byte)0x78,(byte)0x00,(byte)0x78,(byte)0x01,(byte)0xEF,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x1E,(byte)0xE0,(byte)0x03,(byte)0x9C,(byte)0x3E,(byte)0x3D,(byte)0xE0,(byte)0x07,(byte)0xBC,(byte)0x00,(byte)0xF7,(byte)0xC0,(byte)0x1E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x3C,(byte)0xD3,(byte)0xF1,(byte)0x00,(byte)0x03,(byte)0xFF,(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0x3F,(byte)0xFF,(byte)0xEF,(byte)0xFF,(byte)0xF0,(byte)0x07,(byte)0xFF,(byte)0xFC,(byte)0xE0,(byte)0x03,(byte)0x9C,(byte)0x3C,(byte)0x3D,(byte)0xFF,(byte)0xFF,(byte)0xBF,(byte)0xFF,(byte)0xF3,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x19,(byte)0xC9,(byte)0xFA,(byte)0x00,(byte)0x03,(byte)0xFF,(byte)0xFF,(byte)0x7F,(byte)0xFF,(byte)0x3F,(byte)0xFF,(byte)0xCF,(byte)0xFF,(byte)0xF0,(byte)0x07,(byte)0xFF,(byte)0xFC,(byte)0xE0,(byte)0x03,(byte)0x9C,(byte)0x18,(byte)0x3D,(byte)0xFF,(byte)0xFF,(byte)0x1F,(byte)0xFF,(byte)0xE3,(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xE4,(byte)0xFC,(byte)0x00,(byte)0x03,(byte)0xFF,(byte)0xFE,(byte)0x7F,(byte)0xFF,(byte)0x1F,(byte)0xFF,(byte)0x8F,(byte)0xFF,(byte)0xF0,(byte)0x07,(byte)0xFF,(byte)0xF8,(byte)0xE0,(byte)0x03,(byte)0x9C,(byte)0x18,(byte)0x3D,(byte)0xFF,(byte)0xFF,(byte)0x0F,(byte)0xFF,(byte)0xC1,(byte)0xFF,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xF2,(byte)0x78,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xC0,(byte)0xC0,(byte)0x01,(byte)0x9C,(byte)0x00,(byte)0x19,(byte)0xFF,(byte)0xF8,(byte)0x03,(byte)0xFF,(byte)0x00,(byte)0x3F,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xF9,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0xFC,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xFE,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x3F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x1F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0E,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,};*/

            byte[] bytes2 = PrintTools_58mm.decodeBitmap(targetImage);



            sendData(bytes2);

            byte[] newline 	= Printer.printfont("\n\n",FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);

            sendData(newline);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


  /*  private void printImage() {
        try {
            byte[] formats  = { (byte)0x1B, (byte)0x58, (byte)0x31, (byte)0x24, (byte)0x2D };

            //bluebamboo logo
            byte[] image = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00, ....... (byte)0x00};

            sendData(bytes);

            byte[] newline  = Printer.printfont("\n\n",FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A,PocketPos.LANGUAGE_ENGLISH);

            sendData(newline);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

/*
	//print photo
	public void printPhoto() {
		try {
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.s);
			if(bmp!=null){
				byte[] command = Utils.decodeBitmap(bmp);
				printText2(command);
			}else{
				Log.e("Print Photo error", "the file isn't exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("PrintTools", "the file isn't exists");
		}
	}*/

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state 	= intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    showDisabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED) {
                    showToast("Paired");

                    connect();
                }
            }
        }
    };




}



