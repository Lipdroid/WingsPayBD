package com.example.com.wingsbangladesh.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.com.wingsbangladesh.R;

/**
 * Created by Mou on 2/26/2017.
 */


public class SettingActivity extends AppCompatActivity {

    String usernameText,passwordText;
    EditText username,password;
    String code;
    String URL;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();


         String[] state = { "UPC-A CODE", "ITF", "CODE 128" };

        Spinner s = (Spinner)findViewById(R.id.spinner);
      //  spinnerOsversions = (Spinner) findViewById(R.id.osversions);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, state);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter_state);






    }


}





