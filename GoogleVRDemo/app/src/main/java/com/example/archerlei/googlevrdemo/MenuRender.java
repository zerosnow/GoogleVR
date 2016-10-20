package com.example.archerlei.googlevrdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import com.example.archerlei.googlevrdemo.VR.VRRenderer;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.primitives.Plane;

/**
 * Created by archerlei on 2016/10/18.
 */

public class MenuRender extends VRRenderer {
    private final static String TAG = "MenuRender";

    private Activity activity;
    private Plane treasurePlane;
    private Plane songPlane;
    private Plane flyPlane;

    public MenuRender(Activity activity) {
        super(activity);
        this.activity = activity;
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

        getCurrentScene().setBackgroundColor(0x333333);

        try {
            Material simpleMaterial = new Material();
            simpleMaterial.addTexture(new Texture("simple", R.drawable.treasure_hunt));

            treasurePlane = new Plane(8, 4, 1, 1);
            treasurePlane.setZ(-9f);
            treasurePlane.setOrientation(treasurePlane.getOrientation().fromAngleAxis(0, 1, 0, 180));
            treasurePlane.setMaterial(simpleMaterial);
            treasurePlane.setColor(Color.TRANSPARENT);
            treasurePlane.setBackSided(true);

            getCurrentScene().addChild(treasurePlane);

            simpleMaterial = new Material();
            simpleMaterial.addTexture(new Texture("simple", R.drawable.song_play_test));

            songPlane = new Plane(8, 4, 1, 1);
            songPlane.setPosition(-8f, 0f, -4.5f);
            songPlane.setOrientation(songPlane.getOrientation().fromAngleAxis(0, 1, 0, 120));
            songPlane.setMaterial(simpleMaterial);
            songPlane.setColor(Color.TRANSPARENT);
            songPlane.setBackSided(true);

            getCurrentScene().addChild(songPlane);

            simpleMaterial = new Material();
            simpleMaterial.addTexture(new Texture("simple", R.drawable.plane));

            flyPlane = new Plane(8, 4, 1, 1);
            flyPlane.setPosition(8f, 0, -4.5f);
            flyPlane.setOrientation(flyPlane.getOrientation().fromAngleAxis(0, 1, 0, 240));
            flyPlane.setMaterial(simpleMaterial);
            flyPlane.setColor(Color.TRANSPARENT);

            flyPlane.setBackSided(true);

            getCurrentScene().addChild(flyPlane);

        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    private double songLookTime = 0;
    private double planeLookTime = 0;
    private double treasureLookTime = 0;

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);

        if (isLookingAtObject(treasurePlane)) {
            treasurePlane.setScale(1.5);
            treasureLookTime += deltaTime;
            songLookTime = 0;
            planeLookTime = 0;
            if (treasureLookTime >= 2) {
                treasureLookTime = 0;
                Intent intent = new Intent(mContext, TreasureActivity.class);
                mContext.startActivity(intent);
            }
        } else {
            treasurePlane.setScale(1);
            treasureLookTime = 0;
        }
        if (isLookingAtObject(songPlane)) {
            songPlane.setScale(1.5);
            songLookTime += deltaTime;
            treasureLookTime = 0;
            planeLookTime = 0;
            if (songLookTime >= 2) {
                songLookTime = 0;
                Intent intent = new Intent(mContext, SongActivity.class);
                mContext.startActivity(intent);
            }
        } else {
            songPlane.setScale(1);
            songLookTime = 0;
        }
        if (isLookingAtObject(flyPlane)) {
            flyPlane.setScale(1.5);
            planeLookTime += deltaTime;
            treasureLookTime = 0;
            songLookTime = 0;
            if (planeLookTime >= 2) {
                planeLookTime = 0;
                Intent intent = new Intent(mContext, PlaneActivity.class);
                mContext.startActivity(intent);
            }
        } else {
            flyPlane.setScale(1);
            planeLookTime = 0;
        }
    }
}
