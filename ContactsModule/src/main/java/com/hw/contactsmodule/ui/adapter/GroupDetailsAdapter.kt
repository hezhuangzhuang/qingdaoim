package com.hw.contactsmodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.contactsmodule.R
import com.hw.provider.net.respone.contacts.PeopleBean

/**
 * 群聊详情的适配器
 */
class GroupDetailsAdapter : BaseQuickAdapter<PeopleBean, BaseViewHolder> {
    constructor(layoutResId: Int, data: MutableList<PeopleBean>?) : super(layoutResId, data)

    override fun convert(helper: BaseViewHolder, item: PeopleBean?) {
        if ("-1".equals(item?.id)) {
            helper.setText(R.id.tv_name, "添加")
                .setImageResource(R.id.ivHead, R.mipmap.ic_add_group_people)
                .addOnClickListener(R.id.ivHead)
        } else {
            helper.setText(R.id.tv_name, item?.name)
                .setImageResource(R.id.ivHead, R.mipmap.ic_personal_head_one_online)
        }
    }
}