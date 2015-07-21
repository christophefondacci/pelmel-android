package com.nextep.pelmel.model;

import java.util.Date;

public interface ChatMessage {

	/**
	 * The user which wrote this message
	 * 
	 * @return the {@link User} who sent the message
	 */
	User getFrom();

	/**
	 * The user who received this message
	 * 
	 * @return the {@link User} who received this message
	 */
	User getTo();

	/**
	 * Date of the message
	 * 
	 * @return the message's {@link Date}
	 */
	Date getDate();

	/**
	 * The content of the message
	 * 
	 * @return the message contents
	 */
	String getMessage();
}
