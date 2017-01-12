package com.wy.testdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void playVideo(View view){
        Intent intent = new Intent();
//        intent.setDataAndType(Uri.parse("http://192.168.191.1:8080/yellow.mp4"),"video/*");
//        intent.setDataAndType(Uri.parse("http://192.168.191.1:8080/rmvb.rmvb"),"video/*");
//        intent.setDataAndType(Uri.parse("http://192.168.191.1:8080/rmvb11.rmvb"),"video/*");
        intent.setDataAndType(Uri.parse("http://wvideo.spriteapp.cn/video/2016/1019/1badd2ce-95b3-11e6-bd5a-90b11c479401_wpd.mp4"),"video/*");
//        intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2016/07/19/mp4/160719095812990469.mp4"),"video/*");
        startActivity(intent);

    }

}
