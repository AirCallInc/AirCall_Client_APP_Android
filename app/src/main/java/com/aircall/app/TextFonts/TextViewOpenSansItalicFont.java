package com.aircall.app.TextFonts;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewOpenSansItalicFont extends TextView {

	public TextViewOpenSansItalicFont(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		AssetManager assetManager = context.getAssets();
		setTypeface(Typeface.createFromAsset(assetManager, "OpenSans-Italic.ttf"));
	}

	public TextViewOpenSansItalicFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TextViewOpenSansItalicFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
}