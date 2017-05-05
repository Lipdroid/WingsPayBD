package com.example.com.wingsbangladesh.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.com.wingsbangladesh.R;
import com.example.com.wingsbangladesh.pockdata.PocketPos;
import com.example.com.wingsbangladesh.util.FontDefine;
import com.example.com.wingsbangladesh.util.GlobalUtils;
import com.example.com.wingsbangladesh.util.PrintTools_58mm;
import com.example.com.wingsbangladesh.util.Printer;
import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.IBarcode;
import com.onbarcode.barcode.android.UPCA;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Set;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WorkingPrintActivity extends AppCompatActivity {

    private ListView bluetooth_device_list = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mmSocket = null;
    private BluetoothDevice mmDevice = null;
    private ArrayList<BluetoothDevice> mDeviceList = null;
    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    private Bitmap targetImage;
    private BitSet dots;
    private int mWidth;
    private int mHeight;
    private String barcode = null;
    private String orderId = null;
    private String marchantref = null;
    private String marchantcode = null;
    private String productprice = null;
    private String phone = null;

    private  int try_count = 0;
    @Override
    protected void onResume() {
        super.onResume();
        findBT();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_working_print);

        mDeviceList = new ArrayList<BluetoothDevice>();
        bluetooth_device_list = (ListView) findViewById(R.id.bluetooth_device_list);
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        marchantref = intent.getStringExtra("marchantref");
        marchantcode = intent.getStringExtra("marchantcode");
        productprice = intent.getStringExtra("productprice");
        phone = intent.getStringExtra("phone");
        barcode = intent.getStringExtra("barcode");

        generateUPCACode(barcode);
        //find all the bluetooth devices and show them in the list

        findBT();



        bluetooth_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e("pressed", position + "");
                mmDevice = mDeviceList.get(position);
                GlobalUtils.prev_connected_device = mmDevice;
                try {
                    connectWithSelectedDevice();
                    printImage();
                    finish();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    // tries to open a connection to the bluetooth printer device

    void connectWithSelectedDevice() throws IOException {
        try {
            closeBT();
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            Toast.makeText(WorkingPrintActivity.this, "Printing", Toast.LENGTH_LONG).show();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            Log.e("connected", mmDevice.getName() + " connected");

        } catch (Exception e) {
            if(try_count < 2) {
                try_count++;
                Log.e("connection fail", mmDevice.getName() + try_count+" Try again");
                //try again
                connectWithSelectedDevice();
            }else{
                showpopUp();
            }
            e.printStackTrace();
        }
    }

    private void showpopUp() {
        Toast.makeText(WorkingPrintActivity.this, "Printer is stopped,please restart the printer and try again", Toast.LENGTH_LONG).show();
        finish();
    }

    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(WorkingPrintActivity.this, "No bluetooth adapter available", Toast.LENGTH_LONG).show();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                mDeviceList.clear();
                mDeviceList.addAll(pairedDevices);

                if (GlobalUtils.prev_connected_device != null){

                    for (int i = 0; i< mDeviceList.size();i++){
                        BluetoothDevice device = mDeviceList.get(i);
                        if(device.getAddress().equals(GlobalUtils.prev_connected_device.getAddress())){
                            mmDevice = GlobalUtils.prev_connected_device;
                            bluetooth_device_list.performItemClick(bluetooth_device_list, i, bluetooth_device_list.getItemIdAtPosition(i));

                        }

                    }
                }
                populateDeviceList();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(WorkingPrintActivity.this, android.R.layout.simple_list_item_1, getArray(mDeviceList));
        bluetooth_device_list.setAdapter(adapter);
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

    private void printImage() {
        try {
            targetImage = Bitmap.createScaledBitmap(targetImage, 240, 105, true);
            if (targetImage != null) {

                byte[] bytes = PrintTools_58mm.decodeBitmap(targetImage);
                byte[] one = Printer.printfont(orderId, FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
                byte[] two = Printer.printfont(marchantref + "|" + marchantcode, FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
                byte[] three = Printer.printfont(productprice + "|" + phone, FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
                byte[] four = Printer.printfont(barcode+"\n", FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

                byte[] singleLine = Printer.printfont("\n", FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
                byte[] fourLine = Printer.printfont("\n\n\n\n", FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);
                byte[] newline = Printer.printfont("\n\n", FontDefine.FONT_32PX, FontDefine.Align_CENTER, (byte) 0x1A, PocketPos.LANGUAGE_ENGLISH);

                sendData(newline);

                sendData(one);
                sendData(two);
                sendData(three);

                sendData(newline);
                sendData(newline);

                sendData(bytes);
                //print1DBarcode();

                sendData(singleLine);

                sendData(four);

                sendData(fourLine);

            } else {
                System.out.println("SAADBITMAPIS NULL");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    private void sendData(byte[] bytes) throws IOException {
        try {
            mmOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void print1DBarcode() {
        String content	= "123456789013";

        //1D barcode format (hex): 1d 6b 02 0d + barcode data

        byte[] formats	= {(byte) 0x1d, (byte) 0x6b, (byte) 0x02, (byte) 0x0d};
        byte[] contents	= content.getBytes();

        byte[] bytes	= new byte[formats.length + contents.length];

        System.arraycopy(formats, 0, bytes, 0, formats.length);
        System.arraycopy(contents, 0, bytes, formats.length, contents.length);

        try {
            byte[] newline2 = Printer.printfont("\n\n\n", FontDefine.FONT_32PX,FontDefine.Align_CENTER,(byte)0x1A, PocketPos.LANGUAGE_ENGLISH);

            sendData(newline2);
//            sendData(newline2);

            sendData(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(WorkingPrintActivity.this, data, Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void generateUPCACode(String data) {
        //Create an image for drawing the barcodes
        Bitmap barcode_image = Bitmap.createBitmap(240, 110, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(barcode_image);
        canvas.drawColor(Color.WHITE);

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
        //  barcode.setShowText(true);
        // barcodes encoding data font style
        //barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 12));
        // space between barcodes and barcodes encoding data
        //barcode.setTextMargin(6);
        // barcode.setTextColor(AndroidColor.black);
        // barcodes bar color and background color in Android device
        barcode.setForeColor(AndroidColor.black);
        barcode.setBackColor(AndroidColor.white);

        RectF bounds = new RectF(0, 0, 0, 0);
        try {
            barcode.drawBarcode(canvas, bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }

        targetImage = barcode_image;

    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            if (mmOutputStream != null) {
                mmOutputStream.close();
                mmInputStream.close();
            }
            if (mmSocket != null)
                mmSocket.close();

            Log.e("Disconnected", mmDevice.getName() + " Disconnected");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
