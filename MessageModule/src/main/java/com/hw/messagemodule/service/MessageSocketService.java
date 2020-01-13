package com.hw.messagemodule.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.hw.baselibrary.net.Urls;
import com.hw.baselibrary.utils.LogUtils;

import java.net.URI;

public class MessageSocketService extends Service {
    private MessageSocketClient socketClient;


    public MessageSocketService() {
    }

    /**
     * 启动服务
     *
     * @param context
     */
    public static void startService(Context context) {
        Intent startIntent = new Intent(context, MessageSocketService.class);
        context.startService(startIntent);
    }

    /**
     * 停止服务
     *
     * @param context
     */
    public static void stopService(Context context) {
        Intent stopIntent = new Intent(context, MessageSocketService.class);
        context.stopService(stopIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSocketClient();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化socketClient
     */
    private void initSocketClient() {
        try {
            URI uri = URI.create(Urls.WEBSOCKET_URL);
            socketClient = new MessageSocketClient(uri) {
                @Override
                public void onMessage(String message) {
                    LogUtils.e("MessageSocketService-->message-->" + message);
                    handleMsg(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    super.onClose(code, reason, remote);
                }
            };
            socketClient.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理消息
     *
     * @param message
     */
    private void handleMsg(String message) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
