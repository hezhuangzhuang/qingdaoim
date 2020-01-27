package com.hw.confmodule.ui.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.confmodule.R
import com.hw.confmodule.data.bean.ConfBean
import kotlinx.android.synthetic.main.item_my_conf.view.*

/**
 * 我的会议适配器
 */
class ConfListAdapter : BaseQuickAdapter<ConfBean.DataBean, BaseViewHolder> {

    constructor(layoutResId: Int, data: MutableList<ConfBean.DataBean>?) : super(layoutResId, data)

    override fun convert(helper: BaseViewHolder, item: ConfBean.DataBean) {
        helper.addOnClickListener(R.id.tv_join)
            .setText(R.id.tv_conf_name, item.confName)
            .setImageResource(
                R.id.iv_img,
                if (3 == item.confStatus) R.mipmap.ic_conf_3 else R.mipmap.ic_conf_more
            )
            .setText(R.id.tv_time, item.beginTime)
            .setText(R.id.tv_originator, "发起人:" + item.creatorName)
            .setText(R.id.tv_join_code, item.accessCode)
            .setText(R.id.tv_attendee, getAttendee(item.siteStatusInfoList))
            .setVisible(R.id.tv_join,3 == item.confStatus)
    }

    private fun getAttendee(siteStatusInfoList: List<ConfBean.SiteStatusInfoListBean>): String {
        return siteStatusInfoList.joinToString {
            it.siteName
        }
    }
}