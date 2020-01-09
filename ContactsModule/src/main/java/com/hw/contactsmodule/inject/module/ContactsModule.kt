package com.hw.contactsmodule.inject.module

import com.hw.contactsmodule.mvp.model.ContactsService
import dagger.Module
import dagger.Provides

/**
 *author：pc-20171125
 *data:2020/1/8 17:21
 */
@Module
class ContactsModule {

    @Provides
    fun provideContactsService(): ContactsService {
        return ContactsService()
    }

}
