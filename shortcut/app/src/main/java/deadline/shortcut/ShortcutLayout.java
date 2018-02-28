package deadline.shortcut;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * @author deadline
 * @time 2017/8/10
 */

public class ShortcutLayout extends LinearLayout{

    private List<ShortcutItem> shortcutItems;

    private OnItemClickListener onItemClickListener;

    private boolean isExpanded;

    private int mWidth;

    public ShortcutLayout(Context context) {
        this(context, null);
    }

    public ShortcutLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShortcutLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        setOrientation(VERTICAL);
        shortcutItems = new ArrayList<>();
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mWidth = getMeasuredWidth();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    /**
     * set item list
     * @param shortcutItemList
     * @param alignLeft
     *          if true icon align left
     */
    public void setShortcutItemList(List<ShortcutItem> shortcutItemList, boolean alignLeft){
        shortcutItems.clear();
        if(shortcutItemList != null){
            shortcutItems.addAll(shortcutItemList);
        }

        removeAllViews();

        ShortcutItem shortcutItem = null;
        for (int i = 0; i < shortcutItems.size(); i++) {
            shortcutItem = shortcutItems.get(i);
            if(shortcutItem != null){
                addShortcutView(i, shortcutItem, alignLeft);
            }
        }
    }

    private void addShortcutView(final int position, ShortcutItem shortcutItem, boolean alignLeft) {
        final ShortcutView shortcutView = new ShortcutView(getContext(), alignLeft);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(mWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        shortcutView.setShortcutItem(shortcutItem);
        shortcutView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(shortcutView, position);
                }
            }
        });
        addView(shortcutView, params);
    }

    /**
     * expand with animation
     * @param itemAnimationTime
     * @param itemAnimationGap
     */
    public void expand(int itemAnimationTime, int itemAnimationGap){
        for (int i = 0; i < getChildCount(); i++) {
            if(getChildAt(i) instanceof ShortcutView){
                ((ShortcutView) getChildAt(i)).expand(true, itemAnimationTime, (getChildCount() - i) * itemAnimationGap);
            }
        }
        isExpanded = true;
    }

    /**
     * collapse with animation
     * @param itemAnimationTime
     * @param itemAnimationGap
     */
    public void collapse(int itemAnimationTime, int itemAnimationGap){
        for (int i = 0; i < getChildCount(); i++) {
            if(getChildAt(i) instanceof ShortcutView){
                ((ShortcutView) getChildAt(i)).collapse(true, itemAnimationTime, i * itemAnimationGap);
            }
        }

        isExpanded = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }
}
