package deadline.shortcut;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author deadline
 * @time 2017/8/10
 */

public class ShortcutDrawable extends Drawable {

    public static final int ARROW_BOTTOM_LEFT = 0;
    public static final int ARROW_BOTTOM_CENTER = 1;
    public static final int ARROW_BOTTOM_RIGHT = 2;
    public static final int ARROW_TOP_LEFT = 3;
    public static final int ARROW_TOP_CENTER = 4;
    public static final int ARROW_TOP_RIGHT = 5;

    @IntDef({
            ARROW_BOTTOM_LEFT,
            ARROW_BOTTOM_CENTER,
            ARROW_BOTTOM_RIGHT,
            ARROW_TOP_LEFT,
            ARROW_TOP_CENTER,
            ARROW_TOP_RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ArrowPosition{

    }

    private Paint mPaint;
    private Path mPath;

    private int color;

    /**
     * 显示与按压颜色
     */
    private int normalColor;
    private int pressedColor;

    /**
     * 是否使能阴影以及阴影高度
     */
    private boolean shadowEnable;
    private int shadowHeight;

    /**
     * 箭头方向，仅仅定义常用的几个问题
     * 箭头高度，箭头宽度，箭头偏移量（调整箭头角度）
     * 以及是否使能箭头
     */
    private @ArrowPosition
    int arrowPosition;
    private int arrowHeight;
    private int arrowWidth;
    private int arrowOffset;
    private boolean arrowEnable;

    private RectF rectF;

    private float tempArrowHeight;
    private float tempArrowWidth;
    private ValueAnimator animator;

    private Xfermode xfermode;

    private ShortcutDrawable(Builder build){

        mPath = new Path();
        rectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //不相交的地方绘制，相交的地方不绘制, 为了去除箭头的部分阴影
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);

        color = normalColor = build.normalColor;
        pressedColor = build.pressedColor;

        shadowEnable = build.shadowEnable;
        shadowHeight = build.shadowHeight;

        arrowEnable = build.arrowEnable;
        arrowPosition = build.arrowPosition;
        arrowHeight = build.arrowHeight;
        arrowWidth = build.arrowWidth;
        arrowOffset = build.arrowOffset;
        tempArrowHeight = 0;
        tempArrowWidth = 0;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        float radius = rectF.height() / 2;

        //shadow
        if(shadowEnable) {
            mPaint.setShadowLayer(8, shadowHeight / 2, shadowHeight / 2, 0x20000000);
        }
        mPaint.setColor(color);

        mPaint.setXfermode(null);
        canvas.drawRoundRect(rectF, radius, radius, mPaint);

        mPaint.setXfermode(xfermode);
        if(arrowEnable){
            drawArrow(radius, canvas);
        }
    }

    private void drawArrow(float radius, Canvas canvas){
        mPath.reset();
        float startX = 0;
        switch (arrowPosition){
            case ARROW_BOTTOM_LEFT:
                startX = rectF.left + radius;
                mPath.moveTo(startX, rectF.bottom);
                mPath.lineTo(startX + tempArrowWidth / 2 + arrowOffset, rectF.bottom + tempArrowHeight);
                mPath.lineTo(startX + tempArrowWidth, rectF.bottom);
                break;

            case ARROW_BOTTOM_CENTER:
                startX = rectF.width() / 2 - tempArrowWidth / 2;
                mPath.moveTo(startX, rectF.bottom);
                mPath.lineTo(rectF.width() / 2 + arrowOffset, rectF.bottom + tempArrowHeight);
                mPath.lineTo(startX + tempArrowWidth, rectF.bottom);
                break;

            case ARROW_BOTTOM_RIGHT:
                startX = rectF.right - radius - tempArrowWidth;
                mPath.moveTo(startX, rectF.bottom);
                mPath.lineTo(startX + tempArrowWidth / 2 + arrowOffset, rectF.bottom + tempArrowHeight);
                mPath.lineTo(rectF.right - radius, rectF.bottom);
                break;

            case ARROW_TOP_LEFT:
                startX = rectF.left + radius;
                mPath.moveTo(startX, rectF.top);
                mPath.lineTo(startX + tempArrowWidth / 2 + arrowOffset, rectF.top - tempArrowHeight);
                mPath.lineTo(startX + tempArrowWidth, rectF.top);
                break;

            case ARROW_TOP_CENTER:
                startX = rectF.width() / 2 - tempArrowWidth / 2;
                mPath.moveTo(startX, rectF.top);
                mPath.lineTo(rectF.width() / 2 + arrowOffset, rectF.top - tempArrowHeight);
                mPath.lineTo(startX + tempArrowWidth, rectF.top);
                break;

            case ARROW_TOP_RIGHT:
                startX = rectF.right - radius - tempArrowWidth;
                mPath.moveTo(startX, rectF.top);
                mPath.lineTo(startX + tempArrowWidth / 2 + arrowOffset, rectF.top - tempArrowHeight);
                mPath.lineTo(rectF.right - radius, rectF.top);
                break;

            default:
                break;

        }
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    public void animationArrow(boolean show, int animationTime, int delayTime){

        if(!arrowEnable
                || (show && tempArrowHeight == arrowHeight)
                || (!show && tempArrowHeight == 0)){
            return;
        }

        if(animator != null){
            animator.removeAllListeners();
            animator = null;
        }

        animator = ValueAnimator.ofFloat(show ? 0 : 1, show ? 1 : 0);
        animator.setDuration(animationTime);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tempArrowWidth = arrowWidth * (float) animation.getAnimatedValue();
                tempArrowHeight = arrowHeight * (float) animation.getAnimatedValue();
                invalidateSelf();
            }
        });

