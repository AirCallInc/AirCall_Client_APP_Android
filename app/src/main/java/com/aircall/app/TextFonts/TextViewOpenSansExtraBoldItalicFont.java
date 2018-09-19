package com.aircall.app.TextFonts;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewOpenSansExtraBoldItalicFont extends TextView {

	public TextViewOpenSansExtraBoldItalicFont(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		AssetManager assetManager = context.getAssets();
		setTypeface(Typeface.createFromAsset(assetManager, "OpenSans-ExtraBoldItalic.ttf"));
	}

	public TextViewOpenSansExtraBoldItalicFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TextViewOpenSansExtraBoldItalicFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
}