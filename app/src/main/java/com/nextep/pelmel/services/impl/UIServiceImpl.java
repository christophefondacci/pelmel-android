package com.nextep.pelmel.services.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.providers.impl.ContextSnippetInfoProvider;
import com.nextep.pelmel.providers.impl.EventInfoProvider;
import com.nextep.pelmel.providers.impl.PlaceInfoProvider;
import com.nextep.pelmel.providers.impl.UserInfoProvider;
import com.nextep.pelmel.services.UIService;

import java.text.MessageFormat;

public class UIServiceImpl implements UIService {

    private TextView badgeView;
    private int unreadMessages = 0;
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void setUnreadMessagesCount(int msgCount) {
        this.unreadMessages = msgCount;
        if (badgeView != null) {
            PelMelApplication.runOnMainThread(new Runnable() {

                @Override
                public void run() {
                    badgeView.setText(String.valueOf(unreadMessages));
                    if (unreadMessages > 0) {
                        badgeView.setVisibility(View.VISIBLE);
                    } else {
                        badgeView.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void registerUnreadMsgBadgeView(TextView badgeView) {
        this.badgeView = badgeView;
        setUnreadMessagesCount(unreadMessages);
    }

    @Override
    public int getColorForPlaceType(String placeType) {
        if (PelMelConstants.PLACE_TYPE_ASSOCIATION.equals(placeType)) {
            return R.color.type_asso;
        } else if (PelMelConstants.PLACE_TYPE_BAR.equals(placeType)) {
            return R.color.type_bar;
        } else if (PelMelConstants.PLACE_TYPE_CLUB.equals(placeType)) {
            return R.color.type_club;
        } else if (PelMelConstants.PLACE_TYPE_SEXCLUB.equals(placeType)) {
            return R.color.type_sexclub;
        } else if (PelMelConstants.PLACE_TYPE_SHOP.equals(placeType)) {
            return R.color.type_sexshop;
        } else if (PelMelConstants.PLACE_TYPE_RESTAURANT.equals(placeType)) {
            return R.color.type_restaurant;
        } else if (PelMelConstants.PLACE_TYPE_HOTEL.equals(placeType)) {
            return R.color.type_hotel;
        } else if (PelMelConstants.PLACE_TYPE_SAUNA.equals(placeType)) {
            return R.color.type_sauna;
        } else {
            return R.color.type_bar;
        }
    }

    @Override
    public int getLabelForPlaceType(String placeType) {
        int labelResource;
        if (PelMelConstants.PLACE_TYPE_ASSOCIATION.equals(placeType)) {
            labelResource = R.string.placeType_asso;
        } else if (PelMelConstants.PLACE_TYPE_BAR.equals(placeType)) {
            labelResource = R.string.placeType_bar;
        } else if (PelMelConstants.PLACE_TYPE_CLUB.equals(placeType)) {
            labelResource = R.string.placeType_club;
        } else if (PelMelConstants.PLACE_TYPE_SEXCLUB.equals(placeType)) {
            labelResource = R.string.placeType_sexclub;
        } else if (PelMelConstants.PLACE_TYPE_SHOP.equals(placeType)) {
            labelResource = R.string.placeType_sexshop;
        } else if (PelMelConstants.PLACE_TYPE_RESTAURANT.equals(placeType)) {
            labelResource = R.string.placeType_restaurant;
        } else if (PelMelConstants.PLACE_TYPE_HOTEL.equals(placeType)) {
            labelResource = R.string.placeType_hotel;
        } else if (PelMelConstants.PLACE_TYPE_SAUNA.equals(placeType)) {
            labelResource = R.string.placeType_sauna;
        } else {
            labelResource = R.string.placeType_other;
        }
        return labelResource;
    }

    @Override
    public int getIconForPlaceType(String placeType) {
        switch (placeType) {
            case PelMelConstants.PLACE_TYPE_ASSOCIATION:
                return R.drawable.snp_icon_asso;
            case PelMelConstants.PLACE_TYPE_CLUB:
                return R.drawable.snp_icon_club;
            case PelMelConstants.PLACE_TYPE_HOTEL:
                return R.drawable.snp_icon_hotel;
            case PelMelConstants.PLACE_TYPE_RESTAURANT:
                return R.drawable.snp_icon_restaurant;
            case PelMelConstants.PLACE_TYPE_SAUNA:
                return R.drawable.snp_icon_sauna;
            case PelMelConstants.PLACE_TYPE_SEXCLUB:
                return R.drawable.snp_icon_sexclub;
            case PelMelConstants.PLACE_TYPE_SHOP:
                return R.drawable.snp_icon_shop;
            case PelMelConstants.PLACE_TYPE_OUTDOORS:
                return R.drawable.snp_icon_outdoor;
            case PelMelConstants.PLACE_TYPE_BAR:
            default:
                return R.drawable.snp_icon_bar;
        }

    }

    @Override
    public SnippetInfoProvider buildInfoProviderFor(CalObject object) {
        if(object instanceof Place) {
            return new PlaceInfoProvider((Place) object);
        } else if(object instanceof User) {
            return new UserInfoProvider((User) object);
        } else if(object instanceof Event) {
            return new EventInfoProvider((Event)object);
        } else if(object ==null) {
            return new ContextSnippetInfoProvider();
        }
        throw new IllegalArgumentException("Unsupported object for infoProvider builder: " + object.getClass().getName());
    }

    @Override
    public void showInfoMessage(Context context, int resTitle, int resMessage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(resMessage).setTitle(resTitle).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @Override
    public void showInfoMessage(Context context, int resTitle, int resMessage, String arg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String template = PelMelApplication.getInstance().getResources().getString(resMessage);
        final String msg = MessageFormat.format(template,arg);
        builder.setMessage(msg).setTitle(resTitle).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public Bitmap getNoPhotoFor(CalObject obj, boolean thumb) {
        int res = 0;
        if(obj instanceof Event) {
            res = R.drawable.no_photo_event;
        } else {
            res = R.drawable.no_photo_add;
        }

        return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(),res);
    }

    @Override
    public void executeOnUiThread(Runnable task) {
        uiThreadHandler.post(task);
    }
}
