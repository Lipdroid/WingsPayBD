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
import android.os.AsyncTask;
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

import com.example.com.wingsbangladesh.util.ConstantURLs;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PrintActivity extends AppCompatActivity {

    String barcode,orderId,marchantref,marchantcode,productprice,phone;
    TextView paperfy_order_id, marchent_ref, marchent_code, product_price, customer_phone, marchent_code2;
    Bitmap targetImage;
    LinearLayout ln,view;
    ImageView barcode_imageView = null;
    ImageView bmImage;

    private Button mConnectBtn,mEnableBtn;
    private Spinner mDeviceSp;
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




        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        marchantref = intent.getStringExtra("marchantref");
        marchantcode = intent.getStringExtra("marchantcode");
        productprice= intent.getStringExtra("productprice");
        phone= intent.getStringExtra("phone");
        barcode= intent.getStringExtra("barcode");


        barcode_imageView = (ImageView) findViewById(R.id.barcode_img);

       // getSupportActionBar().hide();
        findViewById();



        if (barcode.length()==11) {

            //barcode = jsonObject.getString("barcode_upca");
            generateUPCACode(barcode);


        }


        else{

            Toast.makeText(PrintActivity.this, "No barcode found!",
                    Toast.LENGTH_LONG).show();
        }
        /*else if (barcode.length()==14) {
           // barcode = jsonObject.getString("barcode_code128");
            generateCode128Code(barcode);

        } else if(barcode.length()==13){
        //    barcode = jsonObject.getString("barcode_itf");
            generateITFCode(barcode);

        }*/


        view = (LinearLayout) findViewById(R.id.barcodeid);
        bmImage = (ImageView) findViewById(R.id.saad);
        view.setDrawingCacheEnabled(true);

        paperfy_order_id.setText(orderId);
        marchent_ref.setText(marchantref);
        marchent_code.setText(marchantcode);
        marchent_code2.setText(barcode);
        product_price.setText(productprice);
        customer_phone.setText(phone);


        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache(true);
        targetImage = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false); // clear drawing cache


        //no need to set preview
      //  bmImage.setImageBitmap(targetImage);
        view.setVisibility(View.GONE);




        connectToBluetooth();


        if(ConstantURLs.FLAG==1) {


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
                    new GetData().execute();
                }
            });

        }
        else{

            showToast("Please Connect to the Bluetooth And Try Again!");

        }



    }

    @Override
    public void onBackPressed()
    {

        super.onBackPressed();
        finish();
    }

    public void findViewById() {



        paperfy_order_id = (TextView) findViewById(R.id.paperfly_order_id);
        marchent_ref = (TextView) findViewById(R.id.marchant_ref);
        marchent_code2 = (TextView) findViewById(R.id.marchant_code2);
        marchent_code = (TextView) findViewById(R.id.marchant_code);
        product_price = (TextView) findViewById(R.id.product_price);
        customer_phone = (TextView) findViewById(R.id.customer_phone);
        mConnectBtn = (Button) findViewById(R.id.btn_connect);
        mEnableBtn = (Button) findViewById(R.id.btn_enable);
     //   mPrintBarcodeBtn = (Button) findViewById(R.id.btn_print_barcode);
        mDeviceSp = (Spinner) findViewById(R.id.sp_device);
        ln=(LinearLayout)findViewById(R.id.mother);


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
        int size = data.size();
        list = new String[size];
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
     //   mPrintBarcodeBtn.setEnabled(false);
        mDeviceSp.setEnabled(false);
    }

    private void showConnected() {
        showToast("Connected");

        mConnectBtn.setText("Disconnect");

       // mPrintBarcodeBtn.setEnabled(true);

        mDeviceSp.setEnabled(false);

    }

    private void showDisonnected() {
        showToast("Disconnected");

        mConnectBtn.setText("Connect");

      //  mPrintBarcodeBtn.setEnabled(false);

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
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};

            Method method = cl.getMethod("createBond", par);

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

    private void printImage() {
        try {



            Bitmap bitmap = Bitmap.createScaledBitmap(targetImage, 300, 210, true);
            byte[] bytes = PrintTools_58mm.decodeBitmap(bitmap);

            sendData(bytes);

            byte[] newline = Printer.printfont("\n\n", FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

            sendData(newline);

            bitmap.recycle();
            bytes = null;

            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

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

    private class GetData extends AsyncTask<Void, Void, Boolean> {
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new SweetAlertDialog(PrintActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... uRls) {


            printImage();

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            pDialog.dismiss();

            finish();


        }

    }

    private void connectToBluetooth(){


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

            mProgressDlg = new ProgressDialog(this);

            mProgressDlg.setMessage("Scanning...");
            mProgressDlg.setCancelable(false);
            mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    mBluetoothAdapter.cancelDiscovery();
                }
            });

            mConnectingDlg = new ProgressDialog(this);

            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            mConnector = new P25Connector(new P25Connector.P25ConnectionListener() {

                @Override
                public void onStartConnecting() {
                    mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    mConnectingDlg.dismiss();

                    showConnected();

                    ConstantURLs.FLAG=1;

                }

                @Override
                public void onConnectionFailed(String error) {
                    mConnectingDlg.dismiss();

                    ConstantURLs.FLAG=0;

                }

                @Override
                public void onConnectionCancelled() {
                    mConnectingDlg.dismiss();
                    ConstantURLs.FLAG=0;
                }

                @Override
                public void onDisconnected() {
                    showDisonnected();
                    ConstantURLs.FLAG=0;
                }
            });


            //print demo text


            //print barcode 1D or 2D
     /*       mPrintBarcodeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // printBarcode();
                    new GetData().execute();
                }
            });*/

        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, filter);
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



