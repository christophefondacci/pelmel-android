package com.nextep.pelmel.adapters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;

public class YearSpinnerAdapter implements SpinnerAdapter {

	private static final int MAX_AGE = 100;
	public static final int DEFAULT_AGE = 25;
	private int baseYear;
	private List<Integer> years = new ArrayList<Integer>();
	private Context context;

	public YearSpinnerAdapter(Context context) {
		this.context = context;
		final Calendar c = Calendar.getInstance();
		final int currentYear = c.get(Calendar.YEAR);
		baseYear = currentYear - MAX_AGE;
		for (int i = baseYear; i < currentYear; i++) {
			years.add(i);
		}
	}

	@Override
	public int getCount() {
		return years.size();
	}

	@Override
	public Object getItem(int index) {
		return years.get(index);
	}

	@Override
	public long getItemId(int index) {
		return years.get(index).longValue();
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
		viewHolder.rowText.setText(String.valueOf(years.get(index)));
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

	public int getBaseYear() {
		return baseYear;
	}
}
