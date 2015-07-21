package com.nextep.pelmel.services.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonManyToOneMessageList;
import com.nextep.json.model.impl.JsonMessage;
import com.nextep.json.model.impl.JsonOneToOneMessageList;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.impl.ChatMessageImpl;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.MessageService;
import com.nextep.pelmel.services.WebService;

public class MessageServiceImpl implements MessageService {

	private static final String LOG_MSG_TAG = "MsgService";
	private DataService dataService;
	private WebService webService;

	@Override
	public List<ChatMessage> listMessages(User currentUser, double latitude,
			double longitude) {
		final JsonManyToOneMessageList messagesList = webService.getMessages(
				currentUser, latitude, longitude);

		// Preparing a hash map with all referenced users hashed by their
		// respective key
		final List<JsonLightUser> users = messagesList.getUsers();
		final Map<String, User> usersMap = new HashMap<String, User>();
		for (JsonLightUser jsonUser : users) {
			final User user = dataService.getUserFromLightJson(jsonUser);
			usersMap.put(jsonUser.getKey(), user);
		}
		usersMap.put(currentUser.getKey(), currentUser);

		final List<JsonMessage> jsonMessages = messagesList.getMessages();
		return buildChatMessagesFromJson(jsonMessages, usersMap);
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
	public List<ChatMessage> listConversation(User currentUser,
			String otherUserKey, double latitude, double longitude) {
		final JsonOneToOneMessageList messagesList = webService.getMessages(
				currentUser, otherUserKey, latitude, longitude);

		// Building from/to user map
		final Map<String, User> usersMap = new HashMap<String, User>();
		if (messagesList != null) {
			final User fromUser = dataService.getUserFromLightJson(messagesList
					.getFromUser());
			usersMap.put(fromUser.getKey(), fromUser);
			final User toUser = dataService.getUserFromLightJson(messagesList
					.getToUser());
			usersMap.put(toUser.getKey(), toUser);

			return buildChatMessagesFromJson(messagesList.getMessages(),
					usersMap);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void sendMessage(final User currentUser, final String otherUserKey,
			final String message, final MessageCallback callback) {
		new AsyncTask<Void, Void, ChatMessage>() {
			@Override
			protected ChatMessage doInBackground(Void... params) {

				HttpClient http = new DefaultHttpClient();
				HttpPost post = new HttpPost(WebService.BASE_URL
						+ "/mobileSendMsg");

				MultipartEntity multipart = new MultipartEntity();
				try {
					multipart.addPart("nxtpUserToken", new StringBody(
							currentUser.getToken()));
					multipart.addPart("to", new StringBody(otherUserKey));
					multipart.addPart("msgText", new StringBody(message,
							Charset.forName("UTF-8")));
					post.setEntity(multipart);
					HttpResponse response = http.execute(post);
					final ChatMessageImpl msg = new ChatMessageImpl();
					msg.setFrom(currentUser);
					msg.setTo(PelMelApplication.getDataService().getUser(
							otherUserKey));
					msg.setMessage(message);
					if (response.getStatusLine().getStatusCode() == 200) {
						msg.setDate(new Date());
						return msg;
					} else {
						return msg;
					}
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
			protected void onPostExecute(ChatMessage result) {
				if (result != null && result.getDate() != null) {
					callback.messageSentOk(result);
				} else {
					callback.messageSentFailed(result);
				}
			}
		}.execute();
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setWebService(WebService webService) {
		this.webService = webService;
	}
}
