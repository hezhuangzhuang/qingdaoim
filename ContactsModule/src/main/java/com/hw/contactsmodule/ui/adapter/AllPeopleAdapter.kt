package com.hw.contactsmodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.contactsmodule.R
import com.hw.provider.net.respone.contacts.PeopleBean

/**
 *author：pc-20171125
 *data:2020/1/8 15:05
 * 全部联系人
 */
class AllPeopleAdapter(layoutRes: Int, data: MutableList<PeopleBean>) :
    BaseQuickAdapter<PeopleBean, BaseViewHolder>(layoutRes, data) {

    override fun convert(helper: BaseViewHolder, item: PeopleBean) {
        helper.setText(R.id.tvName, item.name)
            .setImageResource(
                R.id.ivHead,
                if (0 == item.online) R.mipmap.ic_personal_head_one_online else R.mipmap.ic_personal_head_one_offline
            )
    }
}