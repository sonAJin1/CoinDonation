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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.example.sonaj.coindonation.FaceCamera.camera.GraphicOverlay;
import com.example.sonaj.coindonation.R;



class FaceGraphic extends GraphicOverlay.Graphic {

  private static final String TAG = "FaceGraphic";

  private static final float DOT_RADIUS = 3.0f;
  private static final float TEXT_OFFSET_Y = -30.0f;

  private boolean mIsFrontFacing;

  // This variable may be written to by one of many threads. By declaring it as volatile,
  // we guarantee that when we read its contents, we're reading the most recent "write"
  // by any thread.
  private volatile FaceData mFaceData;

  private Paint mHintTextPaint;
  private Paint mHintOutlinePaint;
  private Paint mEyeWhitePaint;
  private Paint mIrisPaint;
  private Paint mEyeOutlinePaint;
  private Paint mEyelidPaint;

  private Drawable mPigNoseGraphic;
  private Drawable mMustacheGraphic;
  private Drawable mHappyStarGraphic;
  private Drawable mHatGraphic;
  private Drawable mQuestionGraphic;
  private Drawable mDogNoseGraphic;
  private Drawable mDogEarGraphic;
  private Drawable mWinkStarGraphic;
  private Drawable mSmileFlower;
  private Drawable mRoundFlower;
  private Drawable mRoundFlowerSmile;
  private Drawable mFaceMask;
  private Drawable mEyeMask;

  // We want each iris to move independently,
  // so each one gets its own physics engine.
  private EyePhysics mLeftPhysics = new EyePhysics();
  private EyePhysics mRightPhysics = new EyePhysics();

  private int maskType;

  FaceGraphic(GraphicOverlay overlay, Context context, boolean isFrontFacing) {
    super(overlay);
    mIsFrontFacing = isFrontFacing;
    Resources resources = context.getResources();
    initializePaints(resources);
    initializeGraphics(resources);
  }

  private void initializeGraphics(Resources resources) {
    mPigNoseGraphic = resources.getDrawable(R.drawable.pig_nose);
    mMustacheGraphic = resources.getDrawable(R.drawable.mustache);
    mHappyStarGraphic = resources.getDrawable(R.drawable.happy_star);
    mHatGraphic = resources.getDrawable(R.drawable.red_hat);
    mQuestionGraphic = resources.getDrawable(R.drawable.question_mark);
    mDogNoseGraphic = resources.getDrawable(R.drawable.dog_nose_2);
    mDogEarGraphic = resources.getDrawable(R.drawable.dog_ear_2);
    mWinkStarGraphic = resources.getDrawable(R.drawable.star);
    mSmileFlower = resources.getDrawable(R.drawable.flower);
    mRoundFlower = resources.getDrawable(R.drawable.flower_round_01);
    mRoundFlowerSmile = resources.getDrawable(R.drawable.flower_round_02);
    mFaceMask = resources.getDrawable(R.drawable.facemask);
    mEyeMask = resources.getDrawable(R.drawable.eyemask);
  }

  private void initializePaints(Resources resources) {
    mHintTextPaint = new Paint();
    mHintTextPaint.setColor(resources.getColor(R.color.overlayHint));
    mHintTextPaint.setTextSize(10);

    mHintOutlinePaint = new Paint();
    mHintOutlinePaint.setColor(resources.getColor(R.color.overlayHint));
    mHintOutlinePaint.setStyle(Paint.Style.STROKE);
    mHintOutlinePaint.setStrokeWidth(3);

    mEyeWhitePaint = new Paint();
    mEyeWhitePaint.setColor(resources.getColor(R.color.eyeWhite));
    mEyeWhitePaint.setStyle(Paint.Style.FILL);

    mIrisPaint = new Paint();
    mIrisPaint.setColor(resources.getColor(R.color.iris));
    mIrisPaint.setStyle(Paint.Style.FILL);

    mEyeOutlinePaint = new Paint();
    mEyeOutlinePaint.setColor(resources.getColor(R.color.eyeOutline));
    mEyeOutlinePaint.setStyle(Paint.Style.STROKE);
    mEyeOutlinePaint.setStrokeWidth(5);

    mEyelidPaint = new Paint();
    mEyelidPaint.setColor(resources.getColor(R.color.eyelid));
    mEyelidPaint.setStyle(Paint.Style.FILL);
  }

