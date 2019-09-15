    package pe.mobytes.squaresdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.ViewStub;
import static android.Manifest.permission.CAMERA;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.android.material.snackbar.Snackbar;

    public class ScanQRCodeActivity extends AppCompatActivity
            implements ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {

    private static final int CAMERA_REQUEST_CODE = 0;

    private QRCodeReaderView qrCodeReaderView;
    private ViewStub qrCodeStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        qrCodeStub = findViewById(R.id.qr_code_stub);

        findViewById(R.id.cancel_button).setOnClickListener(view -> finish());

        if (ActivityCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
                Snackbar.make(qrCodeStub, R.string.permission_request, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok_button, view -> requestCameraPermission())
                        .show();
            } else {
                requestCameraPermission();
            }
        }
    }

        private void initQRCodeReaderView() {
            qrCodeStub.inflate();
            qrCodeReaderView = findViewById(R.id.qr_code_reader);
            qrCodeReaderView.setAutofocusInterval(2000L);
            qrCodeReaderView.setOnQRCodeReadListener(this);
            qrCodeReaderView.setBackCamera();
            qrCodeReaderView.setQRDecodingEnabled(true);
            qrCodeReaderView.startCamera();
        }

        @Override public void onBackPressed() {
            startActivity(new Intent(this, StartAuthorizeActivity.class));
            finish();
        }

        @Override public void onQRCodeRead(String authorizationCode, PointF[] points) {
            qrCodeReaderView.setOnQRCodeReadListener(null);
            MainActivity.start(this, authorizationCode);
            finish();
        }

        private void requestCameraPermission() {
            String[] permissions = { CAMERA };
            ActivityCompat.requestPermissions(this, permissions, CAMERA_REQUEST_CODE);
        }

        @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                         @NonNull int[] grantResults) {
            if (requestCode != CAMERA_REQUEST_CODE) {
                return;
            }
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeReaderView();
            } else {
                finish();
            }
        }

        @Override protected void onResume() {
            super.onResume();
            if (qrCodeReaderView != null) {
                qrCodeReaderView.startCamera();
            }
        }

        @Override protected void onPause() {
            super.onPause();
            if (qrCodeReaderView != null) {
                qrCodeReaderView.stopCamera();
            }
        }
}
