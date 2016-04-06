package pt.ua.smartWars.playing;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import Bio.Library.namespace.BioLib;
import pt.ua.smartWars.OnGameData.FirePlayers;
import pt.ua.smartWars.R;
import userData.userInfo;

public class Gaming extends AppCompatActivity {


    private LocationManager locationManager;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private BioLib lib;
    private int dataFreq = 0;
    private Timer timer;
    private TimerTask timerTask;
    private Fragment_Pessoal.DataListener mDataListener;
    private GPSModule gps;
    private Firebase ret_ref;//retrieve data ref
    private GoogleMap mMap;
    private Marker[] markeppp;
    final Handler handler = new Handler();

    //private DataReadFragment.DataListener mDataListener;
    //private ProfileFragment.OnProfileInteractionListener mProfileInteractionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaming);
        Firebase.setAndroidContext(this);
        startTimer();

        //create gps module
        gps = new GPSModule(Gaming.this);
        //retrieve data location
        ret_ref = new Firebase("https://paintmonitor.firebaseio.com/Game/"+FirePlayers.getInstance().getMatch_id()+"/"+FirePlayers.getInstance().getTeam()+"/");
        ret_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " players on team");
                Iterator po = snapshot.getChildren().iterator();
                while(po.hasNext())
                {
                    Object ops = po.next();
                    DataSnapshot p = (DataSnapshot) ops;
                    String pId="";
                    String x="";
                    String y="";
                    String hRate="";
                    Iterator s= p.getChildren().iterator();
                    while(s.hasNext())
                    {
                        Object r = s.next();
                        DataSnapshot sl = (DataSnapshot) r;
                        if(sl.getKey().equals("pId"))
                        {
                            pId=sl.getValue().toString();
                        }
                        else if(sl.getKey().equals("x"))
                        {
                            x=sl.getValue().toString();
                        }
                        else if(sl.getKey().equals("y"))
                            y=sl.getValue().toString();
                        else if(sl.getKey().equals("hRate")){
                            Log.d("HR",sl.getValue().toString());
                            hRate=sl.getValue().toString();}




                    }
                    System.out.println(pId + "  x  "+ x + "    y  " + y + "    hr: " + hRate);
                    if(hRate.equals("")||hRate.equals("-.-"))
                    {
                        hRate="0";
                    }
                    FirePlayers.getInstance().setTeam_pos(pId,Double.parseDouble(x),Double.parseDouble(y),Integer.parseInt(hRate));
                    FirePlayers.getInstance().setTeam_hr(pId,Integer.parseInt(hRate));
                }


                Log.d("RET","data changed");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }});

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tab1);

        if (mTabLayout != null) {
            mTabLayout.setTabsFromPagerAdapter(mSectionsPagerAdapter);
        }
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
        }
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

    }

    private final Handler dataHandler = new Handler(){

        @Override
        public void handleMessage(Message msg)
        {
            Log.d("now", "yes");
            switch (msg.what)
            {
                case BioLib.MESSAGE_READ:
                    Log.d("RECEIVED: " ,""+ msg.arg1);
                    break;
                case BioLib.MESSAGE_DATA_UPDATED:
                    BioLib.Output out = (BioLib.Output) msg.obj;
                    Log.d("Battery", "" +out.battery);
                    Log.d("Pulse", "" + out.pulse);
                    FirePlayers.getInstance().setTeam_hr(userInfo.getInstance().getUid(), out.pulse);
                    dataFreq++;
                    if(dataFreq==40) {
                        dataFreq=0;
                        //new SendtoDBTask().execute("1", out.pulse + "", user.getId() + "");
                        mDataListener.updateBatteryText(out.battery);
                        mDataListener.updatePulseText(out.pulse);
                    }
                    break;
            }
        }
    };



    public void changeToOn(View view){
        SwitchButton device_switch = (SwitchButton) findViewById(R.id.device_switch);

        EditText address_dev = (EditText) findViewById(R.id.add_dev);

        String address= null;
        if (address_dev != null) {
            address = address_dev.getText().toString();
        }
        System.out.println(address);


        if(!device_switch.isChecked()) {
            try {
                lib = new BioLib(this, dataHandler);
                lib.Connect(address, 5);

            }catch (Exception e) {

                Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_LONG).show();
                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //builder.setMessage("Connection failed!").setNeutralButton("OK", null);
               //builder.create();
                //builder.show();

                device_switch.setChecked(false);
            }
        }
        else {
            try {
                lib.Disconnect();

            } catch (Exception e) {
                Log.d("vital", e.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Disconnecting isn't possible!")
                        .setNeutralButton("OK", null);
                builder.create();
                builder.show();

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setDataListener(Fragment_Pessoal.DataListener dataListener) {
        mDataListener = dataListener;
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
//                        //get the current timeStamp
//                        Calendar calendar = Calendar.getInstance();
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
//                        final String strDate = simpleDateFormat.format(calendar.getTime());
//
                            //get coordinates
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        FirePlayers.getInstance().setTeam_pos(userInfo.getInstance().getUid(), latitude, longitude,0);

//                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), "sending lat:"+ latitude + " long:" + longitude, duration);
                        toast.show();
                        Log.d("TIME","sending lat:"+ latitude + " long:" + longitude);
                        String path = "https://paintmonitor.firebaseio.com/Game/" +FirePlayers.getInstance().getMatch_id()+"/" +FirePlayers.getInstance().getTeam()+"/"+userInfo.getInstance().getUid()+"/";
                        Firebase ref = new Firebase(path);
                        Log.d("POS", FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()).getX() + "  " + latitude);
                        ref.setValue(FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()));



                    }
                });
            }
        };
    }



