package com.wy.phonemedia.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wy.phonemedia.base.BasePager;

import java.util.List;

/**
 * Created by Wyman on 2016/10/20.
 * WeChat: wy391920778
 * Effect:
 */

public class MyFragment extends Fragment {
    public static List<BasePager> pagers;
    public static int currentPager;

    @SuppressLint("ValidFragment")
    private MyFragment() {
    }

    public static MyFragment getInStance(int num, List<BasePager> list) {
        MyFragment fragment = new MyFragment();
        currentPager = num;
        pagers = list;
        return fragment;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        BasePager basePager = getBasePager();
        //各个页面的视图
        return basePager.rootView;
    }

    private BasePager getBasePager() {
        BasePager basePager = pagers.get(currentPager);
        if (basePager != null && !basePager.isInitData) {
            basePager.isInitData = true;
            basePager.initData();//联网请求，绑定数据
        }
        return basePager;
    }
}
