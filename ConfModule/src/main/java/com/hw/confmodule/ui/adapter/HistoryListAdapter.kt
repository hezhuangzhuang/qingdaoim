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
        item?.apply {
            helper.setImageResource(R.id.iv_img, R.mipmap.ic_conf_3)
                .setText(R.id.tv_conf_name, confName)
                .setText(R.id.tv_time, "开始时间:${createTime}")
                .setText(R.id.tv_originator, "发起人:${creatorUriName}")
                .setText(R.id.tv_account_number, "参会人数:${accountNumber}")
                .setText(R.id.tv_duration, "会议时长:${confLength}分钟")
                .setText(R.id.tv_attendee, sitesName)
        }
    }
}