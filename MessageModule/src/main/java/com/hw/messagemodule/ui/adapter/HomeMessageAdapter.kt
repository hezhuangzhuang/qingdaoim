package com.hw.messagemodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.messagemodule.R

/**
 *author：pc-20171125
 *data:2020/1/8 10:28
 */
class HomeMessageAdapter(layoutResId: Int, data: MutableList<String>?) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: String?) {
        helper.setText(R.id.tvName, item + "名称")
        helper.setText(R.id.tvContent, item + "内容")
        helper.setText(R.id.tvTime, item + "时间")
    }
}