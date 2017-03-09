# DpParticleSystem
##Introduce
**DpParticelSystem** is a Android particle System from **[Leonids](https://github.com/plattysoft/Leonids)** . It support use export file from **[Particle Designer](https://71squared.com/particledesigner)**, which is a good Particle simulation software for Mac, but it only support IOS device. This library will help you run the same particle on Android device.

##Base usage
###Get config file
First you can export file from Particle Designer using the **pex** format,and the rename it to a **xml** format file. Then you can use the xml file as your particle system config file. The file looks like this:

```
<particleEmitterConfig>
    <absolutePosition value="0"></absolutePosition>
    <yCoordFlipped value="1"></yCoordFlipped>
    <sourcePosition x="150.00" y="50.00"></sourcePosition>
    <sourcePositionVariance x="-64.00" y="6.00"></sourcePositionVariance>
    <speed value="11.72"></speed>
    <speedVariance value="445.88"></speedVariance>
    <particleLifeSpan value="0.0000"></particleLifeSpan>
    <particleLifespanVariance value="2.1711"></particleLifespanVariance>
    <angle value="167.51"></angle>
    <angleVariance value="174.17"></angleVariance>
    <gravity x="-502.98" y="-493.73"></gravity>
    <radialAcceleration value="-460.22"></radialAcceleration>
    <tangentialAcceleration value="573.50"></tangentialAcceleration>
    <radialAccelVariance value="744.45"></radialAccelVariance>
    <tangentialAccelVariance value="0.00"></tangentialAccelVariance>
    <startColor red="1.00" green="0.15" blue="0.00" alpha="1.00"></startColor>
    <startColorVariance red="1.00" green="0.00" blue="0.00" alpha="0.00"></startColorVariance>
    <finishColor red="0.93" green="1.00" blue="0.93" alpha="0.19"></finishColor>
    <finishColorVariance red="0.00" green="0.00" blue="0.00" alpha="0.24"></finishColorVariance>
    <maxParticles value="2000"></maxParticles>
    <startParticleSize value="0.00"></startParticleSize>
    <startParticleSizeVariance value="49.00"></startParticleSizeVariance>
    <finishParticleSize value="0.00"></finishParticleSize>
    <finishParticleSizeVariance value="0.00"></finishParticleSizeVariance>
    <duration value="-1.00"></duration>
    <emitterType value="0"></emitterType>
    <maxRadius value="10.00"></maxRadius>
    <maxRadiusVariance value="20.00"></maxRadiusVariance>
    <minRadius value="0.00"></minRadius>
    <minRadiusVariance value="0.00"></minRadiusVariance>
    <rotatePerSecond value="0.00"></rotatePerSecond>
    <rotatePerSecondVariance value="0.00"></rotatePerSecondVariance>
    <blendFuncSource value="770"></blendFuncSource>
    <blendFuncDestination value="1"></blendFuncDestination>
    <rotationStart value="504.03"></rotationStart>
    <rotationStartVariance value="0.00"></rotationStartVariance>
    <rotationEnd value="0.00"></rotationEnd>
    <rotationEndVariance value="0.00"></rotationEndVariance>
</particleEmitterConfig>
```
You can delete the **texture** label, because android dont support the image format, so the image data is useless and I use the local image to run the particle system. 
###Run it in Android
Base usage in android is same as [Leonids](https://github.com/plattysoft/Leonids). For example:

```
DPParticleSystem pSystem = new DPParticleSystem(activity, imageResId, parentView, configXmlAsstesPath);
pSystem.setEmitSourcePos(sourceX, sourceY);
pSystem.startEmitInfinite();
```
**configXmlAsstesPath** is the xml config file export from Particle Designer.

##Attention
I just create DpParticelSystem class which extents Leonids and modify the origin Libbary a little, so you can use other functions as same as Leonids,but there are some differences from the origin Library, I will percfect the system afterwards.  

