package pt.ua.smartWars.playing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by drcc on 22/03/16.
 */
public class GPSModule extends AppCompatActivity implements LocationListener {

    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private final Context mContext;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    String provider;

    Location location;
    double latitude;
    double longitude;

    // The minimum distace to update (meters)
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // The minimum time between updates
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    protected LocationManager locationManager;

    public GPSModule(Context context){
        this.mContext = context;
        getLocation();
    }

    public Location getLocation(){

        try{
            // Initialize locationManager
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            // Get GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // Get Network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            // Check for GPS and Network on
            if(!isGPSEnabled || !isNetworkEnabled){
                System.out.println("GPS or Network not enabled!");
            }else {
                try {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            this.location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                this.latitude = location.getLatitude();
                                this.longitude = location.getLongitude();
                            }
                        }
                    }
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            if (locationManager != null) {
                                this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    this.latitude = location.getLatitude();
                                    this.longitude = location.getLongitude();

                                }
                            }
                        }
                    }
                } catch(SecurityException e){}
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return location;
    }



    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    public Double getLatitude(){

        getLocation();
        return this.latitude;
    }

    public Double getLongitude(){
        getLocation();
        return this.longitude;
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location loc){
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}
