package teacher.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import adapters.QuestionAdapter;
import helpers.BASE64Encoder;
import models.QuestionApi;
import models.QuizApi;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import utils.RestService;
import utils.Utils;

/**
 * Created by emin on 20.6.2015.
 */
public class AddQuestionFragment extends Fragment  {

    TextView name_tv, quiz_time ;
    RecyclerView mRecyclerView;
    LinearLayoutManager layoutManager ;
    RelativeLayout add_question_btn;
    JSONObject jsonObj ;
    int type = -1 ;
    String attachment = "";

    //for image upload
    static String videoPath ;
    int serverResponseCode = 0;
    private ProgressDialog pd = null;
    private String upLoadServerUri = null;
    private String imagepath="";
    static String teacherID;
    static int quizID;
    public RestService api ;
    public FloatingActionButton fab ;
    QuizApi quiz ;
    RelativeLayout progressBar, noresult ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addquestion, container, false);

        api = MyApp.getApi();

        try {
            jsonObj = new JSONObject(getArguments().getString("json", ""));
            Utils.savePrefs(getActivity(),"last_quiz_json",getArguments().getString("json", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        name_tv = (TextView)rootView.findViewById(R.id.name_tv);
        quiz_time = (TextView)rootView.findViewById(R.id.quiz_count_tv);
        progressBar = (RelativeLayout)rootView.findViewById(R.id.progressbar);
        noresult = (RelativeLayout)rootView.findViewById(R.id.noresult);
        fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setColorNormalResId(R.color.primary);
        fab.setColorPressedResId(R.color.primary_pressed);
        fab.setColorRippleResId(R.color.primary_pressed);

        teacherID = Utils.getPrefValue(getActivity(), "userID", "0");

        add_question_btn = (RelativeLayout)rootView.findViewById(R.id.add_question_btn);

        name_tv.setText(jsonObj.optString("subject"));
        quiz_time.setText(String.valueOf(jsonObj.optInt("time")) + ":00 min");
        progressBar = (RelativeLayout)rootView.findViewById(R.id.progressbar);
        Gson gson = new Gson();

        quiz = gson.fromJson(jsonObj.toString(), QuizApi.class);

        type = Integer.valueOf(quiz.type);
        quizID = quiz.getId();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.itemsRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        fab.attachToRecyclerView(mRecyclerView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fab.hide();
            }

            @Override
            public void onScrollUp() {
                fab.show();
            }
        });

        add_question_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddQuestionDialog();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCopyClipboardDialog();
            }
        });
        fetchData();

        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public void fetchData(){

        api.getQuestions(String.valueOf(quiz.getId()), new Callback<ArrayList<QuestionApi>>() {
            @Override
            public void success(ArrayList<QuestionApi> questionApis, retrofit.client.Response response) {
                Log.d("sonuc", String.valueOf(questionApis));
                QuestionAdapter adapter = new QuestionAdapter(getActivity(), questionApis);
                mRecyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                noresult.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("hata", error.getLocalizedMessage());
                noresult.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void showAddQuestionDialog(){

        attachment = "";
        imagepath = "";

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_question);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView dismiss = (ImageView)dialog.findViewById(R.id.cancel);
        RelativeLayout save = (RelativeLayout)dialog.findViewById(R.id.add_quiz_btn);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final ArrayList<LinearLayout> match = new ArrayList<>();
        final ArrayList<LinearLayout> check_cont = new ArrayList<>();
        final ArrayList<LinearLayout> question_cont = new ArrayList<>();
        final ArrayList<LinearLayout> feedback_cont = new ArrayList<>();

        final ArrayList<EditText> match_array = new ArrayList<>();
        final ArrayList<CheckBox> check_array = new ArrayList<>();
        final ArrayList<EditText> question_array = new ArrayList<>();
        final ArrayList<EditText> feedback_array = new ArrayList<>();

        final EditText  question, genel_feedback, a_text_et, b_text_et, c_text_et, d_text_et, a_feedback_et, b_feedback_et, c_feedback_et, d_feedback_et, a_match_et, b_match_et, c_match_et, d_match_et ;
        CheckBox a_chk, b_chk, c_chk, d_chk;
        RelativeLayout save_btn, add_image_btn ;
        ImageView cancel_iv ;
        TextView instruction ;
        LinearLayout a_cont, b_cont, c_cont, d_cont, a_m_cont, b_m_cont,c_m_cont, d_m_cont, a_c_cont,b_c_cont,c_c_cont,d_c_cont,a_f_cont,b_f_cont,c_f_cont,d_f_cont ;

        cancel_iv = (ImageView)dialog.findViewById(R.id.cancel);

        cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save_btn = (RelativeLayout)dialog.findViewById(R.id.add_quiz_btn);
        add_image_btn = (RelativeLayout)dialog.findViewById(R.id.add_attachment_btn);

        instruction = (TextView)dialog.findViewById(R.id.instruction);

        instruction.setText(getResources().getStringArray(R.array.question_instructions)[type]);

        a_text_et = (EditText)dialog.findViewById(R.id.choice_a_et);
        a_feedback_et = (EditText)dialog.findViewById(R.id.feedback_a_et);
        a_match_et = (EditText)dialog.findViewById(R.id.match_a_et);
        a_chk = (CheckBox)dialog.findViewById(R.id.a_check);
        a_cont = (LinearLayout)dialog.findViewById(R.id.a_container);
        a_m_cont = (LinearLayout)dialog.findViewById(R.id.a_match_container);
        a_c_cont = (LinearLayout)dialog.findViewById(R.id.a_check_container);
        a_f_cont = ( LinearLayout)dialog.findViewById(R.id.a_feedback_container);

        b_text_et = (EditText)dialog.findViewById(R.id.choice_b_et);
        b_feedback_et = (EditText)dialog.findViewById(R.id.feedback_b_et);
        b_match_et = (EditText)dialog.findViewById(R.id.match_b_et);
        b_chk = (CheckBox)dialog.findViewById(R.id.b_check);
        b_cont = (LinearLayout)dialog.findViewById(R.id.b_container);
        b_m_cont = (LinearLayout)dialog.findViewById(R.id.b_match_container);
        b_c_cont = (LinearLayout)dialog.findViewById(R.id.b_check_container);
        b_f_cont = ( LinearLayout)dialog.findViewById(R.id.b_feedback_container);

        c_text_et = (EditText)dialog.findViewById(R.id.choice_c_et);
        c_feedback_et = (EditText)dialog.findViewById(R.id.feedback_c_et);
        c_match_et = (EditText)dialog.findViewById(R.id.match_c_et);
        c_chk = (CheckBox)dialog.findViewById(R.id.c_check);
        c_cont = (LinearLayout)dialog.findViewById(R.id.c_container);
        c_m_cont = (LinearLayout)dialog.findViewById(R.id.c_match_container);
        c_c_cont = (LinearLayout)dialog.findViewById(R.id.c_check_container);
        c_f_cont = ( LinearLayout)dialog.findViewById(R.id.c_feedback_container);

        d_text_et = (EditText)dialog.findViewById(R.id.choice_d_et);
        d_feedback_et = (EditText)dialog.findViewById(R.id.feedback_d_et);
        d_match_et = (EditText)dialog.findViewById(R.id.match_d_et);
        d_chk = (CheckBox)dialog.findViewById(R.id.d_check);
        d_cont = (LinearLayout)dialog.findViewById(R.id.d_container);
        d_m_cont = (LinearLayout)dialog.findViewById(R.id.d_match_container);
        d_c_cont = (LinearLayout)dialog.findViewById(R.id.d_check_container);
        d_f_cont = ( LinearLayout)dialog.findViewById(R.id.d_feedback_container);

        question = (EditText)dialog.findViewById(R.id.name_et);
        genel_feedback = (EditText)dialog.findViewById(R.id.time_et);

        question_cont.add(a_cont);
        question_cont.add(b_cont);
        question_cont.add(c_cont);
        question_cont.add(d_cont);

        match.add(a_m_cont);
        match.add(b_m_cont);
        match.add(c_m_cont);
        match.add(d_m_cont);

        check_cont.add(a_c_cont);
        check_cont.add(b_c_cont);
        check_cont.add(c_c_cont);
        check_cont.add(d_c_cont);

        feedback_cont.add(a_f_cont);
        feedback_cont.add(b_f_cont);
        feedback_cont.add(c_f_cont);
        feedback_cont.add(d_f_cont);

        question_array.add(a_text_et);
        question_array.add(b_text_et);
        question_array.add(c_text_et);
        question_array.add(d_text_et);

        match_array.add(a_match_et);
        match_array.add(b_match_et);
        match_array.add(c_match_et);
        match_array.add(d_match_et);

        check_array.add(a_chk);
        check_array.add(b_chk);
        check_array.add(c_chk);
        check_array.add(d_chk);

        feedback_array.add(a_feedback_et);
        feedback_array.add(b_feedback_et);
        feedback_array.add(c_feedback_et);
        feedback_array.add(d_feedback_et);

        switch(type){

            case 0:

                for(int i = 0 ; i<4 ; i++){

                    match.get(i).setVisibility(View.GONE);

                }

                break;

            case 1:

                for(int i = 0 ; i<4 ; i++){

                    match.get(i).setVisibility(View.GONE);
                    feedback_cont.get(i).setVisibility(View.GONE);
                    add_image_btn.setVisibility(View.GONE);

                }

                break;

            case 2:

                for(int i = 0 ; i<4 ; i++){

                    match.get(i).setVisibility(View.GONE);
                    feedback_cont.get(i).setVisibility(View.GONE);
                    check_cont.get(i).setVisibility(View.GONE);
                    add_image_btn.setVisibility(View.GONE);

                }

                break;

            case 3:

                for(int i = 0 ; i<4 ; i++){

                    match.get(i).setVisibility(View.GONE);
                    check_cont.get(i).setVisibility(View.GONE);
                    feedback_cont.get(i).setVisibility(View.GONE);
                    add_image_btn.setVisibility(View.GONE);

                }

                break;

            case 4:

                for(int i = 0 ; i<4 ; i++){

                    check_cont.get(i).setVisibility(View.GONE);
                    feedback_cont.get(i).setVisibility(View.GONE);
                    add_image_btn.setVisibility(View.GONE);

                }

                break;

            case 5:

                for(int i = 0 ; i<4 ; i++){

                    question_cont.get(i).setVisibility(View.GONE);
                    add_image_btn.setVisibility(View.GONE);

                }

                break;


        }

        add_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 50);
                        } else if (items[item].equals("Choose from Library")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select File"),
                                    100);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int questioncount = 0;
                int matchcount = 0;
                int checkcount = 0;

                if(question.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getString(R.string.question_empty_error),Toast.LENGTH_LONG).show();
                    return  ;}

                for(int i=0;i<4;i++){

                    if(!question_array.get(i).getText().toString().equals(""))
                       questioncount++;

                    if(!question_array.get(i).getText().toString().equals("")) {
                        if (!match_array.get(i).getText().toString().equals(""))
                            matchcount++;
                    }

                    if(!question_array.get(i).getText().toString().equals("")) {
                        if (check_array.get(i).isChecked())
                            checkcount++;
                    }
                }

                Log.d("degerlendir","sonuc -> type = " + String.valueOf(type) + " questioncount =" + String.valueOf(questioncount) +  " matchcount =" + String.valueOf(matchcount) +  " checkcount =" + String.valueOf(checkcount) );

                if(questioncount<2&&type!=5&&type!=2){
                    Toast.makeText(getActivity(), getString(R.string.atleast_two_choice_error),Toast.LENGTH_LONG).show();
                    return  ;}

                if(type == 0&&checkcount>1)
                {
                    Toast.makeText(getActivity(), getString(R.string.question_onecorrect_error),Toast.LENGTH_LONG).show();
                    return  ;
                }

                if((type == 4)&&(matchcount!=questioncount))
                {
                    Toast.makeText(getActivity(), getString(R.string.question_match_empty_error),Toast.LENGTH_LONG).show();
                    return  ;
                }


                if((type == 0||type==1)&&checkcount==0)
                {
                    Toast.makeText(getActivity(), getString(R.string.question_onecorrect_atleast_error),Toast.LENGTH_LONG).show();
                    return  ;
                }




                JSONObject obj = new JSONObject();

                try {
                    obj.put("text",question.getText().toString());
                    obj.put("feedback",genel_feedback.getText().toString());
                    obj.put("attachment","");
                    obj.put("quiz_id",quiz.getId());

                    JSONArray array = new JSONArray();

                    for(int i=0;i<4;i++){
                        JSONObject objTemp = new JSONObject();
                        if(!question_array.get(i).getText().toString().equals("")){

                            objTemp.put("text",question_array.get(i).getText().toString());
                            objTemp.put("correct", check_array.get(i).isChecked());
                            objTemp.put("feedback", feedback_array.get(i).getText().toString());
                            objTemp.put("match", match_array.get(i).getText().toString());

                            array.put(objTemp);

                        }
                    }

                    obj.put("answers", array);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("sonucjson",obj.toString());
                addQuestion(obj);
                dialog.cancel();

            }
        });


        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public void addQuestion(JSONObject obj){

        if(!imagepath.equals(""))
            uploadFile(imagepath,BASE64Encoder.encode(obj));
        else {


            api.addQuestion(BASE64Encoder.encode(obj), new Callback<String>() {
                @Override
                public void success(String s, retrofit.client.Response response) {
                    Log.d("sonuc",s);
                    Toast.makeText(getActivity(), "Question Added Succesfully", Toast.LENGTH_LONG).show();
                    fetchData();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("hata",error.getLocalizedMessage());
             }
            });

    }}

    public static AddQuestionFragment newInstance(JSONObject object) {
        AddQuestionFragment fragmentDemo = new AddQuestionFragment();
        Bundle args = new Bundle();
        args.putString("json", object.toString());
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public static AddQuestionFragment newInstance(QuizApi object) {
        AddQuestionFragment fragmentDemo = new AddQuestionFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("json", gson.toJson(object));
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public static AddQuestionFragment newInstance(String json) {
        AddQuestionFragment fragmentDemo = new AddQuestionFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("json", json);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        // Burada uploadfile fonksiyonu çalıştırılmaya başlanıyor
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 100)
            {
                if (data != null) {
                    File myFile;

                    Uri selectedImageUri = data.getData();
                    String filemanagerstring = selectedImageUri.getPath();
                    String selectedImagePath = getPath(getActivity(),selectedImageUri);
                    if (selectedImagePath != null){
                        myFile = new File(selectedImagePath);
                        videoPath = selectedImagePath;}
                    else if (filemanagerstring != null){
                        myFile = new File(filemanagerstring);
                        videoPath = filemanagerstring ;}
                }

                imagepath = videoPath;

                Log.d("path",videoPath);

            } else if (requestCode == 50) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagepath = destination.getAbsolutePath();
            }

        }
    }

    public void uploadFile(String sourceFileUri, String base64) {

        Bitmap bm = BitmapFactory.decodeFile(sourceFileUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();


        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("photo",encodedImage);
        api.addQuestionWithPhoto(base64, encodedImage, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d("resim",s);
                Toast.makeText(getActivity(), "Question Added Succesfully", Toast.LENGTH_LONG).show();
                fetchData();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("hata",error.getMessage());
            }
        });
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void showCopyClipboardDialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_id_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView dismiss = (ImageView)dialog.findViewById(R.id.cancel);
        final TextView id = (TextView)dialog.findViewById(R.id.id);
        Button copy = (Button)dialog.findViewById(R.id.copy);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(id.getText().toString());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("text label",id.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(getActivity(),getActivity().getString(R.string.copied),Toast.LENGTH_LONG).show();
                dialog.cancel();
            }

        });

        id.setText(String.valueOf(quiz.getId()));

        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

}
