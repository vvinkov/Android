package com.example.rememberer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddNewItemActivity extends AppCompatActivity {

    private static final String ID_ITEM = "ID_ITEM";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_LOCATION = 3;
    private int id_item;
    String mCurrentPhotoPath;
    ImageView mImageView;
    private MapView mapView;

    private double longitude = 0.0;
    private double latitude = 0.0;

//    private PermissionsManager permissionsManager;
//    private MapboxMap mapboxMap;

    DBAdapter db = new DBAdapter(this);

    private static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        db = new DBAdapter(this);

        Bundle bundle = getIntent().getExtras();
        id_item = bundle.getInt(ID_ITEM);

        mImageView = findViewById(R.id.imageItem);

       // Mapbox.getInstance(this, getString(R.string.access_token));

//        mapView = findViewById(R.id.mapViewAdd);
//        mapView.onCreate(savedInstanceState);
        //mapView.getMapAsync(this);
    }

    public void addNewItem_DB(View view) {
        if(checkIfNameNotEmpty()){


            db.open();
            EditText nameItem = findViewById(R.id.itemName);
            RatingBar rating = findViewById(R.id.ratingBar);

            String img_name;
            if(mCurrentPhotoPath != null && mCurrentPhotoPath.length() > 0){
                img_name = mCurrentPhotoPath;
            }
            else{
                img_name = "default";
            }

            if(id_item == -2){
                // radi se o skroz novom itemu !!!!
                // dohvatimo zadnji it_itema
                Cursor cursor = db.getItemId();
                cursor.moveToFirst();
                id_item = cursor.getInt(0) + 1;
                db.insertIntoItem(id_item);
            }

            // dodajemo u item history
            long result = db.insertIntoItemHistory(nameItem.getText().toString(),
                    (int)rating.getRating(),
                    longitude,
                    latitude,
                    img_name,
                    id_item);

            db.close();

            if(result > 0){
                Toast.makeText(this, "Item successfully added!", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
        else{
            Toast.makeText(this, "Name should not be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIfNameNotEmpty(){
        EditText nameItem = findViewById(R.id.itemName);
        if(nameItem.getText().toString().trim().length() > 0)
            return true;
        return false;
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    public void addItemLocation(View view) {
        fetchLocation();
    }

    public static void verifyStoragePermission(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {PERMISSIONS[1]},
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void verifyCameraPermission(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {PERMISSIONS[2]},
                    REQUEST_CAMERA
            );
        }
        else{
            dispatchTakePictureIntent();
        }
    }

    public void verifyLocationPermission(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {PERMISSIONS[3]},
                    REQUEST_LOCATION
            );
        }
        else{
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults){
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_CAMERA: {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dispatchTakePictureIntent();
                }
                else{
                    Toast.makeText(this, "Picture will be set to default.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case REQUEST_LOCATION:{
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
                else{
                    Toast.makeText(this, "Location won't be set.", Toast.LENGTH_SHORT).show();
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){

            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch(IOException e){

            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.rememberer.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){
            File imgFile = new File(mCurrentPhotoPath);
            if(imgFile.exists()){
                setPicture();
            }
        }
    }

    private void setPicture(){

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
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);

    }

    private File createImageFile() throws IOException{
        // create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + id_item;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
          imageFileName,
          ".jpg",
          storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openCamera(View view) {
        verifyCameraPermission(this);
    }

    public void fetchLocation(){
        verifyLocationPermission(this);

    }


}

