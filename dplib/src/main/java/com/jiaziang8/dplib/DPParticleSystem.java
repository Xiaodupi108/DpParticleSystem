package com.jiaziang8.dplib;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Xml;
import android.view.ViewGroup;

import com.plattysoft.leonids.AnimatedParticle;
import com.plattysoft.leonids.Particle;
import com.plattysoft.leonids.ParticleSystem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoxy on 6/29/15.
 */
public class DPParticleSystem extends ParticleSystem {
    private static Map<String, String> funcMap = new HashMap<String, String>(){
        {
            put("emitterType","setEmitterType");
            put("texture","setTexture");
            put("speed", "setSpeed");
            put("speedVariance", "setSpeedVariance");
            put("particleLifeSpan", "setParticleLifeSpan");
            put("particleLifespanVariance","setParticleLifespanVariance");
            put("angle", "setAngle");
            put("angleVariance", "setAngleVariance");
            put("gravity", "setGravity");
            put("radialAcceleration","setRadialAcceleration");
            put("tangentialAcceleration","setTangentialAcceleration");
            put("radialAccelVariance","setRadialAccelerationVariance");
            put("tangentialAccelVariance", "setTangentialAccelerationVariance");
            put("startColor", "setStartColor");
            put("startColorVariance", "setStartColorVariance");
            put("finishColor", "setFinishColor");
            put("finishColorVariance", "setFinishColorVariance");
            put("maxParticles", "setMaxParticles");
            put("startParticleSize", "setStartSize");
            put("startParticleSizeVariance", "setStartSizeVariance");
            put("finishParticleSize", "setFinishSize");
            put("FinishParticleSizeVariance", "setFinishSizeVariance");
            put("duration", "setDuration");
            put("rotationStart", "setRotationStart");
            put("rotationStartVariance", "setRotationStartVariance");
            put("rotationEnd", "setRotationEnd");
            put("rotationEndVariance", "setRotationEndVariance");
            put("sourcePositionVariance", "setSourcePosVariance");
            put("maxRadius","setMaxRadius");
            put("maxRadiusVariance","setMaxRadiusVariance");
            put("minRadius","setMinRadius");
            put("rotatePerSecond","setRotatePerSecond");
            put("rotatePerSecondVariance","setRotatePerSecondVariance");
        }
    };
    //粒子效果类型,0为Gravity模式,1为Radial模式
    private int emitterType;
    private String fileName;
    private String textureContent;

    private float speed;
    private float speedVariance;
    private float lifeSpan;
    private float lifeSpanVariance;
    private float angle;
    private float angleVariance;
    private float gravityX;
    private float gravityY;
    private float radialAcceleration;
    private float tangentialAcceleration;
    private float radialAccelerationVariance;
    private float tangentialAccelerationVariance;

    private float startRed;
    private float startGreen;
    private float startBlue;
    private float startAlpha;
    private float startRedVariance;
    private float startGreenVariance;
    private float startBlueVariance;
    private float startAlphaVariance;
    private float sourcePosVarianceX;
    private float sourcePosVarianceY;

    private float endRed;
    private float endGreen;
    private float endBlue;
    private float endAlpha;
    private float endRedVariance;
    private float endGreenVariance;
    private float endBlueVariance;
    private float endAlphaVariance;

    private int maxParticles;
    private float startSize;
    private float startVarianceSize;
    private float endSize;
    private float endVarianceSize;

    private float duration;

    private float rotationStart;
    private float rotationStartVariance;
    private float rotationEnd;
    private float rotationEndVariance;

    private float maxRadius;
    private float maxRadiusVariance;
    private float minRadius;
    private float minRadiusVariance;
    private float rotatePerSecond;
    private float rotatePerSecondVariance;
    private static final int GRAVITY_MODE = 0;
    public static final int RADIAL_MODE = 1;

