package kr.co.starmark.kidsheriff.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

public class CloseableContainer extends LinearLayout {

	private long mAnimationTime;
	public interface OnAddChildCallback
	{
		void onComplete();
	}
	public interface OnDismissCallback {
		void onDismiss(View view, Object token);
	}

	public CloseableContainer(Context context) {
		super(context);
		initView();
	}

	public CloseableContainer(Context context, AttributeSet set) {
		super(context, set);
		initView();
	}

	public CloseableContainer(Context context, AttributeSet set, int arg) {
		super(context, set, arg);
		initView();
	}

	private void initView() {
		mAnimationTime = getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
		setOrientation(LinearLayout.VERTICAL);
	}

	private void performDismiss(final View targetView) {
		final OnDismissCallback callback = new OnDismissCallback() {
			@Override
			public void onDismiss(View view, Object token) {
				
				CloseableContainer.this.removeView(targetView);
				int childCnt = getChildCount();
				if (childCnt == 1)
					return;
				View last = getChildAt(childCnt - 1);
			}
		};

		final ViewGroup.LayoutParams lp = targetView.getLayoutParams();
		final int originalHeight = targetView.getHeight();

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				callback.onDismiss(targetView, null);
				// Reset view presentation
				com.nineoldandroids.view.ViewHelper.setAlpha(targetView, 1f);
				com.nineoldandroids.view.ViewHelper.setTranslationX(targetView, 0);
				lp.height = originalHeight;
				targetView.setLayoutParams(lp);
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				targetView.setLayoutParams(lp);
			}
		});
		animator.start();
	}
}
