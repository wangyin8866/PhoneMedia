package com.wy.phonemedia.base;

import android.content.Context;
import android.view.View;

/**
 * Created by Wyman on 2016/9/14.
 * WeChat: wy391920778
 * Effect:base基类
 */
public abstract class BasePager {
    public View rootView;
    public Context context;
    public boolean isInitData;

    public BasePager(Context context) {
        this.context = context;
        rootView= initView();
    }

    /**
     * 强制子类实现的方法，初始化页面
     * @return
     */
    public abstract View initView();

    /**
     * 当子页面需要绑定数据或者联网请求数据和绑定数据的时候，重写此方法
    */
    public void  initData(){}
}
