/*
Copyright © 2012 SSAD Team 37

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*Authors: Rashi Aswani, Abhinandan Panigrahi */

/**
 * Represents a draggable start or end marker.
 *
 * Most events are passed back to the client class using a
 * listener interface.
 *
 * This class directly keeps track of its own velocity, though,
 * accelerating as the user holds down the left or right arrows
 * while this control is focused.
 */
package com.SpeechEd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MarkerView extends ImageView {

    public interface MarkerListener {
        public void markerTouchStart(MarkerView marker, float pos);
        public void markerTouchMove(MarkerView marker, float pos);
        public void markerTouchEnd(MarkerView marker);
        public void markerFocus(MarkerView marker);
        public void markerLeft(MarkerView marker, int velocity);
        public void markerRight(MarkerView marker, int velocity);
        public void markerEnter(MarkerView marker);
        public void markerKeyUp();
        public void markerDraw();
    };

    private int mVelocity;
    private MarkerListener mListener;
    private float mTouchStart;
    private int mTouchLast;

    public MarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Make sure we get keys
        setFocusable(true);

        mVelocity = 0;
        mListener = null;
    }

    public void setListener(MarkerListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            requestFocus();
            // We use raw x because this window itself is going to
            // move, which will screw up the "local" coordinates
            mListener.markerTouchStart(this, event.getRawX());
            break;
        case MotionEvent.ACTION_MOVE:
            // We use raw x because this window itself is going to
            // move, which will screw up the "local" coordinates
            mListener.markerTouchMove(this, event.getRawX());
            break;
        case MotionEvent.ACTION_UP:
            mListener.markerTouchEnd(this);
            break;
        }
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        if (gainFocus && mListener != null)
            mListener.markerFocus(this);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mListener != null)
            mListener.markerDraw();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mVelocity++;
        int v = (int)Math.sqrt(1 + mVelocity / 2);
        if (mListener != null) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mListener.markerLeft(this, v);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mListener.markerRight(this, v);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                mListener.markerEnter(this);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mVelocity = 0;
        if (mListener != null)
            mListener.markerKeyUp();
        return super.onKeyDown(keyCode, event);
    }
}
