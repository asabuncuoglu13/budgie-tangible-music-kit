package com.alpay.twinmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alpay.twinmusic.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;


public class Utils {

    public static String SP_START_KEY_IS_PRESSED = "startKeyIsPressed";

    public static boolean isCameraAvailable(AppCompatActivity appCompatActivity) {
        PackageManager pm = appCompatActivity.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    public static void addStringToSharedPreferences(AppCompatActivity appCompatActivity, String key, String value) {
        SharedPreferences settings = appCompatActivity.getSharedPreferences("woppydata", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void addIntegerToSharedPreferences(AppCompatActivity appCompatActivity, String key, int value) {
        SharedPreferences settings = appCompatActivity.getSharedPreferences("woppydata", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void addBooleanToSharedPreferences(AppCompatActivity appCompatActivity, String key, boolean value) {
        SharedPreferences settings = appCompatActivity.getSharedPreferences("woppydata", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getStringFromSharedPreferences(AppCompatActivity appCompatActivity, String key) {
        SharedPreferences settings = appCompatActivity.getSharedPreferences("woppydata", 0);
        return settings.getString(key, "").toString();
    }

    public static int getIntegerFromSharedPreferences(AppCompatActivity appCompatActivity, String key) {
        SharedPreferences settings = appCompatActivity.getSharedPreferences("woppydata", 0);
        return settings.getInt(key, 0);
    }

    public static boolean getBooleanFromSharedPreferences(AppCompatActivity appCompatActivity, String key) {
        SharedPreferences settings = appCompatActivity.getSharedPreferences("woppydata", 0);
        return settings.getBoolean(key, false);
    }

    public static int convertToDip(Context ctx, float px) {
        return (int) (px * (ctx.getResources().getDisplayMetrics().density + 0.5f));
    }

    public static int convertToPx(Context ctx, float dp) {
        Resources r = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static int convertPxFromSp(Context ctx, float sp) {
        Resources r = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, r.getDisplayMetrics());
    }

    public static String getStringFromResource(Context context, int resourceId) {
        return context.getResources().getString(resourceId);
    }

    public static boolean isInternetAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static String readInputStreamAsString(InputStream in)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }


    public static void showToast(AppCompatActivity activityCompat, int stringID, int duration) {
        Toast.makeText(activityCompat, stringID, duration).show();
    }
}
