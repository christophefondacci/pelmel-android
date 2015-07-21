package com.nextep.pelmel.services.impl;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.services.UIService;
import com.nextep.pelmel.views.BadgeView;

public class UIServiceImpl implements UIService {

	private BadgeView badgeView;
	private int unreadMessages = 0;

	@Override
	public void setUnreadMessagesCount(int msgCount) {
		this.unreadMessages = msgCount;
		if (badgeView != null) {
			PelMelApplication.runOnMainThread(new Runnable() {

				@Override
				public void run() {
					badgeView.setText(String.valueOf(unreadMessages));
					if (badgeView.isShown() != (unreadMessages > 0)) {
						badgeView.toggle();
					} else {
						badgeView.show(true);
					}
				}
			});
		}
	}

	@Override
	public void registerUnreadMsgBadgeView(BadgeView badgeView) {
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
}
