package com.plattysoft.leonids;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.plattysoft.leonids.initializers.*;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ParticleModifier;

import java.util.*;

public class ParticleSystem {

	private static final long TIMMERTASK_INTERVAL = 50;
	private ViewGroup mParentView;
	protected int mMaxParticles;
	private Random mRandom;

	public ParticleField mDrawingView;

	protected ArrayList<Particle> mParticles;
	private ArrayList<Particle> mActiveParticles;
	protected long mTimeToLive;
	private long mCurrentTime = 0;

	private float mParticlesPerMilisecond;
	private int mActivatedParticles;
	private long mEmitingTime;

	private List<ParticleModifier> mModifiers;
	private List<ParticleInitializer> mInitializers;
	private ValueAnimator mAnimator;
	private Timer mTimer;

	private float mDpToPxScale;
	private int[] mParentLocation;
	
	protected int mEmiterXMin;
	protected int mEmiterXMax;
	protected int mEmiterYMin;
	protected int mEmiterYMax;

	protected float sourcePosX;
	protected float sourcePosY;

	protected ParticleSystem(Activity a, ViewGroup mParentView){
		mRandom = new Random();
		this.mParentView = mParentView;

		mModifiers = new ArrayList<>();
		mInitializers = new ArrayList<>();

		// Create the particles
		mActiveParticles = new ArrayList<>();
		mParticles = new ArrayList<> ();

		mParentLocation = new int[2];
		mParentView.getLocationInWindow(mParentLocation);

		DisplayMetrics displayMetrics = a.getResources().getDisplayMetrics();
		mDpToPxScale = (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
	}

	protected ParticleSystem(Activity a, int parentResId) {
		this(a,(ViewGroup)a.findViewById(parentResId));
	}

	private ParticleSystem(Activity a, int maxParticles, long timeToLive, int parentResId) {
		mRandom = new Random();
		mParentView = (ViewGroup) a.findViewById(parentResId);

		mModifiers = new ArrayList<>();
		mInitializers = new ArrayList<>();

		mMaxParticles = maxParticles;
		// Create the particles
		mActiveParticles = new ArrayList<>();
		mParticles = new ArrayList<> ();
		mTimeToLive = timeToLive;
		
		mParentLocation = new int[2];		
		mParentView.getLocationInWindow(mParentLocation);
		
		DisplayMetrics displayMetrics = a.getResources().getDisplayMetrics();
		mDpToPxScale = (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
	}

	/**
	 * Creates a particle system with the given parameters
	 * 
	 * @param a The parent activity
	 * @param maxParticles The maximum number of particles
	 * @param drawableRedId The drawable resource to use as particle (supports Bitmaps and Animations)
	 * @param timeToLive The time to live for the particles
	 */
	public ParticleSystem(Activity a, int maxParticles, int drawableRedId, long timeToLive) {
		this(a, maxParticles, a.getResources().getDrawable(drawableRedId), timeToLive, android.R.id.content);
	}

    /**
     * Creates a particle system with the given parameters
     *
     * @param a The parent activity
     * @param maxParticles The maximum number of particles
     * @param drawableRedId The drawable resource to use as particle (supports Bitmaps and Animations)
     * @param timeToLive The time to live for the particles
     * @param parentViewId The view Id for the parent of the particle system
     */
    public ParticleSystem(Activity a, int maxParticles, int drawableRedId, long timeToLive, int parentViewId) {
        this(a, maxParticles, a.getResources().getDrawable(drawableRedId), timeToLive, parentViewId);
    }

    /**
     * Utility constructor that receives a Drawable
     *
     * @param a The parent activity
     * @param maxParticles The maximum number of particles
     * @param drawable The drawable to use as particle (supports Bitmaps and Animations)
     * @param timeToLive The time to live for the particles
     */
    public ParticleSystem(Activity a, int maxParticles, Drawable drawable, long timeToLive) {
        this(a, maxParticles, drawable, timeToLive, android.R.id.content);
    }
	/**
	 * Utility constructor that receives a Drawable
	 * 
	 * @param a The parent activity
	 * @param maxParticles The maximum number of particles
	 * @param drawable The drawable to use as particle (supports Bitmaps and Animations)
	 * @param timeToLive The time to live for the particles
     * @param parentViewId The view Id for the parent of the particle system
	 */
	public ParticleSystem(Activity a, int maxParticles, Drawable drawable, long timeToLive, int parentViewId) {
		this(a, maxParticles, timeToLive, parentViewId);
		if (drawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			for (int i=0; i<mMaxParticles; i++) {
				mParticles.add (new Particle (bitmap));
			}
		}
		else if (drawable instanceof AnimationDrawable) {
			AnimationDrawable animation = (AnimationDrawable) drawable;
			for (int i=0; i<mMaxParticles; i++) {
				mParticles.add (new AnimatedParticle (animation));
			}
		}
		else {
			// Not supported, no particles are being created
		}
	}

	public float dpToPx(float dp) {
		return dp * mDpToPxScale;
	}

    /**
     * Utility constructor that receives a Bitmap
     *
     * @param a The parent activity
     * @param maxParticles The maximum number of particles
     * @param bitmap The bitmap to use as particle
     * @param timeToLive The time to live for the particles
     */
    public ParticleSystem(Activity a, int maxParticles, Bitmap bitmap, long timeToLive) {
        this(a, maxParticles, bitmap, timeToLive, android.R.id.content);
    }
	/**
	 * Utility constructor that receives a Bitmap
	 * 
	 * @param a The parent activity
	 * @param maxParticles The maximum number of particles
	 * @param bitmap The bitmap to use as particle
	 * @param timeToLive The time to live for the particles
     * @param parentViewId The view Id for the parent of the particle system
	 */
	public ParticleSystem(Activity a, int maxParticles, Bitmap bitmap, long timeToLive, int parentViewId) {
		this(a, maxParticles, timeToLive, parentViewId);
		for (int i=0; i<mMaxParticles; i++) {
			mParticles.add (new Particle (bitmap));
		}
	}

    /**
     * Utility constructor that receives an AnimationDrawble
     *
     * @param a The parent activity
     * @param maxParticles The maximum number of particles
     * @param animation The animation to use as particle
     * @param timeToLive The time to live for the particles
     */
    public ParticleSystem(Activity a, int maxParticles, AnimationDrawable animation, long timeToLive) {
        this(a, maxParticles, animation, timeToLive, android.R.id.content);
    }

	/**
	 * Utility constructor that receives an AnimationDrawble
	 * 
	 * @param a The parent activity
	 * @param maxParticles The maximum number of particles
	 * @param animation The animation to use as particle
	 * @param timeToLive The time to live for the particles
     * @param parentViewId The view Id for the parent of the particle system
	 */
	public ParticleSystem(Activity a, int maxParticles, AnimationDrawable animation, long timeToLive, int parentViewId) {
		this(a, maxParticles, timeToLive, parentViewId);
		// Create the particles
		for (int i=0; i<mMaxParticles; i++) {
			mParticles.add (new AnimatedParticle (animation));
		}
	}

	/**
	 * Adds a modifier to the Particle system, it will be executed on each update.
	 * 
	 * @param modifier
	 */
	public ParticleSystem addModifier(ParticleModifier modifier) {
		mModifiers.add(modifier);
		return this;
	}

	public ParticleSystem setSpeedRange(float speedMin, float speedMax) { 
		mInitializers.add(new SpeeddModuleAndRangeInitializer(dpToPx(speedMin), dpToPx(speedMax), 0, 360));		
		return this;
	}

	public ParticleSystem setSpeedModuleAndAngleRange(float speedMin, float speedMax, int minAngle, int maxAngle) {
		mInitializers.add(new SpeeddModuleAndRangeInitializer(dpToPx(speedMin), dpToPx(speedMax), minAngle, maxAngle));		
		return this;
	}

	public ParticleSystem setSpeedByComponentsRange(float speedMinX, float speedMaxX, float speedMinY, float speedMaxY) {
		mInitializers.add(new SpeeddByComponentsInitializer(dpToPx(speedMinX), dpToPx(speedMaxX), 
				dpToPx(speedMinY), dpToPx(speedMaxY)));		
		return this;
	}

	/**
	 * 设置粒子的旋转角度
	 * @param rotationStart 开始的角度
	 * @param rotationStartVariance 变化量
	 * @param rotationEnd 结束角度
	 * @param rotationEndVariance 变化亮
     * @return
     */
	public ParticleSystem setInitialRotationRange (float rotationStart, float rotationStartVariance, float rotationEnd, float rotationEndVariance) {
		mInitializers.add(new RotationInitializer(rotationStart, rotationStartVariance, rotationEnd, rotationEndVariance));
		return this;
	}

	public ParticleSystem setRadiusInitiazer(float maxRadius,float minRadius,float maxRadiusVariance,float minRadiusVariance,float rotatePerSecond,float rotatePerSecondVariance,float angle,float angleVariance){
		mInitializers.add(new RadiusInitiazer(dpToPx(maxRadius),dpToPx(minRadius),dpToPx(maxRadiusVariance),dpToPx(minRadiusVariance),rotatePerSecond,rotatePerSecondVariance,angle,angleVariance));
		return this;
	}

	public ParticleSystem setScaleRange(float endMinScale, float endMaxScale) {
		mInitializers.add(new ScaleInitializer(endMinScale, endMaxScale));
		return this;
	}

	public ParticleSystem setScaleRange(float startSize , float startVarience , float endSize, float endVarienceSize) {
		mInitializers.add(new ScaleInitializer(dpToPx(startSize),dpToPx(startVarience),dpToPx(endSize), dpToPx(endVarienceSize)));
		return this;
	}

	public ParticleSystem setRotationSpeed(float rotationSpeed) {
		mInitializers.add(new RotationSpeedInitializer(rotationSpeed, rotationSpeed));
		return this;
	}

	public ParticleSystem setColor(float redStart,float greenStart,float blueStart,
								   float redStartVarience,float greenStartVarience, float blueStartVarience,
								   float redEnd,float greenEnd,float blueEnd,
								   float redEndVarience,float greenEndVarience,float blueEndVarience,
								   float startAlpha,float startVarienceAlpha,float endAlpha,float endVarienceAlpha){
		mInitializers.add(new ColorFilterInitializer(redStart,greenStart,blueStart,redStartVarience,greenStartVarience,
				blueStartVarience,redEnd,greenEnd,blueEnd,redEndVarience,greenEndVarience,blueEndVarience,startAlpha,
				startVarienceAlpha,endAlpha,endVarienceAlpha));
		return this;
	}

	public ParticleSystem setRotationSpeedRange(float minRotationSpeed, float maxRotationSpeed) {
		mInitializers.add(new RotationSpeedInitializer(minRotationSpeed, maxRotationSpeed));
		return this;
	}

	public ParticleSystem setAccelerationModuleAndAndAngleRange(float minAcceleration, float maxAcceleration, int minAngle, int maxAngle) {
		mInitializers.add(new AccelerationInitializer(dpToPx(minAcceleration), dpToPx(maxAcceleration), 
				minAngle, maxAngle));
		return this;
	}

	public ParticleSystem setAcceleration(float accelerationX, float accelerationY, float radioAcceleration, float tangentialAcceleration,
										  float radialAccelerationVariance, float tangentialAccelerationVariance) {
		mInitializers.add(new AccelerationInitializer(accelerationX,accelerationY, radioAcceleration, tangentialAcceleration, radialAccelerationVariance,
				tangentialAccelerationVariance));
		return this;
	}

	public ParticleSystem setParentViewGroup(ViewGroup viewGroup) {
		mParentView = viewGroup;
		return this;
	}

	public ParticleSystem setStartTime(int time) {
		mCurrentTime = time;
		return this;
	}

	/**
	 * Configures a fade out for the particles when they disappear
	 * 
	 * @param milisecondsBeforeEnd fade out duration in miliseconds
	 * @param interpolator the interpolator for the fade out (default is linear)
	 */
	public ParticleSystem setFadeOut(long milisecondsBeforeEnd, Interpolator interpolator) {
		mModifiers.add(new AlphaModifier(255, 0, mTimeToLive-milisecondsBeforeEnd, mTimeToLive, interpolator));
		return this;
	}

	/**
	 * Configures a fade out for the particles when they disappear
	 * 
	 * @param duration fade out duration in miliseconds
	 */
	public ParticleSystem setFadeOut(long duration) {
		return setFadeOut(duration, new LinearInterpolator());
	}

	/**
	 * Starts emiting particles from a specific view. If at some point the number goes over the amount of particles availabe on create
	 * no new particles will be created
	 * 
	 * @param emiter  View from which center the particles will be emited
	 * @param gravity Which position among the view the emission takes place
	 * @param particlesPerSecond Number of particles per second that will be emited (evenly distributed)
	 * @param emitingTime time the emiter will be emiting particles
	 */
	public void emitWithGravity (View emiter, int gravity, int particlesPerSecond, int emitingTime) {
		// Setup emiter
		configureEmiter(emiter, gravity);
		startEmiting(particlesPerSecond, emitingTime);
	}
	
	/**
	 * Starts emiting particles from a specific view. If at some point the number goes over the amount of particles availabe on create
	 * no new particles will be created
	 * 
	 * @param emiter  View from which center the particles will be emited
	 * @param particlesPerSecond Number of particles per second that will be emited (evenly distributed)
	 * @param emitingTime time the emiter will be emiting particles
	 */
	public void emit (View emiter, int particlesPerSecond, int emitingTime) {
		emitWithGravity(emiter, Gravity.CENTER, particlesPerSecond, emitingTime);
	}
	
	/**
	 * Starts emiting particles from a specific view. If at some point the number goes over the amount of particles availabe on create
	 * no new particles will be created
	 * 
	 * @param emiter  View from which center the particles will be emited
	 * @param particlesPerSecond Number of particles per second that will be emited (evenly distributed)
	 */
	public void emit (View emiter, int particlesPerSecond) {
		// Setup emiter
		emitWithGravity(emiter, Gravity.CENTER, particlesPerSecond);
	}

	/**
	 * Starts emiting particles from a specific view. If at some point the number goes over the amount of particles availabe on create
	 * no new particles will be created
	 * 
	 * @param emiter  View from which center the particles will be emited
	 * @param gravity Which position among the view the emission takes place
	 * @param particlesPerSecond Number of particles per second that will be emited (evenly distributed)
	 */
	public void emitWithGravity (View emiter, int gravity, int particlesPerSecond) {
		// Setup emiter
		configureEmiter(emiter, gravity);
		startEmiting(particlesPerSecond);
	}
	
	public void startEmiting(int particlesPerSecond) {
		mActivatedParticles = 0;
		mParticlesPerMilisecond = particlesPerSecond/1000f;
		// Add a full size view to the parent view		
		mDrawingView = new ParticleField(mParentView.getContext());
		mParentView.addView(mDrawingView);
		mEmitingTime = -1; // Meaning infinite
		mDrawingView.setParticles (mActiveParticles);
		updateParticlesBeforeStartTime(particlesPerSecond);
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				onUpdate(mCurrentTime);
				mCurrentTime += TIMMERTASK_INTERVAL;
			}
		}, 0, TIMMERTASK_INTERVAL);
	}

	public void emit (int emitterX, int emitterY, int particlesPerSecond, int emitingTime) {
		configureEmiter(emitterX, emitterY);
		startEmiting(particlesPerSecond, emitingTime);
	}
	
	private void configureEmiter(int emitterX, int emitterY) {
		// We configure the emiter based on the window location to fix the offset of action bar if present		
		mEmiterXMin = emitterX - mParentLocation[0];
		mEmiterXMax = mEmiterXMin;
		mEmiterYMin = emitterY - mParentLocation[1];
		mEmiterYMax = mEmiterYMin;
	}

	protected void startEmiting(int particlesPerSecond, int emitingTime) {
		mActivatedParticles = 0;
		mParticlesPerMilisecond = particlesPerSecond/1000f;
		// Add a full size view to the parent view		
		mDrawingView = new ParticleField(mParentView.getContext());
		mParentView.addView(mDrawingView);

		mDrawingView.setParticles (mActiveParticles);
		updateParticlesBeforeStartTime(particlesPerSecond);
		mEmitingTime = emitingTime;
		startAnimator(new LinearInterpolator(), emitingTime+mTimeToLive);
	}

	public void emit (int emitterX, int emitterY, int particlesPerSecond) {
		configureEmiter(emitterX, emitterY);
		startEmiting(particlesPerSecond);
	}


	public void updateEmitPoint (int emitterX, int emitterY) {
		configureEmiter(emitterX, emitterY);
	}
	/**
	 * Launches particles in one Shot
	 * 
	 * @param emiter View from which center the particles will be emited
	 * @param numParticles number of particles launched on the one shot
	 */
	public void oneShot(View emiter, int numParticles) {
		oneShot(emiter, numParticles, new LinearInterpolator());
	}

	/**
	 * Launches particles in one Shot using a special Interpolator
	 * 
	 * @param emiter View from which center the particles will be emited
	 * @param numParticles number of particles launched on the one shot
	 * @param interpolator the interpolator for the time
	 */
	public void oneShot(View emiter, int numParticles, Interpolator interpolator) {
		//configureEmiter(emiter, Gravity.NO_GRAVITY);
		configureEmiter(emiter, Gravity.CENTER);
		mActivatedParticles = 0;
		mEmitingTime = mTimeToLive;
		// We create particles based in the parameters
		for (int i=0; i<numParticles && i<mMaxParticles; i++) {
			activateParticle(0);
		}
		// Add a full size view to the parent view		
		mDrawingView = new ParticleField(mParentView.getContext());
		mParentView.addView(mDrawingView);
		mDrawingView.setParticles (mActiveParticles);
		// We start a property animator that will call us to do the update
		// Animate from 0 to timeToLiveMax
		startAnimator(interpolator, mTimeToLive);
	}


	private void startAnimator(Interpolator interpolator, long animnationTime) {
		mAnimator = ValueAnimator.ofInt(0, (int) animnationTime);
		mAnimator.setDuration(animnationTime);
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int miliseconds = (Integer) animation.getAnimatedValue();
				onUpdate(miliseconds);
			}
		});
		mAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {}

			@Override
			public void onAnimationRepeat(Animator animation) {}

			@Override
			public void onAnimationEnd(Animator animation) {
				cleanupAnimation();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				cleanupAnimation();
			}
		});
		mAnimator.setInterpolator(interpolator);
		mAnimator.start();
	}

	private void configureEmiter(View emiter, int gravity) {
		// It works with an emision range
		int[] location = new int[2];
		emiter.getLocationInWindow(location);
		
		// Check horizontal gravity and set range
		if (hasGravity(gravity, Gravity.LEFT)) {
			mEmiterXMin = location[0] - mParentLocation[0];
			mEmiterXMax = mEmiterXMin;
		}
		else if (hasGravity(gravity, Gravity.RIGHT)) {
			mEmiterXMin = location[0] + emiter.getWidth() - mParentLocation[0];
			mEmiterXMax = mEmiterXMin;
		}
		else if (hasGravity(gravity, Gravity.CENTER_HORIZONTAL)){
			mEmiterXMin = location[0] + emiter.getWidth()/2 - mParentLocation[0];
			mEmiterXMax = mEmiterXMin;
		}
		else {
			// All the range
			mEmiterXMin = location[0] - mParentLocation[0];
			mEmiterXMax = location[0] + emiter.getWidth() - mParentLocation[0];
		}
		
		// Now, vertical gravity and range
		if (hasGravity(gravity, Gravity.TOP)) {
			mEmiterYMin = location[1] - mParentLocation[1];
			mEmiterYMax = mEmiterYMin;
		}
		else if (hasGravity(gravity, Gravity.BOTTOM)) {
			mEmiterYMin = location[1] + emiter.getHeight() - mParentLocation[1];
			mEmiterYMax = mEmiterYMin;
		}
		else if (hasGravity(gravity, Gravity.CENTER_VERTICAL)){
			mEmiterYMin = location[1] + emiter.getHeight()/2 - mParentLocation[1];
			mEmiterYMax = mEmiterYMin;
		}
		else {
			// All the range
			mEmiterYMin = location[1] - mParentLocation[1];
			mEmiterYMax = location[1] + emiter.getHeight() - mParentLocation[1];
		}
	}

	private boolean hasGravity(int gravity, int gravityToCheck) {
		return (gravity & gravityToCheck) == gravityToCheck;
	}

	private void activateParticle(long delay) {
		Particle p = mParticles.remove(0);	
		p.init();
		// Initialization goes before configuration, scale is required before can be configured properly
		for (int i=0; i<mInitializers.size(); i++) {
			if(mInitializers.get(i) == null){

			}
			mInitializers.get(i).initParticle(p, mRandom);
		}
		int particleX = getFromRange (mEmiterXMin, mEmiterXMax);
		int particleY = getFromRange (mEmiterYMin, mEmiterYMax);
		p.configure(mTimeToLive, particleX, particleY, sourcePosX, sourcePosY);
		p.activate(delay, mModifiers);
		mActiveParticles.add(p);
		mActivatedParticles++;
	}

	private int getFromRange(int minValue, int maxValue) {
		if (minValue == maxValue) {
			return minValue;
		}
		return mRandom.nextInt(maxValue-minValue) + minValue;
	}

	private void onUpdate(long miliseconds) {
		while (((mEmitingTime > 0 && miliseconds < mEmitingTime)|| mEmitingTime == -1) && // This point should emit
				!mParticles.isEmpty() && // We have particles in the pool 
				mActivatedParticles < mParticlesPerMilisecond*miliseconds) { // and we are under the number of particles that should be launched
			// Activate a new particle
			activateParticle(miliseconds);			
		}
		for (int i=0; i<mActiveParticles.size(); i++) {
			boolean active = mActiveParticles.get(i).update(miliseconds);
			if (!active) {
				Particle p = mActiveParticles.remove(i);
				i--; // Needed to keep the index at the right position
				mParticles.add(p);
			}
		}
		if(mDrawingView!=null) mDrawingView.postInvalidate();
	}

	protected void cleanupAnimation() {
		mParentView.removeView(mDrawingView);
		mDrawingView = null;
		mParentView.postInvalidate();
		mParticles.addAll(mActiveParticles);
	}

	/**
	 * Stops emitting new particles, but will draw the existing ones until their timeToLive expire
	 * For an cancellation and stop drawing of the particles, use cancel instead.
	 */
	public void stopEmitting () {
		// The time to be emiting is the current time (as if it was a time-limited emiter
		mEmitingTime = mCurrentTime;
	}
	
	/**
	 * Cancels the particle system and all the animations.
	 * To stop emitting but animate until the end, use stopEmitting instead.
	 */
	public void cancel() {
		if (mAnimator != null && mAnimator.isRunning()) {
			mAnimator.cancel();
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			cleanupAnimation();
		}
	}

	private void updateParticlesBeforeStartTime(int particlesPerSecond) {
		if (particlesPerSecond == 0) {
			return;
		}
		long currentTimeInMs = mCurrentTime / 1000;
		long framesCount = currentTimeInMs / particlesPerSecond;
		if (framesCount == 0) {
			return;
		}
		long frameTimeInMs = mCurrentTime / framesCount;
		for (int i = 1; i <= framesCount; i++) {
			onUpdate(frameTimeInMs * i + 1);
		}
	}

	public static float getStartValueNormal(float value ,float valueVariance){
		float result = value-valueVariance;
//		if(result<0){
//			return 0;
//		}
		return result;
	}

	public static float getEndValueAngle(float value ,float valueVariance){
		float result = value+valueVariance;
		if(result>360){
			return 360;
		}
		return result;
	}

	public static float dealOverRange(float value){
		if(value<0){
			return 0f;
		}
		return value;
	}
}