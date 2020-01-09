package com.hw.contactsmodule.ui.adapter.item

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hw.provider.net.respone.contacts.OrganizationBean
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.contactsmodule.ui.adapter.OrganizationAdapter

/**
 *author：pc-20171125
 *data:2020/1/8 19:10
 */
class OrganizationItem( var organizationBean: OrganizationBean) : AbstractExpandableItem<PeopleBean>(),
    MultiItemEntity {

    override fun getLevel(): Int {
        return OrganizationAdapter.TYPE_LEVEL_ORGANIZATION;
    }

    override fun getItemType(): Int {
        return OrganizationAdapter.TYPE_LEVEL_ORGANIZATION;
    }
}