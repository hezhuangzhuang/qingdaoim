package com.hw.confmodule.mvp.model

import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.confmodule.data.api.ConfApi
import com.hw.confmodule.data.bean.ConfBean
import com.hw.confmodule.data.bean.HistoryConfBean
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.provider.constacts.impl.ContactsModuleRouteService
import com.hw.provider.user.UserContants
import io.reactivex.Observable
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.http.Query
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/9 14:27
 */
class CreateConfService @Inject constructor() {

    /**
     * 获取所有联系人
     */
    fun getAllPeople(): Observable<BaseData<PeopleBean>> {
        return ContactsModuleRouteService.getAllPeople()
    }

    /**
     * 获取当前登录账户的所有会议
     */
    fun getAllConf(siteUri: String): Observable<ConfBean> {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("siteUri", SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString())

        return RetrofitManager.create(ConfApi::class.java, Urls.FILE_URL)
            .getAllConfList(body)
            .compose(CustomCompose())
    }

    /**
     * 获取历史会议列表
     */
    fun getHistoryConfList(pageNum: Int): Observable<HistoryConfBean> {
        return RetrofitManager.create(ConfApi::class.java, Urls.WEBSOCKET_URL)
            .getHistoryConfList(pageNum = pageNum)
            .compose(CustomCompose())
    }

    /**
     * 创建群组
     */
    fun createGroupChat(
        groupName: String,
        createId: String,
        ids: String
    ): Observable<BaseData<String>> {
        return RetrofitManager.create(ConfApi::class.java, Urls.WEBSOCKET_URL)
            .createGroupChat(groupName, createId, ids)
            .compose(CustomCompose())
    }
}