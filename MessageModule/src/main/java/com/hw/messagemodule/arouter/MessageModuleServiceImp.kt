package com.hw.messagemodule.arouter

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.messagemodule.db.GreenDaoUtil
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.message.IMessageModuleService
import com.hw.provider.user.UserContants

/**
 *author：pc-20171125
 *data:2020/1/15 10:09
 * 实际提供初始化的地方
 */
@Route(path = RouterPath.Chat.CHAT_MODULE_SERVICE)
class MessageModuleServiceImp : IMessageModuleService {
    /**
     * 初始化数据库
     */
    override fun initDb() {
        GreenDaoUtil.initDataBase(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT))
    }

    override fun init(context: Context?) {
    }
}