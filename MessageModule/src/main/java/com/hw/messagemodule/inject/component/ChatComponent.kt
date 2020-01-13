package com.hw.messagemodule.inject.component

import com.hw.baselibrary.injection.PerComponentScope
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.messagemodule.inject.module.ChatModule
import com.hw.messagemodule.ui.activity.ChatActivity
import dagger.Component

/**
 *author：pc-20171125
 *data:2020/1/13 19:17
 */
@PerComponentScope
@Component(dependencies = arrayOf(ActivityComponent::class),
    modules = arrayOf(ChatModule::class))
interface ChatComponent {

    fun inject(activity:ChatActivity)
}
