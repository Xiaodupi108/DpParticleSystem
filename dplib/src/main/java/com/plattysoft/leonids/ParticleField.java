package com.plattysoft.leonids;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

class ParticleField extends View {

	private ArrayList<Particle> mParticles;

	public ParticleField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public ParticleField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ParticleField(Context context) {
		super(context);
	}

	public void setParticles(ArrayList<Particle> particles) {
		mParticles = particles;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Draw all the particles
		for (int i=0; i<mParticles.size(); i++) {
			try {
				Particle particle = mParticles.get(i);
				if(particle == null){
					continue;
				}
				particle.draw(canvas);
			}catch (IndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}
	}
}