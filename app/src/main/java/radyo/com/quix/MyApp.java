package radyo.com.quix;

import android.app.Application;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import utils.LenientGsonConverter;
import utils.RestService;

/**
 * Created by emin on 15.11.2015.
 */
public class MyApp extends Application {

    public static RestService api;
    public static RestAdapter restAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5000,
                TimeUnit.MILLISECONDS);
        client.setReadTimeout(5000,
                TimeUnit.MILLISECONDS);
        client.setWriteTimeout(5000,
                TimeUnit.MILLISECONDS);

         restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.api_root))
                .setConverter(new LenientGsonConverter(new Gson()))
                .setClient(new OkClient(client))
                .build();

    }

    public static RestService getApi(){

        api = restAdapter.create(RestService.class);
        return api;
    }
}
