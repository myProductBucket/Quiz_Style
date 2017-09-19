package utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by emin on 29.3.2015.
 */
public class Utils {
    private static SharedPreferences prefs ;

    public static void savePrefs(Context ctx , String key, String value){


        prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static String getPrefValue(Context ctx , String key, String defValue){

        prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        return prefs.getString(key, defValue);

    }

    public static void clearPrefs(Context ctx ){
        prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();


    }

    public static void isFullScreenApp(Activity ctx, Boolean state){

        if(state){

            ctx.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ctx.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {


        }

    }

    public static boolean haveConnection(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




}