//    @Override
//    public void onMapReady(GoogleMap googleMap)
//    {
//        this.mMap = googleMap;
//        Position[] m = FirePlayers.getInstance().getTeamP();
//        this.markeppp = new Marker[7];
//        int i=0;
//        while(m[i] != null && i < m.length)
//        {
//            this.markeppp[i]= mMap.addMarker(new MarkerOptions().position(new LatLng(m[i].getX(), m[i].getY())));
//
//            i++;
//        }
//        this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(m[0].getX(), m[0].getY()), 11.0f);
//        mMap.animateCamera(yourLocation);
//    }






//    private void updateMapMarkers() {
//        Position[] m = FirePlayers.getInstance().getTeamP();
//        int i=0;
//        while(m[i] != null && i < m.length)
//        {
//            //mMap.addMarker(new MarkerOptions().position(new LatLng(m[i].getX(), m[i].getY())));
//            if(this.markeppp[i] != null)
//                this.markeppp[i].setPosition(  new LatLng(m[i].getX(), m[i].getY()));
//            else
//                this.markeppp[i]= mMap.addMarker(new MarkerOptions().position(new LatLng(m[i].getX(), m[i].getY())));
//            i++;
//        }
//
//    }

//    public void animateMarker(final Marker marker, final LatLng toPosition,
//                              final boolean hideMarker) {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        Projection proj = mGoogleMapObject.getProjection();
//        Point startPoint = proj.toScreenLocation(marker.getPosition());
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//        final long duration = 500;
//
//        final Interpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed
//                        / duration);
//                double lng = t * toPosition.longitude + (1 - t)
//                        * startLatLng.longitude;
//                double lat = t * toPosition.latitude + (1 - t)
//                        * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                } else {
//                    if (hideMarker) {
//                        marker.setVisible(false);
//                    } else {
//                        marker.setVisible(true);
//                    }
//                }
//            }
//        });
//    }



    @Override
    public void onStop()
    {
        stoptimertask();
        super.onStop();
    }
    public void startTimer() {

        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 1000ms the TimerTask will run every 3000ms
        timer.schedule(timerTask, 5000, 9000); //
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_pessoal, container, false);

            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
        */
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position== 0) {
                return Fragment_Main.newInstance();
            }
            else if (position== 1)
                return Fragment_Pessoal.newInstance();
            else
                return Fragment_Option.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "GERAL";
                case 1:
                    return "PESSOAL";
                case 2:
                    return "OPÇÕES";
            }
            return null;
        }
    }
}
