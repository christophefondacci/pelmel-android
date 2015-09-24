package com.nextep.pelmel.services.impl;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.nextep.json.model.impl.JsonLikeInfo;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.activities.ChatConversationActivity;
import com.nextep.pelmel.activities.ListCheckinsFragment;
import com.nextep.pelmel.activities.ListDealsFragment;
import com.nextep.pelmel.model.Action;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
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
                PelMelApplication.getUserService().checkOut(place,null);
                return parameter;
            }
        };
        commandsActionMap.put(Action.CHECKOUT,cmd);
    }
}
