package com.example.archerlei.googlevrdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.archerlei.googlevrdemo.VR.VRRenderer;
import com.google.vr.sdk.audio.GvrAudioEngine;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.SplineTranslateAnimation3D;
import org.rajawali3d.curves.CatmullRomCurve3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.LoaderAWD;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.terrain.SquareTerrain;
import org.rajawali3d.terrain.TerrainGenerator;

/**
 * Created by archerlei on 2016/10/17.
 */

public class MyRender extends VRRenderer {
    private SquareTerrain terrain;
    private Sphere lookatSphere;
    private Object3D capital;
    private GvrAudioEngine gvrAudioEngine;
    private volatile int spaceShipSoundId = GvrAudioEngine.INVALID_ID;
    private volatile int sonarSoundId = GvrAudioEngine.INVALID_ID;

    public MyRender(Context context) {
        super(context);
    }

    @Override
    public void initScene() {
        DirectionalLight light = new DirectionalLight(0.2f, -1f, 0f);
        light.setPower(.7f);
        getCurrentScene().addLight(light);

        light = new DirectionalLight(0.2f, 1f, 0f);
        light.setPower(1f);
        getCurrentScene().addLight(light);

        getCurrentCamera().setFarPlane(1000);

        getCurrentScene().setBackgroundColor(0xdddddd);

        createTerrain();

        try {

            getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx, R.drawable.posy, R.drawable.negy, R.drawable.posz, R.drawable.negz);

            LoaderAWD loader = new LoaderAWD(getContext().getResources(), getTextureManager(), R.raw.space_cruiser);
            loader.parse();

            Material cruiserMaterial = new Material();
            cruiserMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            cruiserMaterial.setColorInfluence(0);
            cruiserMaterial.enableLighting(true);
            cruiserMaterial.addTexture(new Texture("spaceCruiserTex", R.drawable.space_cruiser_4_color_1));

            Object3D spaceCruiser = loader.getParsedObject();
            spaceCruiser.setMaterial(cruiserMaterial);
            spaceCruiser.setZ(-6);
            spaceCruiser.setY(1);
            getCurrentScene().addChild(spaceCruiser);

            spaceCruiser = spaceCruiser.clone(true);
            spaceCruiser.setZ(-12);
            spaceCruiser.setY(-3);
            spaceCruiser.setRotY(180);
            getCurrentScene().addChild(spaceCruiser);

            loader = new LoaderAWD(getContext().getResources(), getTextureManager(), R.raw.dark_fighter);
            loader.parse();

            Material darkFighterMaterial = new Material();
            darkFighterMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            darkFighterMaterial.setColorInfluence(0);
            darkFighterMaterial.enableLighting(true);
            darkFighterMaterial.addTexture(new Texture("darkFighterTex", R.drawable.dark_fighter_6_color));

            Object3D darkFighter = loader.getParsedObject();
            darkFighter.setMaterial(darkFighterMaterial);
            getCurrentScene().addChild(darkFighter);

            CatmullRomCurve3D path = new CatmullRomCurve3D();
            path.addPoint(new Vector3(0, -5, -10));
            path.addPoint(new Vector3(10, -5, 0));
            path.addPoint(new Vector3(0, -4, 8));
            path.addPoint(new Vector3(-16, -6, 0));
            path.isClosedCurve(true);

            SplineTranslateAnimation3D anim = new SplineTranslateAnimation3D(path);
            anim.setDurationMilliseconds(44000);
            anim.setRepeatMode(Animation.RepeatMode.INFINITE);
            // -- orient to path
            anim.setOrientToPath(true);
            anim.setTransformable3D(darkFighter);
            getCurrentScene().registerAnimation(anim);
            anim.play();

            loader = new LoaderAWD(getContext().getResources(), getTextureManager(), R.raw.capital);
            loader.parse();

            Material capitalMaterial = new Material();
            capitalMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            capitalMaterial.setColorInfluence(0);
            capitalMaterial.enableLighting(true);
            capitalMaterial.addTexture(new Texture("capitalTex", R.drawable.hullw));
            capitalMaterial.addTexture(new NormalMapTexture("capitalNormTex", R.drawable.hulln));

            capital = loader.getParsedObject();
            capital.setMaterial(capitalMaterial);
            capital.setScale(18);
            getCurrentScene().addChild(capital);

            path = new CatmullRomCurve3D();
            path.addPoint(new Vector3(0, 13, 34));
            path.addPoint(new Vector3(34, 13, 0));
            path.addPoint(new Vector3(0, 13, -34));
            path.addPoint(new Vector3(-34, 13, 0));
            path.isClosedCurve(true);

            anim = new SplineTranslateAnimation3D(path);
            anim.setDurationMilliseconds(60000);
            anim.setRepeatMode(Animation.RepeatMode.INFINITE);
            anim.setOrientToPath(true);
            anim.setTransformable3D(capital);
            getCurrentScene().registerAnimation(anim);
            anim.play();

            lookatSphere = new Sphere(1, 12, 12);
            Material sphereMaterial = new Material();
            sphereMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            sphereMaterial.enableLighting(true);
            sphereMaterial.addTexture(new Texture("earth", R.drawable.earthtruecolor_nasa_big));
            lookatSphere.setMaterial(sphereMaterial);
            lookatSphere.setColor(Color.YELLOW);
            lookatSphere.setPosition(0, 0, -6);
            getCurrentScene().addChild(lookatSphere);

        } catch (Exception e) {
            e.printStackTrace();
        }





