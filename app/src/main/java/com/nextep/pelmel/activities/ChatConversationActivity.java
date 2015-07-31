package com.nextep.pelmel.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.MessageAdapter;
import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.db.Message;
import com.nextep.pelmel.services.MessageService;
import com.nextep.pelmel.services.UserService;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ChatConversationActivity extends MainActionBarActivity implements
		UserListener, OnItemClickListener, MessageCallback, MessageService.OnNewMessageListener {

	public static final String CHAT_WITH_USER_KEY = "userKey";

	private ListView listView;
	private View sendButton;
	private EditText chatTextView;
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
		}
		setContentView(R.layout.activity_chat_conversation);

		// Configuring list view
		listView = (ListView) findViewById(R.id.chat_list);
		listView.setOnItemClickListener(this);
		listView.setDividerHeight(0);

		// Configuring text editor
		chatTextView = (EditText)findViewById(R.id.chatText);

		// Configuring send action
		sendButton = findViewById(R.id.chat_send_button);
		sendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (otherUserKey != null) {
					final String message = chatTextView.getText()
							.toString();
					if (message != null
							&& !"".equals(message.trim())) {
						PelMelApplication.getMessageService()
								.sendMessage(PelMelApplication.getUserService().getLoggedUser(), otherUserKey,
										message, ChatConversationActivity.this);
					}
					chatTextView.setText("");
				}
			}
		});


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
		updateData();
	}

	private void updateData() {
		final User currentUser = PelMelApplication.getUserService().getLoggedUser();
		Realm realm = Realm.getInstance(this, currentUser.getKey());
		RealmQuery<Message> query = realm.where(Message.class);
		query.beginGroup();
		query.equalTo("from.itemKey", currentUser.getKey());
		query.equalTo("toItemKey", otherUserKey);
//		query.equalTo("replyTo.itemKey",currentUser.getKey());
		query.endGroup();
		query.or().beginGroup();
		query.equalTo("from.itemKey", otherUserKey);
		query.equalTo("toItemKey",currentUser.getKey());
//		query.equalTo("replyTo.itemKey", otherUserKey);
		query.endGroup();

		final RealmResults<Message> messages = query.findAllSorted("messageDate", true);

		final MessageAdapter adapter=  new MessageAdapter(this,messages,currentUser,otherUserKey,this);
		listView.setAdapter(adapter);
		listView.setSelection(messages.size() - 1);
	}
	@Override
	public void userInfoUnavailable() {
		Log.e("ChatActivity", "User info not available");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Message msg = (Message) listView.getAdapter().getItem(
				position);
		if (msg != null) {
				// If tapped, then we jump to overview of this user
			final CalObject obj = PelMelApplication.getDataService().getCalObject(msg.getFrom().getItemKey(),null);
				final Intent intent = new Intent(this, OverviewActivity.class);
				PelMelApplication.setOverviewObject(user);
				startActivity(intent);
		} else {
			// Refreshing display
			progressDialog.show();
			userService.getCurrentUser(this);
		}
	}

	@Override
	public void messageSentOk(ChatMessage message) {
//		messages.add(message);
//		listView.setAdapter(new MessageAdapter(getBaseContext(),
//				android.R.layout.simple_list_item_2, messages, currentUser,
//				otherUserKey, this));
//		listView.setSelection(messages.size());
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
												ChatConversationActivity.this);
							}
						}).show();
	}

	@Override
	public void onNewMessages() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateData();
			}
		});

	}

}
