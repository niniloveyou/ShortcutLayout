package deadline.shortcut;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author deadline
 * @time 2017/8/7
 */

public class ShortcutView extends RelativeLayout {


    private static final float MIN_ALPHA = .3f;

    private ImageView ivIcon;
    private TextView tvTitle;

    private int collapseWidth;
    private int expandWidth;
    private boolean alignLeft;
    private boolean isExpanded;
    private boolean isAnimationRunning;

    private ShortcutDrawable arrowDrawable;
    private ValueAnimator valueAnimator;

    public ShortcutView(Context context, boolean alignLeft) {
        this(context, null, alignLeft);
    }

    public ShortcutView(Context context, AttributeSet attrs, boolean alignLeft) {
        this(context, attrs, 0, alignLeft);
    }

    public ShortcutView(Context context, AttributeSet attrs, int defStyleAttr, boolean alignLeft) {
        super(context, attrs, defStyleAttr);
        this.alignLeft = alignLeft;
        setup();
    }

    private void setup() {
        View view = View.inflate(getContext(), alignLeft ? R.layout.widget_shortcut_align_left : R.layout.widget_shortcut_align_right, this);

        ivIcon = (ImageView) view.findViewById(R.id.item_icon);
        tvTitle = (TextView) view.findViewById(R.id.item_text);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        LinearLayout parent = (LinearLayout) getParent();
                        expandWidth = parent.getMeasuredWidth();
                        adjustSelf();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    void expand(boolean animEnable, int animationTime, int delayTime) {
        animationSelf(true, false, animationTime, delayTime);
    }

    void collapse(boolean animEnable, int animationTime, int delayTime) {
        animationSelf(false, true, animationTime, delayTime);
    }

    private void animationSelf(final boolean expand, final boolean withAlpha, int animationTime, int delayTime) {

        if (isAnimationRunning) {
            return;
        }

        isExpanded = expand;

        stopAnimation();

        valueAnimator = ValueAnimator.ofFloat(expand ? 0 : 1, expand ? 1 : 0);
        valueAnimator.setDuration(animationTime);
        Interpolator interpolator = null;
        if (expand) {
            interpolator = new AccelerateDecelerateInterpolator();
        } else {
            interpolator = new AccelerateInterpolator();
        }
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
                params.width = collapseWidth + (int) ((expandWidth - collapseWidth) * (float) animation.getAnimatedValue());
                setLayoutParams(params);

                if (withAlpha) {
                    setAlpha(expand ? MIN_ALPHA + (float) animation.getAnimatedValue() : (float) animation.getAnimatedValue());
                }
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationRunning = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isAnimationRunning = true;
            }
        });
        valueAnimator.setStartDelay(delayTime);
        valueAnimator.start();
        if (expand && arrowDrawable != null) {
            arrowDrawable.animationArrow(true, animationTime / 2, delayTime);
        } else if (!expand && arrowDrawable != null) {
            arrowDrawable.animationArrow(false, animationTime / 2, delayTime);
        }
    }

    private void stopAnimation() {

        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            valueAnimator = null;
        }
    }

    /**
     * update item information
     * @param shortcutItem
     */
    void setShortcutItem(ShortcutItem shortcutItem) {
        if (shortcutItem != null) {
            ivIcon.setImageResource(shortcutItem.icon);
            tvTitle.setText(shortcutItem.text);
            if (shortcutItem.textColor != 0) {
                tvTitle.setTextColor(shortcutItem.textColor);
            }
            if (shortcutItem.textSize != 0) {
                tvTitle.setTextSize(shortcutItem.textSize);
            }

            if (shortcutItem.builder != null) {
                arrowDrawable = shortcutItem.builder.build();
            } else {
                arrowDrawable = new ShortcutDrawable.Builder(getContext()).build();
            }
            adjustSelf();
        }
    }

    /**
     * 根据设置箭头调整自身宽高
     */
    private void adjustSelf() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        if (params == null) {
            return;
        }

        int arrow = arrowDrawable.isArrowEnable() ? arrowDrawable.getArrowHeight() : 0;

        params.height = getMeasuredHeight() + arrow;
        params.width = getMeasuredHeight();
        collapseWidth = params.width;

        if (arrowDrawable.isArrowEnable()) {
            if (arrowDrawable.getArrowPosition() == ShortcutDrawable.ARROW_BOTTOM_CENTER
                    || arrowDrawable.getArrowPosition() == ShortcutDrawable.ARROW_BOTTOM_LEFT
                    || arrowDrawable.getArrowPosition() == ShortcutDrawable.ARROW_BOTTOM_RIGHT) {

                params.bottomMargin = arrowDrawable.getArrowHeight();

            } else if (arrowDrawable.getArrowPosition() == ShortcutDrawable.ARROW_TOP_LEFT
                    || arrowDrawable.getArrowPosition() == ShortcutDrawable.ARROW_TOP_CENTER
                    || arrowDrawable.getArrowPosition() == ShortcutDrawable.ARROW_TOP_RIGHT) {

                params.topMargin = - arrowDrawable.getArrowHeight();
                setPadding(getPaddingLeft(), getTop() + arrow, getPaddingRight(), getPaddingBottom());
            }
        } else {
            params.topMargin = 0;
            params.bottomMargin = 0;
        }
        ViewCompat.setBackground(this, arrowDrawable);
        setLayoutParams(params);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }
}
