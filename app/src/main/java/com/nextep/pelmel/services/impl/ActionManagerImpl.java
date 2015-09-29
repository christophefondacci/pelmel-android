package com.nextep.pelmel.services.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.nextep.json.model.impl.JsonLikeInfo;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.ChatConversationActivity;
import com.nextep.pelmel.activities.ListCheckinsFragment;
import com.nextep.pelmel.activities.ListDealsFragment;
import com.nextep.pelmel.exception.PelmelException;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.Action;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.NetworkStatus;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.ActionManager;
import com.nextep.pelmel.services.WebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cfondacci on 29/07/15.
 */
public class ActionManagerImpl implements ActionManager {

    private static final String LOG_TAG = "ACTION_MGR";
    private Map<Action,ActionCommand> commandsActionMap = new HashMap<>();
    private WebService webService;
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public ActionManagerImpl() {
        registerLikeAction();
        registerChatAction();
        registerCheckinAction();
        registerCheckoutAction();
        registerListDealsAction();
        registerPrivateNetworkCancelAction();
        registerPrivateNetworkRequestAction();
        registerPrivateNetworkOtherActions(Action.NETWORK_INVITE);
        registerPrivateNetworkOtherActions(Action.NETWORK_ACCEPT);
        registerPrivateNetworkRespondAction();
        webService = new WebService();
    }
    @Override
    public void executeAction(Action action, Object parameter) {
        executeAction(action, parameter, null);
    }

