package com.nextep.pelmel.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView that keeps aspect ratio when scaled
 */
public class ScaleImageView extends ImageView {

	public ScaleImageView(Context context) {
		super(context);
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			final Drawable drawable = getDrawable();

			if (drawable == null) {
				setMeasuredDimension(0, 0);
			} else {
				final float imageSideRatio = (float) drawable
						.getIntrinsicWidth()
						/ (float) drawable.getIntrinsicHeight();
				final float viewSideRatio = (float) MeasureSpec
						.getSize(widthMeasureSpec)
						/ (float) MeasureSpec.getSize(heightMeasureSpec);
				if (imageSideRatio >= viewSideRatio) {
					// Image is wider than the display (ratio)
					final int width = MeasureSpec.getSize(widthMeasureSpec);
					final int height = (int) (width / imageSideRatio);
					setMeasuredDimension(width, height);
				} else {
					// Image is taller than the display (ratio)
					final int height = MeasureSpec.getSize(heightMeasureSpec);
					final int width = (int) (height * imageSideRatio);
					setMeasuredDimension(width, height);
				}
			}
		} catch (final Exception e) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}