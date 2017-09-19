package helpers;

import android.util.Base64;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by emin on 15.11.2015.
 */
public class BASE64Encoder {

    public static String encode(JSONObject obj){

        byte[] data = new byte[0];
        try {
            data = obj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64json = Base64.encodeToString(data, Base64.DEFAULT);

        return base64json;

    }


}
