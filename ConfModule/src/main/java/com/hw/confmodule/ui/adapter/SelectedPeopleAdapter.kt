package com.hw.confmodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.confmodule.R
import com.hw.provider.net.respone.contacts.PeopleBean

/**
 * 已选择的成员列表
 */
class SelectedPeopleAdapter :BaseQuickAdapter<PeopleBean,BaseViewHolder>{

    constructor(layoutResId: Int, data: List<PeopleBean>?) : super(layoutResId, data)


    override fun convert(helper: BaseViewHolder, item: PeopleBean?) {
        helper.setText(R.id.tv_name,item?.name)
            .addOnClickListener(R.id.iv_delete)

    }
}