package com.wy.phonemedia.pager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wy.phonemedia.R;
import com.wy.phonemedia.adapter.NetAudioAdapter;
import com.wy.phonemedia.base.BasePager;
import com.wy.phonemedia.entity.AudioItem;
import com.wy.phonemedia.service.MyService;
import com.wy.phonemedia.utils.CashUtils;
import com.wy.phonemedia.utils.LogUtil;
import com.wy.phonemedia.view.XListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Wyman on 2016/9/14.
 * WeChat: wy391920778
 * Effect: 网络音乐
 */
public class NetAudioPager extends BasePager {
    private static final String TAG = "NetAudioPager";
    private XListView mListView;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private NetAudioAdapter adapter;
    private boolean isLoadMore = false;
    private int num=5;
    private String ALL_RES_URL = "http://s.budejie.com/topic/list/jingxuan/1/budejie-android-6.2.8/0-"+num+".json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8";
    /**
     * 页面的数据
     */
    private List<AudioItem.ListBean> mData;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_audio_pager, null);
        mListView = (XListView) view.findViewById(R.id.listView);
        mTextView = (TextView) view.findViewById(R.id.tv_no_net);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                AudioItem.ListBean listBean=mData.get(position);
//                if (listBean.getType().equals("image")) {
//                    Log.e(TAG, "onItemClick:" + listBean.getType());
//                    Intent intent = new Intent(context, ImageActivity.class);
//                    intent.putExtra("url", listBean.getImage().getBig().get(0));
//                    context.startActivity(intent);
//
//                }
//            }
//        });

        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new MyIXListViewListener());
        return view;
    }

    private class MyIXListViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {
            getDataFromNet(ALL_RES_URL);
            onLoad();

        }

        @Override
        public void onLoadMore() {
            num+=5;
            String url="http://s.budejie.com/topic/list/jingxuan/1/budejie-android-6.2.8/0-"+num+".json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8";
            getMoreData(url);
        }
    }

    /**
     * 加载更多数据
     */
    private void getMoreData(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://s.budejie.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyService call = retrofit.create(MyService.class);
        call.getNetAudioData(url).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AudioItem>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(AudioItem audioItem) {
                        //吧更多的数据添加到原来的集合中
                        List<AudioItem.ListBean> moreData = audioItem.getList();
                        mData.addAll(moreData);
                        //刷新适配器
                        adapter.notifyDataSetChanged();
                        onLoad();
                    }
                });
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(new Date()));
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音乐的数据被初始化了...");
        //联网
        getDataFromNet(ALL_RES_URL);
        //音乐内容
    }

    private void getDataFromNet(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://s.budejie.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyService call = retrofit.create(MyService.class);
        call.getNetAudioData(url).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AudioItem>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError:" + e.getMessage());
                        //没有网的时候从缓存中获取数据
                        String saveJson = CashUtils.getString(context, ALL_RES_URL);
                        if (!TextUtils.isEmpty(saveJson)) {
                            processData(null, saveJson);
                        } else {
                            mTextView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNext(AudioItem audioItem) {
                        String json = new Gson().toJson(audioItem);
                        Log.e(TAG, "json:" + json);
                        //缓存
                        CashUtils.putString(context, ALL_RES_URL, json);
                        processData(audioItem, null);
                    }
                });
    }

    /**
     * 处理数据
     *
     * @param json
     */
    private void processData(AudioItem audioItem, String json) {
        if (audioItem != null) {
            mData = audioItem.getList();
            Log.e(TAG, "processData:" + mData.size());
        } else if (json != null) {
            mData = parsedJson(json).getList();
            Log.e(TAG, "processData:" + mData.size());
        }
        Log.e(TAG, "processData:" + "往下走。。。。");
        showData();
    }

    /**
     * 显示数据
     */
    private void showData() {
        if (mData != null && mData.size() > 0) {
            mTextView.setVisibility(View.GONE);
            //设置适配器
            adapter = new NetAudioAdapter(mData, context);
            mListView.setAdapter(adapter);
            onLoad();
        } else {
            mTextView.setVisibility(View.VISIBLE);
        }
        //隐藏pb
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 解析json数据
     *
     * @param json
     * @return
     */
    private AudioItem parsedJson(String json) {
        return new Gson().fromJson(json, AudioItem.class);
    }
}
