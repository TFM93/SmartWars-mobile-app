package pt.ua.smartWars.playing;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import pt.ua.smartWars.OnGameData.FirePlayers;
import pt.ua.smartWars.R;
import userData.userInfo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {link Fragment_Main.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {link Fragment_Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Main extends Fragment implements OnClickListener {



    private Marker m1,m2,m3,m4,m5;
    private MapView mapView;
    private GoogleMap map;
    private CameraUpdate cameraUpdate;
    Button pre_button,pre_button2,pre_button3;

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


        setupConnectionFactory();
        publishToAMQP();

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");



                String[] arrayMsg = message.split(",");

                // sound of notification when received a pre-message
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);

                cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(arrayMsg[1]),Double.parseDouble(arrayMsg[2])), 16);
                map.animateCamera(cameraUpdate);
                if(arrayMsg[0].equals("1")) {
                    Toast.makeText(getActivity(), "FOLLOW HIM", Toast.LENGTH_SHORT).show();
                }else if(arrayMsg[0].equals("2"))
                {
                    Toast.makeText(getActivity(), "GO GO GO", Toast.LENGTH_SHORT).show();
                }
                else if(arrayMsg[0].equals("3"))
                {
                    Toast.makeText(getActivity(), "HELP HIM", Toast.LENGTH_SHORT).show();
                }


            }
        };
        subscribe(incomingMessageHandler);
    }


    void setupPubButton() {
        pre_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String concat = "1,";
                concat += FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()).getX() + ",";
                concat += FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()).getY();
                publishMessage(concat);
                //et.setText("");
            }
        });
        pre_button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String concat = "2,";
                concat += FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()).getX() + ",";
                concat += FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()).getY();
                publishMessage(concat);
            }
        });
        pre_button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String concat = "3,";
                concat += FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()).getX() + ",";
                concat += FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()).getY();
                publishMessage(concat);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment// Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_main, container, false);
        // Gets the MapView from the XML layout and creates it

        MapsInitializer.initialize(getActivity());

        pre_button = (Button) v.findViewById(R.id.button1);
        pre_button2 = (Button) v.findViewById(R.id.button2);
        pre_button3 = (Button) v.findViewById(R.id.button3);

        setupPubButton();

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
                        m1 = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pait))
                                .anchor(0.2f, 1.0f) // Anchors the marker on the bottom left
                                .position(new LatLng(FirePlayers.getInstance().getTeamP()[0].getX(), FirePlayers.getInstance().getTeamP()[0].getY())).title("Player1"));
                    }

                    if (FirePlayers.getInstance().getTeamP()[1] != null) {
                        m2 = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pait))
                                .anchor(0.2f, 1.0f) // Anchors the marker on the bottom left
                                .position(new LatLng(FirePlayers.getInstance().getTeamP()[1].getX(), FirePlayers.getInstance().getTeamP()[1].getY())).title("Player2"));
                    }
                    if (FirePlayers.getInstance().getTeamP()[2] != null) {
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
                        m5 = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pait))
                                .anchor(0.2f, 1.0f) // Anchors the marker on the bottom left
                                .position(new LatLng(FirePlayers.getInstance().getTeamP()[4].getX(), FirePlayers.getInstance().getTeamP()[4].getY())).title("Player5"));
                    }

                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(40.633549,-8.652342), 16);
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

        timer.schedule(doAsyncTask, 0, 3000);

    }




    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public Fragment_Main()
    {}


    Thread subscribeThread;
    Thread publishThread;
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        publishThread.interrupt();
        subscribeThread.interrupt();
    }

    private BlockingDeque<String> queue = new LinkedBlockingDeque<String>();
    void publishMessage(String message) {
        //Adds a message to internal blocking queue
        try {
            Log.d("", "[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void subscribe(final Handler handler) {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        channel.basicQos(1);
                        DeclareOk q = channel.queueDeclare();
                        channel.queueBind(q.getQueue(), "amq.fanout", "chat");
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(q.getQueue(), true, consumer);

                        // Process deliveries
                        while (true) {

                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                            String message = new String(delivery.getBody());
                            Log.d("", "[r] " + message);

                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();

                            bundle.putString("msg", message);
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        Log.d("", "Connection broken: " + e1.getClass().getName());
                        try {
                            Thread.sleep(4000); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.start();
    }



    ConnectionFactory factory = new ConnectionFactory();
    private void setupConnectionFactory() {
        String uri = "amqp://alpsoxxu:M0gZxxSxI_Hm5qEfsZ1SXtvTJ65yFxlS@spotted-monkey.rmq.cloudamqp.com/alpsoxxu";
        try {
            factory.setAutomaticRecoveryEnabled(false);
            factory.setUri(uri);
        } catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }


    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        while (true) {
                            String message = queue.takeFirst();
                            try{
                                ch.basicPublish("amq.fanout", "chat", null, message.getBytes());
                                Log.d("", "[s] " + message);
                                ch.waitForConfirmsOrDie();
                            } catch (Exception e){
                                Log.d("","[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }


    @Override
    public void onClick(View v) {

    }
}
