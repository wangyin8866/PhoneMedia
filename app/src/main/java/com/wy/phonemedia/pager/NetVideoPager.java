package com.wy.phonemedia.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wy.phonemedia.R;
import com.wy.phonemedia.service.MyService;
import com.wy.phonemedia.activity.SystemVideoPlayer;
import com.wy.phonemedia.adapter.NetVideoAdapter;
import com.wy.phonemedia.base.BasePager;
import com.wy.phonemedia.entity.MediaItem;
import com.wy.phonemedia.utils.CashUtils;
import com.wy.phonemedia.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wy.phonemedia.R.id.pb_video;

/**
 * Created by Wyman on 2016/9/14.
 * WeChat: wy391920778
 * Effect: 网络视频
 */
public class NetVideoPager extends BasePager{
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private List<MediaItem> mediaItems;
    private String mUrl="http://api.m.mtime.cn/PageSubArea/";
    private NetVideoAdapter adapter;
    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video_pager, null);
        findViews(view);




        return view;
    }

    private void findViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_net_video);
        mTextView = (TextView) view.findViewById(R.id.tv_no_net);
        mProgressBar = (ProgressBar) view.findViewById(pb_video);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频的数据被初始化了...");
        //联网
        loadData(mUrl);


    }

    /**
     * 获取数据
     * @param mUrl
     */
    private void loadData(final String mUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MyService call = retrofit.create(MyService.class);
        call.getNetMovieData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", e.getMessage());
                        //没有网的时候从缓存中获取数据
                        String saveJson= CashUtils.getString(context,mUrl);
                        if (!TextUtils.isEmpty(saveJson)) {
                            processData(saveJson);
                        } else {
                            mTextView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNext(String o) {
                        //缓存
                        CashUtils.putString(context,mUrl,o);
                        processData(o);
                    }
                });
    }

    /**
     * 处理获取到的数据
     * @param json
     */
    private void processData(String json) {
        //解析json数据
        mediaItems=parseJson(json);
        //显示数据
        showData();
    }

    /**
     * 显示数据
     */
    private void showData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //有数据
            //显示数据
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new NetVideoAdapter(context,mediaItems);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置点击动画

            //设置recyclerView的点击事件
            adapter.setOnItemClickListener(new NetVideoAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    MediaItem mediaItem = mediaItems.get(position);
                    //传递集合列表给播放器
                    Intent intent = new Intent(context,SystemVideoPlayer.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videolist", (Serializable) mediaItems);
                    intent.putExtras(bundle);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            //隐藏text
            mTextView.setVisibility(View.GONE);
        } else {
            //没有数据
            //显示text
            mTextView.setVisibility(View.VISIBLE);
        }
        //隐藏pb
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 解析json数据
     * @param json
     * @return
     */
    private List<MediaItem> parseJson(String json) {
       List<MediaItem> mediaItems = new ArrayList<MediaItem>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray!=null&&jsonArray.length()>0) {
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    if (jsonObjectItem!=null) {
                        MediaItem mediaItem = new MediaItem();
                        String movieName = jsonObjectItem.optString("movieName");
                        mediaItem.setName(movieName);
                        String videoTitle = jsonObjectItem.optString("videoTitle");
                        mediaItem.setDescribe(videoTitle);
                        String coverImg = jsonObjectItem.optString("coverImg");
                        mediaItem.setImageUrl(coverImg);
                        String hightUrl = jsonObjectItem.optString("hightUrl");
                        mediaItem.setData(hightUrl);
                        mediaItems.add(mediaItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaItems;
    }
}
