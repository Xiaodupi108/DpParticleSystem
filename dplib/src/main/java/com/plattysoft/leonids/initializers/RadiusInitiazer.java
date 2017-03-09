package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;

import java.util.Random;

/**
 * Created by yangzhenyu on 15/11/19.
 */
public class RadiusInitiazer implements ParticleInitializer {
    private float maxRadius;
    private float maxRadiusVariance;
    private float minRadius;
    private float minRadiusVariance;
    private float rotatePerSecond;
    private float rotatePerSecondVariance;
    private float angle;
    private float angleVariance;

    /**
     * @param maxRadius 最大半径
     * @param rotatePerSecond 影响粒子移动的方向和速度
     * @param  rotatePerSecondVariance 以上浮动值
     */
    public RadiusInitiazer(float maxRadius,float minRadius,float maxRadiusVariance,float minRadiusVariance, float rotatePerSecond,float rotatePerSecondVariance,float angle,float angleVariance){
        this.maxRadius=maxRadius;
        this.maxRadiusVariance=maxRadiusVariance;
        this.minRadius=minRadius;
        this.minRadiusVariance = minRadiusVariance;
        this.rotatePerSecond=rotatePerSecond;
        this.rotatePerSecondVariance=rotatePerSecondVariance;
        this.angle=angle;
        this.angleVariance=angleVariance;
    }

    @Override
    public void initParticle(Particle p, Random r) {
        p.mInitRadius=maxRadius+(r.nextFloat()-0.5f)*2f*maxRadiusVariance;
        p.maxRadius=maxRadius;
        //如果是二代ParticleDesigner,使用minRadiusVariance
        if(minRadiusVariance != -1){
            p.minRadius = minRadius+(r.nextFloat()-0.5f)*2*minRadiusVariance;
        }
        else{
            p.minRadius=minRadius;
        }
        p.radius=p.mInitRadius;
        p.mInitialAngle=(angle+(r.nextFloat()-0.5f)*2f*angleVariance);
        p.degreesPerSecond=rotatePerSecond+(r.nextFloat()-0.5f)*2f*rotatePerSecondVariance;
        p.isVortex=true;
    }
}