        animator.setStartDelay(delayTime);
        animator.start();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean validate = false;
        boolean pressedContains = false;
        for (int i = 0; i < state.length; i++) {
            if (state[i] == android.R.attr.state_pressed) {
                pressedContains = true;
                break;
            }
        }
        final int newColor = pressedContains ? pressedColor : normalColor;
        if (newColor != color) {
            validate = true;
            color = newColor;
        }
        if (validate) {
            invalidateSelf();
        }
        return validate;
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        invalidateAfterLayout(bounds);
    }


    public void invalidateAfterLayout(Rect bounds){

        int shadowTemp = shadowEnable ? shadowHeight : 0;
        int arrowTemp = arrowEnable ? arrowHeight : 0;

        rectF.left = bounds.left + shadowTemp;
        rectF.top = bounds.top + shadowTemp;
        rectF.right = bounds.right - shadowTemp;
        rectF.bottom = bounds.bottom - shadowTemp;

        if (arrowPosition == ARROW_BOTTOM_CENTER
                || arrowPosition == ARROW_BOTTOM_LEFT
                || arrowPosition == ARROW_BOTTOM_RIGHT){

            rectF.bottom = rectF.bottom - arrowTemp;

        }else if(arrowPosition == ARROW_TOP_LEFT
                || arrowPosition == ARROW_TOP_CENTER
                || arrowPosition == ARROW_TOP_RIGHT){

            rectF.top = rectF.top + arrowTemp;
        }

        invalidateSelf();
    }

    @Override
    public int getIntrinsicHeight() {
        return (int)rectF.height();
    }

    @Override
    public int getIntrinsicWidth() {
        return (int)rectF.width();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public int getNormalColor() {
        return normalColor;
    }

    public int getPressedColor() {
        return pressedColor;
    }

    public boolean isShadowEnable() {
        return shadowEnable;
    }

    public int getShadowHeight() {
        return shadowHeight;
    }

    public int getArrowPosition() {
        return arrowPosition;
    }

    public int getArrowHeight() {
        return arrowHeight;
    }

    public int getArrowWidth() {
        return arrowWidth;
    }

    public int getArrowOffset() {
        return arrowOffset;
    }

    public boolean isArrowEnable() {
        return arrowEnable;
    }

    public static class Builder {

        private int normalColor;
        private int pressedColor;

        private boolean shadowEnable;
        private int shadowHeight;

        private @ArrowPosition
        int arrowPosition;
        private int arrowHeight;
        private int arrowWidth;
        private int arrowOffset;
        private boolean arrowEnable;

        public Builder(Context context){
            normalColor = Color.WHITE;
            pressedColor = Color.LTGRAY;

            arrowEnable = false;
            shadowEnable = true;

            shadowHeight = dip2px(context, 3);
            arrowHeight = dip2px(context, 8);
            arrowWidth = dip2px(context, 8);
            arrowOffset = 0;
        }

        private int dip2px(Context c, float dpValue) {
            final float scale = c.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        public ShortcutDrawable.Builder setNormalColor(int normalColor) {
            this.normalColor = normalColor;
            return this;
        }

        public ShortcutDrawable.Builder setPressedColor(int pressedColor) {
            this.pressedColor = pressedColor;
            return this;
        }

        public ShortcutDrawable.Builder setShadowEnable(boolean shadowEnable) {
            this.shadowEnable = shadowEnable;
            return this;
        }

        public ShortcutDrawable.Builder setShadowHeight(int shadowHeight) {
            this.shadowHeight = shadowHeight;
            return this;
        }

        public ShortcutDrawable.Builder setArrowPosition(@ArrowPosition int arrowPosition) {
            this.arrowPosition = arrowPosition;
            return this;
        }

        public ShortcutDrawable.Builder setArrowHeight(int arrowHeight) {
            this.arrowHeight = arrowHeight;
            return this;
        }

        public ShortcutDrawable.Builder setArrowWidth(int arrowWidth) {
            this.arrowWidth = arrowWidth;
            return this;
        }

        public ShortcutDrawable.Builder setArrowOffset(int arrowOffset) {
            this.arrowOffset = arrowOffset;
            return this;
        }

        public ShortcutDrawable.Builder setArrowEnable(boolean arrowEnable) {
            this.arrowEnable = arrowEnable;
            return this;
        }

        public ShortcutDrawable build(){
            return new ShortcutDrawable(this);
        }
    }
}
