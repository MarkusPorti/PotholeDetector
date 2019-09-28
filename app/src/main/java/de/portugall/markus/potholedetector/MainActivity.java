package de.portugall.markus.potholedetector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_CODE = 10;

    private TextureView cameraView;
    private DetectorView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.texture_view);
        drawView = findViewById(R.id.detector_view);
        drawView.setZOrderOnTop(true);
        cameraView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateTransform();
            }
        });
        if (!hasPermissions(getApplicationContext())) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
        } else {
            cameraView.post(new Runnable() {
                @Override
                public void run() {
                    startCamera();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (hasPermissions(getBaseContext())) {
                cameraView.post(new Runnable() {
                    @Override
                    public void run() {
                        startCamera();
                    }
                });
            } else {
                Toast.makeText(this, "No Permissions get", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startCamera() {
        PreviewConfig config = new PreviewConfig.Builder()
                .setTargetAspectRatio(new Rational(1, 1))
                .setTargetResolution(new Size(600, 800))
                .build();

        Preview preview = new Preview(config);
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup) cameraView.getParent();
                parent.removeView(cameraView);
                parent.addView(cameraView);

                cameraView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });

        cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // Hier bekommst du ständig updates.
                // TODO Hier haste deine Bitmap :D
                Bitmap bild = cameraView.getBitmap();

                // Hiermit kannste dein Zeug zeichnen.
                drawView.paintRectangle((float) (Math.random() * 500), (float) (Math.random() * 700), 100, 100);
            }
        });
        CameraX.bindToLifecycle(this, preview);
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();
        float centerX = cameraView.getWidth() / 2f;
        float centerY = cameraView.getHeight() / 2f;
        int rotationDegrees;
        switch (cameraView.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegrees = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegrees = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegrees = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegrees = 270;
                break;
            default:
                return;
        }
        matrix.postRotate(-rotationDegrees, centerX, centerY);
        cameraView.setTransform(matrix);
    }

    private boolean hasPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
