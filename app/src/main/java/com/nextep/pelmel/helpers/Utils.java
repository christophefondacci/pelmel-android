package com.nextep.pelmel.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout.LayoutParams;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static final String FONT_OPENSANS= "OpenSans-Regular.ttf";
    private static final Map<String, Typeface> fontsMap = new HashMap<>();

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static Typeface getFont(String fontName, Context context) {
        Typeface font = fontsMap.get(fontName);
        if(font == null) {
            font = Typeface.createFromAsset(context.getResources().getAssets(),fontName);
            fontsMap.put(fontName,font);
        }
        return font;
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if(context != null) {
            final WindowManager mgr = ((Activity) context).getWindowManager();
            if (mgr != null && mgr.getDefaultDisplay() != null) {
                mgr.getDefaultDisplay().getMetrics(displayMetrics);

                view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
                view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
                view.buildDrawingCache();
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static int getColor(int colorRes) {
        return PelMelApplication.getInstance().getResources().getColor(colorRes);
    }

    public static float getDimension(int dimen) {
        return PelMelApplication.getInstance().getResources().getDimension(dimen);
    }
    public static <T extends CalObject> List<CalObject> sortCalObjectsForDisplay(final List<T> objects) {
        final List<CalObject> sortedObjects = new ArrayList<>();
        sortedObjects.addAll(objects);
        Collections.sort(sortedObjects, new Comparator<CalObject>() {
            @Override
            public int compare(CalObject lhs, CalObject rhs) {
                if (lhs.getThumb()!= null && rhs.getThumb()== null) {
                    return -1;
                } else if (rhs.getThumb()!= null && lhs.getThumb() == null) {
                    return 1;
                } else if(lhs instanceof User && rhs instanceof User) {
                    final User lu = (User)lhs;
                    final User ru = (User)rhs;

                    if (lu.isOnline() && !ru.isOnline()) {
                        return -1;
                    } else if (lu.isOnline() && ru.isOnline()) {
                        return 1;
                    }
                }
                return objects.indexOf(lhs)- objects.indexOf(rhs);
            }
        });
        return sortedObjects;
    }
}