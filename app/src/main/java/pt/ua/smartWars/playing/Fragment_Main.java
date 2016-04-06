package pt.ua.smartWars.playing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

import pt.ua.smartWars.OnGameData.FirePlayers;
import pt.ua.smartWars.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {link Fragment_Main.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {link Fragment_Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Main extends Fragment{



    private Marker m1,m2,m3,m4,m5;
    private MapView mapView;
    private GoogleMap map;

    public MapView getMapView() {
        return mapView;
    }

    public GoogleMap getMap() {
        return map;
    }

    public static Fragment_Main newInstance() {
        Fragment_Main fragment = new Fragment_Main();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment// Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_main, container, false);
        // Gets the MapView from the XML layout and creates it

        MapsInitializer.initialize(getActivity());

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) v.findViewById(R.id.map);
                mapView.onCreate(savedInstanceState);
                // Gets to GoogleMap from the MapView and does initialization stuff
                if (mapView != null) {
                    map = mapView.getMap();
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        map.setMyLocationEnabled(true);
                    }

                    if (FirePlayers.getInstance().getTeamP()[0] != null) {
                        Log.d("diogo", FirePlayers.getInstance().getTeamP()[0] + "");
                        m1 = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pait))
                                .anchor(0.2f, 1.0f) // Anchors the marker on the bottom left
                                .position(new LatLng(FirePlayers.getInstance().getTeamP()[0].getX(), FirePlayers.getInstance().getTeamP()[0].getY())).title("Player1"));
                    }

                    if (FirePlayers.getInstance().getTeamP()[1] != null) {
                        Log.d("diogo", FirePlayers.getInstance().getTeamP()[1] + "");
                        m2 = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pait))
                                .anchor(0.2f, 1.0f) // Anchors the marker on the bottom left
                                .position(new LatLng(FirePlayers.getInstance().getTeamP()[1].getX(), FirePlayers.getInstance().getTeamP()[1].getY())).title("Player2"));
                    }
                    if (FirePlayers.getInstance().getTeamP()[2] != null) {
                        Log.d("diogo", FirePlayers.getInstance().getTeamP()[2] + "");
                        m3 = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pait))
                                .anchor(0.2f, 1.0f) // Anchors the marker on the bottom left
                                .position(new LatLng(FirePlayers.getInstance().getTeamP()[2].getX(), FirePlayers.getInstance().getTeamP()[2].getY())).title("Player3"));
                    }
                    if (FirePlayers.getInstance().getTeamP()[3] != null) {
                        m4 = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pait))
                                .anchor(0.2f, 1.0f) // Anchors the marker on the bottom left
                                .position(new LatLng(FirePlayers.getInstance().getTeamP()[3].getX(), FirePlayers.getInstance().getTeamP()[3].getY())).title("Player4"));
                    }
                    if (FirePlayers.getInstance().getTeamP()[4] != null) {
                        Log.d("diogo", FirePlayers.getInstance().getTeamP()[4] + "");
                        m5 = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pait))
                                .anchor(0.2f, 1.0f) // Anchors the marker on the bottom left
                                .position(new LatLng(FirePlayers.getInstance().getTeamP()[4].getX(), FirePlayers.getInstance().getTeamP()[4].getY())).title("Player5"));
                    }

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(40.633549,-8.652342), 16);
                    map.animateCamera(cameraUpdate);
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default: Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }


        callAsyncTask();
//
//        // Updates the location and zoom of the MapView
//        new Thread(new Runnable(){
//
//            public void run(){
//                runOnUiThread(new Runnable(){
//
//                    @Override
//                    public void run() {
//                        m1.setPosition(new LatLng(40,-9));
//                    }
//                });
//            }
//        }).start();






        return v;

    }
    Timer timer;

    public void callAsyncTask(){
        final Handler handler = new Handler();
        final int warning_val_hr=150;
        timer = new Timer();
        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{


                            if (FirePlayers.getInstance().getTeamP()[0] != null) {
                                m1.setPosition(new LatLng(FirePlayers.getInstance().getTeamP()[0].getX(), FirePlayers.getInstance().getTeamP()[0].getY()));
                                if(FirePlayers.getInstance().getTeamP()[0].gethRate() >= warning_val_hr){
                                    m1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait_red));
                                }
                                else{
                                    m1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait));
                                }
                            }
                            if (FirePlayers.getInstance().getTeamP()[1] != null) {
                                m2.setPosition(new LatLng(FirePlayers.getInstance().getTeamP()[1].getX(), FirePlayers.getInstance().getTeamP()[1].getY()));
                                if(FirePlayers.getInstance().getTeamP()[1].gethRate() >= warning_val_hr){
                                    m2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait_red));
                                }
                                else{
                                    m2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait));
                                }
                            }
                            if (FirePlayers.getInstance().getTeamP()[2] != null) {
                                m3.setPosition(new LatLng(FirePlayers.getInstance().getTeamP()[2].getX(), FirePlayers.getInstance().getTeamP()[2].getY()));
                                if(FirePlayers.getInstance().getTeamP()[2].gethRate() >= warning_val_hr){
                                    m3.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait_red));
                                }
                                else{
                                    m3.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait));
                                }
                            }
                            if (FirePlayers.getInstance().getTeamP()[3] != null) {
                                m4.setPosition(new LatLng(FirePlayers.getInstance().getTeamP()[3].getX(), FirePlayers.getInstance().getTeamP()[3].getY()));
                                if(FirePlayers.getInstance().getTeamP()[3].gethRate() >= warning_val_hr){
                                    m4.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait_red));
                                }
                                else{
                                    m4.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait));
                                }
                            }
                            if (FirePlayers.getInstance().getTeamP()[4] != null) {
                                m5.setPosition(new LatLng(FirePlayers.getInstance().getTeamP()[4].getX(), FirePlayers.getInstance().getTeamP()[4].getY()));
                                if(FirePlayers.getInstance().getTeamP()[4].gethRate() >= warning_val_hr){
                                    m5.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait_red));
                                }
                                else{
                                    m5.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pait));
                                }
                            }

                            //new AsynComp().execute();

                        }catch (Exception e){


                        }

                    }
                });
            }
        };

        timer.schedule(doAsyncTask,0,3000);

    }




    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public Fragment_Main()
    {}
}
