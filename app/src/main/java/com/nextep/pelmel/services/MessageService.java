package com.nextep.pelmel.services;

import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.db.MessageRecipient;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public interface MessageService {

	interface OnNewMessageListener {
		void onNewMessages();
		void bindMessageGroup(String replyToGroupKey, String otherUsersKey);
	}
	interface OnPushMessageListener {
		void onPushMessage();
	}
	void registerPushListener(OnPushMessageListener listener);
	void unregisterPushListener(OnPushMessageListener listener);
	void handlePushNotification();

	/**
	 * Retrieves latest messages of the given user from server.
	 * 
	 * @param currentUser
	 *            the current {@link User}
	 * @return <code>true</code> when new messages are available, else <code>false</code>
	 */
	boolean listMessages(User currentUser, double latitude,
			double longitude, OnNewMessageListener listener);

	/**
	 * Fetches all reviews for the given object, loads it into the database and notifies back the
	 * listener when done.
	 * @param itemKey the CalObject's ItemKey to get reviews for
	 * @param listener the OnNewMessageListener to notify when done
	 * @return <code>true</code> when at least one comment has been fetched, else <code>false</code>
	 */
	boolean getReviewsAsMessages(String itemKey, OnNewMessageListener listener);

	/**
	 * Retrieves the conversation between current user and the other given user
	 * from the server
	 * 
	 * @param currentUser
	 *            the current {@link User}
	 * @param otherUserKey
	 *            the key of the other user to retrieve conversation
	 * @param latitude
	 *            current latitude
	 * @param longitude
	 *            current longitude
	 * @return a list of messages
	 */
//	List<ChatMessage> listConversation(User currentUser, String otherUserKey,
//			double latitude, double longitude);

	/**
	 * Marks all messages with this user marked as read on the server
	 * @param otherUserKey ItemKey of the user of the conversation to mark as read
	 */
	void readConversationWith(String otherUserKey);

	/**
	 * Fetches or initializes MessageRecipient instances from/to database from a map of users.
	 *
	 * @param realm the Realm instancefor db calls
	 * @param usersMap the map of users to load / instantiate hashed by their item key
	 * @param recipientMap the map of already loaded MessageRecipient hashed by their item key
	 * @return the list of MessageRecipient
	 */
	List<MessageRecipient> getMessageRecipientsFromUsersMap(Realm realm, Map<String,User> usersMap, Map<String,MessageRecipient> recipientMap);


	/**
	 * Sends a message to the given user.
	 * 
	 * @param currentUser
	 *            current {@link User}
	 * @param otherUserKey
	 *            the user item key to send message to
	 * @param message
	 *            the message to send
	 * @param callback
	 *            the {@link com.nextep.pelmel.services.MessageService.OnNewMessageListener} object to send notifications to
	 */
	void sendMessage(User currentUser, String otherUserKey, String message,
			OnNewMessageListener callback);
	void postComment(final User currentUser, final String otherUserKey,
					 final String message, final MessageService.OnNewMessageListener callback);
	void sendMessageWithPhoto(final User currentUser, final String otherUserKey,
							  final String message,boolean isComment, final File imageFile, final MessageService.OnNewMessageListener callback);

	void requestPushToken();
}
