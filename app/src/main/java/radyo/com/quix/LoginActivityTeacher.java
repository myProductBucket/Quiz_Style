package radyo.com.quix;

import android.app.Application;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import helpers.BASE64Encoder;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import teacher.TeacherDashboardActivity;
import utils.RestService;
import utils.Utils;

public class LoginActivityTeacher extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    ImageView facebook_btn, google_btn, guest_btn;
    CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mShouldResolve = false;
    private boolean mIntentInProgress;
    public Application app ;
    private RestService api ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_teacher);

        api = MyApp.getApi();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        facebook_btn = (ImageView) findViewById(R.id.facebook_btn);
        google_btn = (ImageView) findViewById(R.id.google_btn);
        guest_btn = (ImageView) findViewById(R.id.guest);

        facebook_btn.setOnClickListener(this);
        google_btn.setOnClickListener(this);
        guest_btn.setOnClickListener(this);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("sonuc", "success ->" + loginResult.toString());
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {

                                        final Bundle bFacebookData = getFacebookData(object);

                                        final String id = object.optString("id");
                                        final String email = bFacebookData.getString("email");
                                        final String fname = object.optString("first_name");
                                        final String lname = object.optString("last_name");
                                        final String name = object.optString("name");


                                        JSONObject obj = new JSONObject();

                                        try {
                                            obj.putOpt("userID", id);
                                            obj.putOpt("userName", fname + " " + lname);
                                            obj.putOpt("userEmail", email);
                                            obj.putOpt("userProfilePicture", "https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        api.login(BASE64Encoder.encode(obj), new Callback<String>() {
                                            @Override
                                            public void success(String teacher, Response response) {
                                                Utils.savePrefs(getApplicationContext(), "userName", fname + " " + lname);
                                                Utils.savePrefs(getApplicationContext(), "userID", id);
                                                Utils.savePrefs(getApplicationContext(), "userProfilePic", bFacebookData.getString("profile_pic"));
                                                Utils.savePrefs(getApplicationContext(), "userEmail", email);
                                                Utils.savePrefs(getApplicationContext(), "isLogged", "true");
                                                Utils.savePrefs(getApplicationContext(), "userAge", bFacebookData.getString("birthday"));
                                                Utils.savePrefs(getApplicationContext(), "userLocation", bFacebookData.getString("location"));


                                                Log.d("sonuc", teacher);
                                                Intent i = new Intent(LoginActivityTeacher.this, TeacherDashboardActivity.class);
                                                startActivity(i);
                                                finish();

                                            }

                                            @Override
                                            public void failure(RetrofitError error) {

                                                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        Intent i = new Intent(LoginActivityTeacher.this, TeacherDashboardActivity.class);
                                        startActivity(i);
                                        finish();

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code

                        Log.d("sonuc", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("sonuc", "error ->" + exception.toString());
                    }
                });

        getKeyHash();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "apptologia.com.rouge",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.optString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.optString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.optString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.optString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.optString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.optJSONObject("location").optString("name"));

            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {

        switch ((v.getId())) {

            case R.id.facebook_btn:

                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

                break;

            case R.id.google_btn:

                // mShouldResolve = true;
                mGoogleApiClient.connect();

                break;

            case R.id.guest:

                JSONObject obj = new JSONObject();
                 final String android_id = Settings.Secure.getString(getContentResolver(),
                         Settings.Secure.ANDROID_ID);
                try {
                    obj.putOpt("userID", android_id);
                    obj.putOpt("userName", "Guest");
                    obj.putOpt("userEmail", "Guest");
                    obj.putOpt("userProfilePicture", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                api.login(BASE64Encoder.encode(obj), new Callback<String>() {
                    @Override
                    public void success(String teacher, Response response) {
                        Utils.savePrefs(getApplicationContext(), "userName", "Guest");
                        Utils.savePrefs(getApplicationContext(), "userID", android_id);
                        Utils.savePrefs(getApplicationContext(), "userProfilePic", "");
                        Utils.savePrefs(getApplicationContext(), "userEmail", "");
                        Utils.savePrefs(getApplicationContext(), "isLogged", "true");
                        Utils.savePrefs(getApplicationContext(), "userAge", "");
                        Utils.savePrefs(getApplicationContext(), "userLocation", "");


                        Log.d("sonuc", teacher);
                        Intent i = new Intent(LoginActivityTeacher.this, TeacherDashboardActivity.class);
                        startActivity(i);
                        finish();

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                break;

        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                final String id = currentPerson.getId();
                final String personName = currentPerson.getDisplayName();
                final String personAge = String.valueOf(currentPerson.getBirthday());
                final String personLocation = String.valueOf(currentPerson.getCurrentLocation());
                final String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                final String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.d("username", personName);


                JSONObject obj = new JSONObject();

                obj.put("userID", id);
                obj.put("userName", personName);
                obj.putOpt("userEmail", email);
                obj.putOpt("userProfilePicture", personPhotoUrl);

                api.login(BASE64Encoder.encode(obj), new Callback<String>() {
                    @Override
                    public void success(String teacher, Response response) {

                        Utils.savePrefs(getApplicationContext(), "userName", personName);
                        Utils.savePrefs(getApplicationContext(), "userAge", personAge);
                        Utils.savePrefs(getApplicationContext(), "userLocation", personLocation);
                        Utils.savePrefs(getApplicationContext(), "userID", id);
                        Utils.savePrefs(getApplicationContext(), "userProfilePic", personPhotoUrl);
                        Utils.savePrefs(getApplicationContext(), "userEmail", email);
                        Utils.savePrefs(getApplicationContext(), "isLogged", "true");

                        Log.d("sonuc", teacher);
                        Intent i = new Intent(LoginActivityTeacher.this, TeacherDashboardActivity.class);
                        startActivity(i);
                        finish();

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });


            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!mIntentInProgress && connectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(connectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

}