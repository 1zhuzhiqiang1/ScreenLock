package com.zzq.screenlock.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zzq.screenlock.R;
import com.zzq.screenlock.entry.Point;
import com.zzq.screenlock.utils.VibratorUtils;

import java.util.ArrayList;
import java.util.List;


public class GestureLock extends View {

    private Point[][] points = new Point[3][3];
    private boolean inited = false;//是否初始化

    private boolean isDraw = false;//是否已经绘制
    private ArrayList<Point> pointList = new ArrayList<Point>();//保存经过的点
    private ArrayList<Integer> passList = new ArrayList<Integer>();//保存经过点的数值

    private Bitmap bitmapPointError;/*绘制错误的图片*/
    private Bitmap bitmapPointNormal;/*正常的图片*/
    private Bitmap bitmapPointPress;/*按下的图片*/

    private OnDrawFinishedListener listener;
    float mouseX, mouseY;
    private float bitmapR;//图片的半径

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint pressPaint = new Paint();
    Paint errorPaint = new Paint();

    private Context context = null;
    private Point currentPoint = null;

    public GestureLock(Context context) {
        super(context);
        this.context = context;
    }

    public GestureLock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public GestureLock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        int[] ij;
        int i, j;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetPoints();
                ij = getSelectedPoint();
                if (ij != null) {
                    isDraw = true;
                    i = ij[0];
                    j = ij[1];
                    points[i][j].state = Point.STATE_PRESS;
                    pointList.add(points[i][j]);
                    passList.add(i * 3 + j);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDraw) {
                    ij = getSelectedPoint();
                    if (ij != null) {
                        i = ij[0];
                        j = ij[1];
                        if (!pointList.contains(points[i][j])) {
                            points[i][j].state = Point.STATE_PRESS;
                            pointList.add(points[i][j]);
                            passList.add(i * 3 + j);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean valid = false;
                if (listener != null && isDraw) {
                    valid = listener.OnDrawFinished(passList);
                }
                if (!valid) {
                    for (Point p : pointList) {
                        p.state = Point.STATE_ERROR;
                    }
                }
                isDraw = false;
                currentPoint = null;
                break;
        }
        this.postInvalidate();
        return true;
    }

    /*获得选择的点*/
    private int[] getSelectedPoint() {
        Point pMouse = new Point(mouseX, mouseY);
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                if (points[i][j].distance(pMouse) < bitmapR) {
                    if (currentPoint != points[i][j]) {/*{防止多次调用震动}*/
                        VibratorUtils.getInstance(context).vibrator();
                        currentPoint = points[i][j];
                    }
                    int[] result = new int[2];
                    result[0] = i;
                    result[1] = j;
                    if (pointList.size() > 0) {/*{查找连线中间没有划过的点}*/
                        getMiddlePoint(points[i][j], pointList.get(pointList.size() - 1));
                    }
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * 功能：这里还得判断是不是有别的点在这条直线上并且那个点事没有被加入到pointList中的,这里使用直线方程中的两点式
     *
     * @param point  已经选中但是还没有加入到pointList的点
     * @param point1 pointList中的最后的一个点
     * @return 返回手指没有经过但是在连线上的点
     */
    private void getMiddlePoint(Point point, Point point1) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                if (!pointList.contains(points[i][j]) && point.distance(point1) > points[i][j].distance(point1)) {
                    if ((points[i][j].y - point.y) / (points[i][j].x - point.x) == (point1.y - point.y) / (point1.x - point.x)) {
                        points[i][j].state = Point.STATE_PRESS;
                        pointList.add(points[i][j]);
                        passList.add(i * 3 + j);
                    }
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!inited) {
            init();
        }
        drawPoints(canvas);
        if (pointList.size() > 0) {
            Point a = pointList.get(0);
            for (int i = 1; i < pointList.size(); i++) {
                Point b = pointList.get(i);
                drawLine(canvas, a, b);
                a = b;
            }
            if (isDraw) {
                drawLine(canvas, a, new Point(mouseX, mouseY));
            }
        }
    }

    /*画直线*/
    private void drawLine(Canvas canvas, Point a, Point b) {
        if (a.state == Point.STATE_PRESS) {
            canvas.drawLine(a.x, a.y, b.x, b.y, pressPaint);
        } else if (a.state == Point.STATE_ERROR) {
            canvas.drawLine(a.x, a.y, b.x, b.y, errorPaint);
        }
    }

    /*画点*/
    private void drawPoints(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                if (points[i][j].state == Point.STATE_NORMAL) {
                    //Normal
                    canvas.drawBitmap(bitmapPointNormal, points[i][j].x - bitmapR, points[i][j].y - bitmapR, paint);
                } else if (points[i][j].state == Point.STATE_PRESS) {
                    //Press
                    canvas.drawBitmap(bitmapPointPress, points[i][j].x - bitmapR, points[i][j].y - bitmapR, paint);
                } else {
                    //ERROR
                    canvas.drawBitmap(bitmapPointError, points[i][j].x - bitmapR, points[i][j].y - bitmapR, paint);
                }
            }
        }
    }

    /*初始化9个点的位置*/
    private void init() {
        pressPaint.setColor(Color.YELLOW);
        pressPaint.setStrokeWidth(5);
        errorPaint.setColor(Color.RED);
        errorPaint.setStrokeWidth(5);

        bitmapPointError = BitmapFactory.decodeResource(getResources(), R.mipmap.error);
        bitmapPointNormal = BitmapFactory.decodeResource(getResources(), R.mipmap.normal);
        bitmapPointPress = BitmapFactory.decodeResource(getResources(), R.mipmap.press);

        bitmapR = bitmapPointError.getHeight() / 2;
        int width = getWidth();
        int height = getHeight();
        int offset = Math.abs(width - height) / 2;
        int offsetX, offsetY;
        int space;
        if (width > height) {
            space = height / 4;
            offsetX = offset;
            offsetY = 0;
        } else {
            space = width / 4;
            offsetX = 0;
            offsetY = offset;
        }
        points[0][0] = new Point(offsetX + space, offsetY + space);
        points[0][1] = new Point(offsetX + space * 2, offsetY + space);
        points[0][2] = new Point(offsetX + space * 3, offsetY + space);

        points[1][0] = new Point(offsetX + space, offsetY + space * 2);
        points[1][1] = new Point(offsetX + space * 2, offsetY + space * 2);
        points[1][2] = new Point(offsetX + space * 3, offsetY + space * 2);

        points[2][0] = new Point(offsetX + space, offsetY + space * 3);
        points[2][1] = new Point(offsetX + space * 2, offsetY + space * 3);
        points[2][2] = new Point(offsetX + space * 3, offsetY + space * 3);

        inited = true;
    }

    /*重置*/
    public void resetPoints() {
        passList.clear();
        pointList.clear();
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                points[i][j].state = Point.STATE_NORMAL;
            }
        }
        this.postInvalidate();
    }

    /*绘制完成接口*/
    public interface OnDrawFinishedListener {
        boolean OnDrawFinished(List<Integer> passList);
    }

    public void setOnDrawFinishedListener(OnDrawFinishedListener listener) {
        this.listener = listener;
    }
}
