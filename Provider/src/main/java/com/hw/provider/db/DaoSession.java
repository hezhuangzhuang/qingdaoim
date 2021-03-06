package com.hw.provider.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.hw.provider.chat.bean.ChatBean;
import com.hw.provider.chat.bean.ChatBeanLastMessage;
import com.hw.provider.chat.bean.ConstactsBean;
import com.hw.provider.chat.bean.LocalFileBean;

import com.hw.provider.db.ChatBeanDao;
import com.hw.provider.db.ChatBeanLastMessageDao;
import com.hw.provider.db.ConstactsBeanDao;
import com.hw.provider.db.LocalFileBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig chatBeanDaoConfig;
    private final DaoConfig chatBeanLastMessageDaoConfig;
    private final DaoConfig constactsBeanDaoConfig;
    private final DaoConfig localFileBeanDaoConfig;

    private final ChatBeanDao chatBeanDao;
    private final ChatBeanLastMessageDao chatBeanLastMessageDao;
    private final ConstactsBeanDao constactsBeanDao;
    private final LocalFileBeanDao localFileBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        chatBeanDaoConfig = daoConfigMap.get(ChatBeanDao.class).clone();
        chatBeanDaoConfig.initIdentityScope(type);

        chatBeanLastMessageDaoConfig = daoConfigMap.get(ChatBeanLastMessageDao.class).clone();
        chatBeanLastMessageDaoConfig.initIdentityScope(type);

        constactsBeanDaoConfig = daoConfigMap.get(ConstactsBeanDao.class).clone();
        constactsBeanDaoConfig.initIdentityScope(type);

        localFileBeanDaoConfig = daoConfigMap.get(LocalFileBeanDao.class).clone();
        localFileBeanDaoConfig.initIdentityScope(type);

        chatBeanDao = new ChatBeanDao(chatBeanDaoConfig, this);
        chatBeanLastMessageDao = new ChatBeanLastMessageDao(chatBeanLastMessageDaoConfig, this);
        constactsBeanDao = new ConstactsBeanDao(constactsBeanDaoConfig, this);
        localFileBeanDao = new LocalFileBeanDao(localFileBeanDaoConfig, this);

        registerDao(ChatBean.class, chatBeanDao);
        registerDao(ChatBeanLastMessage.class, chatBeanLastMessageDao);
        registerDao(ConstactsBean.class, constactsBeanDao);
        registerDao(LocalFileBean.class, localFileBeanDao);
    }
    
    public void clear() {
        chatBeanDaoConfig.clearIdentityScope();
        chatBeanLastMessageDaoConfig.clearIdentityScope();
        constactsBeanDaoConfig.clearIdentityScope();
        localFileBeanDaoConfig.clearIdentityScope();
    }

    public ChatBeanDao getChatBeanDao() {
        return chatBeanDao;
    }

    public ChatBeanLastMessageDao getChatBeanLastMessageDao() {
        return chatBeanLastMessageDao;
    }

    public ConstactsBeanDao getConstactsBeanDao() {
        return constactsBeanDao;
    }

    public LocalFileBeanDao getLocalFileBeanDao() {
        return localFileBeanDao;
    }

}
