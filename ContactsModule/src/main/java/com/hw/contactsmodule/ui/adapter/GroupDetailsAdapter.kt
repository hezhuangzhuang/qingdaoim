package com.hw.contactsmodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.contactsmodule.R
import com.hw.provider.net.respone.contacts.PeopleBean
import kotlinx.android.synthetic.main.item_group_detail.view.*

/**
 * 群聊的适配器
 */
class GroupDetailsAdapter :BaseQuickAdapter<PeopleBean,BaseViewHolder>{
    constructor(layoutResId: Int, data: MutableList<PeopleBean>?) : super(layoutResId, data)

    override fun convert(helper: BaseViewHolder, item: PeopleBean?) {
        helper.setText(R.id.tv_name,item?.name)
    }
}