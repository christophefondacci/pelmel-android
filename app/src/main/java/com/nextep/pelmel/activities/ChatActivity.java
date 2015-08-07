package com.nextep.pelmel.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
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
import android.widget.ListView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.MessageThreadAdapter;
import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.db.MessageRecipient;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.services.MessageService;
import com.nextep.pelmel.services.UserService;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ChatActivity extends Fragment implements
		UserListener, OnItemClickListener, MessageCallback, SnippetChildSupport, MessageService.OnNewMessageListener {

	public static final String CHAT_WITH_USER_KEY = "userKey";

	private ListView listView;
	private ProgressDialog progressDialog;
	private UserService userService;
	private boolean isOneToOneChat = false;
	private User currentUser;
	private List<ChatMessage> messages = Collections.emptyList();
	private SnippetContainerSupport snippetContainerSupport;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflating our view layout
		final View view = inflater.inflate(R.layout.activity_chat, container, false);

		// Showing progress dialog
		progressDialog = new ProgressDialog(this.getActivity());
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.retrievingData));
		progressDialog.setTitle(getString(R.string.waitTitle));
		progressDialog.setIndeterminate(true);
		progressDialog.show();

		// Accessing list view
		listView = (ListView) view.findViewById(R.id.chat_list);
		listView.setOnItemClickListener(this);

		// Getting service
		userService = PelMelApplication.getUserService();

		// Getting user, notifying us when ready
		userService.getCurrentUser(this);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		snippetContainerSupport.setSnippetChild(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(snippetContainerSupport != null) {
			snippetContainerSupport.setSnippetChild(this);
		}
	}

	@Override
	public void onDestroy() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		super.onDestroy();
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
	public void userInfoAvailable(final User user) {
		currentUser = user;

		// Updating list view with current contents of our database
		updateData();

		// In background, fetch any new message (we'll be notified by callback)
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				final Location loc = PelMelApplication.getLocalizationService().getLocation();
				final MessageService messageService = PelMelApplication
						.getMessageService();
				List<ChatMessage> messages = Collections.emptyList();
				boolean hasNewMessages = messageService.listMessages(user,
						loc.getLatitude(), loc.getLongitude(),ChatActivity.this);
				return hasNewMessages;
			}

//			@Override
//			protected void onPostExecute(final Boolean result) {
//				if(result!=null && result.booleanValue()) {
//					updateData();
//				}
//				messages = new ArrayList<ChatMessage>(result);
//				final MessageAdapter msgAdapter = new MessageAdapter(
//						getBaseContext(), android.R.layout.simple_list_item_2,
//						result, user, otherUserKey, ChatActivity.this);
//
//				// Setting adapter
//				listView.setAdapter(msgAdapter);
//				listView.setSelection(result.size() - 1);
//				// if (progressDialog.isShowing()) {
//				try {
//					progressDialog.dismiss();
//				} catch (final IllegalArgumentException e) {
//					Log.e("ChatActivity",
//							"Error while dismissing progress dialog: "
//									+ e.getMessage(), e);
//				}
//				// }
//			}
		}.execute();
	}

	public void updateData() {
		final User currentUser = PelMelApplication.getUserService().getLoggedUser();
		Realm realm = Realm.getInstance(this.getActivity(), currentUser.getKey());
		RealmQuery<MessageRecipient> query = realm.where(MessageRecipient.class);
		query.notEqualTo("itemKey", currentUser.getKey());
		query.equalTo("lastMessageDefined", true);
		final RealmResults<MessageRecipient> threads = query.findAllSorted("lastMessageDate", false);

		final MessageThreadAdapter adapter=  new MessageThreadAdapter(this.getActivity(),threads,this);
		listView.setAdapter(adapter);
//		listView.setSelection(threads.size() - 1);
		try {
			progressDialog.dismiss();
		} catch (final IllegalArgumentException e) {
			Log.e("ChatActivity",
					"Error while dismissing progress dialog: "
							+ e.getMessage(), e);
		}

	}
	@Override
	public void userInfoUnavailable() {
		Log.e("ChatActivity", "User info not available");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final MessageRecipient msg = (MessageRecipient) listView.getAdapter().getItem(
				position);
		final String otherUserKey = msg.getItemKey();
		final ChatConversationActivity conversationActivity = new ChatConversationActivity();
		conversationActivity.setOtherUserKey(otherUserKey);
		snippetContainerSupport.showSnippetForFragment(conversationActivity,true,false);

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
												ChatActivity.this);
							}
						}).show();
	}

	@Override
	public void onNewMessages() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateData();
			}
		});

	}

	@Override
	public void onSnippetOpened(boolean snippetOpened) {

	}

	@Override
	public View getScrollableView() {
		return listView;
	}
}
