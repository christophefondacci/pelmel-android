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

public class MessageRealmProxy extends Message
    implements RealmObjectProxy {

    private static long INDEX_MESSAGEKEY;
    private static long INDEX_MESSAGEDATE;
    private static long INDEX_UNREAD;
    private static long INDEX_MESSAGEIMAGEKEY;
    private static long INDEX_MESSAGEIMAGEURL;
    private static long INDEX_MESSAGEIMAGETHUMBURL;
    private static long INDEX_MESSAGETEXT;
    private static long INDEX_TOITEMKEY;
    private static long INDEX_FROM;
    private static long INDEX_REPLYTO;
    private static Map<String, Long> columnIndices;
    private static final List<String> FIELD_NAMES;
    static {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("messageKey");
        fieldNames.add("messageDate");
        fieldNames.add("unread");
        fieldNames.add("messageImageKey");
        fieldNames.add("messageImageUrl");
        fieldNames.add("messageImageThumbUrl");
        fieldNames.add("messageText");
        fieldNames.add("toItemKey");
        fieldNames.add("from");
        fieldNames.add("replyTo");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    @Override
    public String getMessageKey() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_MESSAGEKEY);
    }

    @Override
    public void setMessageKey(String value) {
        realm.checkIfValid();
        row.setString(INDEX_MESSAGEKEY, (String) value);
    }

    @Override
    public Date getMessageDate() {
        realm.checkIfValid();
        return (java.util.Date) row.getDate(INDEX_MESSAGEDATE);
    }

    @Override
    public void setMessageDate(Date value) {
        realm.checkIfValid();
        row.setDate(INDEX_MESSAGEDATE, (Date) value);
    }

    @Override
    public boolean isUnread() {
        realm.checkIfValid();
        return (boolean) row.getBoolean(INDEX_UNREAD);
    }

    @Override
    public void setUnread(boolean value) {
        realm.checkIfValid();
        row.setBoolean(INDEX_UNREAD, (boolean) value);
    }

    @Override
    public String getMessageImageKey() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_MESSAGEIMAGEKEY);
    }

    @Override
    public void setMessageImageKey(String value) {
        realm.checkIfValid();
        row.setString(INDEX_MESSAGEIMAGEKEY, (String) value);
    }

    @Override
    public String getMessageImageUrl() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_MESSAGEIMAGEURL);
    }

    @Override
    public void setMessageImageUrl(String value) {
        realm.checkIfValid();
        row.setString(INDEX_MESSAGEIMAGEURL, (String) value);
    }

    @Override
    public String getMessageImageThumbUrl() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_MESSAGEIMAGETHUMBURL);
    }

    @Override
    public void setMessageImageThumbUrl(String value) {
        realm.checkIfValid();
        row.setString(INDEX_MESSAGEIMAGETHUMBURL, (String) value);
    }

    @Override
    public String getMessageText() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_MESSAGETEXT);
    }

    @Override
    public void setMessageText(String value) {
        realm.checkIfValid();
        row.setString(INDEX_MESSAGETEXT, (String) value);
    }

    @Override
    public String getToItemKey() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_TOITEMKEY);
    }

    @Override
    public void setToItemKey(String value) {
        realm.checkIfValid();
        row.setString(INDEX_TOITEMKEY, (String) value);
    }

    @Override
    public MessageRecipient getFrom() {
        if (row.isNullLink(INDEX_FROM)) {
            return null;
        }
        return realm.get(com.nextep.pelmel.model.db.MessageRecipient.class, row.getLink(INDEX_FROM));
    }

    @Override
    public void setFrom(MessageRecipient value) {
        if (value == null) {
            row.nullifyLink(INDEX_FROM);
            return;
        }
        row.setLink(INDEX_FROM, value.row.getIndex());
    }

    @Override
    public MessageRecipient getReplyTo() {
        if (row.isNullLink(INDEX_REPLYTO)) {
            return null;
        }
        return realm.get(com.nextep.pelmel.model.db.MessageRecipient.class, row.getLink(INDEX_REPLYTO));
    }

    @Override
    public void setReplyTo(MessageRecipient value) {
        if (value == null) {
            row.nullifyLink(INDEX_REPLYTO);
            return;
        }
        row.setLink(INDEX_REPLYTO, value.row.getIndex());
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if (!transaction.hasTable("class_Message")) {
            Table table = transaction.getTable("class_Message");
            table.addColumn(ColumnType.STRING, "messageKey");
            table.addColumn(ColumnType.DATE, "messageDate");
            table.addColumn(ColumnType.BOOLEAN, "unread");
            table.addColumn(ColumnType.STRING, "messageImageKey");
            table.addColumn(ColumnType.STRING, "messageImageUrl");
            table.addColumn(ColumnType.STRING, "messageImageThumbUrl");
            table.addColumn(ColumnType.STRING, "messageText");
            table.addColumn(ColumnType.STRING, "toItemKey");
            if (!transaction.hasTable("class_MessageRecipient")) {
                MessageRecipientRealmProxy.initTable(transaction);
            }
            table.addColumnLink(ColumnType.LINK, "from", transaction.getTable("class_MessageRecipient"));
            if (!transaction.hasTable("class_MessageRecipient")) {
                MessageRecipientRealmProxy.initTable(transaction);
            }
            table.addColumnLink(ColumnType.LINK, "replyTo", transaction.getTable("class_MessageRecipient"));
            table.addSearchIndex(table.getColumnIndex("messageKey"));
            table.addSearchIndex(table.getColumnIndex("toItemKey"));
            table.setPrimaryKey("messageKey");
            return table;
        }
        return transaction.getTable("class_Message");
    }

    public static void validateTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_Message")) {
            Table table = transaction.getTable("class_Message");
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
                    throw new RealmMigrationNeededException(transaction.getPath(), "Field '" + fieldName + "' not found for type Message");
                }
                columnIndices.put(fieldName, index);
            }
            INDEX_MESSAGEKEY = table.getColumnIndex("messageKey");
            INDEX_MESSAGEDATE = table.getColumnIndex("messageDate");
            INDEX_UNREAD = table.getColumnIndex("unread");
            INDEX_MESSAGEIMAGEKEY = table.getColumnIndex("messageImageKey");
            INDEX_MESSAGEIMAGEURL = table.getColumnIndex("messageImageUrl");
            INDEX_MESSAGEIMAGETHUMBURL = table.getColumnIndex("messageImageThumbUrl");
            INDEX_MESSAGETEXT = table.getColumnIndex("messageText");
            INDEX_TOITEMKEY = table.getColumnIndex("toItemKey");
            INDEX_FROM = table.getColumnIndex("from");
            INDEX_REPLYTO = table.getColumnIndex("replyTo");

            if (!columnTypes.containsKey("messageKey")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'messageKey'");
            }
            if (columnTypes.get("messageKey") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'messageKey'");
            }
            if (table.getPrimaryKey() != table.getColumnIndex("messageKey")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Primary key not defined for field 'messageKey'");
            }
            if (!table.hasSearchIndex(table.getColumnIndex("messageKey"))) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Index not defined for field 'messageKey'");
            }
            if (!columnTypes.containsKey("messageDate")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'messageDate'");
            }
            if (columnTypes.get("messageDate") != ColumnType.DATE) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'Date' for field 'messageDate'");
            }
            if (!columnTypes.containsKey("unread")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'unread'");
            }
            if (columnTypes.get("unread") != ColumnType.BOOLEAN) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'boolean' for field 'unread'");
            }
            if (!columnTypes.containsKey("messageImageKey")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'messageImageKey'");
            }
            if (columnTypes.get("messageImageKey") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'messageImageKey'");
            }
            if (!columnTypes.containsKey("messageImageUrl")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'messageImageUrl'");
            }
            if (columnTypes.get("messageImageUrl") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'messageImageUrl'");
            }
            if (!columnTypes.containsKey("messageImageThumbUrl")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'messageImageThumbUrl'");
            }
            if (columnTypes.get("messageImageThumbUrl") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'messageImageThumbUrl'");
            }
            if (!columnTypes.containsKey("messageText")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'messageText'");
            }
            if (columnTypes.get("messageText") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'messageText'");
            }
            if (!columnTypes.containsKey("toItemKey")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'toItemKey'");
            }
            if (columnTypes.get("toItemKey") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'toItemKey'");
            }
            if (!table.hasSearchIndex(table.getColumnIndex("toItemKey"))) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Index not defined for field 'toItemKey'");
            }
            if (!columnTypes.containsKey("from")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'from'");
            }
            if (columnTypes.get("from") != ColumnType.LINK) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'MessageRecipient' for field 'from'");
            }
            if (!transaction.hasTable("class_MessageRecipient")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing class 'class_MessageRecipient' for field 'from'");
            }
            Table table_8 = transaction.getTable("class_MessageRecipient");
            if (!table.getLinkTarget(INDEX_FROM).hasSameSchema(table_8)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid RealmObject for field 'from': '" + table.getLinkTarget(INDEX_FROM).getName() + "' expected - was '" + table_8.getName() + "'");
            }
            if (!columnTypes.containsKey("replyTo")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'replyTo'");
            }
            if (columnTypes.get("replyTo") != ColumnType.LINK) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'MessageRecipient' for field 'replyTo'");
            }
            if (!transaction.hasTable("class_MessageRecipient")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing class 'class_MessageRecipient' for field 'replyTo'");
            }
            Table table_9 = transaction.getTable("class_MessageRecipient");
            if (!table.getLinkTarget(INDEX_REPLYTO).hasSameSchema(table_9)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid RealmObject for field 'replyTo': '" + table.getLinkTarget(INDEX_REPLYTO).getName() + "' expected - was '" + table_9.getName() + "'");
            }
        } else {
            throw new RealmMigrationNeededException(transaction.getPath(), "The Message class is missing from the schema for this Realm.");
        }
    }

    public static String getTableName() {
        return "class_Message";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    public static Map<String,Long> getColumnIndices() {
        return columnIndices;
    }

    public static Message createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        Message obj = null;
        if (update) {
            Table table = realm.getTable(Message.class);
            long pkColumnIndex = table.getPrimaryKey();
            if (!json.isNull("messageKey")) {
                long rowIndex = table.findFirstString(pkColumnIndex, json.getString("messageKey"));
                if (rowIndex != TableOrView.NO_MATCH) {
                    obj = new MessageRealmProxy();
                    obj.realm = realm;
                    obj.row = table.getUncheckedRow(rowIndex);
                }
            }
        }
        if (obj == null) {
            obj = realm.createObject(Message.class);
        }
        if (!json.isNull("messageKey")) {
            obj.setMessageKey((String) json.getString("messageKey"));
        }
        if (!json.isNull("messageDate")) {
            Object timestamp = json.get("messageDate");
            if (timestamp instanceof String) {
                obj.setMessageDate(JsonUtils.stringToDate((String) timestamp));
            } else {
                obj.setMessageDate(new Date(json.getLong("messageDate")));
            }
        }
        if (!json.isNull("unread")) {
            obj.setUnread((boolean) json.getBoolean("unread"));
        }
        if (!json.isNull("messageImageKey")) {
            obj.setMessageImageKey((String) json.getString("messageImageKey"));
        }
        if (!json.isNull("messageImageUrl")) {
            obj.setMessageImageUrl((String) json.getString("messageImageUrl"));
        }
        if (!json.isNull("messageImageThumbUrl")) {
            obj.setMessageImageThumbUrl((String) json.getString("messageImageThumbUrl"));
        }
        if (!json.isNull("messageText")) {
            obj.setMessageText((String) json.getString("messageText"));
        }
        if (!json.isNull("toItemKey")) {
            obj.setToItemKey((String) json.getString("toItemKey"));
        }
        if (!json.isNull("from")) {
            com.nextep.pelmel.model.db.MessageRecipient fromObj = MessageRecipientRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("from"), update);
            obj.setFrom(fromObj);
        }
        if (!json.isNull("replyTo")) {
            com.nextep.pelmel.model.db.MessageRecipient replyToObj = MessageRecipientRealmProxy.createOrUpdateUsingJsonObject(realm, json.getJSONObject("replyTo"), update);
            obj.setReplyTo(replyToObj);
        }
        return obj;
    }

    public static Message createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        Message obj = realm.createObject(Message.class);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("messageKey") && reader.peek() != JsonToken.NULL) {
                obj.setMessageKey((String) reader.nextString());
            } else if (name.equals("messageDate")  && reader.peek() != JsonToken.NULL) {
                if (reader.peek() == JsonToken.NUMBER) {
                    long timestamp = reader.nextLong();
                    if (timestamp > -1) {
                        obj.setMessageDate(new Date(timestamp));
                    }
                } else {
                    obj.setMessageDate(JsonUtils.stringToDate(reader.nextString()));
                }
            } else if (name.equals("unread")  && reader.peek() != JsonToken.NULL) {
                obj.setUnread((boolean) reader.nextBoolean());
            } else if (name.equals("messageImageKey")  && reader.peek() != JsonToken.NULL) {
                obj.setMessageImageKey((String) reader.nextString());
            } else if (name.equals("messageImageUrl")  && reader.peek() != JsonToken.NULL) {
                obj.setMessageImageUrl((String) reader.nextString());
            } else if (name.equals("messageImageThumbUrl")  && reader.peek() != JsonToken.NULL) {
                obj.setMessageImageThumbUrl((String) reader.nextString());
            } else if (name.equals("messageText")  && reader.peek() != JsonToken.NULL) {
                obj.setMessageText((String) reader.nextString());
            } else if (name.equals("toItemKey")  && reader.peek() != JsonToken.NULL) {
                obj.setToItemKey((String) reader.nextString());
            } else if (name.equals("from")  && reader.peek() != JsonToken.NULL) {
                com.nextep.pelmel.model.db.MessageRecipient fromObj = MessageRecipientRealmProxy.createUsingJsonStream(realm, reader);
                obj.setFrom(fromObj);
            } else if (name.equals("replyTo")  && reader.peek() != JsonToken.NULL) {
                com.nextep.pelmel.model.db.MessageRecipient replyToObj = MessageRecipientRealmProxy.createUsingJsonStream(realm, reader);
                obj.setReplyTo(replyToObj);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return obj;
    }

    public static Message copyOrUpdate(Realm realm, Message object, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        if (object.realm != null && object.realm.getPath().equals(realm.getPath())) {
            return object;
        }
        Message realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(Message.class);
            long pkColumnIndex = table.getPrimaryKey();
            if (object.getMessageKey() == null) {
                throw new IllegalArgumentException("Primary key value must not be null.");
            }
            long rowIndex = table.findFirstString(pkColumnIndex, object.getMessageKey());
            if (rowIndex != TableOrView.NO_MATCH) {
                realmObject = new MessageRealmProxy();
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

    public static Message copy(Realm realm, Message newObject, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        Message realmObject = realm.createObject(Message.class, newObject.getMessageKey());
        cache.put(newObject, (RealmObjectProxy) realmObject);
        realmObject.setMessageKey(newObject.getMessageKey() != null ? newObject.getMessageKey() : "");
        realmObject.setMessageDate(newObject.getMessageDate() != null ? newObject.getMessageDate() : new Date(0));
        realmObject.setUnread(newObject.isUnread());
        realmObject.setMessageImageKey(newObject.getMessageImageKey() != null ? newObject.getMessageImageKey() : "");
        realmObject.setMessageImageUrl(newObject.getMessageImageUrl() != null ? newObject.getMessageImageUrl() : "");
        realmObject.setMessageImageThumbUrl(newObject.getMessageImageThumbUrl() != null ? newObject.getMessageImageThumbUrl() : "");
        realmObject.setMessageText(newObject.getMessageText() != null ? newObject.getMessageText() : "");
        realmObject.setToItemKey(newObject.getToItemKey() != null ? newObject.getToItemKey() : "");

        com.nextep.pelmel.model.db.MessageRecipient fromObj = newObject.getFrom();
        if (fromObj != null) {
            com.nextep.pelmel.model.db.MessageRecipient cachefrom = (com.nextep.pelmel.model.db.MessageRecipient) cache.get(fromObj);
            if (cachefrom != null) {
                realmObject.setFrom(cachefrom);
            } else {
                realmObject.setFrom(MessageRecipientRealmProxy.copyOrUpdate(realm, fromObj, update, cache));
            }
        }

        com.nextep.pelmel.model.db.MessageRecipient replyToObj = newObject.getReplyTo();
        if (replyToObj != null) {
            com.nextep.pelmel.model.db.MessageRecipient cachereplyTo = (com.nextep.pelmel.model.db.MessageRecipient) cache.get(replyToObj);
            if (cachereplyTo != null) {
                realmObject.setReplyTo(cachereplyTo);
            } else {
                realmObject.setReplyTo(MessageRecipientRealmProxy.copyOrUpdate(realm, replyToObj, update, cache));
            }
        }
        return realmObject;
    }

    static Message update(Realm realm, Message realmObject, Message newObject, Map<RealmObject, RealmObjectProxy> cache) {
        realmObject.setMessageDate(newObject.getMessageDate() != null ? newObject.getMessageDate() : new Date(0));
        realmObject.setUnread(newObject.isUnread());
        realmObject.setMessageImageKey(newObject.getMessageImageKey() != null ? newObject.getMessageImageKey() : "");
        realmObject.setMessageImageUrl(newObject.getMessageImageUrl() != null ? newObject.getMessageImageUrl() : "");
        realmObject.setMessageImageThumbUrl(newObject.getMessageImageThumbUrl() != null ? newObject.getMessageImageThumbUrl() : "");
        realmObject.setMessageText(newObject.getMessageText() != null ? newObject.getMessageText() : "");
        realmObject.setToItemKey(newObject.getToItemKey() != null ? newObject.getToItemKey() : "");
        MessageRecipient fromObj = newObject.getFrom();
        if (fromObj != null) {
            MessageRecipient cachefrom = (MessageRecipient) cache.get(fromObj);
            if (cachefrom != null) {
                realmObject.setFrom(cachefrom);
            } else {
                realmObject.setFrom(MessageRecipientRealmProxy.copyOrUpdate(realm, fromObj, true, cache));
            }
        } else {
            realmObject.setFrom(null);
        }
        MessageRecipient replyToObj = newObject.getReplyTo();
        if (replyToObj != null) {
            MessageRecipient cachereplyTo = (MessageRecipient) cache.get(replyToObj);
            if (cachereplyTo != null) {
                realmObject.setReplyTo(cachereplyTo);
            } else {
                realmObject.setReplyTo(MessageRecipientRealmProxy.copyOrUpdate(realm, replyToObj, true, cache));
            }
        } else {
            realmObject.setReplyTo(null);
        }
        return realmObject;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("Message = [");
        stringBuilder.append("{messageKey:");
        stringBuilder.append(getMessageKey());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{messageDate:");
        stringBuilder.append(getMessageDate());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{unread:");
        stringBuilder.append(isUnread());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{messageImageKey:");
        stringBuilder.append(getMessageImageKey());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{messageImageUrl:");
        stringBuilder.append(getMessageImageUrl());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{messageImageThumbUrl:");
        stringBuilder.append(getMessageImageThumbUrl());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{messageText:");
        stringBuilder.append(getMessageText());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{toItemKey:");
        stringBuilder.append(getToItemKey());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{from:");
        stringBuilder.append(getFrom() != null ? "MessageRecipient" : "null");
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{replyTo:");
        stringBuilder.append(getReplyTo() != null ? "MessageRecipient" : "null");
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
        MessageRealmProxy aMessage = (MessageRealmProxy)o;

        String path = realm.getPath();
        String otherPath = aMessage.realm.getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;;

        String tableName = row.getTable().getName();
        String otherTableName = aMessage.row.getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (row.getIndex() != aMessage.row.getIndex()) return false;

        return true;
    }

}
