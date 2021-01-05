package fila.ble;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class HorizontalDottedProgress extends View{

    //to get identified in which position dot has to bounce
    private int  mDotPosition = 9;

    //specify how many dots you need in a progressbar
    private int mDotAmount = 9;

    public HorizontalDottedProgress(Context context) {
        super(context);
    }

    public HorizontalDottedProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalDottedProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //Method to draw your customized dot on the canvas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();

        //set the color for the dot that you want to draw
        paint.setColor(Color.parseColor("#36b320"));

        //function to create dot
        createDot(canvas,paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Animation called when attaching to the window, i.e to your screen
        startAnimation();
    }

    int k = 0;

    private void createDot(Canvas canvas, Paint paint) {

        //here i have setted progress bar with 10 dots , so repeat and wnen i = mDotPosition  then increase the radius of dot i.e mBounceDotRadius
        for(int i = 0; i < mDotAmount; i++ ){

            if(i < mDotPosition)
            {
                canvas.drawRect(10+(i*k), 10, k+(i*k), 27, paint);
            }
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        switch(mDotAmount)
        {
            case 2: k = 205; break;
            case 3: k = 138; break;
            case 4: k = 102; break;
            case 5: k = 82; break;
            case 6: k = 69; break;
            case 7: k = 59; break;
            case 8: k = 52; break;
            case 9: k = 46; break;
            case 10: k = 41; break;
            default: k = 41; break;
        }


        int width;
        int height;

        //calculate the view width
        int calculatedWidth = k+(mDotAmount*k)+10;

        width = calculatedWidth;
        height = (30);



        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    private void startAnimation() {
        BounceAnimation bounceAnimation = new BounceAnimation();
        bounceAnimation.setDuration(100);
        bounceAnimation.setRepeatCount(Animation.INFINITE);
        bounceAnimation.setInterpolator(new LinearInterpolator());
        bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                /*mDotPosition++;
                //when mDotPosition == mDotAmount , then start again applying animation from 0th positon , i.e  mDotPosition = 0;
                if (mDotPosition == mDotAmount) {
                    mDotPosition = 0;
                }*/
                Log.d("INFOMETHOD","----On Animation Repeat----");

            }
        });
        startAnimation(bounceAnimation);
    }


    private class BounceAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //call invalidate to redraw your view againg.
            invalidate();
        }
    }
}