package com.nextep.pelmel.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.MessageAdapter;
import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.MessageService;
import com.nextep.pelmel.services.UserService;

public class ChatActivity extends MainActionBarActivity implements
		UserListener, OnItemClickListener, MessageCallback {

	public static final String CHAT_WITH_USER_KEY = "userKey";

	private ListView listView;
	private ProgressDialog progressDialog;
	private UserService userService;
	private boolean isOneToOneChat = false;
	private String otherUserKey;
	private User currentUser;
	private List<ChatMessage> messages = Collections.emptyList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		if (intent != null) {
			if (intent.getExtras() != null) {
				otherUserKey = (String) intent.getExtras().get(
						CHAT_WITH_USER_KEY);
			}
			isOneToOneChat = (otherUserKey != null);
		}
		setContentView(R.layout.activity_chat);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.retrievingData));
		progressDialog.setTitle(getString(R.string.waitTitle));
		progressDialog.setIndeterminate(true);
		progressDialog.show();

		listView = (ListView) findViewById(R.id.chat_list);
		listView.setOnItemClickListener(this);

		userService = PelMelApplication.getUserService();
		// Getting user, notifying us when ready
		userService.getCurrentUser(this);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	@Override
	protected void onDestroy() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void userInfoAvailable(final User user) {
		currentUser = user;
		new AsyncTask<Void, Void, List<ChatMessage>>() {

			@Override
			protected List<ChatMessage> doInBackground(Void... params) {
				final Location loc = getLocalizationService().getLocation();
				final MessageService messageService = PelMelApplication
						.getMessageService();
				List<ChatMessage> messages = Collections.emptyList();
				if (!isOneToOneChat) {
					messages = messageService.listMessages(user,
							loc.getLatitude(), loc.getLongitude());
				} else {
					messages = messageService
							.listConversation(user, otherUserKey,
									loc.getLatitude(), loc.getLongitude());
				}
				return messages;
			}

			@Override
			protected void onPostExecute(final List<ChatMessage> result) {
				messages = new ArrayList<ChatMessage>(result);
				final MessageAdapter msgAdapter = new MessageAdapter(
						getBaseContext(), android.R.layout.simple_list_item_2,
						result, user, otherUserKey, ChatActivity.this);

				// Setting adapter
				listView.setAdapter(msgAdapter);
				listView.setSelection(result.size() - 1);
				// if (progressDialog.isShowing()) {
				try {
					progressDialog.dismiss();
				} catch (final IllegalArgumentException e) {
					Log.e("ChatActivity",
							"Error while dismissing progress dialog: "
									+ e.getMessage(), e);
				}
				// }
			}
		}.execute();
	}

	@Override
	public void userInfoUnavailable() {
		Log.e("ChatActivity", "User info not available");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final ChatMessage msg = (ChatMessage) listView.getAdapter().getItem(
				position);
		if (msg != null) {
			if (!isOneToOneChat) {
				final String otherUserKey = msg.getFrom().getKey();
				final Intent chatDetailIntent = new Intent(this,
						ChatActivity.class);
				chatDetailIntent.putExtra(CHAT_WITH_USER_KEY, otherUserKey);
				startActivity(chatDetailIntent);
			} else {
				// If tapped, then we jump to overview of this user
				final User user = msg.getFrom();
				final Intent intent = new Intent(this, OverviewActivity.class);
				PelMelApplication.setOverviewObject(user);
				startActivity(intent);
			}
		} else {
			// Refreshing display
			progressDialog.show();
			userService.getCurrentUser(this);
		}
	}

	@Override
	public void messageSentOk(ChatMessage message) {
		messages.add(message);
		listView.setAdapter(new MessageAdapter(getBaseContext(),
				android.R.layout.simple_list_item_2, messages, currentUser,
				otherUserKey, this));
		listView.setSelection(messages.size());
	}

	@Override
	public void messageSentFailed(final ChatMessage message) {
		new AlertDialog.Builder(this)
				.setTitle(getText(R.string.chat_send_fail_title))
				.setMessage(getText(R.string.chat_send_fail))
				.setPositiveButton(getText(R.string.ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// Nothing to do
							}
						})
				.setNegativeButton(getText(R.string.retry),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Retrying to send message
								PelMelApplication.getMessageService()
										.sendMessage(currentUser,
												message.getTo().getKey(),
												message.getMessage(),
												ChatActivity.this);
							}
						}).show();
	}

}
