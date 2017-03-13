package com.example.com.wingsbangladesh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.com.wingsbangladesh.R;

/**
 * Created by Sourov00 on 04-12-15.
 */
public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();


        ImageView myImageView = (ImageView)findViewById(R.id.tigerCementLogo);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        myImageView.startAnimation(myFadeInAnimation); //Set animation to your ImageView



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.my_fade_in, R.anim.my_fade_out);
                    finish();
                }
                catch (Exception ex)
                {
                    Toast.makeText(getApplicationContext(),ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);

    }

}
