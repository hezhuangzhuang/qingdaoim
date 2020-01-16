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
import com.hw.provider.huawei.commonservice.common.LocContext;
import com.hw.provider.user.UserContants;

import io.reactivex.functions.Consumer;

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
        LoadingActivity.startActivty(LocContext.getContext(), confName);

        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.FILE_URL)
                .createConf(confName, duration, accessCode, memberSipList, createUri, groupId, type)
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {

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
