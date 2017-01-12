package com.wy.phonemedia.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wy.phonemedia.entity.Lyric;
import com.wy.phonemedia.utils.DensityUtil;

import java.util.List;

/**
 * Created by Wyman on 2016/10/12.
 * WeChat: wy391920778
 * Effect:
 */

public class ShowLyricView extends TextView {
    public static final String AAAAAAAAAAA = "aaaaaaaaaaa";
    private static final String TAG = "ShowLyricView";
    private List<Lyric> lyrics;
    private TextPaint mPaint;
    private TextPaint whitePaint;
    private int mWidth;
    private int mHeight;
    private String mContentText = "没有歌词...";
    private Rect mTextBound;
    /**
     * 当前歌词位置，第几句歌词
     */
    private int index;
    /**
     * 文本的高度
     */
    private float textHeight;
    /**
     * 当前播放进度
     */
    private float currentDuration;
    /**
     * 高亮的时间
     */
    private float sleepTime;
    /**
     * 时间戳，什么时间高亮歌词
     */
    private float timePoint;

    /**
     * 设置歌词列表
     *
     * @param lyrics
     */
    public void setLyrics(List<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {
            //是歌词向上平移
            float plush = 0;
            if (sleepTime == 0) {
                plush = 0;
            } else {//平移
                /**
                 * 这一句所化的时间：休眠时间=移动的距离：总距离（行高）；
                 * 移动距离=（这一句所化的时间：休眠时间）*总距离（行高）；
                 * 屏幕的坐标=行高+移动距离
                 */
                plush = textHeight + ((currentDuration - timePoint) / sleepTime * textHeight);
            }
            canvas.translate(0, -plush);
            //绘制歌词：绘制当前句，
            String currentText = lyrics.get(index).getContent();
//            mPaint.getTextBounds(currentText, 0, currentText.length(), mTextBound);
//            canvas.drawText(currentText, mWidth / 2 - mTextBound.width() / 2, mHeight / 2-mTextBound.height()/2, mPaint);
            //  使用mPaint.setTextAlign(Paint.Align.CENTER);使字体居中
            canvas.drawText(currentText, mWidth / 2 , mHeight / 2, mPaint);
//            Log.e(TAG, "currentText:" + currentText);
//            Log.e(TAG, "currentTextmWidth:" + mWidth);
//            Log.e(TAG, "currentTextmHeight:" + mHeight);
//            Log.e(TAG, "currentTextgetWidth:" + getWidth());
//            Log.e(TAG, "currentTextgetHeight:" + getHeight());
//            Log.e(TAG, "currentTextmTextBound.width:" + mTextBound.width());
//            Log.e(TAG, "currentTextmTextBound.height:" + mTextBound.height());
            // 绘制前面部分，
            int tempY = mHeight / 2;//Y轴的中间坐标
            for (int i = index - 1; i >= 0; i--) {
                //每一句歌词
                String preContent = lyrics.get(i).getContent();
                tempY -= textHeight;
                if (tempY < 0) {
                    break;
                }
//                whitePaint.getTextBounds(preContent, 0, preContent.length(), mTextBound);
//                canvas.drawText(preContent, mWidth / 2 -mTextBound.width()/2, tempY-mTextBound.height()/2, whitePaint);
                canvas.drawText(preContent, mWidth / 2 , tempY, whitePaint);
//                Log.e(TAG, "preContentmWidth:" + mWidth);
//                Log.e(TAG, "preContentmHeight:" + mHeight);
//                Log.e(TAG, "preContentmTextBound.width:" + mTextBound.width());
//                Log.e(TAG, "preContentmTextBound.height:" + mTextBound.height());
            }
            // 绘制后面部分
            tempY = mHeight / 2;//Y轴的中间坐标
            for (int i = index + 1; i < lyrics.size(); i++) {
                //每一句歌词
                String nextContent = lyrics.get(i).getContent();
                tempY += textHeight;
                if (tempY > mHeight) {
                    break;
                }
//                whitePaint.getTextBounds(nextContent, 0, nextContent.length(), mTextBound);
//                canvas.drawText(nextContent, mWidth / 2 - mTextBound.width() / 2, tempY-mTextBound.height()/2, whitePaint);
                canvas.drawText(nextContent, mWidth / 2 , tempY, whitePaint);
//                Log.e(TAG, "nextContentmWidth:" + mWidth);
//                Log.e(TAG, "nextContentmHeight:" + mHeight);
//                Log.e(TAG, "nextContentmTextBound.width:" + mTextBound.width());
//                Log.e(TAG, "nextContentmTextBound.height:" + mTextBound.height());
            }

//            Log.e(TAG, "getWidth:" + getWidth());
//            Log.e(TAG, "getHeight:" + getHeight());
//            Log.e(TAG, "mWidth:" + mWidth);
//            Log.e(TAG, "mHeight:" + mHeight);
//            Log.e(TAG, "mTextBound.width:" + mTextBound.width());
//            Log.e(TAG, "mTextBound.height:" + mTextBound.height());

        } else {
//            mPaint.getTextBounds(mContentText, 0, mContentText.length(), mTextBound);
//            canvas.drawText(mContentText, mWidth / 2-mTextBound.width()/2, mHeight / 2-mTextBound.height()/2, mPaint);
            canvas.drawText("没有歌词...", mWidth / 2, mHeight / 2, mPaint);
        }
    }

    private void init(Context context) {
        textHeight = DensityUtil.dip2px(context, 15);//转换成对应的像素
        //初始化画笔
        mPaint = new TextPaint();
        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(DensityUtil.dip2px(context, 12));
        mPaint.setAntiAlias(true);
        //使字体居中显示,----不需要减去mTextBound.width()，mTextBound.height()
        mPaint.setTextAlign(Paint.Align.CENTER);

        whitePaint = new TextPaint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(DensityUtil.dip2px(context, 12));
        whitePaint.setAntiAlias(true);
        //使字体居中显示,----不需要减去mTextBound.width()，mTextBound.height()
        whitePaint.setTextAlign(Paint.Align.CENTER);
        // 计算绘画字体需要的范围
//        mTextBound = new Rect();
        //假数据
//        lyrics = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            Lyric lyric = new Lyric();
//            lyric.setTimePoint(1000 * i);
//            lyric.setSleepTime(1500 + i);
//            lyric.setContent(AAAAAAAAAAA+i);
//            lyrics.add(lyric);
//        }
    }

    /**
     * 根据当前播放位置，找出高亮显示的歌词
     *
     * @param currentDuration
     */
    public void setShowNextLyric(int currentDuration) {
        this.currentDuration = currentDuration;
        if (lyrics == null || lyrics.size() == 0) {
            return;
        }
        for (int i = 1; i < lyrics.size(); i++) {
            if (currentDuration < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1;
                if (currentDuration >= lyrics.get(tempIndex).getTimePoint()) {
                    //当前正在播放的哪句歌词
                    index = tempIndex;
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();
                }
            }
        }
        //重新绘制
        invalidate();//主线程
//        postInvalidate();//子线程
    }
}
