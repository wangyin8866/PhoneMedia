package com.wy.phonemedia.service;

import com.wy.phonemedia.entity.AudioItem;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Wyman on 2016/9/27.
 * WeChat: wy391920778
 * Effect:
 */

public interface MyService {
    @GET("TrailerList.api")
    Observable<String> getNetMovieData();
    @POST
    Observable<AudioItem>getNetAudioData(@Url String url);
}
