package kr.co.starmark.kidsheriff.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class DimImageButton extends ImageButton {

	public DimImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}


	public DimImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public DimImageButton(Context context) {
		super(context);
		initView();
	}
	
	private void initView() {
		setBackgroundResource(0);
	}
	
	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		int [] state  = getDrawableState();
		boolean filter = false;
		for(int st:state)
		{
			if(st == android.R.attr.state_pressed)
				filter = true;
		}
		if(filter)
			setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		else
			setColorFilter(null);
	}

}

