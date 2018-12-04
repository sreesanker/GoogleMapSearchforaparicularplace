package com.example.asus.googlemapsearchforaparicularplace;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

//at first add some permissions in the manifest
//implement three more methods by putting ","

//.CONNECTIONCallbacks class overrides onConnected method which is called whenever the device is connected
//.onConnectionFailedListner class overrides onConnectionFailed method and is called whenever the connection is lost
//.LocationLister class is used whenever their is a change of location and onLocationChanged method is used
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentlocationMarker;
    public static final int Requst_location_code = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //calling checkLocationPermissions method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermissions();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is for handling permission request response
        //dialog box appears for permission
        //we use this method to check whether the permisison was granded or not
        switch (requestCode)
        {
            case Requst_location_code:
            {
                //in this case we need to check whether the permisssion was granded or not
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //this means the permission was granded
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        //This means the permission is granded
                        //also we need to check whether the client is null or not
                        if (client == null)
                        {
                            //ie,if the client is null
                            //then we need to create a client by calling buildGoogleApiclient method
                            buildGoogleApiclient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    //this means the permission is denied
                    Toast.makeText(this,"permission denied!",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //this method is called whenever the map is ready to use

        //first setLocationenabled - true
        //and make permission as in if loop
        mMap = googleMap;
        //in this we can add functions like marker etc..
        //also we can change map type here
        //to do this like below
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

       if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
       {
            buildGoogleApiclient();
            mMap.setMyLocationEnabled(true);
        }
        //after this  create the method buildGoogleApiClient() method
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //FUSEDLOACTIONAPI is used to get current location
        //the permission should be checked
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,this);
            //after finishing this work on onLocationChanged method
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation = location;

        //first if already a marker is set to some place we need to remove that marker
        if (currentlocationMarker != null)
        {
            //ie, it is set to some location
            //so remove it
            currentlocationMarker.remove();
        }
        //after we can set it to new location
        //for that we need to get the lattitude and longitude
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        //MARKER OPTIONS IS TO SET PROPERTIES TO MARKER LIKE POSITION,TITLE,ICON COLOR ETC...
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        currentlocationMarker = mMap.addMarker(markerOptions);
        //we see that when we click on the icon the camera move to that position
        //for that we use movecamera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        //after that we need to stop the location updates after setting it to the current location
        //for that we need to check whether the client=null
        //if client=null then no location is set
        if (client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
            //after this create two more methods
            //first a constant RequestLocationCode = 99
        }
    }

    protected synchronized void buildGoogleApiclient()
    {
        //this method is used to make a new client
        //this method is called in onMapReady method
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
        //after finishng it call this method in onMapsReady
    }

    public boolean checkLocationPermissions()
    {
        //this method will check whether the permission is granded or not
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //ie,if the permissions is not granded
            //then we need to check whether we need to ask for the permissions
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                //if it is true it should request for permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Requst_location_code);

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Requst_location_code);
            }
            return false;
        }
        else
            return true;
        //after this override the onRequestPermissionsResult method
    }
    //Search Button click
    public void Search_Button(View v)
    {
        if (v.getId() == R.id.search_button_id)
        {
           //this if method check whether the user clicked the search button or not
           //if the user entered something we need to get it first
            EditText searched_location = (EditText)findViewById(R.id.Search_edittext);
            String searched_location_str = searched_location.getText().toString();

            List<Address> addressList = null;
            MarkerOptions mo = new MarkerOptions();
            //now we need to check whether the user actually entered something or not
            if (!searched_location_str.equals(""))
            {
                //this make sure that the string is not empty
                //we use Geocoder class to search a particular location
                Geocoder geocoder = new Geocoder(this);
                try {
                    //this addressList have the address of the location
                    addressList = geocoder.getFromLocationName(searched_location_str,5);
                    //this can return a list of two or three address
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //we can put the marker on all the returned address list
                //for that we use a for loop
                Log.e("Address_List",addressList.size()+"");
                for (int i=0;i<addressList.size();i++)
                {
                Address address = addressList.get(i);
                //this will get the address
                    //now we need to get the lattitude and longitude
                    LatLng searchedlatlng = new LatLng(address.getLatitude(),address.getLongitude());
                    //now we need to create marker options
                    mo.position(searchedlatlng); //we setted the position
                    mo.title("your searched result");
                    mMap.addMarker(mo);//here we added the marker to the map
                    //here we get multiple results
                    //if we set camera focus
                    //it will focus to the last result
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(searchedlatlng));
                }
            }
        }
    }
}
