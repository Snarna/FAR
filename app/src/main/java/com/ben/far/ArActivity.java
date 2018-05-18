package com.ben.far;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArActivity extends AppCompatActivity {
    private static final String TAG = ArActivity.class.getSimpleName();

    private ArFragment arFrag;
    private ModelRenderable modelRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        // Get ar fragment
        arFrag = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        // Build renderable
        ModelRenderable.builder()
                .setSource(this, R.raw.class2010_ka)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        }
                );

        arFrag.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if(modelRenderable == null) {
                        return;
                    }

                    if(plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                        return;
                    }

                    // Create the anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFrag.getArSceneView().getScene());

                    // Create transformable class2010
                    TransformableNode trans = new TransformableNode(arFrag.getTransformationSystem());
                    trans.setParent(anchorNode);
                    trans.setRenderable(modelRenderable);
                    trans.select();
                }
        );
    }
}
