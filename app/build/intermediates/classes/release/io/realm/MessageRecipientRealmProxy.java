package io.realm;


import android.util.JsonReader;
import android.util.JsonToken;
import com.nextep.pelmel.model.db.Message;
import com.nextep.pelmel.model.db.MessageRecipient;
import io.realm.RealmObject;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnType;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.LinkView;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Table;
import io.realm.internal.TableOrView;
import io.realm.internal.android.JsonUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageRecipientRealmProxy extends MessageRecipient
    implements RealmObjectProxy {

    private static long INDEX_ITEMKEY;
    private static long INDEX_LASTMESSAGEDATE;
    private static long INDEX_LASTMESSAGEDEFINED;
    private static long INDEX_UNREADMESSAGECOUNT;
    private static long INDEX_IMAGEKEY;
    private static long INDEX_IMAGEURL;
    private static long INDEX_IMAGETHUMBURL;
    private static long INDEX_USERNAME;
    private static long INDEX_USERS;
    private static long INDEX_MESSAGES;
    private static Map<String, Long> columnIndices;
    private static final List<String> FIELD_NAMES;
    static {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("itemKey");
        fieldNames.add("lastMessageDate");
        fieldNames.add("lastMessageDefined");
        fieldNames.add("unreadMessageCount");
        fieldNames.add("imageKey");
        fieldNames.add("imageUrl");
        fieldNames.add("imageThumbUrl");
        fieldNames.add("username");
        fieldNames.add("users");
        fieldNames.add("messages");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    @Override
    public String getItemKey() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_ITEMKEY);
    }

    @Override
    public void setItemKey(String value) {
        realm.checkIfValid();
        row.setString(INDEX_ITEMKEY, (String) value);
    }

    @Override
    public Date getLastMessageDate() {
        realm.checkIfValid();
        return (java.util.Date) row.getDate(INDEX_LASTMESSAGEDATE);
    }

    @Override
    public void setLastMessageDate(Date value) {
        realm.checkIfValid();
        row.setDate(INDEX_LASTMESSAGEDATE, (Date) value);
    }

    @Override
    public boolean isLastMessageDefined() {
        realm.checkIfValid();
        return (boolean) row.getBoolean(INDEX_LASTMESSAGEDEFINED);
    }

    @Override
    public void setLastMessageDefined(boolean value) {
        realm.checkIfValid();
        row.setBoolean(INDEX_LASTMESSAGEDEFINED, (boolean) value);
    }

    @Override
    public int getUnreadMessageCount() {
        realm.checkIfValid();
        return (int) row.getLong(INDEX_UNREADMESSAGECOUNT);
    }

    @Override
    public void setUnreadMessageCount(int value) {
        realm.checkIfValid();
        row.setLong(INDEX_UNREADMESSAGECOUNT, (long) value);
    }

    @Override
    public String getImageKey() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_IMAGEKEY);
    }

    @Override
    public void setImageKey(String value) {
        realm.checkIfValid();
        row.setString(INDEX_IMAGEKEY, (String) value);
    }

    @Override
    public String getImageUrl() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_IMAGEURL);
    }

    @Override
    public void setImageUrl(String value) {
        realm.checkIfValid();
        row.setString(INDEX_IMAGEURL, (String) value);
    }

    @Override
    public String getImageThumbUrl() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_IMAGETHUMBURL);
    }

    @Override
    public void setImageThumbUrl(String value) {
        realm.checkIfValid();
        row.setString(INDEX_IMAGETHUMBURL, (String) value);
    }

    @Override
    public String getUsername() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_USERNAME);
    }

    @Override
    public void setUsername(String value) {
        realm.checkIfValid();
        row.setString(INDEX_USERNAME, (String) value);
    }

    @Override
    public RealmList<MessageRecipient> getUsers() {
        return new RealmList<MessageRecipient>(MessageRecipient.class, row.getLinkList(INDEX_USERS), realm);
    }

    @Override
    public void setUsers(RealmList<MessageRecipient> value) {
        LinkView links = row.getLinkList(INDEX_USERS);
        if (value == null) {
            return;
        }
        links.clear();
        for (RealmObject linkedObject : (RealmList<? extends RealmObject>) value) {
            links.add(linkedObject.row.getIndex());
        }
    }

    @Override
    public RealmList<Message> getMessages() {
        return new RealmList<Message>(Message.class, row.getLinkList(INDEX_MESSAGES), realm);
    }

    @Override
    public void setMessages(RealmList<Message> value) {
        LinkView links = row.getLinkList(INDEX_MESSAGES);
        if (value == null) {
            return;
        }
        links.clear();
        for (RealmObject linkedObject : (RealmList<? extends RealmObject>) value) {
            links.add(linkedObject.row.getIndex());
        }
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if (!transaction.hasTable("class_MessageRecipient")) {
            Table table = transaction.getTable("class_MessageRecipient");
            table.addColumn(ColumnType.STRING, "itemKey");
            table.addColumn(ColumnType.DATE, "lastMessageDate");
            table.addColumn(ColumnType.BOOLEAN, "lastMessageDefined");
            table.addColumn(ColumnType.INTEGER, "unreadMessageCount");
            table.addColumn(ColumnType.STRING, "imageKey");
            table.addColumn(ColumnType.STRING, "imageUrl");
            table.addColumn(ColumnType.STRING, "imageThumbUrl");
            table.addColumn(ColumnType.STRING, "username");
            if (!transaction.hasTable("class_MessageRecipient")) {
                MessageRecipientRealmProxy.initTable(transaction);
            }
            table.addColumnLink(ColumnType.LINK_LIST, "users", transaction.getTable("class_MessageRecipient"));
            if (!transaction.hasTable("class_Message")) {
                MessageRealmProxy.initTable(transaction);
            }
            table.addColumnLink(ColumnType.LINK_LIST, "messages", transaction.getTable("class_Message"));
            table.addSearchIndex(table.getColumnIndex("itemKey"));
            table.setPrimaryKey("itemKey");
            return table;
        }
        return transaction.getTable("class_MessageRecipient");
    }

    public static void validateTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_MessageRecipient")) {
            Table table = transaction.getTable("class_MessageRecipient");
            if (table.getColumnCount() != 10) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field count does not match - expected 10 but was " + table.getColumnCount());
            }
            Map<String, ColumnType> columnTypes = new HashMap<String, ColumnType>();
            for (long i = 0; i < 10; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }

            columnIndices = new HashMap<String, Long>();
            for (String fieldName : getFieldNames()) {
                long index = table.getColumnIndex(fieldName);
                if (index == -1) {
                    throw new RealmMigrationNeededException(transaction.getPath(), "Field '" + fieldName + "' not found for type MessageRecipient");
                }
                columnIndices.put(fieldName, index);
            }
            INDEX_ITEMKEY = table.getColumnIndex("itemKey");
            INDEX_LASTMESSAGEDATE = table.getColumnIndex("lastMessageDate");
            INDEX_LASTMESSAGEDEFINED = table.getColumnIndex("lastMessageDefined");
            INDEX_UNREADMESSAGECOUNT = table.getColumnIndex("unreadMessageCount");
            INDEX_IMAGEKEY = table.getColumnIndex("imageKey");
            INDEX_IMAGEURL = table.getColumnIndex("imageUrl");
            INDEX_IMAGETHUMBURL = table.getColumnIndex("imageThumbUrl");
            INDEX_USERNAME = table.getColumnIndex("username");
            INDEX_USERS = table.getColumnIndex("users");
            INDEX_MESSAGES = table.getColumnIndex("messages");

            if (!columnTypes.containsKey("itemKey")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'itemKey'");
            }
            if (columnTypes.get("itemKey") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'itemKey'");
            }
            if (table.getPrimaryKey() != table.getColumnIndex("itemKey")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Primary key not defined for field 'itemKey'");
            }
            if (!table.hasSearchIndex(table.getColumnIndex("itemKey"))) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Index not defined for field 'itemKey'");
            }
            if (!columnTypes.containsKey("lastMessageDate")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'lastMessageDate'");
            }
            if (columnTypes.get("lastMessageDate") != ColumnType.DATE) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'Date' for field 'lastMessageDate'");
            }
            if (!columnTypes.containsKey("lastMessageDefined")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'lastMessageDefined'");
            }
            if (columnTypes.get("lastMessageDefined") != ColumnType.BOOLEAN) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'boolean' for field 'lastMessageDefined'");
            }
            if (!columnTypes.containsKey("unreadMessageCount")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'unreadMessageCount'");
            }
            if (columnTypes.get("unreadMessageCount") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'unreadMessageCount'");
            }
            if (!columnTypes.containsKey("imageKey")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'imageKey'");
            }
            if (columnTypes.get("imageKey") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'imageKey'");
            }
            if (!columnTypes.containsKey("imageUrl")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'imageUrl'");
            }
            if (columnTypes.get("imageUrl") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'imageUrl'");
            }
            if (!columnTypes.containsKey("imageThumbUrl")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'imageThumbUrl'");
            }
            if (columnTypes.get("imageThumbUrl") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'imageThumbUrl'");
            }
            if (!columnTypes.containsKey("username")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'username'");
            }
            if (columnTypes.get("username") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'username'");
            }
            if (!columnTypes.containsKey("users")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'users'");
            }
            if (columnTypes.get("users") != ColumnType.LINK_LIST) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'MessageRecipient' for field 'users'");
            }
            if (!transaction.hasTable("class_MessageRecipient")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing class 'class_MessageRecipient' for field 'users'");
            }
            Table table_8 = transaction.getTable("class_MessageRecipient");
            if (!table.getLinkTarget(INDEX_USERS).hasSameSchema(table_8)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid RealmList type for field 'users': '" + table.getLinkTarget(INDEX_USERS).getName() + "' expected - was '" + table_8.getName() + "'");
            }
            if (!columnTypes.containsKey("messages")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'messages'");
            }
            if (columnTypes.get("messages") != ColumnType.LINK_LIST) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'Message' for field 'messages'");
            }
            if (!transaction.hasTable("class_Message")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing class 'class_Message' for field 'messages'");
            }
            Table table_9 = transaction.getTable("class_Message");
            if (!table.getLinkTarget(INDEX_MESSAGES).hasSameSchema(table_9)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid RealmList type for field 'messages': '" + table.getLinkTarget(INDEX_MESSAGES).getName() + "' expected - was '" + table_9.getName() + "'");
            }
        } else {
            throw new RealmMigrationNeededException(transaction.getPath(), "The MessageRecipient class is missing from the schema for this Realm.");
        }
    }

    public static String getTableName() {
        return "class_MessageRecipient";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    public static Map<String,Long> getColumnIndices() {
        return columnIndices;
    }

    public static MessageRecipient createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        MessageRecipient obj = null;
        if (update) {
            Table table = realm.getTable(MessageRecipient.class);
            long pkColumnIndex = table.getPrimaryKey();
            if (!json.isNull("itemKey")) {
                long rowIndex = table.findFirstString(pkColumnIndex, json.getString("itemKey"));
                if (rowIndex != TableOrView.NO_MATCH) {
                    obj = new MessageRecipientRealmProxy();
                    obj.realm = realm;
                    obj.row = table.getUncheckedRow(rowIndex);
                }
            }
        }
        if (obj == null) {
            obj = realm.createObject(MessageRecipient.class);
        }
        if (!json.isNull("itemKey")) {
            obj.setItemKey((String) json.getString("itemKey"));
        }
        if (!json.isNull("lastMessageDate")) {
            Object timestamp = json.get("lastMessageDate");
            if (timestamp instanceof String) {
                obj.setLastMessageDate(JsonUtils.stringToDate((String) timestamp));
            } else {
                obj.setLastMessageDate(new Date(json.getLong("lastMessageDate")));
            }
        }
        if (!json.isNull("lastMessageDefined")) {
            obj.setLastMessageDefined((boolean) json.getBoolean("lastMessageDefined"));
        }
        if (!json.isNull("unreadMessageCount")) {
            obj.setUnreadMessageCount((int) json.getInt("unreadMessageCount"));
        }
        if (!json.isNull("imageKey")) {
            obj.setImageKey((String) json.getString("imageKey"));
        }
        if (!json.isNull("imageUrl")) {
            obj.setImageUrl((String) json.getString("imageUrl"));
        }
        if (!json.isNull("imageThumbUrl")) {
            obj.setImageThumbUrl((String) json.getString("imageThumbUrl"));
        }
        if (!json.isNull("username")) {
            obj.setUsername((String) json.getString("username"));
        }
        if (!json.isNull("users")) {
            obj.getUsers().clear();
            JSONArray array = json.getJSONArray("users");
            for (int i = 0; i < array.length(); i++) {
                com.nextep.pelmel.model.db.MessageRecipient item = MessageRecipientRealmProxy.createOrUpdateUsingJsonObject(realm, array.getJSONObject(i), update);
                obj.getUsers().add(item);
            }
        }
        if (!json.isNull("messages")) {
            obj.getMessages().clear();
            JSONArray array = json.getJSONArray("messages");
            for (int i = 0; i < array.length(); i++) {
                com.nextep.pelmel.model.db.Message item = MessageRealmProxy.createOrUpdateUsingJsonObject(realm, array.getJSONObject(i), update);
                obj.getMessages().add(item);
            }
        }
        return obj;
    }

    public static MessageRecipient createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        MessageRecipient obj = realm.createObject(MessageRecipient.class);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("itemKey") && reader.peek() != JsonToken.NULL) {
                obj.setItemKey((String) reader.nextString());
            } else if (name.equals("lastMessageDate")  && reader.peek() != JsonToken.NULL) {
                if (reader.peek() == JsonToken.NUMBER) {
                    long timestamp = reader.nextLong();
                    if (timestamp > -1) {
                        obj.setLastMessageDate(new Date(timestamp));
                    }
                } else {
                    obj.setLastMessageDate(JsonUtils.stringToDate(reader.nextString()));
                }
            } else if (name.equals("lastMessageDefined")  && reader.peek() != JsonToken.NULL) {
                obj.setLastMessageDefined((boolean) reader.nextBoolean());
            } else if (name.equals("unreadMessageCount")  && reader.peek() != JsonToken.NULL) {
                obj.setUnreadMessageCount((int) reader.nextInt());
            } else if (name.equals("imageKey")  && reader.peek() != JsonToken.NULL) {
                obj.setImageKey((String) reader.nextString());
            } else if (name.equals("imageUrl")  && reader.peek() != JsonToken.NULL) {
                obj.setImageUrl((String) reader.nextString());
            } else if (name.equals("imageThumbUrl")  && reader.peek() != JsonToken.NULL) {
                obj.setImageThumbUrl((String) reader.nextString());
            } else if (name.equals("username")  && reader.peek() != JsonToken.NULL) {
                obj.setUsername((String) reader.nextString());
            } else if (name.equals("users")  && reader.peek() != JsonToken.NULL) {
                reader.beginArray();
                while (reader.hasNext()) {
                    com.nextep.pelmel.model.db.MessageRecipient item = MessageRecipientRealmProxy.createUsingJsonStream(realm, reader);
                    obj.getUsers().add(item);
                }
                reader.endArray();
            } else if (name.equals("messages")  && reader.peek() != JsonToken.NULL) {
                reader.beginArray();
                while (reader.hasNext()) {
                    com.nextep.pelmel.model.db.Message item = MessageRealmProxy.createUsingJsonStream(realm, reader);
                    obj.getMessages().add(item);
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return obj;
    }

    public static MessageRecipient copyOrUpdate(Realm realm, MessageRecipient object, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        if (object.realm != null && object.realm.getPath().equals(realm.getPath())) {
            return object;
        }
        MessageRecipient realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(MessageRecipient.class);
            long pkColumnIndex = table.getPrimaryKey();
            if (object.getItemKey() == null) {
                throw new IllegalArgumentException("Primary key value must not be null.");
            }
            long rowIndex = table.findFirstString(pkColumnIndex, object.getItemKey());
            if (rowIndex != TableOrView.NO_MATCH) {
                realmObject = new MessageRecipientRealmProxy();
                realmObject.realm = realm;
                realmObject.row = table.getUncheckedRow(rowIndex);
                cache.put(object, (RealmObjectProxy) realmObject);
            } else {
                canUpdate = false;
            }
        }

        if (canUpdate) {
            return update(realm, realmObject, object, cache);
        } else {
            return copy(realm, object, update, cache);
        }
    }

    public static MessageRecipient copy(Realm realm, MessageRecipient newObject, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        MessageRecipient realmObject = realm.createObject(MessageRecipient.class, newObject.getItemKey());
        cache.put(newObject, (RealmObjectProxy) realmObject);
        realmObject.setItemKey(newObject.getItemKey() != null ? newObject.getItemKey() : "");
        realmObject.setLastMessageDate(newObject.getLastMessageDate() != null ? newObject.getLastMessageDate() : new Date(0));
        realmObject.setLastMessageDefined(newObject.isLastMessageDefined());
        realmObject.setUnreadMessageCount(newObject.getUnreadMessageCount());
        realmObject.setImageKey(newObject.getImageKey() != null ? newObject.getImageKey() : "");
        realmObject.setImageUrl(newObject.getImageUrl() != null ? newObject.getImageUrl() : "");
        realmObject.setImageThumbUrl(newObject.getImageThumbUrl() != null ? newObject.getImageThumbUrl() : "");
        realmObject.setUsername(newObject.getUsername() != null ? newObject.getUsername() : "");

        RealmList<MessageRecipient> usersList = newObject.getUsers();
        if (usersList != null) {
            RealmList<MessageRecipient> usersRealmList = realmObject.getUsers();
            for (int i = 0; i < usersList.size(); i++) {
                MessageRecipient usersItem = usersList.get(i);
                MessageRecipient cacheusers = (MessageRecipient) cache.get(usersItem);
                if (cacheusers != null) {
                    usersRealmList.add(cacheusers);
                } else {
                    usersRealmList.add(MessageRecipientRealmProxy.copyOrUpdate(realm, usersList.get(i), update, cache));
                }
            }
        }


        RealmList<Message> messagesList = newObject.getMessages();
        if (messagesList != null) {
            RealmList<Message> messagesRealmList = realmObject.getMessages();
            for (int i = 0; i < messagesList.size(); i++) {
                Message messagesItem = messagesList.get(i);
                Message cachemessages = (Message) cache.get(messagesItem);
                if (cachemessages != null) {
                    messagesRealmList.add(cachemessages);
                } else {
                    messagesRealmList.add(MessageRealmProxy.copyOrUpdate(realm, messagesList.get(i), update, cache));
                }
            }
        }

        return realmObject;
    }

    static MessageRecipient update(Realm realm, MessageRecipient realmObject, MessageRecipient newObject, Map<RealmObject, RealmObjectProxy> cache) {
        realmObject.setLastMessageDate(newObject.getLastMessageDate() != null ? newObject.getLastMessageDate() : new Date(0));
        realmObject.setLastMessageDefined(newObject.isLastMessageDefined());
        realmObject.setUnreadMessageCount(newObject.getUnreadMessageCount());
        realmObject.setImageKey(newObject.getImageKey() != null ? newObject.getImageKey() : "");
        realmObject.setImageUrl(newObject.getImageUrl() != null ? newObject.getImageUrl() : "");
        realmObject.setImageThumbUrl(newObject.getImageThumbUrl() != null ? newObject.getImageThumbUrl() : "");
        realmObject.setUsername(newObject.getUsername() != null ? newObject.getUsername() : "");
        RealmList<MessageRecipient> usersList = newObject.getUsers();
        RealmList<MessageRecipient> usersRealmList = realmObject.getUsers();
        usersRealmList.clear();
        if (usersList != null) {
            for (int i = 0; i < usersList.size(); i++) {
                MessageRecipient usersItem = usersList.get(i);
                MessageRecipient cacheusers = (MessageRecipient) cache.get(usersItem);
                if (cacheusers != null) {
                    usersRealmList.add(cacheusers);
                } else {
                    usersRealmList.add(MessageRecipientRealmProxy.copyOrUpdate(realm, usersList.get(i), true, cache));
                }
            }
        }
        RealmList<Message> messagesList = newObject.getMessages();
        RealmList<Message> messagesRealmList = realmObject.getMessages();
        messagesRealmList.clear();
        if (messagesList != null) {
            for (int i = 0; i < messagesList.size(); i++) {
                Message messagesItem = messagesList.get(i);
                Message cachemessages = (Message) cache.get(messagesItem);
                if (cachemessages != null) {
                    messagesRealmList.add(cachemessages);
                } else {
                    messagesRealmList.add(MessageRealmProxy.copyOrUpdate(realm, messagesList.get(i), true, cache));
                }
            }
        }
        return realmObject;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("MessageRecipient = [");
        stringBuilder.append("{itemKey:");
        stringBuilder.append(getItemKey());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{lastMessageDate:");
        stringBuilder.append(getLastMessageDate());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{lastMessageDefined:");
        stringBuilder.append(isLastMessageDefined());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{unreadMessageCount:");
        stringBuilder.append(getUnreadMessageCount());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{imageKey:");
        stringBuilder.append(getImageKey());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{imageUrl:");
        stringBuilder.append(getImageUrl());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{imageThumbUrl:");
        stringBuilder.append(getImageThumbUrl());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{username:");
        stringBuilder.append(getUsername());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{users:");
        stringBuilder.append("RealmList<MessageRecipient>[").append(getUsers().size()).append("]");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{messages:");
        stringBuilder.append("RealmList<Message>[").append(getMessages().size()).append("]");
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        String realmName = realm.getPath();
        String tableName = row.getTable().getName();
        long rowIndex = row.getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageRecipientRealmProxy aMessageRecipient = (MessageRecipientRealmProxy)o;

        String path = realm.getPath();
        String otherPath = aMessageRecipient.realm.getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;;

        String tableName = row.getTable().getName();
        String otherTableName = aMessageRecipient.row.getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (row.getIndex() != aMessageRecipient.row.getIndex()) return false;

        return true;
    }

}
