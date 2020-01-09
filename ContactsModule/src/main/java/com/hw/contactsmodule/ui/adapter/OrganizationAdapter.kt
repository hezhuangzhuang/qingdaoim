package com.hw.contactsmodule.ui.adapter

import android.view.View
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hw.contactsmodule.R
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.contactsmodule.ui.adapter.item.OrganizationItem


/**
 *author：pc-20171125
 *data:2020/1/8 15:08
 * 组织结构的适配器
 */
class OrganizationAdapter : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    companion object {
        const val TYPE_LEVEL_ORGANIZATION = 1
        const val TYPE_LEVEL_PERSON = 2
    }

    constructor(data: MutableList<MultiItemEntity>?) : super(data) {
        addItemType(TYPE_LEVEL_ORGANIZATION, R.layout.item_organization_head)
        addItemType(TYPE_LEVEL_PERSON, R.layout.item_all_people)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity?) {
        when (helper.itemViewType) {
            TYPE_LEVEL_ORGANIZATION -> setOrganizationInfo(helper, item)
            TYPE_LEVEL_PERSON -> setPersonInfo(helper, item)
        }
    }

    /**
     * 设置组织信息
     */
    private fun setOrganizationInfo(helper: BaseViewHolder, item: MultiItemEntity?) {
        var organizationItem = item as OrganizationItem;
        var organName = organizationItem.organizationBean.depName
        //设置名称
        helper.setText(R.id.tvName, "${organName}(${organizationItem.organizationBean.count})")
            .setImageResource(
                R.id.ivArrow,
                if (organizationItem.isExpanded) R.mipmap.arrow_b else R.mipmap.arrow_r
            )
        helper.setOnClickListener(R.id.ivArrow, object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //判断组织下是否有成员
                if (organizationItem.organizationBean.count > 0) {
                    var pos = helper.adapterPosition

                    //展开状态
                    if (organizationItem.isExpanded) {
                        clildClickLis?.onCollapseClick(pos, organizationItem.organizationBean.depId)
                    } else {
                        clildClickLis?.onExpandClick(pos, organizationItem.organizationBean.depId)
                    }
                }
            }
        })
    }

    /**
     * 设置人员信息
     */
    private fun setPersonInfo(helper: BaseViewHolder, item: MultiItemEntity?) {
        var peopleBean = item as PeopleBean;

        helper.setText(R.id.tvName, peopleBean.name)

        helper.setOnClickListener(R.id.tvName, object : View.OnClickListener {
            override fun onClick(p0: View?) {
                clildClickLis?.onPersonClick(peopleBean)
            }
        })
    }

    interface onClildClickLis {
        fun onCollapseClick(pos: Int, depId: Int)

        fun onExpandClick(pos: Int, depId: Int)

        fun onPersonClick(peopleBean: PeopleBean)
    }

    var clildClickLis: onClildClickLis? = null

    fun setChildClick(childClick: onClildClickLis) {
        this.clildClickLis = childClick
    }

}
