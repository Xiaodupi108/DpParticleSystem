package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;

import java.util.Random;

public class AccelerationInitializer implements ParticleInitializer {

	private float mMinValue;
	private float mMaxValue;
	private int mMinAngle;
	private int mMaxAngle;

	private float mAccelerationX;
	private float mAccelerationY;
	private float radialAcceleration;
	private float tangentialAcceleration;
	private float radialAccelerationVariance;
	private float tangentialAccelerationVariance;

	public AccelerationInitializer(float minAcceleration, float maxAcceleration, int minAngle, int maxAngle) {
		mMinValue = minAcceleration;
		mMaxValue = maxAcceleration;
		mMinAngle = minAngle;
		mMaxAngle = maxAngle;
	}

	public AccelerationInitializer(float mAccelerationX , float mAccelerationY, float radialAcceleration, float tangentialAcceleration
	, float radialAccelerationVariance, float tangentialAccelerationVariance){
		this.mAccelerationX = mAccelerationX;
		this.mAccelerationY = mAccelerationY;

		this.radialAcceleration = radialAcceleration;
		this.tangentialAcceleration = tangentialAcceleration;
		this.radialAccelerationVariance = radialAccelerationVariance;
		this.tangentialAccelerationVariance = tangentialAccelerationVariance;
	}

	@Override
	public void initParticle(Particle p, Random r) {
		p.mAccelerationX = -mAccelerationX;
		p.mAccelerationY = -mAccelerationY;
		p.radialAcceleration = radialAcceleration + (r.nextFloat()-0.5f)*2f * radialAccelerationVariance;
		p.tangentialAcceleration = -(tangentialAcceleration + (r.nextFloat()-0.5f)*2f * tangentialAccelerationVariance);
	}

}
