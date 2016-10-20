package com.example.archerlei.googlevrdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import com.example.archerlei.googlevrdemo.Util.LyricInfo;
import com.example.archerlei.googlevrdemo.Util.LyricUtil;
import com.example.archerlei.googlevrdemo.VR.VRRenderer;
import com.google.vr.sdk.audio.GvrAudioEngine;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.SplineTranslateAnimation3D;
import org.rajawali3d.curves.CatmullRomCurve3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.Loader3DSMax;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;

import java.util.Comparator;
import java.util.List;

/**
 * Created by archerlei on 2016/10/19.
 */

public class SongRender extends VRRenderer{
    private static final String TAG = "SongRender";

    private GvrAudioEngine gvrAudioEngine;
    private Object3D mHifiObject;
    private Bitmap mLyricBitmap;
    private Canvas mLyricCanvas;
    private Paint mLyricPaint;
    private Material mLyricMaterial;
    private Plane mLyricScreen;
    private Texture mLyricTexture;
    private List<LyricInfo> mLyricList;
    private double songTime = 0;
    private boolean songStart = false;
    private volatile int songId = GvrAudioEngine.INVALID_ID;
    private volatile int id = GvrAudioEngine.INVALID_ID;

    public SongRender(Context context) {
        super(context);
    }

    public void initScene() {
        DirectionalLight light = new DirectionalLight(0.2f, -1f, 0f);
        light.setPower(.7f);
        getCurrentScene().addLight(light);

        light = new DirectionalLight(0.2f, 1f, 0f);
        light.setPower(1f);
        getCurrentScene().addLight(light);

        getCurrentCamera().setFarPlane(1000);

        getCurrentScene().setBackgroundColor(0x444444);



        try {

            mLyricBitmap = Bitmap.createBitmap(512, 128, Bitmap.Config.ARGB_8888);
            // Paint for coloring
            mLyricPaint = new Paint();
            mLyricPaint.setColor(Color.WHITE);
            mLyricPaint.setStrokeWidth(2f);
            mLyricPaint.setTextSize(30);
            mLyricPaint.setStyle(Paint.Style.FILL);

            // Create a canvas to do the drawing
            mLyricCanvas = new Canvas(mLyricBitmap);
            mLyricCanvas.drawColor(Color.BLACK);
            mLyricPaint.setColor(Color.RED);
            mLyricCanvas.drawText(getContext().getResources().getString(R.string.loading), 20, mLyricCanvas.getHeight()  / 2, mLyricPaint);

            mLyricMaterial  = new Material();
            mLyricMaterial.setColor(0x444444);
            mLyricTexture = new Texture("simple", mLyricBitmap);
            mLyricMaterial.addTexture(mLyricTexture);

            mLyricScreen = new Plane(20, 5, 1, 1);
            mLyricScreen.setZ(-9f);
            mLyricScreen.setOrientation(mLyricScreen.getOrientation().fromAngleAxis(0, 1, 0, 180));
            mLyricScreen.setMaterial(mLyricMaterial);
            mLyricScreen.setBackSided(true);

            getCurrentScene().addChild(mLyricScreen);

            mHifiObject = new Sphere(1, 12, 12);
            Material sphereMaterial = new Material();
            sphereMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            sphereMaterial.enableLighting(true);
            sphereMaterial.setColor(Color.TRANSPARENT);
            sphereMaterial.addTexture(new Texture("earth", R.drawable.earthtruecolor_nasa_big));
            mHifiObject.setMaterial(sphereMaterial);
            mHifiObject.setZ(-6);
            getCurrentScene().addChild(mHifiObject);

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
            anim.setTransformable3D(mHifiObject);
            getCurrentScene().registerAnimation(anim);
            anim.play();

        } catch (Exception e) {
            e.printStackTrace();
        }

        initAudio();
        initLyric();

        super.initScene();

    }


    private void initAudio() {
        gvrAudioEngine =
                new GvrAudioEngine(getContext(), GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);

        new Thread(
                new Runnable() {
                    public void run() {
                        gvrAudioEngine.preloadSoundFile("sonar.wav");
                        songId = gvrAudioEngine.createSoundObject("sonar.wav");
                        gvrAudioEngine.setSoundObjectPosition(
                                songId, (float)mHifiObject.getX(), (float)mHifiObject.getY(), (float)mHifiObject.getZ()
                        );
                        gvrAudioEngine.setSoundVolume(songId, 0.3f);
                        gvrAudioEngine.playSound(songId, true);


                        gvrAudioEngine.preloadSoundFile("song.wav");

                        id = gvrAudioEngine.createStereoSound("song.wav");
                        Log.d("maizang", "load finish");
                        songStart = true;
                        gvrAudioEngine.playSound(id, false);
                    }
                })
                .start();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void initLyric() {
        mLyricList = LyricUtil.parse(getContext(), R.raw.song_lyric);
        if (mLyricList == null) {
            return;
        }
        mLyricList.sort(new Comparator<LyricInfo>() {
            @Override
            public int compare(LyricInfo o1, LyricInfo o2) {
                return (int)(o1.lyricTime - o2.lyricTime);
            }
        });

        for (LyricInfo lyricInfo : mLyricList) {
            Log.d(TAG, lyricInfo.lyricTime + " " + lyricInfo.lyricText);
        }
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

    private  int index = 0;

    @Override
    public void onRender(long elapsedTime, double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

        if (songStart && mLyricList != null) {
            songTime += deltaTime;
            if (songTime > mLyricList.get(index).lyricTime / 1000.0 && index < mLyricList.size() - 1) {

                mLyricCanvas.drawColor(Color.BLACK);
                mLyricPaint.setColor(Color.RED);
                mLyricCanvas.drawText(mLyricList.get(index).lyricText, 20, mLyricCanvas.getHeight()  / 2, mLyricPaint);
                mLyricPaint.setColor(Color.WHITE);
                mLyricCanvas.drawText(mLyricList.get(index + 1).lyricText, 100, mLyricCanvas.getHeight()  / 2 + mLyricPaint.getTextSize(), mLyricPaint);
                try {
                    mLyricMaterial.removeTexture(mLyricTexture);
                    mLyricTexture = new Texture("simple", mLyricBitmap);
                    mLyricMaterial.addTexture(mLyricTexture);
                } catch (ATexture.TextureException e) {
                    e.printStackTrace();
                }
                index++;
            }
        }

        if(songId != GvrAudioEngine.INVALID_ID) {
            gvrAudioEngine.setSoundObjectPosition(
                    songId, (float) mHifiObject.getX(), (float) mHifiObject.getY(), (float) mHifiObject.getZ());
        }

        gvrAudioEngine.setHeadRotation(
                (float)mHeadViewQuaternion.x, (float)mHeadViewQuaternion.y, (float)mHeadViewQuaternion.z, (float)mHeadViewQuaternion.w);
    }
}
