package com.pax.ipp.tools.ui.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pax.ipp.tools.R;

public class LoadingView extends FrameLayout{

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	int type = 0;
	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		type = 0;
		init(context);
	}

	public LoadingView(Context context) {
		super(context);
		init(context);
	}

	@SuppressLint("NewApi") private void init(Context context) {
		if (type == 0) {
			View.inflate(context, R.layout.view_loading, this);
		}else if (type == 1) {
//			View.inflate(context, R.layout.loading_small, this);
		}
		
//		ImageView load = (ImageView) findViewById(R.id.load);
//		RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animation.setInterpolator(new LinearInterpolator(){
//			@Override
//			public float getInterpolation(float input) {
//				// TODO Auto-generated method stub
//				return super.getInterpolation(input);
//			}
//		});
//		animation.setRepeatCount(Animation.INFINITE);
//		animation.setDuration(1000);
//		animation.setRepeatMode(Animation.RESTART);
//		load.startAnimation(animation);
		
		
//		ObjectAnimator animator = ObjectAnimator.ofFloat(load, "rotation", 0f,360f);
//		animator.setDuration(1000);
//		animator.setInterpolator(new LinearInterpolator());
//		animator.setRepeatCount(Animation.INFINITE);
//		animator.setRepeatMode(Animation.RESTART);
//		animator.start();
	}
	

}
