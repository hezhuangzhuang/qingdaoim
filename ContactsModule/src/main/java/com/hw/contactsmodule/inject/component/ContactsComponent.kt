package com.hw.contactsmodule.inject.component

import com.hw.baselibrary.injection.PerComponentScope
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.contactsmodule.inject.module.ContactsModule
import com.hw.contactsmodule.ui.fragment.ContactsFragment
import dagger.Component

/**
 *author：pc-20171125
 *data:2020/1/8 17:19
 */

@PerComponentScope
@Component(
    dependencies = arrayOf(ActivityComponent::class),
    modules = arrayOf(ContactsModule::class)
)
interface ContactsComponent {
    fun inject(contactsFragment: ContactsFragment)
}