package com.hw.huaweivclib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.opensdk.ecsdk.logic.CallFunc;
import com.huawei.opensdk.ecsdk.utils.ActivityUtil;
import com.huawei.opensdk.ecsdk.utils.IntentConstant;
import com.hw.baselibrary.ui.activity.BaseActivity;
import com.hw.baselibrary.utils.NotificationUtils;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.provider.huawei.commonservice.common.LocContext;
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcastReceiver;
import com.hw.provider.huawei.commonservice.util.LogUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * author：pc-20171125
 * data:2019/1/4 14:31
 */
public abstract class BaseMediaActivity extends BaseActivity implements LocBroadcastReceiver {
    private static final int CALL_CONNECTED = 100;
    private static final int CALL_UPGRADE = 101;
    private static final int HOLD_CALL_SUCCESS = 102;
    private static final int VIDEO_HOLD_CALL_SUCCESS = 103;
    private static final int MEDIA_CONNECTED = 104;

    private String[] mActions = new String[]{
            CustomBroadcastConstants.ACTION_CALL_CONNECTED,
            CustomBroadcastConstants.CALL_MEDIA_CONNECTED,
            CustomBroadcastConstants.CONF_CALL_CONNECTED,
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.CALL_UPGRADE_ACTION,
            CustomBroadcastConstants.HOLD_CALL_RESULT
    };

    protected String mCallNumber;
    protected String mDisplayName;
    protected boolean mIsVideoCall;
    protected int mCallID = -1;
    protected String mConfID;
    protected boolean mIsConfCall;
    protected boolean mIsCaller;

    private CallFunc mCallFunc;

    private boolean isJoin = false;

    private Gson gson = new Gson();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEDIA_CONNECTED:
                    if (msg.obj instanceof CallInfo && !isJoin) {
                        CallInfo callInfo = (CallInfo) msg.obj;
                        Log.i(TAG, "69-->" + callInfo.toString());
                        if (!callInfo.isVideoCall()) {
                            Intent intent = new Intent(IntentConstant.AUDIO_ACTIVITY_ACTION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//                            intent.putExtra(UIConstants.CALL_INFO, callInfo);

                            SPStaticUtils.put(UIConstants.CALL_INFO, gson.toJson(callInfo));

                            ActivityUtil.startActivity(BaseMediaActivity.this, intent);

                            isJoin = true;
                            finishActivityLine(73);
                        }
                    }
                    break;

                case CALL_CONNECTED:
                    if (msg.obj instanceof CallInfo) {
                        CallInfo callInfo = (CallInfo) msg.obj;
                        Log.i(TAG, "69-->" + callInfo.toString());
                        if (callInfo.isVideoCall()) {
                            boolean isConf = MeetingMgr.getInstance().judgeInviteFormMySelf(callInfo.getConfID());
                            try {
                                isConf = SPStaticUtils.getBoolean(UIConstants.IS_AUTO_ANSWER, false);
                            } catch (Exception e) {
                                isConf = false;
                            }

                            if (isConf) {
                                SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, false);
                                LogUtil.i(UIConstants.DEMO_TAG, "呼叫内容:" + callInfo.toString());
                                String confID = callInfo.getCallID() + "";
                                Intent intent = new Intent(IntentConstant.VIDEO_CONF_ACTIVITY_ACTION);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(UIConstants.CONF_ID, confID);
                                intent.putExtra(UIConstants.CALL_ID, callInfo.getCallID());
                                intent.putExtra(UIConstants.PEER_NUMBER, callInfo.getPeerNumber());

                                SPStaticUtils.put(UIConstants.CALL_INFO, gson.toJson(callInfo));

                                ActivityUtil.startActivity(LocContext.getContext(), intent);

                                finishActivityLine(191);
                            } else {
                                Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//                              intent.putExtra(UIConstants.CALL_INFO, callInfo);

                                SPStaticUtils.put(UIConstants.CALL_INFO, gson.toJson(callInfo));

                                //TODO:判断是否是会议
                                intent.putExtra(UIConstants.IS_MEETING, false);
                                ActivityUtil.startActivity(BaseMediaActivity.this, intent);
                                finishActivityLine(90);
                            }
                        }
                    }
                    break;

