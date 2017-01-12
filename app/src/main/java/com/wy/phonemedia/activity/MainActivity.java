package com.wy.phonemedia.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.wy.phonemedia.R;
import com.wy.phonemedia.base.BasePager;
import com.wy.phonemedia.pager.AudioPager;
import com.wy.phonemedia.pager.NetAudioPager;
import com.wy.phonemedia.pager.NetVideoPager;
import com.wy.phonemedia.pager.VideoPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wy on 2016/9/14.
 */
public class MainActivity extends AppCompatActivity {
    private RadioGroup rg_bottom_tab;
    //页面的集合
    private List<BasePager> pagers;

    //当前选中的页面
    private int currentPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_bottom_tab = (RadioGroup) findViewById(R.id.rg_bottom_tab);
        pagers = new ArrayList<>();
        pagers.add(new VideoPager(this));//添加本地视频页面 -0;
        pagers.add(new AudioPager(this));//添加本地音乐页面 -1;
        pagers.add(new NetVideoPager(this));//添加网络视频页面 -2
        pagers.add(new NetAudioPager(this));//添加网络音乐页面 -3;

        //设置radioGroup监听
        rg_bottom_tab.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tab.check(R.id.rb_video);//默认选中首页
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_video://视频
                    currentPager = 0;
                    break;
                case R.id.rb_audio://音乐
                    currentPager = 1;
                    break;
                case R.id.rb_net_video://网络视频
                    currentPager = 2;
                    break;
                case R.id.rb_net_audio://网络音乐;
                    currentPager = 3;
                    break;
            }
            setFragment();
        }
    }
    private void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fl_content, MyFragment.getInStance(currentPager,pagers));
        ft.commit();
    }

}
