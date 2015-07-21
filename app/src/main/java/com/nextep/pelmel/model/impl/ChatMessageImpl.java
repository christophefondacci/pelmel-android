package com.nextep.pelmel.model.impl;

import java.util.Date;

import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.User;

public class ChatMessageImpl implements ChatMessage {

	private User fromUser;
	private User toUser;
	private Date messageDate;
	private String message;

	public void setFrom(User fromUser) {
		this.fromUser = fromUser;
	}

	public void setTo(User toUser) {
		this.toUser = toUser;
	}

	public void setDate(Date messageDate) {
		this.messageDate = messageDate;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public User getFrom() {
		return fromUser;
	}

	@Override
	public User getTo() {
		return toUser;
	}

	@Override
	public Date getDate() {
		return messageDate;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
