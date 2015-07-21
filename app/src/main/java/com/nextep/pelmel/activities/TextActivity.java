package com.nextep.pelmel.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.nextep.pelmel.R;

public class TextActivity extends Activity {

	public static final String KEY_INTENT_TEXT_ID = "textId";
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_text);

		textView = (TextView) findViewById(R.id.full_text);
		final Intent intent = getIntent();
		if (intent.getExtras() != null) {
			final String id = (String) intent.getExtras().get(
					KEY_INTENT_TEXT_ID);
			if (id != null) {
				final Integer i = Integer.valueOf(id);
				textView.setText(Html.fromHtml(getString(i.intValue())));
			}
		}
	}
}