    public interface OnEmitListener{
        void onEmitFinish();
    }
    public OnEmitListener onEmitListener;
    private int drawableResId;
    private Activity activity;

    public DPParticleSystem(Activity activity, int drawableResId, ViewGroup mParentView, String fileName) {
        super(activity, mParentView);
        this.activity=activity;
        try {
            InputStream inputStream = activity.getAssets().open(fileName);
            parserXml(inputStream);

            Drawable drawable = activity.getResources().getDrawable(drawableResId);
            //添加粒子
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                for (int i = 0; i < mMaxParticles; i++) {
                    mParticles.add(new Particle(bitmap));
                }
            } else if (drawable instanceof AnimationDrawable) {
                AnimationDrawable animation = (AnimationDrawable) drawable;
                for (int i = 0; i < mMaxParticles; i++) {
                    mParticles.add(new AnimatedParticle(animation));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DPParticleSystem(Activity activity, String drawablePath, ViewGroup mParentView, File file) {
        super(activity, mParentView);
        this.activity=activity;
        try {
            InputStream inputStream = new FileInputStream(file);
            parserXml(inputStream);

            File imgFile = new File(drawablePath);
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(drawablePath);
                for (int i = 0; i < mMaxParticles; i++) {
                    mParticles.add(new Particle(bm));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parserXml(InputStream inputStream){
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            parser.nextTag();
            readConfig(parser);
            setSpeedModuleAndAngleRange((speed - speedVariance),
                    (speed + speedVariance),
                    (int) ParticleSystem.getStartValueNormal(angle, angleVariance),
                    (int) ParticleSystem.getEndValueAngle(angle, angleVariance));
            setAcceleration(dpToPx(gravityX), dpToPx(gravityY), dpToPx(radialAcceleration), dpToPx(tangentialAcceleration),
                    dpToPx(radialAccelerationVariance), dpToPx(tangentialAccelerationVariance));
            setScaleRange(startSize, startVarianceSize, endSize, endVarianceSize);
            setInitialRotationRange(rotationStart, rotationStartVariance, rotationEnd, rotationEndVariance);
            setColor(startRed, startGreen, startBlue,
                    startRedVariance, startGreenVariance, startBlueVariance,
                    endRed, endGreen, endBlue,
                    endRedVariance, endGreenVariance, endBlueVariance,
                    startAlpha, startAlphaVariance, endAlpha, endAlphaVariance);
            //如果是radial mode
            if (emitterType == RADIAL_MODE) {
                setRadiusInitiazer(maxRadius, minRadius, maxRadiusVariance, minRadiusVariance, rotatePerSecond, rotatePerSecondVariance, angle, angleVariance);
            }
            mMaxParticles = maxParticles;
            mTimeToLive = (long) (lifeSpan * 1000);

            //使用xml文件里的图片资源
            /*if (drawableResId == -1 && this.fileName != null && this.textureContent != null) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decode(textureContent, Base64.DEFAULT));
                GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
                ByteArrayBuffer bt = new ByteArrayBuffer(4096);
                int len;
                byte[] tmp = new byte[4096];
                while ((len = gzipInputStream.read(tmp)) != -1) {
                    bt.append(tmp, 0, len);
                }
                gzipInputStream.close();
                byteArrayInputStream.close();

                Bitmap bitmap = BitmapFactory.decodeByteArray(bt.toByteArray(), 0, bt.toByteArray().length);
                for (int i = 0; i < mMaxParticles; i++) {
                    mParticles.add(new Particle(bitmap));
                }
            }

            //使用提供的resId加载图片
            else {
                //添加粒子
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    for (int i = 0; i < mMaxParticles; i++) {
                        mParticles.add(new Particle(bitmap));
                    }
                } else if (drawable instanceof AnimationDrawable) {
                    AnimationDrawable animation = (AnimationDrawable) drawable;
                    for (int i = 0; i < mMaxParticles; i++) {
                        mParticles.add(new AnimatedParticle(animation));
                    }
                }
            }*/
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DPParticleSystem(Activity activity, int drawableResId, int parentResId, String fileName) {
        this(activity, drawableResId, (ViewGroup)activity.findViewById(parentResId), fileName);
    }

    /**
     * 根据获得肚皮糖的个数返回不同的粒子系统文件
     * @param gold 肚皮糖个数
     * @return
     */
    public static String returnParticleFile(int gold){
        String particleFile;
        if(gold>200){
            particleFile="particle_gold_little.xml";
        }
        else if(gold>100){
            particleFile="particle_gold_middle.xml";
        }else{
            particleFile="particle_gold_more.xml";
        }
        return particleFile;
    }

    /**
     * 粒子位置
     * @param x
     * @param y
     */
    public void setEmitSourcePos(int x, int y) {
        sourcePosX = x;
        sourcePosY = y;
        mEmiterXMin = (int)(x-dpToPx(sourcePosVarianceX));
        mEmiterXMax = (int)(x+dpToPx(sourcePosVarianceX));
        mEmiterYMin = (int)(y-dpToPx(sourcePosVarianceY));
        mEmiterYMax = (int)(y+dpToPx(sourcePosVarianceY));
    }

    /**
     * 启动粒子系统
     */
    public void startEmit() {
        int particlesPerSecond = (int)(maxParticles/lifeSpan);
        startEmiting(particlesPerSecond, (int)(duration*1000));
    }

    public void startEmitInfinite(){
        int particlesPerSecond = (int)(maxParticles/lifeSpan);
        startEmiting(particlesPerSecond);
    }

    /**
     * 粒子播放结束
     */
    @Override
    protected void cleanupAnimation() {
        super.cleanupAnimation();
        if (onEmitListener!=null) {
            onEmitListener.onEmitFinish();
        }
    }

    /**
     * 读取xml
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readConfig(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "particleEmitterConfig");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String tagName = parser.getName();
            String funcName = funcMap.get(tagName);
            if (funcName!=null) {
                try {
                    Method method = getClass().getDeclaredMethod(funcName, XmlPullParser.class);
                    method.invoke(this, parser);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setEmitterType(XmlPullParser parser){
        this.emitterType = Integer.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setTexture(XmlPullParser parser){
        this.fileName = parser.getAttributeValue(null, "name");
        this.textureContent = parser.getAttributeValue(null, "data");
    }

    private void setSourcePosVariance(XmlPullParser parser) {
        this.sourcePosVarianceX = Float.valueOf(parser.getAttributeValue(null,"x"));
        this.sourcePosVarianceY = Float.valueOf(parser.getAttributeValue(null,"y"));
        this.sourcePosVarianceX = Math.abs(this.sourcePosVarianceX);
        this.sourcePosVarianceY = Math.abs(this.sourcePosVarianceY);
    }

    private void setAngle(XmlPullParser parser) {
        this.angle = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setAngleVariance(XmlPullParser parser) {
        this.angleVariance = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setDuration(XmlPullParser parser) {
        this.duration = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setStartColor(XmlPullParser parser) {
        this.startRed = Float.valueOf(parser.getAttributeValue(null,"red"));
        this.startGreen = Float.valueOf(parser.getAttributeValue(null, "green"));
        this.startBlue = Float.valueOf(parser.getAttributeValue(null,"blue"));
        this.startAlpha = Float.valueOf(parser.getAttributeValue(null,"alpha"));
    }

    private void setStartColorVariance(XmlPullParser parser) {
        this.startRedVariance = Float.valueOf(parser.getAttributeValue(null, "red"));
        this.startGreenVariance = Float.valueOf(parser.getAttributeValue(null, "green"));
        this.startBlueVariance = Float.valueOf(parser.getAttributeValue(null,"blue"));
        this.startAlphaVariance = Float.valueOf(parser.getAttributeValue(null,"alpha"));
    }

    private void setFinishColor(XmlPullParser parser) {
        this.endRed = Float.valueOf(parser.getAttributeValue(null,"red"));
        this.endGreen = Float.valueOf(parser.getAttributeValue(null, "green"));
        this.endBlue = Float.valueOf(parser.getAttributeValue(null,"blue"));
        this.endAlpha = Float.valueOf(parser.getAttributeValue(null,"alpha"));
    }

    private void setFinishColorVariance(XmlPullParser parser) {
        this.endRedVariance = Float.valueOf(parser.getAttributeValue(null,"red"));
        this.endGreenVariance = Float.valueOf(parser.getAttributeValue(null, "green"));
        this.endBlueVariance = Float.valueOf(parser.getAttributeValue(null,"blue"));
        this.endAlphaVariance = Float.valueOf(parser.getAttributeValue(null,"alpha"));
    }

    private void setStartSize(XmlPullParser parser) {
        this.startSize = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setStartSizeVariance(XmlPullParser parser) {
        this.startVarianceSize = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setFinishSize(XmlPullParser parser) {
        this.endSize = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setFinishSizeVariance(XmlPullParser parser) {
        this.endVarianceSize = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setGravity(XmlPullParser parser) {
        this.gravityX = Float.valueOf(parser.getAttributeValue(null,"x"));
        this.gravityY = Float.valueOf(parser.getAttributeValue(null,"y"));
    }

    private void setRadialAcceleration(XmlPullParser parser){
        this.radialAcceleration = Float.valueOf(parser.getAttributeValue(null,"value"));
    }

    private void setTangentialAcceleration(XmlPullParser parser){
        this.tangentialAcceleration = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setRadialAccelerationVariance(XmlPullParser parser){
        this.radialAccelerationVariance = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setTangentialAccelerationVariance(XmlPullParser parser){
        this.tangentialAccelerationVariance = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setParticleLifeSpan(XmlPullParser parser) {
        this.lifeSpan = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setParticleLifespanVariance(XmlPullParser parser){
        this.lifeSpanVariance = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setMaxParticles(XmlPullParser parser) {
        this.maxParticles = Integer.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setRotationStart(XmlPullParser parser) {
        this.rotationStart = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setRotationEnd(XmlPullParser parser) {
        this.rotationEnd = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setRotationEndVariance(XmlPullParser parser) {
        this.rotationEndVariance = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setRotationStartVariance(XmlPullParser parser) {
        this.rotationStartVariance = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setSpeed(XmlPullParser parser) {
        this.speed = Float.valueOf(parser.getAttributeValue(null, "value"));
    }

    private void setSpeedVariance(XmlPullParser parser) {
        this.speedVariance = Float.valueOf(parser.getAttributeValue(null, "value"));
    }
    private void setMaxRadius(XmlPullParser parser){
        this.maxRadius = Float.valueOf(parser.getAttributeValue(null,"value"));
    }
    private void setMaxRadiusVariance(XmlPullParser parser){
        this.maxRadiusVariance = Float.valueOf(parser.getAttributeValue(null,"value"));
    }
    private void setMinRadius(XmlPullParser parser){
        this.minRadius = Float.valueOf(parser.getAttributeValue(null,"value"));
    }
    private void setMinRadiusVariance(XmlPullParser parser){
        //如果是一代ParticleDesigner,没有minRadiusVariance这个参数，设为-1
        if(parser.getAttributeValue(null,"value") == null){
            minRadiusVariance = -1;
        }
        else{
            this.minRadiusVariance = Float.valueOf(parser.getAttributeValue(null,"value"));
        }
    }
    private void setRotatePerSecond(XmlPullParser parser){
        this.rotatePerSecond = Float.valueOf(parser.getAttributeValue(null,"value"));
    }
    private void setRotatePerSecondVariance(XmlPullParser parser){
        this.rotatePerSecondVariance = Float.valueOf(parser.getAttributeValue(null,"value"));
    }
}