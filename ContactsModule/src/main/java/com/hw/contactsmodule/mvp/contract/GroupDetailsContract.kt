package com.hw.contactsmodule.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.provider.net.respone.contacts.PeopleBean

/**
 * 群聊详情界面
 */
interface GroupDetailsContract {

    interface View : IBaseView {
        //显示群聊成员
        fun showGroupChatPeople(groupPeoples: List<PeopleBean>)

        //iscreate：true，群主
        fun showGroupInfo(isCreate: Boolean, groupPeoples: List<PeopleBean>)

        //查询群组信息失败
        fun queryGroupInfoError(errorMsg: String)

        //修改群名称结果
        fun updateGroupNameResult(isSuceess: Boolean, newName: String,groupId:String)

        //删除群聊结果
        fun deleteGroupChatResult(groupId: String)
    }

    interface Presenter {
        //查询群组人员
        fun queryPeopleByGroupId(groupId: String)

        //查询群主
        fun queryGroupCreater(siteUri: String, groupId: String)

        //修改群名称
        fun updateGroupName(
            newName: String, groupId: Int
        )

        //删除群聊
        fun deleteGroupChat(
            groupId: String
        )
    }

}