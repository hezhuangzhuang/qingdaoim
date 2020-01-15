package com.hw.messagemodule.inject.module

import com.hw.messagemodule.mvp.model.MessageService
import dagger.Module
import dagger.Provides

/**
 *author：pc-20171125
 *data:2020/1/8 10:07
 */
@Module
class MessageModule {

    @Provides
    fun providesMessageService(): MessageService {
        return MessageService()
    }
}