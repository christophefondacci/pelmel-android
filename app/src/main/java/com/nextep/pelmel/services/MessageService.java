package com.nextep.pelmel.services;

import java.util.List;

import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.User;

public interface MessageService {

	interface OnNewMessageListener {
		void onNewMessages();
	}
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
	List<ChatMessage> listConversation(User currentUser, String otherUserKey,
			double latitude, double longitude);

	/**
	 * Sends a message to the given user.
	 * 
	 * @param currentUser
	 *            current {@link User}
	 * @param otherUser
	 *            the user to send message to
	 * @param message
	 *            the message to send
	 * @param callback
	 *            the {@link com.nextep.pelmel.services.MessageService.OnNewMessageListener} object to send notifications to
	 */
	void sendMessage(User currentUser, String otherUserKey, String message,
			OnNewMessageListener callback);
}
