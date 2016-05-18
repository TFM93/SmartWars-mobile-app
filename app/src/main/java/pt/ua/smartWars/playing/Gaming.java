package pt.ua.smartWars.playing;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
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
import com.kyleduo.switchbutton.SwitchButton;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;

import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import Bio.Library.namespace.BioLib;
import pt.ua.smartWars.OnGameData.FirePlayers;
import pt.ua.smartWars.R;
import userData.userInfo;

public class Gaming extends AppCompatActivity implements BeaconConsumer {


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
    protected static final String TAG = "Beacon";
    private BeaconManager beaconManager;
    final Handler handler = new Handler();

    //private DataReadFragment.DataListener mDataListener;
    //private ProfileFragment.OnProfileInteractionListener mProfileInteractionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaming);
        Firebase.setAndroidContext(this);


        startTimer();

        Log.d("Passs2","aquuii");
        //create gps module
        gps = new GPSModule(Gaming.this);
        //retrieve data location
        ret_ref = new Firebase("https://pei.firebaseio.com/Game/" + FirePlayers.getInstance().getMatch_id() + "/" + FirePlayers.getInstance().getTeam() + "/");
        ret_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " players on team");
                Iterator po = snapshot.getChildren().iterator();
                while (po.hasNext()) {
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
                    if(hRate.equals("")||hRate.equals("-.-"))
                    {
                        hRate="0";
                    }
                    FirePlayers.getInstance().setTeam_pos(pId,Double.parseDouble(x),Double.parseDouble(y),Integer.parseInt(hRate));
                    FirePlayers.getInstance().setTeam_hr(pId,Integer.parseInt(hRate));
                }


                Log.d("RET", "data changed");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (FirePlayers.getInstance().getTeam().equals("RED")) {
            assert toolbar != null;
            toolbar.setBackgroundColor(getResources().getColor(R.color.dark_red));
        }

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



        // ABOUT BEACON
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.bind(this);

        // BLUETOOTH TO BEACON

        /*
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.disable()) {
            mBluetoothAdapter.enable();
            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth Enable", Toast.LENGTH_SHORT);
            toast.show();
        }
        */
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, org.altbeacon.beacon.Region region) {

                Log.i(TAG, "PASSA AQUI");


                if (beacons.size() > 0) {

                    Log.i(TAG, "DISTANCE= " + beacons.iterator().next().getDistance() + " meters.");
                    Log.i(TAG, "UUID= " + beacons.iterator().next().getId1());


                }
            }
        });


        try {
            beaconManager.startRangingBeaconsInRegion(new org.altbeacon.beacon.Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }


    private final Handler dataHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.d("now", "yes");
            switch (msg.what) {
                case BioLib.MESSAGE_READ:
                    Log.d("RECEIVED: ", "" + msg.arg1);
                    break;
                case BioLib.MESSAGE_DATA_UPDATED:
                    BioLib.Output out = (BioLib.Output) msg.obj;
                    Log.d("Battery", "" + out.battery);
                    Log.d("Pulse", "" + out.pulse);
                    FirePlayers.getInstance().setTeam_hr(userInfo.getInstance().getUid(), out.pulse);
                    dataFreq++;
                    if (dataFreq == 40) {
                        dataFreq = 0;
                        //new SendtoDBTask().execute("1", out.pulse + "", user.getId() + "");
                        mDataListener.updateBatteryText(out.battery);
                        mDataListener.updatePulseText(out.pulse);
                    }
                    break;
            }
        }
    };


    public void changeToOn(View view) {
        SwitchButton device_switch = (SwitchButton) findViewById(R.id.device_switch);

        EditText address_dev = (EditText) findViewById(R.id.add_dev);

        String address = null;
        if (address_dev != null) {
            address = "00:23:FE:00:0B:52";//address_dev.getText().toString();
        }
        System.out.println(address);


        if (!device_switch.isChecked()) {
            try {
                lib = new BioLib(this, dataHandler);
                lib.Connect(address, 5);

            } catch (Exception e) {

                Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_LONG).show();
                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //builder.setMessage("Connection failed!").setNeutralButton("OK", null);
                //builder.create();
                //builder.show();

                device_switch.setChecked(false);
            }
        } else {
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




    public void startTimer() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            //get coordinates
                            double latitude = gps.getLatitude();
                            double longitude = gps.getLongitude();
                            FirePlayers.getInstance().setTeam_pos(userInfo.getInstance().getUid(), latitude, longitude,0);

                            Log.d("TIME", "sending lat:" + latitude + " long:" + longitude);
                            String path = "https://pei.firebaseio.com/Game/" + FirePlayers.getInstance().getMatch_id() + "/" + FirePlayers.getInstance().getTeam() + "/" + userInfo.getInstance().getUid() + "/";
                            Log.d("PATH",path);
                            Firebase ref = new Firebase(path);
                            ref.setValue(FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()));


                        } catch (Exception e) {

                            System.out.println("Erro crl"  + e.toString());
                        }

                    }
                });
            }
        };

        timer.schedule(doAsyncTask, 0, 3000);

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
