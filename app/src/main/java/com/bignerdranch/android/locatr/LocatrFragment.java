package com.bignerdranch.android.locatr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by Mohamed Amr on 6/9/2019.
 */

public class LocatrFragment extends SupportMapFragment{

    private GoogleApiClient mClient;
    private Bitmap mMapImage;
    private GalleryItem mMapItem;
    private Location mCurrentLocation;
    private GoogleMap mMap;

    private static final String TAG = "LocatrFragment";

    public static LocatrFragment newInstance (){
        return new LocatrFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.i(TAG ,"GooglePlay connected ");
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                UpdateUi();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr_menu , menu);
        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate :
                findImage();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private class SearchTask extends AsyncTask<Location ,Void , Void>{
        private Bitmap mBitmap;
        private List<GalleryItem> mGalleryItems;
        GalleryItem mGalleryItem;
        private Location mLocation;
        @Override
        protected Void doInBackground(Location... locations) {
            mLocation = locations[0];
            FlickerFitcher flicker = new FlickerFitcher();
            mGalleryItems = flicker.searchPhotos(locations[0]);
            mGalleryItem =
                    mGalleryItems.get(0);
            try {
                byte[] bytes = flicker.getUrlBytes(mGalleryItem.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes , 0 , bytes.length);
            } catch (IOException e) {
                Log.i(TAG, "Unable to download bitmap", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mMapImage = mBitmap;
            mMapItem = mGalleryItem;
            mCurrentLocation =mLocation;
            UpdateUi();
        }
    }



    private void  UpdateUi(){
        if (mMap == null || mMapImage == null){
            return;
        }
        LatLng myPoint = new LatLng(mCurrentLocation
                .getLatitude() ,
                mCurrentLocation.getLongitude());
        LatLng itemPoint = new LatLng(mMapItem.getLat()
                , mMapItem.getLon());

        BitmapDescriptor itemBitmap = BitmapDescriptorFactory
                .fromBitmap(mMapImage);
        MarkerOptions itemMarker = new MarkerOptions()
                .position(itemPoint)
                .icon(itemBitmap);
        MarkerOptions myMarker = new MarkerOptions()
                .position(myPoint);
        mMap.clear();
        mMap.addMarker(itemMarker);
        mMap.addMarker(myMarker);
        LatLngBounds bounds = new LatLngBounds(myPoint
                , itemPoint);
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory
                .newLatLngBounds(bounds , margin);
        mMap.animateCamera(update);
    }



    private void findImage (){
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(0);
        request.setNumUpdates(1);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request
                        , new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG , "Got A fix : " + location);
                        new SearchTask().execute(location);
                    }
                });

         }



}

