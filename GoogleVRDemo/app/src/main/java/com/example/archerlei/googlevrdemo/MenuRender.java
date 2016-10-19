package com.example.archerlei.googlevrdemo;

import android.app.Activity;
import android.content.Context;
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
    private Plane paintPlane;
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
//            getCurrentScene().setSkybox(R.drawable.posx_1, R.drawable.negx_1, R.drawable.posy_1, R.drawable.negy_1, R.drawable.posz_1, R.drawable.negz_1);


            Material simpleMaterial = new Material();
            simpleMaterial.addTexture(new Texture("simple", R.drawable.treasure_hunt));

            treasurePlane = new Plane(8, 4, 1, 1);
            treasurePlane.setZ(-9f);
            treasurePlane.setOrientation(treasurePlane.getOrientation().fromAngleAxis(0, 1, 0, 180));
//            largeCanvasPlane.setOrientation(new Quaternion(0, 0, 0, 1));
            treasurePlane.setMaterial(simpleMaterial);
            treasurePlane.setColor(Color.TRANSPARENT);
            treasurePlane.setBackSided(true);

            getCurrentScene().addChild(treasurePlane);

            simpleMaterial = new Material();
            simpleMaterial.addTexture(new Texture("simple", R.drawable.controllerpaint));

            paintPlane = new Plane(8, 4, 1, 1);
            paintPlane.setPosition(-8f, 0f, -4.5f);
            paintPlane.setOrientation(paintPlane.getOrientation().fromAngleAxis(0, 1, 0, 120));
//            largeCanvasPlane.setOrientation(new Quaternion(0, 0, 0, 1));
            paintPlane.setMaterial(simpleMaterial);
            paintPlane.setColor(Color.TRANSPARENT);
            paintPlane.setBackSided(true);

            getCurrentScene().addChild(paintPlane);

            simpleMaterial = new Material();
            simpleMaterial.addTexture(new Texture("simple", R.drawable.plane));

            flyPlane = new Plane(8, 4, 1, 1);
            flyPlane.setPosition(8f, 0, -4.5f);
            flyPlane.setOrientation(flyPlane.getOrientation().fromAngleAxis(0, 1, 0, 240));
//            largeCanvasPlane.setOrientation(new Quaternion(0, 0, 0, 1));
            flyPlane.setMaterial(simpleMaterial);
            flyPlane.setColor(Color.TRANSPARENT);

            flyPlane.setBackSided(true);

            getCurrentScene().addChild(flyPlane);

        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);

        if (isLookingAtObject(treasurePlane)) {
            treasurePlane.setScale(1.5);
            Intent intent = new Intent(mContext, SongActivity.class);
            mContext.startActivity(intent);
            ((Activity)mContext).finish();
        } else {
            treasurePlane.setScale(1);
        }
        if (isLookingAtObject(paintPlane)) {
            paintPlane.setScale(1.5);
        } else {
            paintPlane.setScale(1);
        }
        if (isLookingAtObject(flyPlane)) {
            flyPlane.setScale(1.5);
//            Intent intent = new Intent(mContext, PlaneActivity.class);
//            mContext.startActivity(intent);
        } else {
            flyPlane.setScale(1);
        }
    }
}
