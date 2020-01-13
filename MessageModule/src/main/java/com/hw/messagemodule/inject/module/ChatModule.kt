package com.hw.messagemodule.inject.module

import com.hw.messagemodule.mvp.model.ChatService
import dagger.Module
import dagger.Provides

/**
 *author：pc-20171125
 *data:2020/1/13 19:16
 */
@Module
class ChatModule {

    @Provides
    fun provideContactsService(): ChatService {
        return ChatService()
    }

}