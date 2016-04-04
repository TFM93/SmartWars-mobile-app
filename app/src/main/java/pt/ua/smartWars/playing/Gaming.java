package pt.ua.smartWars.playing;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
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

import com.firebase.client.Firebase;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.Timer;
import java.util.TimerTask;

import Bio.Library.namespace.BioLib;
import pt.ua.smartWars.OnGameData.FirePlayers;
import pt.ua.smartWars.R;
import userData.userInfo;

public class Gaming extends AppCompatActivity implements LocationListener {


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

    final Handler handler = new Handler();

    //private DataReadFragment.DataListener mDataListener;
    //private ProfileFragment.OnProfileInteractionListener mProfileInteractionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaming);
        Firebase.setAndroidContext(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Connection failed!")
                        .setNeutralButton("OK", null);
                builder.create();
                builder.show();

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

    public void stoptimertask(View v) {
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
//                        //show the toast
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getApplicationContext(), "sending", duration);
                        toast.show();
                        Log.d("TIME","sending");
                        String path = "https://paintmonitor.firebaseio.com/Game/" +FirePlayers.getInstance().getMatch_id()+"/" +FirePlayers.getInstance().getTeam()+"/";
                        Firebase ref = new Firebase(path);
                        ref.setValue(FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()));
                    }
                });
            }
        };
    }
    public void startTimer() {

        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 1000, 3000); //
    }

    @Override
    public void onLocationChanged(Location location) {
        FirePlayers.getInstance().setTeam_pos(userInfo.getInstance().getUid(), location.getLatitude(), location.getLongitude());//insert current user position on FirePlayers

        FirePlayers.getInstance().setTeam_pos(userInfo.getInstance().getUid(), 0, 0);
        //ref.setValue(FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
            else if (position==2)
                return Fragment_Option.newInstance();
            else
                Log.d("FRAG","possition getItem bigger than 2");
                return null;
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
