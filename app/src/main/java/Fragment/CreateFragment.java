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
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Base64OutputStream;
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
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Utils.SessionManager;
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

//https://android.jlelse.eu/androids-new-image-capture-from-a-camera-using-file-provider-dd178519a954

public class CreateFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int CAPTURE_PICCODE = 989;

    private Bitmap mImageBitmap;
    private String imageFilePath;
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
    SessionManager sessionManager;


    public static CreateFragment newInstance(){
        CreateFragment fragment = new CreateFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marshMallowPermission = new MarshMallowPermission(getActivity());
        filePhoto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        sessionManager = new SessionManager(getActivity());


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


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });

        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("haveUserId", sessionManager.getUserDetails().get("post_btn"));

                startGoPosting("1");


            }
        });





    }


    private void openCameraIntent() {

        if(!marshMallowPermission.checkPermissionForExternalStorage()||!marshMallowPermission.checkPermissionForCamera()){

            marshMallowPermission.checkPermissionForCamera();
            marshMallowPermission.checkPermissionForExternalStorage();

            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
            );
            if(pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    //Uri photoURI = FileProvider.getUriForFile(getActivity(),                                                                                                    "com.example.android.provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));



                    startActivityForResult(pictureIntent,
                            REQUEST_IMAGE_CAPTURE);
                }


            }

        }

        else{

            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
            );
            if(pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),                                                                                                    "com.example.android.provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(pictureIntent,
                            REQUEST_IMAGE_CAPTURE);
                }


            }

        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {

                int targetW = mImageView.getWidth();
                int targetH = mImageView.getHeight();

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;


                BitmapFactory.decodeFile(imageFilePath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, bmOptions);
                mImageView.setImageBitmap(bitmap);

                Log.d("resImagePath", imageFilePath);
                Log.d("resBase64", convertToBase64(imageFilePath));
               // Log.d("resBase64", convertToBase64cad(imageFilePath));


            }
        }
    }

     private String convertToBase64(String imagePath)

    {


        Bitmap bm = loadBitmapfromFile(imagePath);
       // Bitmap newBitmap = getResizedBitmap(bm, 800, 1800);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        //Bitmap resized = Bitmap.createScaledBitmap(bm, 800, 600, true);


        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();


        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);

        return encodedImage;

    }


//       private String convertToBase64cad(String imagePath)
//
//       {
//
//
//           Bitmap bm = loadBitmapfromFile(imagePath);
//           Bitmap newBitmap = getResizedBitmap(bm, 1200, 1200);
//
//           bm = null;
//
//           ByteArrayOutputStream baos = new ByteArrayOutputStream();
//           newBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//           byte[] byteArrayImage = baos.toByteArray();
//
//           newBitmap = null;
//           baos = null;
//
//
//           String encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
//
//           return encodedImage;
//
//       }



    private Bitmap loadBitmapfromFile(String imgPath) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 50;
        options.inPreferQualityOverSpeed=true;


        return BitmapFactory.decodeFile(imgPath, options);
       // return BitmapFactory.decodeFile(imgPath);

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
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private String readFileAsBase64String(String path) {
        try {
            InputStream is = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Base64OutputStream b64os = new Base64OutputStream(baos, Base64.NO_WRAP);
            byte[] buffer = new byte[8192];
            int bytesRead;
            try {
                while ((bytesRead = is.read(buffer)) > -1) {
                    b64os.write(buffer, 0, bytesRead);
                }
                return baos.toString();
            } catch (IOException e) {
                Log.e("TAG_CRF", "Cannot read file " + path, e);
                // Or throw if you prefer
                return "";
            } finally {
                closeQuietly(is);
                closeQuietly(b64os); // This also closes baos
            }
        } catch (FileNotFoundException e) {
            Log.e("TAG_FNFOUND", "File not found " + path, e);
            // Or throw if you prefer
            return "";
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }





    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
//        File storageDir =
//                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                fileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );



        imageFilePath = image.getAbsolutePath();
        Log.d("imageFilePath",imageFilePath);
        return image;
    }



    public void startGoPosting(String userId){


        final RequestBody requestBody = new FormBody.Builder()
                .add("image_encode",  convertToBase64(imageFilePath).trim())
                .add("theposting", edpostingan.getText().toString())
                .build();

        Log.d("stgp_encode", convertToBase64(imageFilePath).trim());

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
            public void onResponse(Call call, Response response) {

                    if(response.isSuccessful()){

                        Log.d("is_success", "OK");

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Berhasil Posting", Toast.LENGTH_LONG).show();
                                edpostingan.setText("");
                                mImageView.setImageBitmap(null);
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