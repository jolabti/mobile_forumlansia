package Fragment;
import android.app.Activity;
import android.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coba.els_connect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import Adapter.DataAdapter;
import Interface.RequestInterface;
import Model.PostModel;
import Response.JsonResponse;
import Utils.SessionManager;
import base.BaseOkHttpClient;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import share.Api;
import share.MarshMallowPermission;

public class ProfileFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int CAPTURE_PICCODE = 989;

    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    public String encodedImageplus;
    private ImageView mImageView;
    private String fileName = System.currentTimeMillis() + ".png";
    String ba1;

    public File filePhoto;
    Bitmap bitmap;
    private static final int REQ_CAMERA = 22222;
    private static final int REQ_PERMISSION_CAMERA = 11111;
    MarshMallowPermission marshMallowPermission;

    SessionManager sessionManager;

    public String TAGPROFIL ="Log profil";



//  Deklarasi Layoutnya
    Button upload;
    EditText atEmail, atKota, atUmur;




    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        // Fragment fragment = PostingFragment.newInstance();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marshMallowPermission = new MarshMallowPermission(getActivity());
        filePhoto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        sessionManager = new SessionManager(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mImageView = rootView.findViewById(R.id.profile_img);
        upload = rootView.findViewById(R.id.button_profile);
        atEmail = rootView.findViewById(R.id.profile_email);
        atKota = rootView.findViewById(R.id.profile_kota);
        atUmur = rootView.findViewById(R.id.profile_umur);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        starRequestProfile("1");

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!marshMallowPermission.checkPermissionForExternalStorage()|| !marshMallowPermission.checkPermissionForCamera()) {
                    marshMallowPermission.requestPermissionForExternalStorage(MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_LOAD_PROFILE);
                    marshMallowPermission.requestPermissionForCamera();

//                    String file = fileName;
//                    File newfile = new File(file);
//                    try {
//                        newfile.createNewFile();
//                    }
//                    catch (IOException e)
//                    {
//                    }
//
//                    Uri outputFileUri = Uri.fromFile(newfile);
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//
//                    startActivityForResult(intent,
//                            REQUEST_IMAGE_CAPTURE);

                    captureImage();

                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {

                Log.d("get_res","OKDONG");
                setPic();



//                Bitmap bmp = (Bitmap) data.getExtras().get("data");
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//
//
//
//                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
//                        byteArray.length);
//
//                mImageView.setImageBitmap(bitmap);


            }
        }
    }

    private void captureImage() {


        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }


            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = fileName;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("Getpath", "Cool" + mCurrentPhotoPath.trim());
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }


    private void upload() {
//        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
//        ByteArrayOutputStream bao = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
//        byte[] ba = bao.toByteArray();
//
//        String encodedImage = Base64.encodeToString(ba, Base64.NO_WRAP);
//
//
//        Log.d("cpth", mCurrentPhotoPath);
//        Log.d("cpth_64", encodedImage);
//        System.out.println(encodedImage);
//
//        startGoPosting("1","Dummy test 1",encodedImage);
            startRequestPostUpdateProfile("1",atKota.getText().toString(),"12");
    }

    private String convertToBase64(String imagePath)

    {

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize=4;
//
//
//        Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
        Bitmap bm = loadBitmapfromFile(imagePath);
        Bitmap newBitmap = getResizedBitmap(bm, 500, 500);
        //Bitmap newBitmap = getUbahBitmap(500, 400);
        bm = null;
        //Bitmap newBitmap = getResizedBitmap(bm, 600, 500);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();

        newBitmap = null;
        baos = null;


        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);

        return encodedImage;

    }

    private Bitmap loadBitmapfromFile(String imgPath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        return BitmapFactory.decodeFile(imgPath, options);

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
//        matrix.postRotate(90);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }


    public void startGoPosting(String userId, String postingLetter, String imgEncode){


        final RequestBody requestBody = new FormBody.Builder()
                .add("image_encode", imgEncode)
                .build();


        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.JSON_UPDATE_PROFIL+"/"+userId+"/"+postingLetter)
                .tag("GOUPLOAD")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(requestBody)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest("GOUPLOAD");

        BaseOkHttpClient.getInstance(getActivity()).newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if(e.getMessage()!=""){
                    Log.d("detection","No network");

                }

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                if(response.isSuccessful()){

                    Log.d("is_success", "OK");
                }
                else if(!response.isSuccessful()){
                    Log.d("is_success", "Failed");
                }
            }
        });

    }

//    REQUEST
    public void starRequestProfile(String iduser ) {

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.JSON_GET_DATA_PROFIL + "/" + iduser)
                .tag("DETAIPROFILE")
                .addHeader("application/json", "charset=utf-8")
                //.addHeader("Content-Type","application/x-www-form-urlencoded")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest("SERVICEHARGA");
        BaseOkHttpClient.getInstance(getActivity()).newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

                if(e.getMessage()!=""){
                        sessionManager.logoutUser();
                }

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()){

                      //Log.d("profilerespon", response.body().string());

                    try {
                        JSONObject jobRoot = new JSONObject(response.body().string());

                        final JSONObject jobChild = jobRoot.getJSONObject("posts");

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    atEmail.setText(jobChild.getString("email"));
                                    atKota.setText(jobChild.getString("kota"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });



                    } catch (Exception e) {
                        e.printStackTrace();
                    }


//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                                                 @Override
//                                                                 public void run() {
//
//                                                                 }
//                                                             });

                }
            }
        });


    }

    public void startRequestPostUpdateProfile ( String iduser, String user_kota, String user_umur){

        final RequestBody requestBody = new FormBody.Builder()
                .add("user_kota", user_kota)
                .add("user_umur",user_umur)
                .build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.JSON_UPDATE_PROFIL + "/"+ iduser)
                .tag(TAGPROFIL)
                .post(requestBody)
//                .addHeader("Content-Type","application/x-www-form-urlencoded")
//                .addHeader("Accept","/")
//                .addHeader("Connection","Keep-Alive")
//                .addHeader("Expect", "100-continue")
//                .addHeader("Accept-Encoding", "")

                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest(TAGPROFIL);
        BaseOkHttpClient.getInstance(getActivity()).newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {


                if(e.getMessage()!=""){
                    sessionManager.logoutUser();
                }



            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()){

                    Log.d("okeresponse", response.body().string());

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                                Toast.makeText(getActivity(),"Profil berhasil diubah", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });


    }

//terakhirss
}






