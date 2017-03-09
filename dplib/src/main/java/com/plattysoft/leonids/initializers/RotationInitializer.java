package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;

import java.util.Random;

public class RotationInitializer implements ParticleInitializer {
	private float rotationStart;
	private float rotationStartVariance;
	private float rotationEnd;
	private float rotationEndVariance;

	public RotationInitializer(float rotationStart, float rotationStartVariance, float rotationEnd, float rotationEndVariance) {
		this.rotationStart = -rotationStart;
		this.rotationStartVariance  = rotationStartVariance;
		this.rotationEnd = -rotationEnd;
		this.rotationEndVariance = rotationEndVariance;
	}

	@Override
	public void initParticle(Particle p, Random r) {
		p.mInitialRotation = rotationStart + (r.nextFloat()-0.5f)*2f*rotationStartVariance;
		p.mEndRotation = rotationEnd + (r.nextFloat()-0.5f)*2f*rotationEndVariance;
		p.mRotation = p.mInitialRotation;
	}

}
