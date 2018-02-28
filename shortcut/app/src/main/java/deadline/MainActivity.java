package deadline;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import deadline.shortcut.ShortcutDrawable;
import deadline.shortcut.OnItemClickListener;
import deadline.shortcut.R;
import deadline.shortcut.ShortcutItem;
import deadline.shortcut.ShortcutLayout;
import deadline.shortcut.ShortcutView;

public class MainActivity extends AppCompatActivity {

    AppCompatButton btHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btHello = (AppCompatButton) findViewById(R.id.bt_hello);
        btHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShortCut();
            }
        });
    }

    private void showShortCut() {
        List<ShortcutItem> shortcutItems = new ArrayList<>();

        ShortcutItem imageSearch = new ShortcutItem();
        imageSearch.icon = R.mipmap.ic_shortcut_image_search;
        imageSearch.text = "image search";
        shortcutItems.add(imageSearch);

        ShortcutItem barcodeSearch = new ShortcutItem();
        barcodeSearch.icon = R.mipmap.ic_shortcut_barcode_search;
        barcodeSearch.text = "barcode search";
        shortcutItems.add(barcodeSearch);

        ShortcutDrawable.Builder builder = new ShortcutDrawable.Builder(this);
        builder.setArrowEnable(true);
        builder.setArrowPosition(ShortcutDrawable.ARROW_BOTTOM_LEFT);
        ShortcutItem itemRefSearch = new ShortcutItem();
        itemRefSearch.icon = R.mipmap.ic_shortcut_itemref_search;
        itemRefSearch.text = "name search";
        itemRefSearch.builder = builder;
        shortcutItems.add(itemRefSearch);

        final ShortcutLayout shortcutLayout = (ShortcutLayout) View.inflate(this, R.layout.layout_shortcut_layout, null);
        shortcutLayout.setShortcutItemList(shortcutItems, true);
        final PopupWindow popupWindow = getPopupWindow(shortcutLayout, btHello);


        shortcutLayout.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(ShortcutView shortcutView, int position) {
                shortcutLayout.collapse(150, 50);
                shortcutLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                    }
                }, 500);
            }
        });

        shortcutLayout.expand(150, 50);
    }

    /**
     * 显示shortcut
     * @param shortcutLayout
     * @param targetView
     * @return
     */
    public PopupWindow getPopupWindow(final ShortcutLayout shortcutLayout, View targetView){
        PopupWindow popView = new PopupWindow(this);
        popView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popView.setFocusable(true);
        popView.setOutsideTouchable(true);
        popView.setContentView(shortcutLayout);
        int[] location = new int[2];
        targetView.getLocationInWindow(location);

        // 未展示之前先测量宽高
        shortcutLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popView.showAtLocation(targetView, Gravity.NO_GRAVITY, location[0], location[1] - shortcutLayout.getMeasuredHeight());
        return popView;
    }
}
