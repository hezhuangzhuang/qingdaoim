package com.hw.contactsmodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.contactsmodule.R
import com.hw.provider.net.respone.contacts.GroupChatBean

/**
 *author：pc-20171125
 *data:2020/1/8 15:16
 * 群聊的名称
 */
class GroupChatAdapter : BaseQuickAdapter<GroupChatBean, BaseViewHolder> {
    constructor(layoutResId: Int, data: MutableList<GroupChatBean>?) : super(layoutResId, data)

    override fun convert(helper: BaseViewHolder, item: GroupChatBean?) {
        helper.setText(R.id.tvName, item?.groupName)
    }

}
