package com.example.test_Pianifica_Itinerario.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.example.test_Pianifica_Itinerario.R;

public class Utils {
/*
    public BitmapDrawable writeOnDrawable(Context context, int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 0, bm.getHeight()/2, paint);

        return new BitmapDrawable(context.getResources(), bm);
    }
*/

    public static BitmapDrawable getMarker(Context context, int drawableId, String text){

        //Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
        //Bitmap bm = drawableToBitmap(drawable);

        Bitmap b = BitmapFactory.decodeResource(context.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Bitmap bm = Bitmap.createScaledBitmap(b,80,80,false);

        if(text != null && text != "") {

            Paint textPaint = new Paint();
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(36);

            Canvas canvas = new Canvas(bm);

            canvas.drawText(text, canvas.getWidth() / 2, canvas.getHeight() / 2, textPaint);
        }


        return new BitmapDrawable(context.getResources(), bm);
    }




    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