    @Override
    public void executeAction(final Action action, Object parameter, final ActionCallback callback) {
        final ActionCommand command = commandsActionMap.get(action);
        if(command != null) {

            new AsyncTask<Object,Void,Object>() {
                private Exception exception;
                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        final Object result = command.execute(params[0]);
                        return result;
                    } catch(Exception e) {
                        Log.e(LOG_TAG,"Error during action " + action.name() + " execution: " + e.getMessage(),e);
                        exception = e;
                        return exception;
                    }
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (callback != null) {
                        if(exception == null) {
                            callback.actionCompleted(true, result);
                        } else {
                            callback.actionCompleted(false,exception);
                        }
                    }
                }
            }.execute(parameter);


        }
    }

    private void registerLikeAction() {
        final ActionCommand likeCommand = createLikeCommand(true);
        commandsActionMap.put(Action.LIKE,likeCommand);
        commandsActionMap.put(Action.ATTEND,likeCommand);

        final ActionCommand unlikeCommand = createLikeCommand(false);
        commandsActionMap.put(Action.UNLIKE,unlikeCommand);
        commandsActionMap.put(Action.UNATTEND,unlikeCommand);
    }

    private ActionCommand createLikeCommand(final boolean like) {
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(Object parameter) {
                // Extracting CAL Object
                final CalObject calObject = (CalObject)parameter;
                // Executing like
                final JsonLikeInfo likeInfo = webService.like(PelMelApplication.getUserService().getLoggedUser(), calObject.getKey(), like);

                // Filling data
                calObject.setLikeCount(likeInfo.getLikeCount());
                calObject.setLiked(like);

                final User currentUser = PelMelApplication.getUserService().getLoggedUser();
                if(calObject instanceof Place) {
                    final Place place = (Place)calObject;
                    boolean containsCurrentUser = false;
                    // Checking wheter current user is already in the liker list (double tap?)
                    for(User user : new ArrayList<>(place.getLikers())) {
                        if(user.getKey().equals(currentUser.getKey())) {
                            containsCurrentUser = true;
                            // Removing right now if unlike (it might not be the same instance of User)
                            if(!like) {
                                place.getLikers().remove(user);
                            }
                            break;
                        }
                    }
                    // Adding current user as a liker
                    if(like && !containsCurrentUser) {
                        place.getLikers().add(currentUser);
                    }
                } else if(calObject instanceof Event) {
                    final Event event = (Event)calObject;
                    boolean containsCurrentUser = false;
                    // Checking wheter current user is already in the liker list (double tap?)
                    for(User user : new ArrayList<>(event.getComers())) {
                        if(user.getKey().equals(currentUser.getKey())) {
                            containsCurrentUser = true;
                            // Removing right now if unattend
                            if(!like) {
                                event.getComers().remove(user);
                            }
                            break;
                        }
                    }
                    // Adding current user as a liker
                    if(like && !containsCurrentUser) {
                        event.getComers().add(currentUser);
                    }
                }

                return likeInfo;
            }
        };
        return cmd;
    }

    private void registerChatAction() {
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(final Object parameter) {
                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final ChatConversationActivity chatFragment = new ChatConversationActivity();
                        chatFragment.setOtherUserKey(((CalObject) parameter).getKey());
                        chatFragment.setCommentMode(!(parameter instanceof User));
                        PelMelApplication.getSnippetContainerSupport().showSnippetForFragment(chatFragment, true, false);
                    }
                });
                return null;
            }
        };
        commandsActionMap.put(Action.CHAT,cmd);
    }

    private void registerCheckinAction() {
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(Object parameter) {
                // If no object passed for checkin we display the selection dialog
                if(parameter == null) {
                    final ListCheckinsFragment checkinsFragment = new ListCheckinsFragment();
                    uiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            PelMelApplication.getSnippetContainerSupport().showDialog(checkinsFragment);
                        }
                    });

                } else {
                    PelMelApplication.getUserService().checkIn((Place)parameter,null);
                }
                return parameter;
            }
        };
        commandsActionMap.put(Action.CHECKIN,cmd);
    }
    private void registerListDealsAction() {
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(Object parameter) {
                // If no object passed for checkin we display the selection dialog
                final ListDealsFragment dealsFragment = new ListDealsFragment();
                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        PelMelApplication.getSnippetContainerSupport().showDialog(dealsFragment);
                    }
                });

                return parameter;
            }
        };
        commandsActionMap.put(Action.LIST_DEALS,cmd);
    }
    private void registerCheckoutAction() {
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(Object parameter) {

                // If no object passed for checkin we display the selection dialog
                Place place = null;

                // If place is omitted we checkout from current place
                if(parameter == null) {
                    place = PelMelApplication.getUserService().getLoggedUser().getLastLocation();
                } else {
                    place = (Place)parameter;
                }
                PelMelApplication.getUserService().checkOut(place, null);
                return parameter;
            }
        };
        commandsActionMap.put(Action.CHECKOUT,cmd);
    }

    private void promptPrivateNetworkAction(int msgTitleResId, int msgResId, final Action action, final User user) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                (Activity)PelMelApplication.getSnippetContainerSupport());
        builder.setTitle(msgTitleResId).setMessage(msgResId);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        executeNetworkAction(action, user);
                    }
                });

        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        PelMelApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                builder.create().show();
            }
        });
    }

    private void executeNetworkAction(final Action action, final User user) {
        PelMelApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                final ProgressDialog progressDialog = new ProgressDialog((Activity)PelMelApplication.getSnippetContainerSupport());
                progressDialog.setCancelable(false);
                progressDialog.setMessage(Strings.getText(R.string.msg_wait));
                progressDialog.setTitle(Strings.getText(R.string.waitTitle));
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            PelMelApplication.getUserService().executeNetworkAction(action, user);
                        } catch(PelmelException e) {
                            Log.e(LOG_TAG,"Exception during private network action: " + e.getMessage(),e);
                            PelMelApplication.getUiService().showInfoMessage(PelMelApplication.getInstance(),R.string.msg_failTitle,R.string.msg_fail);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        progressDialog.dismiss();
                        PelMelApplication.getSnippetContainerSupport().refresh();
                    }
                }.execute();
            }
        });

    }
    private void registerPrivateNetworkRequestAction() {
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(Object parameter) {
                promptPrivateNetworkAction(R.string.network_confirm_title,R.string.network_confirm_request,Action.NETWORK_REQUEST,(User)parameter);
                return null;
            }
        };
        commandsActionMap.put(Action.NETWORK_REQUEST, cmd);
    }

    private void registerPrivateNetworkCancelAction() {
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(Object parameter) {
                final NetworkStatus status = PelMelApplication.getUserService().getNetworkStatusFor((User)parameter);
                if(status != NetworkStatus.PENDING_REQUEST) {
                    promptPrivateNetworkAction(R.string.network_confirm_title, R.string.network_confirm_cancel, Action.NETWORK_CANCEL, (User) parameter);
                } else {
                    executeNetworkAction(Action.NETWORK_CANCEL,(User)parameter);
                }
                return null;
            }
        };
        commandsActionMap.put(Action.NETWORK_CANCEL, cmd);
    }
    private void registerPrivateNetworkOtherActions(final Action action) {
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(Object parameter) {
                executeNetworkAction(action,(User)parameter);
                return null;
            }
        };
        commandsActionMap.put(action,cmd);
    }

    private void registerPrivateNetworkRespondAction(){
        final ActionCommand cmd = new ActionCommand() {
            @Override
            public Object execute(final Object parameter) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        (Activity)PelMelApplication.getSnippetContainerSupport());
                builder.setTitle(R.string.network_action_respond_actionSheetTitle);
                builder.setPositiveButton(R.string.network_action_respond_accept,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                executeNetworkAction(Action.NETWORK_ACCEPT,(User)parameter);
                            }
                        });

                builder.setNegativeButton(R.string.network_action_respond_decline,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                PelMelApplication.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        builder.create().show();
                    }
                });
                return null;
            }
        };
        commandsActionMap.put(Action.NETWORK_RESPOND,cmd);
    }

}
