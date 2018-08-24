package Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.coba.els_connect.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import base.BaseOkHttpClient;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import share.Api;
import share.MarshMallowPermission;

import static android.app.Activity.RESULT_OK;

public class CreateFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int CAPTURE_PICCODE = 989;

    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    public String encodedImageplus;
    private ImageView mImageView;
    private String fileName = System.currentTimeMillis()+"";
    String ba1,encodedImage;
    EditText edpostingan;

    Button uploadPost;

    public File filePhoto;
    Bitmap bitmap;
    private static final int REQ_CAMERA = 22222;
    private static final int REQ_PERMISSION_CAMERA = 11111;
    MarshMallowPermission marshMallowPermission;


    public static CreateFragment newInstance(){
        CreateFragment fragment = new CreateFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marshMallowPermission = new MarshMallowPermission(getActivity());
        filePhoto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);
        mImageView = rootView.findViewById(R.id.new_post_images);
        uploadPost = rootView.findViewById(R.id.post_btn);
        edpostingan = rootView.findViewById(R.id.postingan);

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

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

        uploadPost.setOnClickListener(new View.OnClickListener() {
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
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = fileName;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
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
//        encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
//
//
//        Log.d("cpth", mCurrentPhotoPath);
//        Log.d("cpth_create_64", encodedImage);
//        System.out.println(encodedImage);

        startGoPosting("1");

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


        encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);

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


    public void startGoPosting(String userId){


        final RequestBody requestBody = new FormBody.Builder()
                .add("image_encode", "sdfsfsdfsfsdfsdf")
                .add("theposting", edpostingan.getText().toString())
                .build();


        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.JSON_DO_POSTING+"/"+userId)
                .tag("GOPOSTING")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(requestBody)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest("GOPOSTING");

        BaseOkHttpClient.getInstance(getActivity()).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(e.getMessage()!=""){
                    Log.d("detection","No network");

                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                    if(response.isSuccessful()){

                        Log.d("is_success", "OK");

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Berhasil Posting", Toast.LENGTH_LONG).show();
                                edpostingan.setText("");
                            }
                        });


                    }
                    else if(!response.isSuccessful()){
                        Log.d("is_success", "Failed");
                    }
            }
        });

    }















}