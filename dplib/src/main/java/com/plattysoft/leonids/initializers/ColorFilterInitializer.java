package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;

import java.util.Random;

/**
 * Created by jiaziang8 on 15-5-5.
 */
public class ColorFilterInitializer implements ParticleInitializer{
    private float redStart;
    private float greenStart;
    private float blueStart;
    private float redStartVarience;
    private float greenStartVarience;
    private float blueStartVarience;

    private float redEnd;
    private float greenEnd;
    private float blueEnd;
    private float redEndVarience;
    private float greenEndVarience;
    private float blueEndVarience;

    private float startAlpha;
    private float startVarienceAlpha;
    private float endAlpha;
    private float endVarienceAlpha;

    public ColorFilterInitializer(float redStart,float greenStart,float blueStart,float redStartVarience,float greenStartVarience,
    float blueStartVarience,float redEnd,float greenEnd,float blueEnd,float redEndVarience,float greenEndVarience,float blueEndVarience,
                                  float startAlpha ,float startVarienceAlpha ,float endAlpha ,float endVarienceAlpha){
        this.redStart = redStart;
        this.greenStart = greenStart;
        this.blueStart = blueStart;
        this.redStartVarience = redStartVarience;
        this.greenStartVarience = greenStartVarience;
        this.blueStartVarience = blueStartVarience;

        this.redEnd =redEnd;
        this.greenEnd = greenEnd;
        this.blueEnd = blueEnd;
        this.redEndVarience = redEndVarience;
        this.greenEndVarience = greenEndVarience;
        this.blueEndVarience = blueEndVarience;

        this.startAlpha = startAlpha;
        this.startVarienceAlpha = startVarienceAlpha;
        this.endAlpha = endAlpha;
        this.endVarienceAlpha = endVarienceAlpha;
    }

    @Override
    public void initParticle(Particle p, Random r) {
        p.startRed = (int)(dealOverRange((r.nextFloat()-0.5f)*2f*redStartVarience+redStart)*Particle.RGB_MAX);
        p.startGreen = (int)(dealOverRange((r.nextFloat()-0.5f)*2f*greenStartVarience+greenStart)*Particle.RGB_MAX);
        p.startBlue = (int)(dealOverRange((r.nextFloat()-0.5f)*2f*blueStartVarience+blueStart)*Particle.RGB_MAX);

        p.endRed = (int)(dealOverRange((r.nextFloat()-0.5f)*2f*redEndVarience+redEnd)*Particle.RGB_MAX);
        p.endGreen = (int)(dealOverRange((r.nextFloat()-0.5f)*2f*greenEndVarience+greenEnd)*Particle.RGB_MAX);
        p.endBlue = (int)(dealOverRange((r.nextFloat()-0.5f)*2f*blueEndVarience+blueEnd)*Particle.RGB_MAX);

        p.startAlpha = (int)(dealOverRange((r.nextFloat()-0.5f)*2f*startVarienceAlpha+startAlpha)*Particle.ALPHA_MAX);
        p.endAlpha = (int)(dealOverRange((r.nextFloat()-0.5f)*2f*endVarienceAlpha+endAlpha)*Particle.ALPHA_MAX);

    }

    private float dealOverRange(float value){
        if(value<0){
            return 0f;
        }
        if(value>1){
            return 1f;
        }
        return value;
    }

}
