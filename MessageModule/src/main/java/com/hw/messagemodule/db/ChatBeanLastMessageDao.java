package com.hw.messagemodule.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.hw.provider.chat.bean.ChatBeanLastMessage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAT_BEAN_LAST_MESSAGE".
*/
public class ChatBeanLastMessageDao extends AbstractDao<ChatBeanLastMessage, Long> {

    public static final String TABLENAME = "CHAT_BEAN_LAST_MESSAGE";

    /**
     * Properties of entity ChatBeanLastMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MessageType = new Property(1, int.class, "messageType", false, "MESSAGE_TYPE");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property TextContent = new Property(3, String.class, "textContent", false, "TEXT_CONTENT");
        public final static Property Time = new Property(4, java.util.Date.class, "time", false, "TIME");
        public final static Property IsSend = new Property(5, boolean.class, "isSend", false, "IS_SEND");
        public final static Property IsRead = new Property(6, boolean.class, "isRead", false, "IS_READ");
        public final static Property IsGroup = new Property(7, boolean.class, "isGroup", false, "IS_GROUP");
        public final static Property ConversationId = new Property(8, String.class, "conversationId", false, "CONVERSATION_ID");
        public final static Property ConversationUserName = new Property(9, String.class, "conversationUserName", false, "CONVERSATION_USER_NAME");
    }


    public ChatBeanLastMessageDao(DaoConfig config) {
        super(config);
    }
    
    public ChatBeanLastMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_BEAN_LAST_MESSAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"MESSAGE_TYPE\" INTEGER NOT NULL ," + // 1: messageType
                "\"NAME\" TEXT," + // 2: name
                "\"TEXT_CONTENT\" TEXT," + // 3: textContent
                "\"TIME\" INTEGER," + // 4: time
                "\"IS_SEND\" INTEGER NOT NULL ," + // 5: isSend
                "\"IS_READ\" INTEGER NOT NULL ," + // 6: isRead
                "\"IS_GROUP\" INTEGER NOT NULL ," + // 7: isGroup
                "\"CONVERSATION_ID\" TEXT," + // 8: conversationId
                "\"CONVERSATION_USER_NAME\" TEXT);"); // 9: conversationUserName
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_BEAN_LAST_MESSAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChatBeanLastMessage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getMessageType());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String textContent = entity.getTextContent();
        if (textContent != null) {
            stmt.bindString(4, textContent);
        }
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(5, time.getTime());
        }
        stmt.bindLong(6, entity.getIsSend() ? 1L: 0L);
        stmt.bindLong(7, entity.getIsRead() ? 1L: 0L);
        stmt.bindLong(8, entity.getIsGroup() ? 1L: 0L);
 
        String conversationId = entity.getConversationId();
        if (conversationId != null) {
            stmt.bindString(9, conversationId);
        }
 
        String conversationUserName = entity.getConversationUserName();
        if (conversationUserName != null) {
            stmt.bindString(10, conversationUserName);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChatBeanLastMessage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getMessageType());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String textContent = entity.getTextContent();
        if (textContent != null) {
            stmt.bindString(4, textContent);
        }
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(5, time.getTime());
        }
        stmt.bindLong(6, entity.getIsSend() ? 1L: 0L);
        stmt.bindLong(7, entity.getIsRead() ? 1L: 0L);
        stmt.bindLong(8, entity.getIsGroup() ? 1L: 0L);
 
        String conversationId = entity.getConversationId();
        if (conversationId != null) {
            stmt.bindString(9, conversationId);
        }
 
        String conversationUserName = entity.getConversationUserName();
        if (conversationUserName != null) {
            stmt.bindString(10, conversationUserName);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ChatBeanLastMessage readEntity(Cursor cursor, int offset) {
        ChatBeanLastMessage entity = new ChatBeanLastMessage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // messageType
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // textContent
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // time
            cursor.getShort(offset + 5) != 0, // isSend
            cursor.getShort(offset + 6) != 0, // isRead
            cursor.getShort(offset + 7) != 0, // isGroup
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // conversationId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // conversationUserName
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChatBeanLastMessage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMessageType(cursor.getInt(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTextContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTime(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setIsSend(cursor.getShort(offset + 5) != 0);
        entity.setIsRead(cursor.getShort(offset + 6) != 0);
        entity.setIsGroup(cursor.getShort(offset + 7) != 0);
        entity.setConversationId(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setConversationUserName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ChatBeanLastMessage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ChatBeanLastMessage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChatBeanLastMessage entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
