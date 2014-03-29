package com.speed.rfidaccessreader.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Window;

import com.speed.rfidaccessreader.R;

public class GuiUtils {
	
	public final static int ROBOTO_BOLD = 0;
	public final static int ROBOTO_LIGHT = 1;
	public final static int ROBOTO_REGULAR = 2;
	public final static int ROBOTO_THIN = 3;
	public final static int ROBOTO_MEDIUM = 4;
	public final static String STANDARD_DATE_FORMAT = "dd MM yyyy";
	public static float sPixelDensity = -1f;
	
	/**
	 * Operation for show a dialog
	 * @param context
	 * @param cancelable
	 * @param text
	 */
	public static void showAcceptDialog (Context context, boolean cancelable, CharSequence text) {
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setCancelable(cancelable); 
		dialogBuilder.setMessage(text);
		
		Dialog dialog = dialogBuilder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}
	
	/**
	 * Operation for show a dialog
	 * @param context
	 * @param cancelable
	 * @param text
	 */
	public static void showAcceptDialog (Context context, boolean cancelable, CharSequence text, CharSequence title) {
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setCancelable(cancelable); 
		dialogBuilder.setMessage(text);
		dialogBuilder.setTitle(title);
		dialogBuilder.setPositiveButton(context.getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		
		Dialog dialog = dialogBuilder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}
	
	/**
	 * Operation which get fonts from assets
	 * @param font
	 * @return
	 */
	public static Typeface getFont (int font, Context context) {
		switch (font) {
		case 0:
			return Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
		case 1:
			return Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
		case 2:
			return Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
		case 3:
			return Typeface.createFromAsset(context.getAssets(), "Roboto-Thin.ttf");
		default:
			return Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
		}
	}
	

	public synchronized static float dpToPixel(Context context, float dp) { 

        if (sPixelDensity < 0) { 
            DisplayMetrics metrics = new DisplayMetrics(); 
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics); 
            sPixelDensity = metrics.density; 
        } 

        return sPixelDensity * dp; 

    } 
}