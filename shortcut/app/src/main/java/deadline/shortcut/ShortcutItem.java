package deadline.shortcut;

import android.support.annotation.DrawableRes;

/**
 * @author deadline
 * @time 2017/8/10
 */

public class ShortcutItem {

    public @DrawableRes int icon;

    public CharSequence text;

    public int textColor;

    public int textSize;

    public ShortcutDrawable.Builder builder;
}
