package com.nextep.pelmel.listeners;

import com.nextep.pelmel.model.ChatMessage;

/**
 * A callback for message-related information.
 * 
 * @author cfondacci
 * 
 */
public interface MessageCallback {

	/**
	 * This method is called as soon as messages have been fetched from server
	 * 
	 * @param messages
	 *            the list of requested {@link ChatMessage}
	 */
	// void messagesAvailable(List<ChatMessage> messages);

	/**
	 * Informs that the specified message has properly been sent to the
	 * recipient
	 * 
	 * @param message
	 *            the {@link ChatMessage} that was sent
	 */
	void messageSentOk(ChatMessage message);

	/**
	 * Informs that the given message was not sent to the recipient
	 * 
	 * @param message
	 *            the {@link ChatMessage} which failed
	 */
	void messageSentFailed(ChatMessage message);

}
