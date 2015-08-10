package com.nextep.pelmel.push;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.nextep.pelmel.PelMelApplication;

/**
 * Created by cfondacci on 07/08/15.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        PelMelApplication.getMessageService().requestPushToken();
    }
}
