package com.wy.phonemedia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Wyman on 2016/9/18.
 * WeChat: wy391920778
 * Effect:
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {
    private View tv_search;
    private View fl_game;
    private View iv_circle;
    private Context mContext;

    /**
     * 在代码中实例的时候调用该方法
     * @param context
     */
    public TitleBar(Context context) {
       this(context,null);
    }

    /**
     * 当这个类在布局文件中的时候，系统通过构造方法实例的时候调用该方法
     * @param context
     * @param attrs
     */
    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当需要设置样式的时候调用该方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    /**
     * 当布局文件加载完成的时候回调该方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //初始化子view
        tv_search = getChildAt(1);
        fl_game = getChildAt(2);
        iv_circle = getChildAt(3);
        //设置监听事件

        tv_search.setOnClickListener(this);
        fl_game.setOnClickListener(this);
        iv_circle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == tv_search) {
            Toast.makeText(mContext, "搜索", Toast.LENGTH_SHORT).show();
        } else if (v==fl_game) {
            Toast.makeText(mContext, "游戏", Toast.LENGTH_SHORT).show();
        }else if (v==iv_circle) {
            Toast.makeText(mContext, "历史", Toast.LENGTH_SHORT).show();
        }
    }
}
