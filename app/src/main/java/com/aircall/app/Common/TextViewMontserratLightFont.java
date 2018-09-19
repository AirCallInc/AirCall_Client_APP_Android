package com.aircall.app.Common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewMontserratLightFont extends TextView {

	public TextViewMontserratLightFont(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		AssetManager assetManager = context.getAssets();
		setTypeface(Typeface.createFromAsset(assetManager, "fonts/montserrat-light.ttf"));
	}

	public TextViewMontserratLightFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TextViewMontserratLightFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
}