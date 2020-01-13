package com.hw.messagemodule.service;

import com.hw.baselibrary.utils.LogUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * author：pc-20171125
 * data:2020/1/13 18:04
 */
public class MessageSocketClient extends WebSocketClient {
    public static final String TAG = "MessageSocketClient";

    public MessageSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LogUtils.i(TAG + "onOpen");
    }

    @Override
    public void onMessage(String message) {
        LogUtils.i(TAG + "onMessage"+message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtils.i(TAG + "onClose"+reason);
    }

    @Override
    public void onError(Exception ex) {
        LogUtils.i(TAG + "onError"+ex.getMessage());
    }
}
