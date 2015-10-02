package com.nextep.pelmel.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.CALObjectThumbAdapter;
import com.nextep.pelmel.adapters.MessageAdapter;
import com.nextep.pelmel.dialogs.SelectImageDialogFragment;
import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.listeners.OverviewListener;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.db.Message;
import com.nextep.pelmel.model.db.MessageRecipient;
import com.nextep.pelmel.model.impl.ImageImpl;
import com.nextep.pelmel.model.impl.UserImpl;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.services.MessageService;
import com.nextep.pelmel.services.UserService;
import com.nextep.pelmel.views.HorizontalListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ChatConversationActivity extends Fragment implements
		UserListener, OnItemClickListener, MessageCallback, SnippetChildSupport, MessageService.OnNewMessageListener, MessageService.OnPushMessageListener {

	private static final String LOG_TAG = "CHAT";
	public static final String CHAT_WITH_USER_KEY = "userKey";
	public static final String CAL_TYPE_RECIPIENT_GROUP = "RCPT";
	public static final String BUNDLE_STATE_OTHER_ITEM_KEY = "otherUserKey";

	private HorizontalListView recipientsListView;
	private ListView listView;
	private View sendButton;
	private View sendPhotoButton;
	private EditText chatTextView;
	private UserService userService;
	private String otherUserKey;
	private User currentUser;
	private boolean commentMode;
	private Realm realm;
	private List<ChatMessage> messages = Collections.emptyList();
	private SnippetContainerSupport snippetContainerSupport;

	public void setOtherUserKey(String otherUserKey) {
		this.otherUserKey = otherUserKey;
	}

	public void setCommentMode(boolean commentMode) {
		this.commentMode = commentMode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Instantiating realm
		currentUser = PelMelApplication.getUserService().getLoggedUser();
		realm = Realm.getInstance(ChatConversationActivity.this.getActivity(), currentUser.getKey());

		// Configuring list view
		View view;
		if(otherUserKey == null && savedInstanceState!=null) {
			otherUserKey = savedInstanceState.getString(BUNDLE_STATE_OTHER_ITEM_KEY);
		}
		if(!isGroupChat()) {
			view = inflater.inflate(R.layout.activity_chat_conversation,container,false);
		} else {
			view = inflater.inflate(R.layout.activity_group_chat_conversation,container,false);
			recipientsListView = (HorizontalListView)view.findViewById(R.id.groupRecipientsList);

			// Getting recipient instance
			final Realm realm = Realm.getInstance(ChatConversationActivity.this.getActivity(), PelMelApplication.getUserService().getLoggedUser().getKey());
			realm.beginTransaction();
			final RealmQuery<MessageRecipient> query = realm.where(MessageRecipient.class);

			// Building list of item keys to fetch
			final List<String> itemKeys;
			if(otherUserKey.contains(",")) {
				itemKeys = Arrays.asList(otherUserKey.split(","));
			} else {
				itemKeys = Arrays.asList(otherUserKey);
			}
			boolean isFirst=true;
			for(String itemKey : itemKeys) {
				if(!isFirst) {
					query.or();
				}
				query.equalTo("itemKey", itemKey);
				isFirst = false;
			}

			// Fetching
			List<MessageRecipient> recipients;

			if(itemKeys.size()>1){
				recipients = query.findAll();
			} else {
				recipients = query.findFirst().getUsers();
			}
			final List<CalObject> users = new ArrayList<>();
			for(MessageRecipient recipient : recipients) {
				UserImpl user = PelMelApplication.getDataService().getCachedObject(UserImpl.class,recipient.getItemKey());
				if(user == null) {
					user = new UserImpl();
				}
				user.setKey(recipient.getItemKey());
				if(recipient.getImageKey() != null && !recipient.getImageKey().isEmpty()) {
					final ImageImpl img = new ImageImpl();
					img.setKey(recipient.getImageKey());
					img.setUrl(recipient.getImageUrl());
					img.setThumbUrl(recipient.getImageThumbUrl());
					user.setThumb(img);
				}
				user.setName(recipient.getUsername());
				users.add(user);
			}
			realm.commitTransaction();
			realm.close();
			CALObjectThumbAdapter adapter = new CALObjectThumbAdapter(getActivity(),users);
			recipientsListView.setAdapter(adapter);
			recipientsListView.setOnItemClickListener(adapter);
		}

		listView = (ListView) view.findViewById(R.id.chat_list);
		listView.setOnItemClickListener(this);
		listView.setDividerHeight(0);

		// Configuring text editor
		chatTextView = (EditText)view.findViewById(R.id.chatText);

		// Configuring send action
		sendButton = view.findViewById(R.id.chat_send_button);
		sendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (otherUserKey != null) {
					final String message = chatTextView.getText()
							.toString();
					if (message != null
							&& !"".equals(message.trim())) {
						PelMelApplication.getMessageService()
								.sendMessageWithPhoto(PelMelApplication.getUserService().getLoggedUser(), otherUserKey,
										message, commentMode, null, ChatConversationActivity.this);
					}
					chatTextView.setText("");
				}
			}
		});

		// Configuring photo attachments
		sendPhotoButton = view.findViewById(R.id.chat_add_photo);
		sendPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				snippetContainerSupport.setFragmentForActivityResult(ChatConversationActivity.this);
				final SelectImageDialogFragment selectDialog = new SelectImageDialogFragment();
				selectDialog.setInitiatingFragment(ChatConversationActivity.this);
				selectDialog.show(ChatConversationActivity.this.getActivity().getSupportFragmentManager(), "PHOTO");
			}
		});

		userService = PelMelApplication.getUserService();
		if(savedInstanceState!=null && otherUserKey == null) {
			otherUserKey = savedInstanceState.getString(BUNDLE_STATE_OTHER_ITEM_KEY);
		}
		// Getting user, notifying us when ready
		userService.getCurrentUser(this);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		snippetContainerSupport.setSnippetChild(this);

		return view;
	}

	private boolean isGroupChat() {
		return otherUserKey.startsWith(CAL_TYPE_RECIPIENT_GROUP) || otherUserKey.contains(",");
	}
	@Override
	public void userInfoAvailable(final User user) {
		currentUser = user;
		updateData();
		fetchNewMessages();
	}
	private void fetchNewMessages() {
		// In background, fetch any new message (we'll be notified by callback)
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				final Location loc = PelMelApplication.getLocalizationService().getLocation();
				final MessageService messageService = PelMelApplication
						.getMessageService();
				List<ChatMessage> messages = Collections.emptyList();

				// Fetching list of messages
				if(!commentMode) {
					boolean hasNewMessages = messageService.listMessages(currentUser,
							loc.getLatitude(), loc.getLongitude(), ChatConversationActivity.this);

					// Marking messages as read
					Realm realm = Realm.getInstance(ChatConversationActivity.this.getActivity(), currentUser.getKey());
					realm.beginTransaction();
					final RealmQuery<Message> query = buildMessagesQuery(realm);
					final List<Message> messagesToRead = query.findAll();
					for (int i = 0; i < messagesToRead.size(); i++) {
						final Message m = messagesToRead.get(i);
						if (m.isUnread()) {
							m.setUnread(false);
							m.getFrom().setUnreadMessageCount(m.getFrom().getUnreadMessageCount() - 1);
						}
					}
					realm.commitTransaction();
					realm.close();
					// Marking messages as read on server
					PelMelApplication.getMessageService().readConversationWith(otherUserKey);
					return hasNewMessages;
				} else {
					messageService.getReviewsAsMessages(otherUserKey,ChatConversationActivity.this);
					return true;
				}
			}

		}.execute();
	}

	private RealmQuery<Message> buildMessagesQuery(Realm realm) {
		RealmQuery<Message> query = realm.where(Message.class);
		if(otherUserKey.startsWith(CAL_TYPE_RECIPIENT_GROUP)) {
			Log.d(LOG_TAG,"Fetching group with otherUserKey=" + otherUserKey);
			query.equalTo("replyTo.itemKey",otherUserKey);
			query.equalTo("toItemKey",currentUser.getKey());
		} else if(!commentMode) {
			Log.d(LOG_TAG,"Fetching with otherUserKey=" + otherUserKey);
			query.beginGroup();
			query.equalTo("from.itemKey", otherUserKey);
			query.equalTo("toItemKey", currentUser.getKey());
//		query.equalTo("replyTo.itemKey", otherUserKey);
			query.endGroup();
			if(otherUserKey.startsWith(User.CAL_TYPE)) {
				Log.d(LOG_TAG,"  > With USER otherUserKey=" + otherUserKey);
				query.or().beginGroup();
				query.equalTo("from.itemKey", currentUser.getKey());
				query.equalTo("toItemKey", otherUserKey);
//		query.equalTo("replyTo.itemKey",currentUser.getKey());
				query.endGroup();
			}
		} else {
			Log.d(LOG_TAG,"Fetching comment with otherUserKey=" + otherUserKey);
			query.equalTo("toItemKey",otherUserKey);
		}
		return query;
	}

	public void updateData() {
		final User currentUser = PelMelApplication.getUserService().getLoggedUser();
		final RealmQuery<Message> query = buildMessagesQuery(realm);
		final RealmResults<Message> messages = query.findAllSorted("messageDate", true);
		final MessageAdapter adapter=  new MessageAdapter(this.getActivity(),messages,currentUser,otherUserKey,this);
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
				// If tapped, then we jump to overview of this user
			final CalObject obj = PelMelApplication.getDataService().getCalObject(msg.getFrom().getItemKey(), new OverviewListener() {
				@Override
				public Activity getContext() {
					return ChatConversationActivity.this.getActivity();
				}

				@Override
				public void overviewDataAvailable(CalObject object) {
					snippetContainerSupport.showSnippetFor(object, true, false);
				}
			});

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
		new AlertDialog.Builder(this.getActivity())
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
		Log.d("CHAT", "onNewMessages");
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateData();
			}
		});
	}

	@Override
	public void bindMessageGroup(String replyToGroupKey, String othersUserKey) {
		Log.d("CHAT","Binding '" +replyToGroupKey + "' to users '" + othersUserKey + "'");
		if(othersUserKey.equals(otherUserKey)) {
			this.otherUserKey = replyToGroupKey;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			snippetContainerSupport = (SnippetContainerSupport)activity;
		} catch(ClassCastException e) {
			throw new IllegalStateException("Parent of SnippetListFragment must be a snippetContainerSupport");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(snippetContainerSupport != null) {
			snippetContainerSupport.setSnippetChild(this);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		PelMelApplication.getMessageService().registerPushListener(this);
	}

	@Override
	public void onDestroy() {
		if(realm != null) {
			realm.close();
		}
		PelMelApplication.getMessageService().unregisterPushListener(this);
		super.onDestroy();
	}

	@Override
	public void onSnippetOpened(boolean snippetOpened) {

	}

	@Override
	public View getScrollableView() {
		return listView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BUNDLE_STATE_OTHER_ITEM_KEY, otherUserKey);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if(savedInstanceState != null) {
			otherUserKey = savedInstanceState.getString(BUNDLE_STATE_OTHER_ITEM_KEY);
		}
	}

	@Override
	public void onPushMessage() {
		fetchNewMessages();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		final Uri selectedImage = data.getData();
		final File f = PelMelApplication.getImageService().getOrientedImageFileFromUri(this.getActivity(),
				selectedImage);

		PelMelApplication.getMessageService().sendMessageWithPhoto(PelMelApplication.getUserService().getLoggedUser(), otherUserKey,
				"",commentMode, f, ChatConversationActivity.this);

		// Resetting data to prevent any other upload
		data.setData(null);
	}
}