        initAudio();

        super.initScene();

    }

    private void initAudio() {
        gvrAudioEngine =
                new GvrAudioEngine(getContext(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);

        new Thread(
                new Runnable() {
                    public void run() {
                        gvrAudioEngine.preloadSoundFile("spaceship.wav");
                        spaceShipSoundId = gvrAudioEngine.createSoundObject("spaceship.wav");
                        gvrAudioEngine.setSoundObjectPosition(
                                spaceShipSoundId, (float)capital.getX(), (float)capital.getY(), (float)capital.getZ()
                        );
                        gvrAudioEngine.playSound(spaceShipSoundId, true);

                        gvrAudioEngine.preloadSoundFile("sonar.wav");
                        sonarSoundId = gvrAudioEngine.createSoundObject("sonar.wav");
                        gvrAudioEngine.setSoundObjectPosition(
                                sonarSoundId, (float) lookatSphere.getX(), (float) lookatSphere.getY(), (float) lookatSphere.getZ()
                        );
                        gvrAudioEngine.playSound(sonarSoundId, true);
                    }
                })
                .start();
    }

    public void pauseAudio() {
        if(gvrAudioEngine != null) {
            gvrAudioEngine.pause();
        }
    }

    public void resumeAudio() {
        if(gvrAudioEngine != null) {
            gvrAudioEngine.resume();
        }
    }


    public void createTerrain() {
        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.terrain);

        SquareTerrain.Parameters terrainParams = SquareTerrain.createParameters(bmp);
        terrainParams.setScale(4f, 54f, 4f);
        terrainParams.setDivisions(128);
        terrainParams.setTextureMult(4);
        terrainParams.setColorMapBitmap(bmp);

        terrain = TerrainGenerator.createSquareTerrainFromBitmap(terrainParams, true);

        bmp.recycle();

        Material material = new Material();
        material.enableLighting(true);
        material.useVertexColors(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());

        Texture groundTexture = new Texture("ground", R.drawable.ground);
        groundTexture.setInfluence(.5f);

        try {
            material.addTexture(groundTexture);
            material.addTexture(new NormalMapTexture("groundNormalMap", R.drawable.groundnor));
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

        material.setColorInfluence(0.5f);
        terrain.setY(-100);
        terrain.setMaterial(material);

        getCurrentScene().addChild(terrain);
    }


    @Override
    public void onRender(long elapsedTime, double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        boolean isLookingAt = isLookingAtObject(lookatSphere);
        if(isLookingAt) {
            lookatSphere.setColor(Color.RED);
        } else {
            lookatSphere.setColor(Color.YELLOW);
        }

        if(spaceShipSoundId != GvrAudioEngine.INVALID_ID) {
            gvrAudioEngine.setSoundObjectPosition(
                    spaceShipSoundId, (float) capital.getX(), (float) capital.getY(), (float) capital.getZ());
        }
        if(sonarSoundId != GvrAudioEngine.INVALID_ID) {
            gvrAudioEngine.setSoundObjectPosition(
                    sonarSoundId, (float) lookatSphere.getX(), (float) lookatSphere.getY(), (float) lookatSphere.getZ()
            );
        }

        gvrAudioEngine.setHeadRotation(
                (float)mHeadViewQuaternion.x, (float)mHeadViewQuaternion.y, (float)mHeadViewQuaternion.z, (float)mHeadViewQuaternion.w);
    }
}
