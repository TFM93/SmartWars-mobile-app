package pt.ua.smartWars;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.batch.android.Batch;
import com.batch.android.Config;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.GoogleMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import userData.userInfo;

public class Intro extends AppCompatActivity {


    @InjectView(R.id.join_team)
    Button _joinTeam;


    //paintmonitor

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private String userLog;
    private String pathToFirebase;
    private GoogleMap mMap;
    private com.facebook.login.widget.LoginButton loginButton;
    private Button btn_logout;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //lsghsldig
        Firebase.setAndroidContext(this);
        ButterKnife.inject(this);

        //facebook sdk init
        FacebookSdk.sdkInitialize(getApplicationContext());

        loginButton = (com.facebook.login.widget.LoginButton)findViewById(R.id.login_buttonIntro);
        btn_logout = (Button)findViewById(R.id.logout);

        if(userInfo.getInstance().getLoggedInWith().equals("FACEBOOK")) {
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                    if (newAccessToken == null )//not authenticated
                    {
                        userInfo.getInstance().setLoggedInWith("NONE");
                        userInfo.getInstance().setLoggedIn(false);
                        Intent i = new Intent(Intro.this, Auth.class);
                        startActivity(i);
                    }
                }
            };
            loginButton.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.GONE);
        }
        else
        {
            loginButton.setVisibility(View.GONE);
            btn_logout.setVisibility(View.VISIBLE);
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo.getInstance().setLoggedInWith("NONE");
                userInfo.getInstance().setLoggedIn(false);
                Intent i = new Intent(Intro.this, Auth.class);
                startActivity(i);
            }
        });

        _joinTeam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent i = new Intent(Intro.this, Team.class);
                startActivity(i);
                finish();
            }
        });



        //batch setup config
        Batch.Push.setGCMSenderId("40170863066");
        Batch.setConfig(new Config("DEV56D70ED21459D8880EF3180D288"));





        // Email do user com o login feito
        Firebase myFirebaseRef = new Firebase("https://paintmonitor.firebaseio.com");
        userLog = myFirebaseRef.getAuth().getUid();
        //final Button button = (Button) findViewById(R.id.button);
        //final Button button2 = (Button) findViewById(R.id.button2);
        //final Button button3 = (Button) findViewById(R.id.button3);
        final TextView tv1 = (TextView) findViewById(R.id.textView);

        //tv1.setText(myFirebaseRef.getAuth().getProviderData().get("email").toString());


        //final LinearLayout LL1 = (LinearLayout) findViewById(R.id.linearLayout1);


    }

}




