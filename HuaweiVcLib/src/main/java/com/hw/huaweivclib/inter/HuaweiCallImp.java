package com.hw.huaweivclib.inter;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.utils.DeviceManager;
import com.hw.baselibrary.common.AppManager;
import com.hw.baselibrary.common.BaseApp;
import com.hw.baselibrary.net.RetrofitManager;
import com.hw.baselibrary.net.Urls;
import com.hw.baselibrary.rx.scheduler.CustomCompose;
import com.hw.baselibrary.utils.ToastHelper;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.activity.LoadingActivity;
import com.hw.huaweivclib.net.ConfControlApi;
import com.hw.huaweivclib.net.respone.BaseData;
import com.hw.huaweivclib.net.respone.CreateConfResponeBean;
import com.hw.provider.user.UserContants;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.functions.Consumer;
import okhttp3.RequestBody;

/**
 * author：pc-20171125
 * data:2020/1/15 19:37
 */
public class HuaweiCallImp {
    /**
     * 点对点呼叫
     *
     * @param siteNumber  呼叫的号码
     * @param isVideoCall true:视频,false：音频
     * @return
     */
    public static int callSite(String siteNumber, boolean isVideoCall) {
        if (!DeviceManager.isNetworkAvailable(BaseApp.context)) {
            ToastHelper.INSTANCE.showShort("请检查您的网络");
            return -1;
        }
        return CallMgr.getInstance().startCall(siteNumber, isVideoCall);
    }

    public static int joinConf(String accessCode) {
        if (!DeviceManager.isNetworkAvailable(BaseApp.context)) {
            ToastHelper.INSTANCE.showShort("请检查您的网络");
            return -1;
        }

        //是否需要自动接听
        SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, true);

        //是否是加入会议
        SPStaticUtils.put(UIConstants.JOIN_CONF, true);

        //显示等待界面
//        LoadingActivity.startActivty(BaseApp.context, accessCode);
        return CallMgr.getInstance().startCall(accessCode, true);
    }

    /**
     * @param confName      会议名称
     * @param duration      会议时长，单位(分钟)
     * @param memberSipList 参会人员的sip号码，多个以逗号分隔
     * @param groupId
     * @param accessCode    会议接入码
     * @param type          0：语音会议，1：视频会议
     */
    public static void createConfNetWork(String confName,
                                         String duration,
                                         String accessCode,
                                         String memberSipList,
                                         String groupId,
                                         int type) {
        String createUri = SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT);

        //是否自己创建的会议
        SPStaticUtils.put(UIConstants.IS_CREATE, true);

        //是否需要自动接听
        SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, true);

        //显示等待界面
        LoadingActivity.startActivty(BaseApp.context, confName);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("confName", confName);
            jsonObject.put("duration", duration);
            jsonObject.put("accessCode", accessCode);
            jsonObject.put("sites", memberSipList);
            jsonObject.put("creatorUri", createUri);
            jsonObject.put("groupId", groupId);
            jsonObject.put("confMediaType", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(Urls.INSTANCE.getMEDIA_TYPE(), jsonObject.toString());

        RetrofitManager.INSTANCE.create(ConfControlApi.class,
                Urls.FILE_URL
        )
                .createConf(body)
                .compose(new CustomCompose())
                .subscribe(new Consumer<CreateConfResponeBean>() {
                    @Override
                    public void accept(CreateConfResponeBean baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort("会议召集成功");
                        } else {
                            AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);
                        ToastHelper.INSTANCE.showShort(throwable.getMessage());
                    }
                });

    }

}
