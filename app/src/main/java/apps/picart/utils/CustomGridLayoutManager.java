package apps.picart.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by ilyada on 18.05.2018.
 */
public class CustomGridLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}