package com.nextep.pelmel.helpers;

import android.content.Context;
import android.graphics.Typeface;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
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
}