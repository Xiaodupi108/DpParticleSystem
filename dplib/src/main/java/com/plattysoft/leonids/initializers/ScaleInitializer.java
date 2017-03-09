package com.plattysoft.leonids.initializers;

import java.util.Random;

import com.plattysoft.leonids.Particle;

public class ScaleInitializer implements ParticleInitializer {

	private float mStartMinScale = 1f;
	private float mStartMaxScale = 1f;
	private float mEndMaxScale;
	private float mEndMinScale;

	private float startSize;
	private float startVarienceSize;
	private float endSize;
	private float endVarienceSize;

	public ScaleInitializer(float minEndScale, float maxEndScale) {
		mEndMinScale = minEndScale;
		mEndMaxScale = maxEndScale;
	}

	public ScaleInitializer(float startSize , float startVarienceSize ,float endSize, float endVarienceSize){
		this.startSize = startSize;
		this.startVarienceSize = startVarienceSize;
		this.endSize = endSize;
		this.endVarienceSize = endVarienceSize;
	}

	@Override
	public void initParticle(Particle p, Random r) {
		p.startSize =  dealOverRange((r.nextFloat()-0.5f)*2f*startVarienceSize+startSize) ;
		p.endSize = dealOverRange((r.nextFloat()-0.5f)*2f*endVarienceSize+endSize) ;
	}

	private float dealOverRange(float value){
		if(value<0){
			return 0f;
		}
		return value;
	}

}