package com.example.archerlei.googlevrdemo;

import android.os.Bundle;

import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;

public class PlaneActivity extends GvrActivity {
    private MyRender renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plane);

        GvrView gvrView = (GvrView)findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        renderer = new MyRender(this);
        gvrView.setRenderer(renderer);

        if (gvrView.setAsyncReprojectionEnabled(true)) {
            AndroidCompat.setSustainedPerformanceMode(this, true);
        }

        setGvrView(gvrView);
    }

    @Override
    public void onCardboardTrigger() {
    }

    @Override
    public void onPause() {
        super.onPause();
        renderer.pauseAudio();
    }

    @Override
    public void onResume() {
        super.onResume();
        renderer.resumeAudio();
    }
}
