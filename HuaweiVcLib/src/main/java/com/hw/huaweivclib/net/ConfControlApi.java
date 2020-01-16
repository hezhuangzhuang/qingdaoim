package com.hw.huaweivclib.net;

import com.hw.baselibrary.common.BaseData;
import com.hw.provider.net.respone.contacts.PeopleBean;

import io.reactivex.Observable;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * author：pc-20171125
 * data:2020/1/15 20:00
 */
public interface ConfControlApi {
    /**
     * 创建会议
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/scheduleConf")
    Observable<BaseData<PeopleBean>> createConf(
            @Query("confName") String confName,
            @Query("duration") String duration,
            @Query("accessCode") String accessCode,
            @Query("sites") String sites,
            @Query("creatorUri") String creatorUri,
            @Query("groupId") String groupId,
            @Query("confMediaType") int confMediaType
    );
}
