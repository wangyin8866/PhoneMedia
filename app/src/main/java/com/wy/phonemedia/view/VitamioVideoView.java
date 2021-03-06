package com.wy.phonemedia.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Wyman on 2016/9/22.
 * WeChat: wy391920778
 * Effect:万能播放器
 *
 */

public class VitamioVideoView extends io.vov.vitamio.widget.VideoView{
    public VitamioVideoView(Context context) {
        this(context, null);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
    public void setMediaSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = videoWidth;
        params.height = videoHeight;
        setLayoutParams(params);
    }
}
