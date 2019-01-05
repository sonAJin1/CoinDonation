/*
 * Copyright (c) 2017 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish, 
 * distribute, sublicense, create a derivative work, and/or sell copies of the 
 * Software in any work that is designed, intended, or marketed for pedagogical or 
 * instructional purposes related to programming, coding, application development, 
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works, 
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.sonaj.coindonation.FaceCamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sonaj.coindonation.AR.UnityPlayerActivity;
import com.example.sonaj.coindonation.FaceCamera.camera.CameraSourcePreview;
import com.example.sonaj.coindonation.FaceCamera.camera.GraphicOverlay;
import com.example.sonaj.coindonation.Main.MainActivity;
import com.example.sonaj.coindonation.R;
import com.example.sonaj.coindonation.Util.CallbackEvent;
import com.example.sonaj.coindonation.Util.RecyclerViewItemClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class FaceActivity extends AppCompatActivity {

  private static final String TAG = "FaceActivity";
  Context context;

  private static final int RC_HANDLE_GMS = 9001;
  // permission request codes need to be < 256
  private static final int RC_HANDLE_CAMERA_PERM = 255;

  private CameraSource mCameraSource = null;
  private CameraSourcePreview mPreview;
  private GraphicOverlay mGraphicOverlay;
  private boolean mIsFrontFacing = true;
  static final int CAMERA_STATUS_PREVIEW = 0;
  static final int CAMERA_STATUS_CAPTURE = 1;

  //recyclerView
  private RecyclerView recyclerView;
  private FaceMaskAdapter adapter;
  private RecyclerView.LayoutManager layoutManager;

  //button
  ImageButton btnCapture;
  ImageButton btnChangeCameraView;
  Button btnBackPreview;
  Button btnCaptureComplete;
  Button btnInfoDelete;

  ImageView previewCapture;
  RelativeLayout infoLayout;





  static Uri capturedImageUri=null;
  ImageView imCapture;


  // Activity event handlers
  // =======================

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate called.");
    //------상태바 없애는부분
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_face_camera);
    //-----------

    context = getApplicationContext();
    init();



    if (savedInstanceState != null) {
      mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
    }

    // Start using the camera if permission has been granted to this app,
    // otherwise ask for permission to use it.
    int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    if (rc == PackageManager.PERMISSION_GRANTED) {
      createCameraSource();
    } else {
      requestCameraPermission();
    }

    //face mask 리스트뷰
    setRecyclerview();

  }

  public void init(){
    mPreview = (CameraSourcePreview) findViewById(R.id.preview);
    mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
    btnChangeCameraView= (ImageButton) findViewById(R.id.flipButton);
    btnCapture = (ImageButton) findViewById(R.id.btn_capture);
    imCapture = (ImageView)findViewById(R.id.iv_capture);
    btnBackPreview = (Button)findViewById(R.id.btn_back_preview);
    btnCaptureComplete = (Button)findViewById(R.id.btn_capture_complete);
    previewCapture = (ImageView)findViewById(R.id.previewCapture);
    btnInfoDelete = (Button)findViewById(R.id.ib_enter);
    infoLayout = (RelativeLayout)findViewById(R.id.rl_info);

    //click listener
    btnChangeCameraView.setOnClickListener(mSwitchCameraButtonListener);
    btnCapture.setOnClickListener(CaptureButtonListener);
    btnBackPreview.setOnClickListener(backPreviewListener);
    btnCaptureComplete.setOnClickListener(captureCompleteListener);
    btnInfoDelete.setOnClickListener(deleteInfoListener);

  }

  /** recyclerView 설정 */
  public void setRecyclerview(){

    recyclerView = (RecyclerView)findViewById(R.id.rc_face_mask);
    recyclerView.setHasFixedSize(true);

    //recyclerView item 넣어주기
    ArrayList<FaceMaskItem> faceMaskItems = new ArrayList<>();
    faceMaskItems.add(new FaceMaskItem(R.drawable.eyemask)); // eyeMask
    faceMaskItems.add(new FaceMaskItem(R.drawable.facemask02)); // 병아리
    faceMaskItems.add(new FaceMaskItem(R.drawable.dog)); // 강아지
    faceMaskItems.add(new FaceMaskItem(R.drawable.flower)); // 웃을때 꽃
    faceMaskItems.add(new FaceMaskItem(R.drawable.question_mark)); // 머리 기울일때 물음표
    faceMaskItems.add(new FaceMaskItem(R.drawable.pig_nose)); // 돼지코
    faceMaskItems.add(new FaceMaskItem(R.drawable.mustache)); // 콧수염
    faceMaskItems.add(new FaceMaskItem(R.drawable.happy_star)); // 눈

    //recyclerView layoutManager set (가로 스크롤 설정)
    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    adapter = new FaceMaskAdapter(faceMaskItems, context);

    //RecyclerView 아이템 클릭
    adapter.setOnClickListener(new RecyclerViewItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        mGraphicOverlay.setMaskType(position); // 내가 선택한 아이템만 그리게 인자를 graphicOverlay 로 전달
      }
    });
    recyclerView.setAdapter(adapter);

  }

  /** 카메라 전면 후면 설정하는 버튼*/
  private View.OnClickListener mSwitchCameraButtonListener = new View.OnClickListener() {
    public void onClick(View v) {
      mIsFrontFacing = !mIsFrontFacing;

      if (mCameraSource != null) {
        mCameraSource.release();
        mCameraSource = null;
      }

      createCameraSource();
      startCameraSource();
    }
  };

  /** 다시 카메라 프리뷰로 돌아가는 화면 (사진 찍기전)*/
  private View.OnClickListener backPreviewListener = new View.OnClickListener(){
    @Override
    public void onClick(View view) {
      changeCameraStatus(CAMERA_STATUS_PREVIEW);
      try {
        mPreview.start(mCameraSource, mGraphicOverlay);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  };

  /** 프로필 사진 결정 */
  private View.OnClickListener captureCompleteListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {

    }
  };

  /**화면 캡쳐 버튼*/
  private View.OnClickListener CaptureButtonListener = new View.OnClickListener(){
    public void onClick(View view) {

      Intent intent = new Intent(context, MainActivity.class);
      startActivity(intent);
      Toast.makeText(context,"입장하셨습니다",Toast.LENGTH_LONG).show();

    }
  };

  /** info 화면 삭제 */
  private View.OnClickListener deleteInfoListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      infoLayout.setVisibility(View.GONE);
    }
  };


  public void capture01(){
    Calendar cal = Calendar.getInstance();
    File file = new File(Environment.getExternalStorageDirectory(),(cal.getTimeInMillis()+".jpg"));
    if(!file.exists()){
      try{
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }else{
      file.delete();
      try{
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    //capturedImageUri = Uri.fromFile(file);
    capturedImageUri = FileProvider.getUriForFile(context,"com.example.sonaj.coindonation.fileprovider",file);
    Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
    startActivityForResult(intent, 0);
  }

  public static Bitmap viewToBitmap(View view){
    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    if (view instanceof SurfaceView) {
      SurfaceView surfaceView = (SurfaceView) view;
      surfaceView.setZOrderOnTop(true);
      surfaceView.draw(canvas);
      surfaceView.setZOrderOnTop(false);
      return bitmap;
    } else {
      //For ViewGroup & View
      view.draw(canvas);
      return bitmap;
    }

 }


    public static File ScreenShot(Bitmap capture){
//    view.setDrawingCacheEnabled(true); // 화면에 뿌릴때 캐시를 사용하게 한다
//    Bitmap captureView = view.getDrawingCache(); //캐시를 비트맵으로 변환

        //  Bitmap capture = viewToBitmap(view);

        String fileName = "screenshot.png";
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures",fileName);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            capture.compress(Bitmap.CompressFormat.PNG,90,os); //비트맵을 png파일로 저장
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // view.setDrawingCacheEnabled(false);
        return file;
    }
//
//  public static File ScreenShot(View view){
////    view.setDrawingCacheEnabled(true); // 화면에 뿌릴때 캐시를 사용하게 한다
////    Bitmap captureView = view.getDrawingCache(); //캐시를 비트맵으로 변환
//
//    Bitmap capture = viewToBitmap(view);
//
//    String fileName = "screenshot.png";
//    File file = new File(Environment.getExternalStorageDirectory()+"/Pictures",fileName);
//    FileOutputStream os = null;
//    try {
//      os = new FileOutputStream(file);
//      capture.compress(Bitmap.CompressFormat.PNG,90,os); //비트맵을 png파일로 저장
//      os.close();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    view.setDrawingCacheEnabled(false);
//    return file;
//  }


  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 0) {
      //Bitmap photo = (Bitmap) data.getExtras().get("data");
      //imageView.setImageBitmap(photo);
      try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), capturedImageUri);
        imCapture.setVisibility(View.VISIBLE);
        imCapture.setImageBitmap(bitmap);
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void changeCameraStatus(int CameraStatus){
    switch (CameraStatus){
      case CAMERA_STATUS_PREVIEW : // 촬영전
        recyclerView.setVisibility(View.VISIBLE);
        btnCapture.setVisibility(View.VISIBLE);
        btnBackPreview.setVisibility(View.GONE);
        btnCaptureComplete.setVisibility(View.GONE);
        break;
      case CAMERA_STATUS_CAPTURE : // 촬영후
        recyclerView.setVisibility(View.GONE);
        btnCapture.setVisibility(View.GONE);
        btnBackPreview.setVisibility(View.VISIBLE);
        btnCaptureComplete.setVisibility(View.VISIBLE);
        break;
    }
  }

    @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume called.");

    startCameraSource();
  }

  @Override
  protected void onPause() {
    super.onPause();

    mPreview.stop();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mCameraSource != null) {
      mCameraSource.release();
    }
  }

  // Handle camera permission requests
  // =================================

  private void requestCameraPermission() {
    Log.w(TAG, "Camera permission not acquired. Requesting permission.");

    final String[] permissions = new String[]{Manifest.permission.CAMERA};
    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
      Manifest.permission.CAMERA)) {
      ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
      return;
    }

    final Activity thisActivity = this;
    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
      }
    };
    Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
      Snackbar.LENGTH_INDEFINITE)
      .setAction(R.string.ok, listener)
      .show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode != RC_HANDLE_CAMERA_PERM) {
      Log.d(TAG, "Got unexpected permission result: " + requestCode);
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      return;
    }

    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // We have permission to access the camera, so create the camera source.
      Log.d(TAG, "Camera permission granted - initializing camera source.");
      createCameraSource();
      return;
    }

    // If we've reached this part of the method, it means that the user hasn't granted the app
    // access to the camera. Notify the user and exit.
    Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
      " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        finish();
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.app_name)
      .setMessage(R.string.no_camera_permission)
      .setPositiveButton(R.string.disappointed_ok, listener)
      .show();
  }

  // Camera source
  // =============

  private void createCameraSource() {
    Log.d(TAG, "createCameraSource called.");

//    Context context = getApplicationContext();
    FaceDetector detector = createFaceDetector(context);

    int facing = CameraSource.CAMERA_FACING_FRONT;
    if (!mIsFrontFacing) {
      facing = CameraSource.CAMERA_FACING_BACK;
    }

    // The camera source is initialized to use either the front or rear facing camera.  We use a
    // relatively low resolution for the camera preview, since this is sufficient for this app
    // and the face detector will run faster at lower camera resolutions.
    //
    // However, note that there is a speed/accuracy trade-off with respect to choosing the
    // camera resolution.  The face detector will run faster with lower camera resolutions,
    // but may miss smaller faces, landmarks, or may not correctly detect eyes open/closed in
    // comparison to using higher camera resolutions.  If you have any of these issues, you may
    // want to increase the resolution.
    mCameraSource = new CameraSource.Builder(context, detector)
      .setFacing(facing)
      .setRequestedPreviewSize(320, 240)
      .setRequestedFps(60.0f)
      .setAutoFocusEnabled(true)
      .build();
  }

  private void startCameraSource() {
    Log.d(TAG, "startCameraSource called.");

    // Make sure that the device has Google Play services available.
    int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            context);
    if (code != ConnectionResult.SUCCESS) {
      Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
      dlg.show();
    }

    if (mCameraSource != null) {
      try {
        mPreview.start(mCameraSource, mGraphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        mCameraSource.release();
        mCameraSource = null;
      }
    }
  }

  // Face detector
  // =============

  /**
   *  Create the face detector, and check if it's ready for use.
   */
  @NonNull
  private FaceDetector createFaceDetector(final Context context) {
    Log.d(TAG, "createFaceDetector called.");

    FaceDetector detector = new FaceDetector.Builder(context)
      .setLandmarkType(FaceDetector.ALL_LANDMARKS)
      .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
      .setTrackingEnabled(true)
      .setMode(FaceDetector.FAST_MODE)
      .setProminentFaceOnly(mIsFrontFacing)
      .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
      .build();

    MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
      @Override
      public Tracker<Face> create(Face face) {
        return new FaceTracker(mGraphicOverlay, context, mIsFrontFacing);
      }
    };

    Detector.Processor<Face> processor = new MultiProcessor.Builder<>(factory).build();
    detector.setProcessor(processor);

    if (!detector.isOperational()) {
      Log.w(TAG, "Face detector dependencies are not yet available.");

      // Check the device's storage.  If there's little available storage, the native
      // face detection library will not be downloaded, and the app won't work,
      // so notify the user.
      IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
      boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

      if (hasLowStorage) {
        Log.w(TAG, getString(R.string.low_storage_error));
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            finish();
          }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
          .setMessage(R.string.low_storage_error)
          .setPositiveButton(R.string.disappointed_ok, listener)
          .show();
      }
    }
    return detector;
  }

}