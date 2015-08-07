package com.nextep.pelmel.services.impl;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.nextep.json.model.IJsonLightUser;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonManyToOneMessageList;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.json.model.impl.JsonMessage;
import com.nextep.json.model.impl.JsonRecipientsGroup;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.gson.GsonHelper;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.db.Message;
import com.nextep.pelmel.model.db.MessageRecipient;
import com.nextep.pelmel.model.impl.ChatMessageImpl;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.MessageService;
import com.nextep.pelmel.services.WebService;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MessageServiceImpl implements MessageService {

	private static final String LOG_MSG_TAG = "MsgService";
	private static final String PREF_MAX_MESSAGE_ID= "message.maxId";

	private DataService dataService;
	private WebService webService;
	private boolean messagesFetchInProgress = false;

	@Override
	public boolean listMessages(final User currentUser, final double latitude,
			final double longitude, final OnNewMessageListener listener) {
		if(messagesFetchInProgress) {
			return false;
		}
		messagesFetchInProgress = true;
		int newMessagesCount = 0;
		int iterationMessagesCount = -1;
		// Preparing database
		final Realm realm = Realm.getInstance(PelMelApplication.getInstance(), currentUser.getKey()); // Anyway the non-deprecated constructor calls this one
		while(iterationMessagesCount!=0) {
			final int startMsgId = getMaxMessageId();
			Log.d(LOG_MSG_TAG,"Fetching messages from ID " + startMsgId);
			final JsonManyToOneMessageList messagesList = webService.getMessages(
					currentUser, latitude, longitude, startMsgId);

			iterationMessagesCount = messagesList.getMessages().size();
			Log.d(LOG_MSG_TAG, iterationMessagesCount + " messages fetched from ID " + startMsgId);

			// Processing messages
			final int maxId = processMessages(messagesList,realm);

			// Storing max id
			final int currentMaxId = getMaxMessageId();
			if (maxId > currentMaxId) {
				newMessagesCount++;
				Log.d(LOG_MSG_TAG,"Storing new max ID " + maxId);
				setMaxMessageId(maxId);
			}
			if(iterationMessagesCount>0) {
				listener.onNewMessages();
			}
		}
		realm.close();
		messagesFetchInProgress = false;
		return newMessagesCount>0;
	}

	@Override
	public boolean getReviewsAsMessages(String calItemKey, OnNewMessageListener listener) {
		final User currentUser = PelMelApplication.getUserService().getLoggedUser();
		final Location loc = PelMelApplication.getLocalizationService().getLocation();
		final Realm realm = Realm.getInstance(PelMelApplication.getInstance(), currentUser.getKey()); // Anyway the non-deprecated constructor calls this one
		int reviewsCount=-1;
		int page = 0;
		while(reviewsCount!=0) {
			final int startMsgId = getMaxMessageId();
			Log.d(LOG_MSG_TAG, "Fetching messages from ID " + startMsgId);
			final JsonManyToOneMessageList messagesList = webService.getReviewsAsMessages(
					currentUser, calItemKey, loc.getLatitude(), loc.getLongitude(), page);

			processMessages(messagesList,realm);
			reviewsCount = messagesList.getMessages().size();
			page++;
		}
		realm.close();
		return true;
	}

	private int processMessages(JsonManyToOneMessageList messagesList, Realm realm) {

		realm.beginTransaction();

		// Building / retrieving recipients definition for this list of messages
		final List<JsonLightUser> users = messagesList.getUsers();
		final Map<String, MessageRecipient> recipientsMap = new HashMap<>();
		fillRecipientsMapFromJson(realm, (List) users, recipientsMap);

		// Building group recipient in db
		final List<JsonRecipientsGroup> groups = messagesList.getRecipientsGroups();
		Map<String, MessageRecipient> groupsMap = fillRecipientsGroupMapFromJson(realm, groups, recipientsMap);

		// Hashing messages
		final Map<String, JsonMessage> jsonMessageMap = new HashMap<>();
		int maxId = 0;
		for (JsonMessage jsonMessage : messagesList.getMessages()) {
			jsonMessageMap.put(jsonMessage.getKey(), jsonMessage);

			// Adjusting max message ID
			final int id = getIdFromKey(jsonMessage.getKey());
			if (id > maxId) {
				maxId = id;
			}
		}

		// Looking for already downloaded messages
		RealmQuery<Message> query = realm.where(Message.class);
		boolean isFirst = true;
		for (String key : jsonMessageMap.keySet()) {
			if (!isFirst) {
				query.or();
			}
			query.equalTo("messageKey", key);
			isFirst = false;
		}
		// Executing search
		RealmResults<Message> results = query.findAll();

		// Hashing found messages by key
		Map<String, Message> existingMessagesMap = new HashMap<>();
		for (Message message : results) {
			existingMessagesMap.put(message.getMessageKey(), message);
		}

		// Processing all messages

		for (String messageKey : jsonMessageMap.keySet()) {
			JsonMessage jsonMessage = jsonMessageMap.get(messageKey);

			// Retrieving or creating message in DB
			boolean newMessage = false;
			Message message = existingMessagesMap.get(messageKey);
			if (message == null) {
				message = realm.createObject(Message.class);
				newMessage = true;
			}

			// Filling db structure from JSON
			message.setMessageKey(messageKey);
			message.setMessageDate(new Date(jsonMessage.getTime() * 1000));
			message.setToItemKey(jsonMessage.getToKey());
			final JsonMedia media = jsonMessage.getMedia();
			if (media != null) {
				message.setMessageImageKey(media.getKey());
				message.setMessageImageUrl(media.getUrl());
				message.setMessageImageThumbUrl(media.getThumbUrl());
			}
			message.setMessageText(jsonMessage.getMessage());
			message.setUnread(jsonMessage.isUnread());

			MessageRecipient fromRecipient = recipientsMap.get(jsonMessage.getFromKey());
			fromRecipient.getMessages().add(message);
			message.setFrom(fromRecipient);
			MessageRecipient groupRecipient = null;
			if (jsonMessage.getRecipientsGroupKey() != null && !"".equals(jsonMessage.getRecipientsGroupKey())) {
				groupRecipient = groupsMap.get(jsonMessage.getRecipientsGroupKey());
				message.setReplyTo(groupRecipient);
			}

			// Adjusting unread flag
			MessageRecipient countedRecipient = groupRecipient != null ? groupRecipient : fromRecipient;
			if (newMessage && jsonMessage.isUnread()) {
				// Counting unread on the group if a group message, or user if one to one message
				countedRecipient.setUnreadMessageCount(countedRecipient.getUnreadMessageCount() + 1);
			}

			// Adjusting last date
			if (countedRecipient.getLastMessageDate() == null || message.getMessageDate().compareTo(countedRecipient.getLastMessageDate()) > 0) {
				countedRecipient.setLastMessageDate(message.getMessageDate());
				countedRecipient.setLastMessageDefined(true);
			}
		}
		// Committing changes
		realm.commitTransaction();
		return maxId;
	}
	private int getIdFromKey(String key) {
		return Integer.parseInt(key.substring(4));
	}
	private Map<String,MessageRecipient> fillRecipientsGroupMapFromJson(Realm realm, List<JsonRecipientsGroup> jsonGroups, Map<String,MessageRecipient> recipientMap) {

		// Preparing map of group keys / recipients contents
		final Map<String, List<MessageRecipient>> groupRecipientsMap = new HashMap<>();

		// Processing groups, and fetching all users from every group
		for(JsonRecipientsGroup jsonGroup : jsonGroups) {

			// Getting / creating recipients in database for every user of this group
			final List<MessageRecipient> recipients = fillRecipientsMapFromJson(realm, jsonGroup.getUsers(), recipientMap);

			// Storing in map
			groupRecipientsMap.put(jsonGroup.getKey(), recipients);
		}

		// Querying all groups
		final RealmQuery<MessageRecipient> query = realm.where(MessageRecipient.class);
		boolean isFirst = true;
		for(String groupKey : groupRecipientsMap.keySet()) {
			if(!isFirst) {
				query.or();
			}
			query.equalTo("itemKey", groupKey);
			isFirst = false;
		}

		// Storing a hashmap of resolved groups
		final RealmResults<MessageRecipient> results = query.findAll();
		Map<String, MessageRecipient> dbGroupsMap = new HashMap<>();
		for(MessageRecipient recipient : results ) {
			dbGroupsMap.put(recipient.getItemKey(), recipient);
		}

		// Processing and creating missing groups in db
		for(String groupKey : groupRecipientsMap.keySet()) {
			MessageRecipient group = dbGroupsMap.get(groupKey);
			if(group == null) {
				group = realm.createObject(MessageRecipient.class);
				dbGroupsMap.put(groupKey,group);
				group.setItemKey(groupKey);

				// Processing group members
				final List<MessageRecipient> groupRecipients = groupRecipientsMap.get(groupKey);
				for(MessageRecipient groupRecipient : groupRecipients) {
					group.getUsers().add(groupRecipient);
				}
			}
		}

		return dbGroupsMap;

 	}
	private List<MessageRecipient> fillRecipientsMapFromJson(Realm realm, Collection<IJsonLightUser> jsonUsers, Map<String,MessageRecipient> recipientMap) {

		final Map<String, User> usersMap = new HashMap<>();
		final List<MessageRecipient> recipients = new ArrayList<>();

		// Processing all JSON users
		for(IJsonLightUser jsonUser : jsonUsers) {

			// Getting any previous instance of this recipient
			MessageRecipient recipient = recipientMap.get(jsonUser.getKey());
			if(recipient == null) {
				// Parsing user and storing in map
				final User user = dataService.getUserFromLightJson(jsonUser);
				usersMap.put(jsonUser.getKey(),user);
			} else {
				recipients.add(recipient);
			}
		}

		// Querying all users we have not found
		final User currentUser = PelMelApplication.getUserService().getLoggedUser();
		final RealmQuery<MessageRecipient> query = realm.where(MessageRecipient.class);
		boolean isFirst = true;
		for(String userKey : usersMap.keySet()) {
			if(!isFirst) {
				query.or();
			}
			query.equalTo("itemKey", userKey);
			isFirst = false;
		}

		// Executing query
		RealmResults<MessageRecipient> results = query.findAll();

		// Iterating over found results
		for(int i = 0 ; i < results.size() ; i ++) {
			final MessageRecipient recipient = results.get(i);
			recipientMap.put(recipient.getItemKey(),recipient);

			// Retrieving parsed user definition from JSON and injecting in recipient
			final User user = usersMap.get(recipient.getItemKey());
			if(user != null) {
				fillRecipientFromUser(recipient,user);
			}

			// Appending to our result list
			recipients.add(recipient);

			// Removing from map as this entry is resolved
			usersMap.remove(recipient.getItemKey());
		}

		// Remaining users need to be created
		for(String userKey : usersMap.keySet()) {
			final User user = usersMap.get(userKey);

			// Creating DB recipient and filling from bean
			final MessageRecipient recipient = realm.createObject(MessageRecipient.class);
			fillRecipientFromUser(recipient,user);

			// Filling map of resolved objects
			recipientMap.put(userKey, recipient);

			// Appending to result list
			recipients.add(recipient);
		}
		return recipients;
	}

	private void fillRecipientFromUser(MessageRecipient recipient, User user) {
		recipient.setItemKey(user.getKey());
		// Thumb info
		final Image thumb = user.getThumb();
		if(thumb != null) {
			recipient.setImageKey(thumb.getKey());
			recipient.setImageUrl(thumb.getUrl());
			recipient.setImageThumbUrl(thumb.getThumbUrl());
		}
		recipient.setUsername(user.getName());
	}

	private List<ChatMessage> buildChatMessagesFromJson(
			List<JsonMessage> jsonMessages, Map<String, User> usersMap) {
		final List<ChatMessage> messages = new ArrayList<ChatMessage>();
		for (JsonMessage jsonMessage : jsonMessages) {
			final String fromKey = jsonMessage.getFromKey();
			final User fromUser = usersMap.get(fromKey);
			final String toKey = jsonMessage.getToKey();
			final User toUser = usersMap.get(toKey);
			final Date msgDate = new Date(jsonMessage.getTime() * 1000);

			ChatMessageImpl msg = new ChatMessageImpl();
			msg.setFrom(fromUser);
			msg.setTo(toUser);
			msg.setDate(msgDate);
			msg.setMessage(jsonMessage.getMessage());
			messages.add(msg);
		}
		return messages;
	}

	@Override
	public void readConversationWith(String otherUserKey) {
		final User currentUser = PelMelApplication.getUserService().getLoggedUser();
		final Location location = PelMelApplication.getLocalizationService().getLocation();

		webService.getMessages(
				currentUser, otherUserKey, location.getLatitude(), location.getLongitude(),true);

	}

	@Override
	public void sendMessage(final User currentUser, final String otherUserKey,
			final String message, final MessageService.OnNewMessageListener callback) {
		final boolean isComment = !otherUserKey.startsWith(User.CAL_TYPE);
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {

				HttpClient http = new DefaultHttpClient();
				HttpPost post = new HttpPost(WebService.BASE_URL
						+ "/" + (isComment ?  "mobilePostComment" : "mobileSendMsg"));

				MultipartEntity multipart = new MultipartEntity();
				try {
					multipart.addPart("nxtpUserToken", new StringBody(
							currentUser.getToken()));
					multipart.addPart(isComment ? "commentItemKey" : "to", new StringBody(otherUserKey));
					multipart.addPart(isComment ? "comment" : "msgText", new StringBody(message,
							Charset.forName("UTF-8")));
					post.setEntity(multipart);
					HttpResponse response = http.execute(post);
					final InputStream is = response.getEntity().getContent();
					final InputStreamReader reader = new InputStreamReader(is);

					final JsonMessage jsonMessage = GsonHelper.getGson().fromJson(reader, JsonMessage.class);

					final Realm realm = Realm.getInstance(PelMelApplication.getInstance(), currentUser.getKey());
					realm.beginTransaction();

					// Just in case we first look if not already there
					Message message = getMessage(realm,jsonMessage.getKey());
					if(message == null) {
						message = realm.createObject(Message.class);
					}
					message.setMessageKey(jsonMessage.getKey());
					message.setToItemKey(jsonMessage.getToKey());

					final MessageRecipient recipient = getRecipient(realm,currentUser.getKey());
					message.setFrom(recipient);
					message.setMessageText(jsonMessage.getMessage());
					message.setMessageDate(new Date(jsonMessage.getTime() * 1000));
					if(jsonMessage.getRecipientsGroupKey()!=null) {
						final MessageRecipient groupRecipient = getRecipient(realm,jsonMessage.getRecipientsGroupKey());
						message.setReplyTo(groupRecipient);
					}
					realm.commitTransaction();
					realm.close();
					return message;
				} catch (UnsupportedEncodingException e) {
					Log.e(LOG_MSG_TAG,
							"Error uploading image : " + e.getMessage());
				} catch (ClientProtocolException e) {
					Log.e(LOG_MSG_TAG,
							"Error uploading image : " + e.getMessage());
				} catch (IOException e) {
					Log.e(LOG_MSG_TAG,
							"Error uploading image : " + e.getMessage());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Message result) {
				if (result != null ) {
					callback.onNewMessages();
				} else {
					Log.e(LOG_MSG_TAG,"Error while sending message");
				}
			}
		}.execute();
	}

	private MessageRecipient getRecipient(Realm realm, String itemKey) {
		RealmQuery<MessageRecipient> query = realm.where(MessageRecipient.class);
		query.equalTo("itemKey",itemKey);
		final MessageRecipient recipient = query.findFirst();
		return recipient;
	}
	private Message getMessage(Realm realm, String itemKey) {
		RealmQuery<Message> query = realm.where(Message.class);
		query.equalTo("messageKey",itemKey);
		final Message msg = query.findFirst();
		return msg;
	}
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setWebService(WebService webService) {
		this.webService = webService;
	}

	private int getMaxMessageId() {
		final SharedPreferences preferences = PelMelApplication.getInstance()
				.getSharedPreferences(PelMelConstants.PREFS_NAME, 0);
		return preferences.getInt(PREF_MAX_MESSAGE_ID,0);
	}
	private void setMaxMessageId(int id) {
		final SharedPreferences preferences = PelMelApplication.getInstance()
				.getSharedPreferences(PelMelConstants.PREFS_NAME, 0);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(PREF_MAX_MESSAGE_ID, id);
		editor.commit();
	}
}
