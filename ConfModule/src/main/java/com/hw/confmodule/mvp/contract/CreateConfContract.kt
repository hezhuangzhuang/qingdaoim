package com.hw.confmodule.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.provider.net.respone.contacts.PeopleBean

/**
 *author：pc-20171125
 *data:2020/1/9 14:26
 */
interface CreateConfContract {

    interface View : IBaseView {
        //创建会议成功
        fun createConfSuccess()

        //创建会议失败
        fun createConfFaile()

        //查询人员成功
        fun queryPeopleSuccess(allPeople: List<PeopleBean>)

        //查询人员失败
        fun queryPeopleError(errorMsg: String)

        //创建群组成功
        fun createGroupChatSuccess()

        //创建群组失败
        fun createGroupChatError(errorMsg: String)
    }

    interface Presenter {
        //创建会议
        fun createConf(confName: String,
                       duration: String,
                       accessCode: String,
                       memberSipList: String,
                       groupId: String,
                       type: Int)

        //获取所有联系人
        fun queryAllPeople()

        /**
         * 创建群组
         */
        fun createGroupChat(
            groupName: String,
            createId: String,
            ids: String
        )
    }
}