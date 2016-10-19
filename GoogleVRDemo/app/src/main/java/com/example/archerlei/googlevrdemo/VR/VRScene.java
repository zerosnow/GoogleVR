package com.example.archerlei.googlevrdemo.VR;

import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.scene.RajawaliScene;

/**
 * Created by archerlei on 2016/10/19.
 */

public abstract class VRScene extends RajawaliScene {
    protected VRRenderer vrRenderer;

    public VRScene(VRRenderer renderer) {
        super(renderer);
        this.vrRenderer = renderer;
    }

    public abstract void eventHandle();
}
