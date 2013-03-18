package org.puder.trs80;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

/**
 * http://www.trs-80.com/wordpress/zaps-patches-pokes-tips/internals/#keyboard13
 */
public class Key extends View {

    private String label;
    private int    address;
    private byte   mask;
    private int    size;
    private Paint  paint;
    private byte[] memBuffer;
    private RectF  rect;

    public Key(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Keyboard, 0, 0);
        label = ta.getString(R.styleable.Keyboard_label);
        address = ta.getInteger(R.styleable.Keyboard_address, -1);
        mask = (byte) ta.getInteger(R.styleable.Keyboard_mask, -1);
        size = ta.getInteger(R.styleable.Keyboard_size, 1);
        ta.recycle();
        paint = new Paint();
        memBuffer = TRS80Application.getHardware().getMemoryBuffer();
        rect = new RectF();
        this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    memBuffer[address] |= mask;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    memBuffer[address] &= ~mask;
                }
                return true;
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setAlpha(80);
        paint.setStyle(Style.FILL);
        canvas.drawRoundRect(rect, 10, 10, paint);

        paint.setAlpha(180);
        paint.setColor(Color.GRAY);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRoundRect(rect, 10, 10, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        paint.setStyle(Style.FILL);
        paint.setStrokeWidth(1);
        paint.setTextAlign(Align.CENTER);
        int xPos = (int) (rect.right / 2);
        int yPos = (int) ((rect.bottom / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(label, xPos, yPos, paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        params.setMargins(10, 10, 10, 10);
        this.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 30 * size;
        int height = 30;
        setMeasuredDimension(width | MeasureSpec.EXACTLY, height | MeasureSpec.EXACTLY);
        rect.set(0, 0, width - 1, height - 1);
    }
}
