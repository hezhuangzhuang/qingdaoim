package com.hw.mylibrary.injection.module

import com.hw.mylibrary.mvp.model.UserService
import dagger.Module
import dagger.Provides

/**
 *author：Thinkpad
 *data:2019/12/7 09:29
 */
@Module
class UserModule {

    @Provides
    fun providesUserService(): UserService {
        return UserService()
    }
}