                case CALL_UPGRADE:
                    break;

                case HOLD_CALL_SUCCESS: {
                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
                }
                break;

                case VIDEO_HOLD_CALL_SUCCESS: {
                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
                    textCallNumber = textCallNumber + "Holding";
                }
                break;

                default:
                    break;
            }
        }
    };

    private void finishActivityLine(int line) {
        finish();
    }

    private String TAG = BaseMediaActivity.class.getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    @Override
    public void onReceive(String broadcastName, final Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_CALL_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(CALL_CONNECTED, obj));
                break;

            case CustomBroadcastConstants.CALL_MEDIA_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(MEDIA_CONNECTED, obj));
                break;

            case CustomBroadcastConstants.CONF_CALL_CONNECTED:
//                PreferencesHelper.saveData(UIConstants.IS_AUTO_ANSWER, false);
                SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER,false);

                CallInfo callInfo = (CallInfo) obj;
                LogUtil.i(UIConstants.DEMO_TAG, "呼叫内容:" + callInfo.toString());
                String confID = callInfo.getCallID() + "";
                Intent intent = new Intent(IntentConstant.VIDEO_CONF_ACTIVITY_ACTION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(UIConstants.CONF_ID, confID);
                intent.putExtra(UIConstants.CALL_ID, callInfo.getCallID());
                intent.putExtra(UIConstants.PEER_NUMBER, callInfo.getPeerNumber());

//                PreferencesHelper.saveData(UIConstants.CALL_INFO, callInfo);

                SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER,gson.toJson(callInfo));

                ActivityUtil.startActivity(LocContext.getContext(), intent);
                finishActivityLine(191);
                break;

            case CustomBroadcastConstants.ACTION_CALL_END:
                if (obj instanceof CallInfo) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CallInfo params = (CallInfo) obj;
                            if (403 == params.getReasonCode() || 603 == params.getReasonCode()) {
                                Toast.makeText(BaseMediaActivity.this, "对方已拒接", Toast.LENGTH_SHORT).show();
                                finishActivityLine(202);
                            } else if (404 == params.getReasonCode()) {
                                Toast.makeText(BaseMediaActivity.this, "对方不在线", Toast.LENGTH_SHORT).show();
                                finishActivityLine(208);
                                //getMainThreadHandler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        finishActivityLine(208);
//                                    }
//                                }, 3000);
                            } else if (486 == params.getReasonCode()) {
                                Toast.makeText(BaseMediaActivity.this, "对方正忙", Toast.LENGTH_SHORT).show();
                                //主叫
                                if (params.isCaller()) {
                                    //发送消息
                                    sendTextMsg("已挂断", params.getPeerNumber(), params.isVideoCall());
                                }

                                //保存消息到本地
                                saveLocalMessage("对方正忙", params.getPeerNumber(), params.isVideoCall());

                                finishActivityLine(208);
                            } else if (0 == params.getReasonCode()) {
                                Toast.makeText(BaseMediaActivity.this, "通话结束", Toast.LENGTH_SHORT).show();
                                //如果是主叫
                                if (params.isCaller()) {
                                    //发送消息
                                    //保存消息到本地
                                    saveLocalMessage("已取消", params.getPeerNumber(), params.isVideoCall());
                                }
//                                else {
//                                    保存消息到本地
//                                    saveLocalMessage("已挂断", params.getPeerNumber(), params.isVideoCall());
//                                }
                                finishActivityLine(262);
                            } else {
                                finishActivityLine(210);
                            }

                            //删除notif
                            //NotificationUtils.cancelAll();
                            NotificationUtils.cancel(NotificationUtils.CALL_IN_ID);
                            NotificationUtils.cancel(NotificationUtils.AUDIO_ID);
                            NotificationUtils.cancel(NotificationUtils.VIDEO_ID);
                        }
                    });
                }
                break;

            case CustomBroadcastConstants.CALL_UPGRADE_ACTION:
                mHandler.sendEmptyMessage(CALL_UPGRADE);
                break;

            case CustomBroadcastConstants.HOLD_CALL_RESULT:
                if ("HoldSuccess".equals(obj)) {
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                } else if ("UnHoldSuccess".equals(obj)) {
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                } else if ("VideoHoldSuccess".equals(obj)) {
                    mHandler.sendEmptyMessage(VIDEO_HOLD_CALL_SUCCESS);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 发送消息
     *
     * @param textMsg
     * @param peerNumber
     */
    private boolean sendTextMsg(String textMsg,
                                String peerNumber,
                                boolean isVideoCall) {

//        String account = LoginCenter.getInstance().getAccount();
//
//        String disPlayName = PreferenceUtil.getString(this, Constant.DISPLAY_NAME, "");
//
//        MessageReal messageReal = new MessageReal(
//                textMsg,
//                isVideoCall ? MessageReal.TYPE_VIDEO_CALL : MessageReal.TYPE_VOICE_CALL,
//                "");
//
//        MessageBody msg = new MessageBody(
//                account,
////                Constant.CurrDisPlayName,
//                disPlayName,
//                peerNumber,
//                peerNumber,
//                messageReal,
//                MessageBody.TYPE_PERSONAL
//        );
//        return MsgIOServer.sendMsg(msg);

        return false;
    }

    /**
     * 保存消息到数据库
     *
     * @param textMsg
     * @param peerNumber
     */
    private void saveLocalMessage(String textMsg,
                                  String peerNumber,
                                  boolean isVideoCall) {
//        String disPlayName = PreferenceUtil.getString(this, Constant.DISPLAY_NAME, "");
//
//        ChatBean sendMsg = new ChatBean(
//                isVideoCall ? MultipleItem.SEND_VIDEO_CALL : MultipleItem.SEND_VOICE_CALL,
////                Constant.CurrDisPlayName,
//                disPlayName,
//                new Date(),
//                textMsg);
//        sendMsg.sendId = LoginCenter.getInstance().getAccount();
//        sendMsg.sendName = disPlayName;
//        sendMsg.receiveId = peerNumber;
//        sendMsg.receiveName = peerNumber;
//        sendMsg.isSend = true;
//        sendMsg.isRead = false;
//        sendMsg.conversationId = peerNumber;//会话id
//        sendMsg.conversationUserName = peerNumber;//会话id
//        ChatItem chatItem = new ChatItem(sendMsg);
//
//        //保存消息列表
//        DBUtils.recordSaveIten(chatItem);
//        //保存到最近的消息列表
//        DBUtils.saveLMItem(chatItem);
//
//        //更新单个消息
//        EventMsg eventMsg = new EventMsg();
//        eventMsg.setMessageData(sendMsg);
//        eventMsg.setMsg(EventMsg.RECEIVE_SINGLE_MESSAGE);
//        EventBus.getDefault().post(eventMsg);
//
//        //更新首页消息
//        eventMsg = new EventMsg();
//        eventMsg.setMessageData(sendMsg);
//        eventMsg.setMsg(EventMsg.UPDATE_HOME);
//        EventBus.getDefault().post(eventMsg);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCallFunc = CallFunc.getInstance();

        Intent intent = getIntent();

        CallInfo callInfo = gson.fromJson(SPStaticUtils.getString(UIConstants.CALL_INFO), CallInfo.class);

//        CallInfo callInfo = PreferencesHelper.getData(, CallInfo.class);
        Log.i(TAG, "120-->" + callInfo.toString());
        mCallNumber = callInfo.getPeerNumber();
        mDisplayName = callInfo.getPeerDisplayName();
        mIsVideoCall = callInfo.isVideoCall();
        mCallID = callInfo.getCallID();
        mConfID = callInfo.getConfID();
        mIsConfCall = callInfo.isFocus();
        mIsCaller = callInfo.isCaller();
    }



    @Override
    public void onError(@NotNull String text) {

    }
}
