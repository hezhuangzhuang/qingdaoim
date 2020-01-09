package com.hw.kotlinmvpandroidxframe.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.kotlinmvpandroidxframe.R

/**
 *author：pc-20171125
 *data:2019/11/9 15:46
 */
class TestAdapter(layoutResId: Int, data: MutableList<String>?)//constructor
    : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: String?) {
        helper.setText(R.id.tv_content, item ?: "没有内容")
    }
}