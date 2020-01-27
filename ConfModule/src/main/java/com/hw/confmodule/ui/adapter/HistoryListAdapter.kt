package com.hw.confmodule.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.confmodule.R
import com.hw.confmodule.data.bean.HistoryConfBean

/**
 * 历史会议适配器
 */
class HistoryListAdapter : BaseQuickAdapter<HistoryConfBean.DataBean, BaseViewHolder> {
    constructor(layoutResId: Int, data: MutableList<HistoryConfBean.DataBean>?) : super(
        layoutResId,
        data
    )

    override fun convert(helper: BaseViewHolder, item: HistoryConfBean.DataBean?) {
        helper.addOnClickListener(R.id.tv_join)
            .setImageResource(R.id.iv_img, R.mipmap.ic_conf_3)
            .setText(R.id.tv_conf_name, item?.confName)
            .setText(R.id.tv_time, item?.createTime)
            .setText(R.id.tv_originator, "发起人:" + item?.creatorUriName)
            .setText(R.id.tv_attendee, item?.sitesName)
    }
}