package com.hw.contactsmodule.mvp.contract

import com.hw.baselibrary.common.IBaseView

/**
 * 删除群组成员
 */
interface DeleteGroupPeopleContract {
    interface View : IBaseView {
        fun deletePeopleSuccess()

        fun deletePeopleFailed(errorMsg: String)
    }

    interface Presenter {
        fun deletePeoples(groupId: String, ids: String)
    }
}