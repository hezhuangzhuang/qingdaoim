package com.hw.confmodule.inject.module

import com.hw.confmodule.mvp.model.CreateConfService
import dagger.Module
import dagger.Provides

/**
 *author：pc-20171125
 *data:2020/1/9 14:38
 */
@Module
class ConfModule {

    @Provides
    fun providesConfService(): CreateConfService {
        return CreateConfService()
    }
}