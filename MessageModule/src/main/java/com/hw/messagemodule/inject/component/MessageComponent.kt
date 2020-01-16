package com.hw.messagemodule.inject.component

import com.hw.baselibrary.injection.PerComponentScope
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.messagemodule.inject.module.MessageModule
import com.hw.messagemodule.ui.fragment.HomeMessageFragment
import dagger.Component

/**
 *author：pc-20171125
 *data:2020/1/15 14:15
 */
@PerComponentScope
@Component(
    dependencies = arrayOf(ActivityComponent::class),
    modules = arrayOf(MessageModule::class)
)
interface MessageComponent {
    fun inject(fragment: HomeMessageFragment)
}