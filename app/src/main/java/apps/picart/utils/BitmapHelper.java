package apps.picart.utils;

import android.graphics.Bitmap;

/**
 * Created by ilyada on 15.04.2018.
 */

public class BitmapHelper {
    private Bitmap bitmap =null;
    private static final BitmapHelper instance =new BitmapHelper();

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static BitmapHelper getInstance() {
        return instance;
    }
}
