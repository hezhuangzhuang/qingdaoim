package com.hw.provider.net.respone.contacts

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hw.contactsmodule.ui.adapter.OrganizationAdapter
import java.io.Serializable

/**
 *author：pc-20171125
 *data:2020/1/8 16:10
 */
class PeopleBean(
    val id: String,
    val sip: String,
    val sipAccount: String,
    val name: String,
    val firstLetter: String,
    val isCheck: Boolean = false
) : Serializable, MultiItemEntity {

    override fun getItemType(): Int {
        return OrganizationAdapter.TYPE_LEVEL_PERSON
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PeopleBean

        if (id != other.id) return false
        if (sip != other.sip) return false
        if (sipAccount != other.sipAccount) return false
        if (name != other.name) return false
        if (firstLetter != other.firstLetter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sip.hashCode()
        result = 31 * result + sipAccount.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + firstLetter.hashCode()
        return result
    }


}