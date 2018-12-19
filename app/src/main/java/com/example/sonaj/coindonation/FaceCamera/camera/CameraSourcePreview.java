/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sonaj.coindonation.FaceCamera.camera;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraSourcePreview extends ViewGroup {

  private static final String TAG = "CameraSourcePreview";

  private Context mContext;
  private SurfaceView mSurfaceView;
  private boolean mStartRequested;
  private boolean mSurfaceAvailable;
  private CameraSource mCameraSource;

  private GraphicOverlay mOverlay;

  private String ImageByte;

  public CameraSourcePreview(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    mStartRequested = false;
    mSurfaceAvailable = false;

    mSurfaceView = new SurfaceView(context);
    mSurfaceView.getHolder().addCallback(new SurfaceCallback());

    addView(mSurfaceView);
  }

  public void start(CameraSource cameraSource) throws IOException {
    if (cameraSource == null) {
      stop();
    }

    mCameraSource = cameraSource;

    if (mCameraSource != null) {
      mStartRequested = true;
      startIfReady();
    }
  }

  public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
    mOverlay = overlay;
    start(cameraSource);
  }

  public void stop() {
    if (mCameraSource != null) {
        mCameraSource.stop();
    }
  }

  public void release() {
    if (mCameraSource != null) {
        mCameraSource.release();
        mCameraSource = null;
    }
  }

  private void startIfReady() throws IOException {
    if (mStartRequested && mSurfaceAvailable) {
      mCameraSource.start(mSurfaceView.getHolder());
      if (mOverlay != null) {
        Size size = mCameraSource.getPreviewSize();
        int min = Math.min(size.getWidth(), size.getHeight());
        int max = Math.max(size.getWidth(), size.getHeight());
        if (isPortraitMode()) {
          // Swap width and height sizes when in portrait, since it will be rotated by
          // 90 degrees
          mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
        } else {
          mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
        }
        mOverlay.clear();
      }
      mStartRequested = false;
    }
  }

  public String getImageByte(){
    return ImageByte;
  }
  public void setImageByte(Bitmap bitmap){

  }

  public void capture(){

    try{
      mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] bytes) {
          try {

            // convert byte array into bitmap
            Bitmap loadedImage = null;
            Bitmap sideInversionBitmap = null;
            loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                    bytes.length);

            //sideInversion Image > rotate 이미지 받아서 좌우반전 시켜주기
            Matrix sideInversionMatrix = new Matrix();
            sideInversionMatrix.postScale(-1, 1);
            sideInversionBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                    loadedImage.getWidth(), loadedImage.getHeight(),
                    sideInversionMatrix, false);

            //Image save
            if(sideInversionBitmap!=null){
              File screenShot =ScreenShot(sideInversionBitmap);
              getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
            }else{
              Toast.makeText(getContext(),"capture is null.",Toast.LENGTH_SHORT).show();
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

    }catch (Exception ex){

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


  private class SurfaceCallback implements SurfaceHolder.Callback {
    @Override
    public void surfaceCreated(SurfaceHolder surface) {
      mSurfaceAvailable = true;
      try {
        startIfReady();
      } catch (IOException e) {
        Log.e(TAG, "Could not start camera source.", e);
      }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surface) {
      mSurfaceAvailable = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int previewWidth = 320;
    int previewHeight = 240;
    if (mCameraSource != null) {
      Size size = mCameraSource.getPreviewSize();
      if (size != null) {
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();
      }
    }

    // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
    if (isPortraitMode()) {
      int tmp = previewWidth;
      previewWidth = previewHeight;
      previewHeight = tmp;
    }

    final int viewWidth = right - left;
    final int viewHeight = bottom - top;

    int childWidth;
    int childHeight;
    int childXOffset = 0;
    int childYOffset = 0;
    float widthRatio = (float) viewWidth / (float) previewWidth;
    float heightRatio = (float) viewHeight / (float) previewHeight;

    // To fill the view with the camera preview, while also preserving the correct aspect ratio,
    // it is usually necessary to slightly oversize the child and to crop off portions along one
    // of the dimensions.  We scale up based on the dimension requiring the most correction, and
    // compute a crop offset for the other dimension.
    if (widthRatio > heightRatio) {
        childWidth = viewWidth;
        childHeight = (int) ((float) previewHeight * widthRatio);
        childYOffset = (childHeight - viewHeight) / 2;
    } else {
        childWidth = (int) ((float) previewWidth * heightRatio);
        childHeight = viewHeight;
        childXOffset = (childWidth - viewWidth) / 2;
    }

    for (int i = 0; i < getChildCount(); ++i) {
      // One dimension will be cropped.  We shift child over or up by this offset and adjust
      // the size to maintain the proper aspect ratio.
      getChildAt(i).layout(
              -1 * childXOffset, -1 * childYOffset,
              childWidth - childXOffset, childHeight - childYOffset);
    }

    try {
        startIfReady();
    } catch (IOException e) {
        Log.e(TAG, "Could not start camera source.", e);
    }
  }

  private boolean isPortraitMode() {
    int orientation = mContext.getResources().getConfiguration().orientation;
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return false;
    }
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      return true;
    }

    Log.d(TAG, "isPortraitMode returning false by default");
    return false;
  }


}
