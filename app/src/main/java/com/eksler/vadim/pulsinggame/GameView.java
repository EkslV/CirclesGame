package com.eksler.vadim.pulsinggame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.eksler.vadim.pulsinggame.models.Arc;
import com.eksler.vadim.pulsinggame.models.Circle;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vadimeksler on 27/03/2018.
 */

public class GameView extends View implements View.OnTouchListener {

    public static final int PAINT_WIDTH = 5;
    private Paint mPaint;
    private int mSize, mCenter, mRootRadius, mOffSet = 50, mStage = 3;
    private Handler mHandler;
    private float userX, userY;
    private GameViewListener listener;
    private Circle[] circles;
    private Timer timer;
    private boolean isRunning = false;


    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setListener(GameViewListener listener) {
        this.listener = listener;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(PAINT_WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        circles = new Circle[4];
        mHandler = new Handler();
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mSize = MeasureSpec.getSize(widthMeasureSpec);
        mCenter = mSize / 2;
        mOffSet = mCenter / 4;
        mRootRadius = (mSize - PAINT_WIDTH) / 2;
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawCircle(mCenter, mCenter, mRootRadius, mPaint);

        redrawCircle(canvas);
    }

    private void redrawCircle(Canvas canvas) {
        if (circles[3] != null && circles[2] != null && circles[3].getRadius() == circles[2].getRadius()) {
            circles[3].update();
        }

        switch (mStage) {
            case 0:
                Circle first = circles[0];
                first = drawCurrentCircle(first, canvas);
                circles[0] = first;
            case 1:
                Circle second = circles[1];
                second = drawCurrentCircle(second, canvas);
                circles[1] = second;
            case 2:
                Circle third = circles[2];
                third = drawCurrentCircle(third, canvas);
                circles[2] = third;
            case 3:
                Circle fourth = circles[3];
                fourth = drawCurrentCircle(fourth, canvas);
                circles[3] = fourth;
        }

        //isCrossed();
    }

    private Circle drawCurrentCircle(Circle circle, Canvas canvas) {
        if (circle == null) {
            circle = new Circle(mOffSet, mCenter);
        } else {
            circle.update();
        }

        if (circle.getRectF().left >= mOffSet) {
            List<Arc> arcs = circle.getArcs();
            for (Arc arc : arcs) {
                canvas.drawArc(circle.getRectF(),
                        arc.getStartDegrees(),
                        arc.getEndDegrees() - arc.getStartDegrees(),
                        false, mPaint);
            }
        } else {
            circle.reset();
            List<Arc> arcs = circle.getArcs();
            for (Arc arc : arcs) {
                canvas.drawArc(circle.getRectF(),
                        arc.getStartDegrees(),
                        arc.getEndDegrees() - arc.getStartDegrees(),
                        false, mPaint);
            }
        }

        return circle;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                userX = motionEvent.getX();
                userY = motionEvent.getY();
                if (isRunning && listener != null) {
                    isCrossed();
                    if (!isInRoot() && isRunning) {
                        listener.onFinish();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                userX = motionEvent.getX();
                userY = motionEvent.getY();
                if (isRunning && listener != null) {
                    isCrossed();
                    if (!isInRoot() && isRunning) {
                        listener.onFinish();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isRunning && listener != null) {
                    listener.onFinish();
                }
                break;
        }
        return true;
    }

    private void isCrossed() {
        Circle tmp = null;
        for (Circle circle : circles) {
            if (circle != null && insideCircle(circle)) {
                tmp = circle;
                break;
            }
        }

        if (tmp != null) {
            if (isCrossedCurrent(tmp)) {
                listener.onFinish();
            } else {
                listener.onUpdateCount();
                tmp.reset();
            }
        }
    }

    private boolean isCrossedCurrent(Circle circle) {
        float x = userX - mCenter;
        float y = userY - mCenter;
        double arcTg = Math.atan(y / x);

        arcTg = Math.abs(arcTg);
        int angle = (int) Math.toDegrees(arcTg);

        if (x > 0 && y < 0) {
            angle += 270;
        } else if (x < 0 && y < 0) {
            angle += 180;
        } else if (x < 0 && y > 0) {
            angle += 90;
        }

        for (Arc arc : circle.getArcs()) {
            if (angle >= arc.getStartDegrees() && angle <= arc.getEndDegrees()) {
                return true;
            }
        }

        return false;
    }

    private boolean insideCircle(Circle circle) {
        return (Math.pow((userX - mCenter), 2.0) + Math.pow((userY - mCenter), 2.0)) <= Math.pow(circle.getRadius(), 2.0);
    }

    private boolean isInRoot() {
        return (Math.pow((userX - mCenter), 2.0) + Math.pow((userY - mCenter), 2.0)) <= Math.pow(mRootRadius, 2.0);
    }

    public void start() {
        userX = 0;
        userY = 0;
        timer = new Timer();
        timer.schedule(new RedrawTask(), 1000, 1000);
    }

    public void stop() {
        isRunning = false;
        if (timer != null) {
            timer.cancel();
        }
        mStage = 3;
        circles = new Circle[4];
    }

    private class RedrawTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mStage > 0) {
                        mStage--;
                    }
                    isRunning = true;
                    invalidate();
                }
            });
        }
    }

    interface GameViewListener {
        void onUpdateCount();

        void onFinish();
    }
}
