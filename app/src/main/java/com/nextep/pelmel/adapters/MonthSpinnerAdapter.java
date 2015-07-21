package com.nextep.pelmel.adapters;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonthSpinnerAdapter implements SpinnerAdapter {

	private List<String> monthLabels = new ArrayList<String>();

	public MonthSpinnerAdapter() {
		final Resources r = PelMelApplication.getInstance().getResources();
		monthLabels = Arrays.asList(
				r.getString(com.nextep.pelmel.R.string.month_january),
				r.getString(com.nextep.pelmel.R.string.month_february),
				r.getString(com.nextep.pelmel.R.string.month_march),
				r.getString(com.nextep.pelmel.R.string.month_april),
				r.getString(com.nextep.pelmel.R.string.month_may),
				r.getString(com.nextep.pelmel.R.string.month_june),
				r.getString(com.nextep.pelmel.R.string.month_july),
				r.getString(com.nextep.pelmel.R.string.month_august),
				r.getString(com.nextep.pelmel.R.string.month_september),
				r.getString(com.nextep.pelmel.R.string.month_october),
				r.getString(com.nextep.pelmel.R.string.month_november),
				r.getString(com.nextep.pelmel.R.string.month_december));
	}

	@Override
	public int getCount() {
		return monthLabels.size();
	}

	@Override
	public Object getItem(int index) {
		return monthLabels.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index + 1;
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(PelMelApplication.getInstance())
					.inflate(com.nextep.pelmel.R.layout.spinner_row_text, null);
			viewHolder = new ViewHolder();
			viewHolder.rowText = (TextView) convertView
					.findViewById(com.nextep.pelmel.R.id.rowText);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.rowText.setText(monthLabels.get(index));
		return convertView;

	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getDropDownView(int arg0, View arg1, ViewGroup arg2) {
		return getView(arg0, arg1, arg2);
	}

	class ViewHolder {
		TextView rowText;
	}
}
