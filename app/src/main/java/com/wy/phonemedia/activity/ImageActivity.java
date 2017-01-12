package com.wy.phonemedia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wy.phonemedia.R;

/**
 * Created by Wyman on 2016/10/20.
 * WeChat: wy391920778
 * Effect:
 */

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";
    private ImageView mImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imgae_main);
        mImageView = (ImageView) findViewById(R.id.iv);
       Intent intent= getIntent();
       String url= intent.getStringExtra("url");
        Log.e(TAG, "onCreate:" + url);
        Glide.with(this).load(url).into(mImageView);
    }
}