  /**
   *  Update the face instance based on detection from the most recent frame.
   */
  void update(FaceData faceData) {
    mFaceData = faceData;
    postInvalidate(); // Trigger a redraw of the graphic (i.e. cause draw() to be called).
  }

  @Override
  public void draw(Canvas canvas,int maskType) {
    // Confirm that the face data is still available
    // before using it.
    FaceData faceData = mFaceData;
    if (faceData == null) {
      return;
    }
    PointF detectPosition = faceData.getPosition();
    PointF detectLeftEyePosition = faceData.getLeftEyePosition();
    PointF detectRightEyePosition = faceData.getRightEyePosition();
    PointF detectNoseBasePosition = faceData.getNoseBasePosition();
    PointF detectMouthLeftPosition = faceData.getMouthLeftPosition();
    PointF detectMouthBottomPosition = faceData.getMouthBottomPosition();
    PointF detectMouthRightPosition = faceData.getMouthRightPosition();
    {
      if ((detectPosition == null) ||
        (detectLeftEyePosition == null) ||
        (detectRightEyePosition == null) ||
        (detectNoseBasePosition == null) ||
        (detectMouthLeftPosition == null) ||
        (detectMouthBottomPosition == null) ||
        (detectMouthRightPosition == null)) {
        return;
      }
    }


    // If we've made it this far, it means that the face data *is* available.
    // It's time to translate camera coordinates to view coordinates.

    // Face position, dimensions, and angle
    PointF position = new PointF(translateX(detectPosition.x),
                                 translateY(detectPosition.y));
    float width = scaleX(faceData.getWidth());
    float height = scaleY(faceData.getHeight());

    // Eye coordinates
    PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                                        translateY(detectLeftEyePosition.y));
    PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                                         translateY(detectRightEyePosition.y));

    // Eye state
    boolean leftEyeOpen = faceData.isLeftEyeOpen();
    boolean rightEyeOpen = faceData.isRightEyeOpen();

    // Nose coordinates
    PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
      translateY(detectNoseBasePosition.y));

    // Mouth coordinates
    PointF mouthLeftPosition = new PointF(translateX(detectMouthLeftPosition.x),
      translateY(detectMouthLeftPosition.y));
    PointF mouthRightPosition = new PointF(translateX(detectMouthRightPosition.x),
      translateY(detectMouthRightPosition.y));
    PointF mouthBottomPosition = new PointF(translateX(detectMouthBottomPosition.x),
      translateY(detectMouthBottomPosition.y));

    // Smile state
    boolean smiling = faceData.isSmiling();

    // Head tilt
    float eulerY = faceData.getEulerY();
    float eulerZ = faceData.getEulerZ();

    // Calculate the distance between the eyes using Pythagoras' formula,
    // and we'll use that distance to set the size of the eyes and irises.
    final float EYE_RADIUS_PROPORTION = 0.45f;
    final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;
    float distance = (float) Math.sqrt(
            (rightEyePosition.x - leftEyePosition.x) * (rightEyePosition.x - leftEyePosition.x) +
            (rightEyePosition.y - leftEyePosition.y) * (rightEyePosition.y - leftEyePosition.y));
    float eyeRadius = EYE_RADIUS_PROPORTION * distance;
    float irisRadius = IRIS_RADIUS_PROPORTION * distance;

    if(maskType==0){ // eyeMask
      drawEyeMask(canvas, position, width, height, noseBasePosition);
    }else if(maskType==1){ // 병아리
      drawFaceMask(canvas, position, width, height, noseBasePosition);
      PointF leftIrisPosition = mLeftPhysics.nextIrisPosition(leftEyePosition, eyeRadius, irisRadius);
      drawEye(canvas, leftEyePosition, eyeRadius, leftIrisPosition, irisRadius, leftEyeOpen, smiling);
      PointF rightIrisPosition = mRightPhysics.nextIrisPosition(rightEyePosition, eyeRadius, irisRadius);
      drawEye(canvas, rightEyePosition, eyeRadius, rightIrisPosition, irisRadius, rightEyeOpen, smiling);
    }else if(maskType==2){ // 강아지
      drawdogNose02(canvas, noseBasePosition, mouthLeftPosition, mouthRightPosition, mouthBottomPosition);
      drawDogEar(canvas, position, width, height, noseBasePosition);
    }else if(maskType==3){ // 꽃
      drawRoundFlower(canvas, position, width, height, noseBasePosition,smiling);
      drawSmail(canvas, position, width, height, noseBasePosition,smiling);
    }else if(maskType==4) { // 물음푶
      final float HEAD_TILT_HAT_THRESHOLD = 20.0f;
      if (Math.abs(eulerZ) > HEAD_TILT_HAT_THRESHOLD) {
        drawQuestion(canvas, position, width, height, noseBasePosition);
      }
    }else if(maskType==5){ // 돼지코
        drawNose(canvas, noseBasePosition, leftEyePosition, rightEyePosition, width);
    }else if(maskType==6){ // 콧수염
        drawMustache(canvas, noseBasePosition, mouthLeftPosition, mouthRightPosition);
    }else if(maskType==7){ // 눈
      PointF leftIrisPosition = mLeftPhysics.nextIrisPosition(leftEyePosition, eyeRadius, irisRadius);
      drawEye(canvas, leftEyePosition, eyeRadius, leftIrisPosition, irisRadius, leftEyeOpen, smiling);
      PointF rightIrisPosition = mRightPhysics.nextIrisPosition(rightEyePosition, eyeRadius, irisRadius);
      drawEye(canvas, rightEyePosition, eyeRadius, rightIrisPosition, irisRadius, rightEyeOpen, smiling);
    }

  }

  // Cartoon feature draw routines
  // =============================

  private void drawEyeMask(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition) {
    final float HAT_FACE_WIDTH_RATIO = (float) 1.0;
    final float HAT_FACE_HEIGHT_RATIO = (float) 0.7;
    final float HAT_CENTER_Y_OFFSET_FACTOR = (float) 0.9;

    float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
    float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
    float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

    int left = (int) (noseBasePosition.x - (hatWidth / 2));
    int right = (int) (noseBasePosition.x + (hatWidth / 2));
    int top = (int) (hatCenterY - (hatHeight));
    int bottom = (int) (hatCenterY + (hatHeight / 4));

    mEyeMask.setBounds(left, top, right, bottom);
    mEyeMask.draw(canvas);
  }

  private void drawEye(Canvas canvas,
                       PointF eyePosition, float eyeRadius,
                       PointF irisPosition, float irisRadius,
                       boolean eyeOpen, boolean smiling) {
    if (eyeOpen) {
      canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeWhitePaint);
      canvas.drawCircle(irisPosition.x, irisPosition.y, irisRadius, mIrisPaint);

    } else {
      canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyelidPaint);
      float y = eyePosition.y;
      float start = eyePosition.x - eyeRadius;
      float end = eyePosition.x + eyeRadius;
      canvas.drawLine(start, y, end, y, mEyeOutlinePaint);
    }
    canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeOutlinePaint);
  }

  private void drawNose(Canvas canvas,
                        PointF noseBasePosition,
                        PointF leftEyePosition, PointF rightEyePosition,
                        float faceWidth) {
    final float NOSE_FACE_WIDTH_RATIO = (float)(1 / 5.0);
    float noseWidth = faceWidth * NOSE_FACE_WIDTH_RATIO;
    int left = (int)(noseBasePosition.x - (noseWidth / 2));
    int right = (int)(noseBasePosition.x + (noseWidth / 2));
    int top = (int)(leftEyePosition.y + rightEyePosition.y) / 2;
    int bottom = (int)noseBasePosition.y;

    mPigNoseGraphic.setBounds(left, top, right, bottom);
    mPigNoseGraphic.draw(canvas);
  }


  private void drawFaceMask(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition) {
    final float HAT_FACE_WIDTH_RATIO = (float) 1.2;
    final float HAT_FACE_HEIGHT_RATIO = (float) 1.2;
    final float HAT_CENTER_Y_OFFSET_FACTOR = (float) 1.0;

    float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
    float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
    float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

    int left = (int) (noseBasePosition.x - (hatWidth / 1.5));
    int right = (int) (noseBasePosition.x + (hatWidth / 1.5));
    int top = (int) (hatCenterY - (hatHeight));
    int bottom = (int) (hatCenterY + (hatHeight / 4));

    mFaceMask.setBounds(left, top, right, bottom);
    mFaceMask.draw(canvas);

  }

  private void drawRoundFlower(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition,boolean smailing) {
    final float HAT_FACE_WIDTH_RATIO = (float)1.2 ;
    final float HAT_FACE_HEIGHT_RATIO = (float)1.2;
    final float HAT_CENTER_Y_OFFSET_FACTOR = (float) 1.2;

    float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
    float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
    float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

    int left = (int)(noseBasePosition.x - (hatWidth/1.5));
    int right = (int)(noseBasePosition.x + (hatWidth/1.5));
    int top = (int)(hatCenterY - (hatHeight));
    int bottom = (int)(hatCenterY + (hatHeight/4));

    if(smailing){
      mRoundFlowerSmile.setBounds(left, top, right, bottom);
      mRoundFlowerSmile.draw(canvas);
    }else{
      mRoundFlower.setBounds(left, top, right, bottom);
      mRoundFlower.draw(canvas);
    }
  }

  private void drawSmail(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition,boolean smailing) {
    final float HAT_FACE_WIDTH_RATIO = (float)1.0 ;
    final float HAT_FACE_HEIGHT_RATIO = (float)1.0;
    final float HAT_CENTER_Y_OFFSET_FACTOR = (float) 1.2;

    float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
    float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
    float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

    int left = (int)(noseBasePosition.x - (hatWidth / 2));
    int right = (int)(noseBasePosition.x + (hatWidth / 2));
    int top = (int)(hatCenterY - (hatHeight / 2));
    int bottom = (int)(hatCenterY + (hatHeight / 2));
    if(smailing){
      mSmileFlower.setBounds(left, top, right, bottom);
      mSmileFlower.draw(canvas);
    }

  }

  private void drawMustache(Canvas canvas,
                            PointF noseBasePosition,
                            PointF mouthLeftPosition, PointF mouthRightPosition) {
    int left = (int)mouthLeftPosition.x;
    int top = (int)noseBasePosition.y;
    int right = (int)mouthRightPosition.x;
    int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y);


    // We need to check which camera is being used because the mustache graphic's bounds
    // are based on the left and right corners of the mouth, from the subject's persepctive.
    // With the front camera, the subject's left will be on the *left* side of the view,
    // but with the back camera, the subject's left will be on the *right* side.
    if (mIsFrontFacing) {
      mMustacheGraphic.setBounds(left, top, right, bottom);
    } else {
      mMustacheGraphic.setBounds(right, top, left, bottom);
    }
    mMustacheGraphic.draw(canvas);
  }

  private void drawHat(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition) {
    final float HAT_FACE_WIDTH_RATIO = (float)(1.0 / 4.0);
    final float HAT_FACE_HEIGHT_RATIO = (float)(1.0 / 6.0);
    final float HAT_CENTER_Y_OFFSET_FACTOR = (float)(1.0 / 8.0);

    float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
    float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
    float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

    int left = (int)(noseBasePosition.x - (hatWidth / 2));
    int right = (int)(noseBasePosition.x + (hatWidth / 2));
    int top = (int)(hatCenterY - (hatHeight / 2));
    int bottom = (int)(hatCenterY + (hatHeight / 2));
    mHatGraphic.setBounds(left, top, right, bottom);
    mHatGraphic.draw(canvas);
  }

  private void drawQuestion(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition) {
    final float HAT_FACE_WIDTH_RATIO = (float)(1.0 / 2.0);
    final float HAT_FACE_HEIGHT_RATIO = (float)(1.0 / 3.0);
    final float HAT_CENTER_Y_OFFSET_FACTOR = (float)(1.0 / 8.0);

    float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
    float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
    float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

    int left = (int)(noseBasePosition.x - (hatWidth / 2));
    int right = (int)(noseBasePosition.x + (hatWidth / 2));
    int top = (int)(hatCenterY - (hatHeight / 2));
    int bottom = (int)(hatCenterY + (hatHeight / 2));
    mQuestionGraphic.setBounds(left, top, right, bottom);
    mQuestionGraphic.draw(canvas);
  }

  private void drawDogEar(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition) {
    final float HAT_FACE_WIDTH_RATIO = (float)1.0;
    final float HAT_FACE_HEIGHT_RATIO = (float)(1.0 / 2.5);
    final float HAT_CENTER_Y_OFFSET_FACTOR = (float)(1.0 / 8.0);

    float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
    float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
    float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

    int left = (int)(noseBasePosition.x - (hatWidth / 2));
    int right = (int)(noseBasePosition.x + (hatWidth / 2));
    int top = (int)(hatCenterY - (hatHeight / 2));
    int bottom = (int)(hatCenterY + (hatHeight / 2));
    mDogEarGraphic.setBounds(left, top, right, bottom);
    mDogEarGraphic.draw(canvas);
  }

  private void drawdogNose02(Canvas canvas,
                            PointF noseBasePosition,
                            PointF mouthLeftPosition, PointF mouthRightPosition, PointF mouthButtomPosition) {
    int left = (int)mouthLeftPosition.x;
    int top = (int)noseBasePosition.y;
    int right = (int)mouthRightPosition.x;
    //int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y);
    int bottom = (int) mouthButtomPosition.y;

    // We need to check which camera is being used because the mustache graphic's bounds
    // are based on the left and right corners of the mouth, from the subject's persepctive.
    // With the front camera, the subject's left will be on the *left* side of the view,
    // but with the back camera, the subject's left will be on the *right* side.
    if (mIsFrontFacing) {
      mDogNoseGraphic.setBounds(left, top, right, bottom);
    } else {
      mDogNoseGraphic.setBounds(right, top, left, bottom);
    }
    mDogNoseGraphic.draw(canvas);
  }

  private void drawdogmouth(Canvas canvas,
                             PointF noseBasePosition,
                             PointF mouthLeftPosition, PointF mouthRightPosition, PointF mouthButtomPosition) {
    int left = (int)mouthLeftPosition.x;
    int top = (int)noseBasePosition.y;
    int right = (int)mouthRightPosition.x;
    //int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y);
    int bottom = (int) mouthButtomPosition.y;

    // We need to check which camera is being used because the mustache graphic's bounds
    // are based on the left and right corners of the mouth, from the subject's persepctive.
    // With the front camera, the subject's left will be on the *left* side of the view,
    // but with the back camera, the subject's left will be on the *right* side.
    if (mIsFrontFacing) {
      mDogNoseGraphic.setBounds(left, top, right, bottom);
    } else {
      mDogNoseGraphic.setBounds(right, top, left, bottom);
    }
    mDogNoseGraphic.draw(canvas);
  }

}