package com.huawei.opensdk.sdkwrapper.login;


import com.huawei.tup.login.LoginAuthorizeResult;

import object.TupRegisterResult;

/**
 * This class is about login status result.
 * 登录状态结果类
 */
public class LoginStatus {

    /**
     * user id
     * 用户id
     */
    private int userID;

    /**
     * SP&IMS Hosted VC login authorize result
     * (SP&IMS Hosted VC) 鉴权登录结果
     */
    private LoginAuthorizeResult authResult;

    /**
     * Register result
     * 注册结果信息
     */
    private TupRegisterResult callResult;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public LoginAuthorizeResult getAuthResult() {
        return authResult;
    }

    public void setAuthResult(LoginAuthorizeResult authResult) {
        this.authResult = authResult;
    }

    public TupRegisterResult getCallResult() {
        return callResult;
    }

    public void setCallResult(TupRegisterResult callResult) {
        this.callResult = callResult;
    }

    @Override
    public String toString() {
        return "LoginStatus{" +
                "userID=" + userID +
                ", authResult=" + authResult +
                ", callResult=" + getTupRegisterResultString() +
                '}';
    }

    private String getTupRegisterResultString(){
        return "regState= "+callResult.getRegState()+
                ", reasonCode= "+callResult.getReasonCode()+
                ", sipAccountID= "+callResult.getSipAccountID();
    }
}
