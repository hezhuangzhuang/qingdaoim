package com.hw.confmodule.inject.component

import com.hw.baselibrary.injection.PerComponentScope
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.confmodule.inject.module.ConfModule
import com.hw.confmodule.ui.activity.CreateConfActivity
import com.hw.confmodule.ui.activity.MyConfActivity
import dagger.Component

/**
 *author：pc-20171125
 *data:2020/1/9 14:38
 */
@PerComponentScope
@Component(
    dependencies = arrayOf(ActivityComponent::class),
    modules = arrayOf(ConfModule::class)
)
interface ConfComponent {
    fun inject(createConfActivity:CreateConfActivity)


    fun inject (myConfActivity: MyConfActivity)
}