package com.hw.mylibrary.injection.component

import com.hw.baselibrary.injection.PerComponentScope
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.mylibrary.injection.module.UserModule
import com.hw.mylibrary.ui.activity.LoginActivity
import dagger.Component

/**
 *author：Thinkpad
 *data:2019/12/7 09:29
 */
@PerComponentScope
@Component(
    dependencies = arrayOf(ActivityComponent::class),
    modules = arrayOf(UserModule::class)
)
interface UserComponent {
    fun inject(loginActivity: LoginActivity)
}