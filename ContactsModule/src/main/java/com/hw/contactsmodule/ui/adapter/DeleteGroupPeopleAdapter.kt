package com.hw.contactsmodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.contactsmodule.R
import com.hw.provider.net.respone.contacts.PeopleBean

/**
 *author：pc-20171125
 *data:2020/1/16 20:48
 * 删除群组人员的适配器
 */
class DeleteGroupPeopleAdapter : BaseQuickAdapter<PeopleBean, BaseViewHolder> {
    constructor(layoutResId: Int, data: MutableList<PeopleBean>?) : super(layoutResId, data)

    override fun convert(helper: BaseViewHolder, item: PeopleBean?) {
        helper.setImageResource(
            R.id.iv_check,
            if (item!!.isCheck) R.mipmap.ic_blue_check_true else R.mipmap.ic_blue_check_false
        )
            .setText(R.id.tv_name, item.name)
    }
}