package com.plattysoft.leonids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import com.jiaziang8.dplib.utils.Vector;
import com.jiaziang8.dplib.utils.VectorUtils;
import com.plattysoft.leonids.modifiers.ParticleModifier;

import java.util.List;

public class Particle {

	protected Bitmap mImage;

	public float bitmapWidth;
	
	public float mCurrentX;
	public float mCurrentY;

	public float startScale = 1f;
	public float endScale = 1f;
	public float mScale = 1f;
	public int mAlpha = 255;

	public float startSize = 0f;
	public float endSize = 0f;
	
	public float mInitialRotation = 0f;
	public float mEndRotation = 0f;
	
	public float mRotationSpeed = 0f;
	
	public float mSpeedX = 0f;
	public float mSpeedY = 0f;

	public float mAccelerationX;
	public float mAccelerationY;
	public float radialAcceleration;
	public float tangentialAcceleration;

	public static final int RGB_MAX = 255;
	public static final int ALPHA_MAX = 255;

	public int startRed;
	public int startGreen;
	public int startBlue;
	public int endRed;
	public int endGreen;
	public int endBlue;

	public int redValue;
	public int greenValue;
	public int blueValue;

	public int startAlpha;
	public int endAlpha;

	private Matrix mMatrix;
	private Paint mPaint;

	private float mStartX;
	private float mStartY;

	public float mRotation;

	public long mTimeToLive;

	protected float lastUpdateSecond;
	protected long mStartingMillisecond;

	private int mBitmapHalfWidth;
	private int mBitmapHalfHeight;

	public float degreesPerSecond;

	public float mInitialAngle;
	public float radius;
	public float angle;
	//默认为Gravity mode
	public boolean isVortex=false;
	public float maxRadius;
	public float minRadius;
	public float mInitRadius;

	private List<ParticleModifier> mModifiers;

	private PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.rgb(0,0,0), PorterDuff.Mode.SRC_ATOP);

	protected Particle() {		
		mMatrix = new Matrix();
		mPaint = new Paint();
	}
	
	public Particle (Bitmap bitmap) {
		this();
		mImage = bitmap;
	}

	public void init() {
		mScale = 1;
		mAlpha = 255;
		bitmapWidth = mImage.getWidth();
	}
	
	public void configure(long timeToLive, float emiterX, float emiterY, float mStartX, float mStartY) {
		mBitmapHalfWidth = mImage.getWidth()/2;
		mBitmapHalfHeight = mImage.getHeight()/2;
		
		this.mStartX = mStartX;
		this.mStartY = mStartY;
		mCurrentX = emiterX - mBitmapHalfWidth;
		mCurrentY =  emiterY - mBitmapHalfHeight;
		mTimeToLive = timeToLive;
		lastUpdateSecond = 0;
	}

	public boolean update (long milliseconds) {
		//此粒子已存在的时间 ms
		long realMilliseconds = milliseconds - mStartingMillisecond;
		//此粒子已存在的时间 s
		float realSeconds = (float) realMilliseconds/1000;
		//距离上次刷新的时间
		float deltaSeconds = realSeconds - lastUpdateSecond;
		//占生命周期的时间百分比
		float timePercent = (float)realMilliseconds/mTimeToLive;
		//生命周期结束,remove掉此粒子
		if (realMilliseconds > mTimeToLive) {
			return false;
		}
		if(isVortex){
			radius = (1 - timePercent)*mInitRadius;
			//落在最小半径以内，remove掉
			if(radius<minRadius){
				return false;
			}
			angle = mInitialAngle + degreesPerSecond * realSeconds;
			mCurrentX = mStartX - ((float)Math.sin(Math.toRadians(angle)))*radius;
			mCurrentY = mStartY - ((float)Math.cos(Math.toRadians(angle)))*radius;
		}else{
			//根据切向加速度和法相加速度改变速度
			Vector currentPositionVector = new Vector(mCurrentX, mCurrentY);
			Vector startPositionVector = new Vector(mStartX, mStartY);
			Vector relativePosition = VectorUtils.vectorSubtract(currentPositionVector, startPositionVector);
			Vector radial = new Vector(0, 0);
			if(relativePosition.getX()!=0 && relativePosition.getY() !=0){
				radial = VectorUtils.vectorNormalize(relativePosition);
			}
			Vector tangential = radial;
			radial = VectorUtils.vectorMultiplyScale(radial, radialAcceleration);
			float newY  = tangential.getX();
			tangential.setX(-tangential.getY());
			tangential.setY(newY);
			tangential = VectorUtils.vectorMultiplyScale(tangential, tangentialAcceleration);

			Vector gravity = new Vector(mAccelerationX, mAccelerationY);
			Vector tmp = VectorUtils.vectorAdd(VectorUtils.vectorAdd(radial, tangential), gravity);
			tmp = VectorUtils.vectorMultiplyScale(tmp, deltaSeconds);
			mSpeedX = mSpeedX + tmp.getX();
			mSpeedY = mSpeedY + tmp.getY();
			mCurrentX = mCurrentX + mSpeedX * deltaSeconds;
			mCurrentY = mCurrentY + mSpeedY * deltaSeconds;
		}
		//缩放
		startScale = startSize/bitmapWidth;
		endScale = endSize/bitmapWidth;
		mScale = startScale + timePercent*(endScale-startScale);

		redValue = startRed+(int)(timePercent*(endRed-startRed));
		greenValue = startGreen+(int)(timePercent*(endGreen-startGreen));
		blueValue = startBlue+(int)(timePercent*(endBlue-startBlue));
		filter = new PorterDuffColorFilter(Color.argb(mAlpha, redValue,greenValue,blueValue), PorterDuff.Mode.MULTIPLY);
		mAlpha = startAlpha+(int)(timePercent*(endAlpha-startAlpha));

		//旋转角度=（结束角度-开始角度）/ 生命周期
		mRotationSpeed=(mEndRotation-mInitialRotation)/mTimeToLive*1000;
		mRotation = mRotation + mRotationSpeed * deltaSeconds;
		for (int i=0; i<mModifiers.size(); i++) {
			mModifiers.get(i).apply(this, (long)(realSeconds*1000));
		}

		lastUpdateSecond = realSeconds;
		return true;
	}
	
	public void draw (Canvas c) {
		mMatrix.reset();
		mMatrix.postRotate(mRotation, mBitmapHalfWidth, mBitmapHalfHeight);
		mMatrix.postScale(mScale, mScale, mBitmapHalfWidth, mBitmapHalfHeight);
		mMatrix.postTranslate(mCurrentX, mCurrentY);
		mPaint.setAlpha(mAlpha);
		mPaint.setColorFilter(filter);
		c.drawBitmap(mImage, mMatrix, mPaint);

	}

	public Particle activate(long startingMillisecond, List<ParticleModifier> modifiers) {
		mStartingMillisecond = startingMillisecond;
		// We do store a reference to the list, there is no need to copy, since the modifiers do not care about states
		mModifiers = modifiers;
		return this;
	}

}