package com.example.rememberer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.File;
import java.util.List;

public class ItemDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final String NAME_ITEM = "NAME_ITEM";
    private static final String ID_ITEM_HISTORY = "ID_ITEM";
    private static final String RATING_ITEM = "RATING_ITEM";
    private static final String LONGITUDE_ITEM = "LONGITUDE_ITEM";
    private static final String LATITUDE_ITEM = "LATITUDE_ITEM";
    private static final String IMG_NAME_ITEM = "IMG_NAME_ITEM";
    private static final String ID_ITEM = "ID_ITEM";

    private int id_item_history = -1;
    private String name_item = "";
    private int rating = -1;
    private double longitude = -1;
    private double latitude = -1;
    private String img_name = "";
    private int id_item = -1;

    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_details);

        try {
            Bundle bundle = getIntent().getExtras();
            id_item_history = bundle.getInt(ID_ITEM_HISTORY);
            name_item = bundle.getString(NAME_ITEM);
            rating = bundle.getInt(RATING_ITEM);
            longitude = bundle.getDouble(LONGITUDE_ITEM);
            latitude = bundle.getDouble(LATITUDE_ITEM);
            img_name = bundle.getString(IMG_NAME_ITEM);
            id_item = bundle.getInt(ID_ITEM);

            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.itemDetailsToolbarLayout);
            String newTitle = collapsingToolbarLayout.getTitle() + " " + name_item;
            collapsingToolbarLayout.setTitle(newTitle);

            Toolbar toolbar = findViewById(R.id.itemDetailsToolbar);
            setSupportActionBar(toolbar);

            imageView = findViewById(R.id.imageForDetailsItem);
            setPicture();
            imageView.requestLayout();


            RatingBar ratingBar = findViewById(R.id.ratingBarItem);
            ratingBar.setRating(rating);
            ratingBar.setIsIndicator(true);


            Mapbox.getInstance(this, getString(R.string.access_token));
            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }
        catch (Exception e){}
    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        ItemDetailsActivity.this.mapboxMap = mapboxMap;
        MarkerOptions m = new MarkerOptions();
        m.setPosition(new LatLng(latitude, longitude));
        mapboxMap.addMarker(m);
        pomak(latitude, longitude);

    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponentOptions options =
                    LocationComponentOptions.builder(this)
                            .trackingGesturesManagement(true)
                            .accuracyColor(ContextCompat.getColor(this,
                                    R.color.mapbox_blue))
                            .build();

            LocationComponent locationComponent =
                    mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(this, options);

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Gimme",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Toast.makeText(this, "User wont't give permission",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void zoom(double koliko)
    {
        CameraPosition pos = new CameraPosition.Builder()
                .target(mapboxMap.getCameraPosition().target)
                .bearing(mapboxMap.getCameraPosition().bearing)
                .tilt(mapboxMap.getCameraPosition().tilt)
                .zoom(mapboxMap.getCameraPosition().zoom * koliko).build();
        mapboxMap.setCameraPosition(pos);
    }

    private void pomak(double geoSirina, double geoDuzina)
    {
        CameraPosition pos = new CameraPosition.Builder()
                .target(new LatLng(geoSirina, geoDuzina))
                .bearing(mapboxMap.getCameraPosition().bearing)
                .tilt(mapboxMap.getCameraPosition().tilt)
                .zoom(mapboxMap.getCameraPosition().zoom).build();
        mapboxMap.setCameraPosition(pos);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.deleteItemHistory){
            deleteItemHistory(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteItemHistory(MenuItem item){
        DBAdapter db = new DBAdapter(this);
        db.open();
        boolean deletedHistory = db.deleteItemHistory(id_item_history);
        db.close();

        if(deletedHistory){
            Toast.makeText(this,
                    "Item history successfully deleted!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,
                    "Item history NOT successfully deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPicture(){

        if(new File(img_name).exists()){
            // Get the dimensions of the View
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();
            if(targetH == 0 || targetW == 0){
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                targetH = 180;
                targetW = displayMetrics.widthPixels;
            }

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(img_name, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(img_name, bmOptions);
            imageView.setImageBitmap(bitmap);
            findViewById(R.id.itemDetailsToolbarLayout).setBackgroundColor(R.color.myBlue);
        }
    }
}
