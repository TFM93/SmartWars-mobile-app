package pt.ua.smartWars.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.ua.smartWars.menus.Intro;
import pt.ua.smartWars.R;
import userData.userInfo;

public class Auth extends AppCompatActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Button loginButtonMP; //login button for mail/passw authentication
    private TextView registerButton;//register with mail/passw authentication
    private EditText mail;
    private EditText passw;
    private AccessTokenTracker accessTokenTracker;


    private static final String TAG = "Login_Screen";
    private static final int REQUEST_SIGNUP = 0;
    private static boolean valid = true;

    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.login_button)
    Button _loginButton;
    @InjectView(R.id.btn_login)
    Button _loginButtonMP;
    @InjectView(R.id.link_signup)
    TextView _signupLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);//FIREBASE CONTEXT
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };
        updateWithToken(AccessToken.getCurrentAccessToken());
        if (!userInfo.getInstance().isLoggedIn()) {

            //LoginManager.getInstance().logOut();
            setContentView(R.layout.activity_auth);
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);

            loginButtonMP = (Button) findViewById(R.id.btn_login);
            registerButton = (TextView) findViewById(R.id.link_signup);
            mail = (EditText) findViewById(R.id.input_email);
            passw = (EditText) findViewById(R.id.input_password);

            EnableLoginFacebook();
            ButterKnife.inject(this);

            _loginButtonMP.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    loginMP();
                }
            });

            _signupLink.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Start the Signup activity
                    Intent intent = new Intent(getApplicationContext(), Register.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }
            });
        }
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {//if authenticated
            userInfo.getInstance().setLoggedIn(true);
            userInfo.getInstance().setLoggedInWith("FACEBOOK");
            registerButton.setClickable(false);
            loginButton.setClickable(false);
            loginButtonMP.setClickable(false);
            Intent i = new Intent(Auth.this, Intro.class);
            startActivity(i);
        } else //if not authenticated
        {
            if (userInfo.getInstance().getLoggedInWith().equals("FACEBOOK")) {
                userInfo.getInstance().setLoggedIn(false);
                userInfo.getInstance().setLoggedInWith("NONE");
                registerButton.setClickable(true);
                loginButton.setClickable(true);
                loginButtonMP.setClickable(true);
            }
        }
    }

    private void loginMP() {
        userInfo.getInstance().getRef().authWithPassword(mail.getText().toString(), passw.getText().toString(),
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        // Authentication just completed successfully :)
                        System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("provider", authData.getProvider());
                        if (authData.getProviderData().containsKey("displayName")) {
                            map.put("displayName", authData.getProviderData().get("displayName").toString());
                        }
                        userInfo.getInstance().getRef().child("users").child(authData.getUid()).setValue(map);
                        userInfo.getInstance().setLoggedIn(true);
                        userInfo.getInstance().setLoggedInWith("MAILPW");
                        userInfo.getInstance().setUid(authData.getUid());

                        Context context = getApplicationContext();
                        CharSequence text = "LoggedIn successfully";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent i = new Intent(Auth.this, Intro.class);
                        startActivity(i);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        registerButton.setClickable(true);
                        loginButton.setClickable(true);
                        loginButtonMP.setClickable(true);
                        switch (error.getCode()) {
                            case FirebaseError.INVALID_EMAIL:
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                // handle a non existing user
                                Log.d("ERROR", "user not exists");
                                //add highlight on mail
                                mail.setError("USER DOES NOT EXIST");
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                // handle an invalid password
                                Log.d("ERROR", "password invalid");
                                //add highlight on password
                                passw.setError("INVALID PASSWORD!");
                                break;
                            default:
                                // handle other errors
                                Log.d("ERROR", error.toString());
                                break;
                        }
                    }
                });
    }


    public void login() {
        Log.d(TAG, "Login");

        valid = true;
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Auth.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        Firebase myFirebaseRef = new Firebase("https://paintmonitor.firebaseio.com");
        myFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                System.out.println("--> ERRO LOGIN!!!");
                valid = false;
            }
        });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (valid)
                            onLoginSuccess();
                        else
                            onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent myIntent = new Intent(this, Intro.class);
        Auth.this.startActivity(myIntent);
        //finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private void EnableLoginFacebook() {


        //register callback
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (loginResult.getAccessToken().getToken() != null) {
                    userInfo.getInstance().getRef().authWithOAuthToken("facebook", loginResult.getAccessToken().getToken(), new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // The Facebook user is now authenticated with your Firebase app

                            System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("provider", authData.getProvider());
                            if (authData.getProviderData().containsKey("displayName")) {
                                map.put("displayName", authData.getProviderData().get("displayName").toString());
                            }
                            userInfo.getInstance().getRef().child("users").child(authData.getUid()).setValue(map);
                            userInfo.getInstance().setUid(authData.getUid());


                            userInfo.getInstance().setLoggedIn(true);
                            userInfo.getInstance().setLoggedInWith("FACEBOOK");

                            Toast.makeText(getBaseContext(), "LoggedIn with " + userInfo.getInstance().getLoggedInWith(), Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(Auth.this, Intro.class);
//                            startActivity(i);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // there was an error
                            Log.d("AUTH", "ERROR firebase");
                            LoginManager.getInstance().logOut();
                            userInfo.getInstance().setLoggedIn(false);
                            userInfo.getInstance().setLoggedInWith("NONE");
                            userInfo.getInstance().setUid(null);
                            Intent i = new Intent(Auth.this, Auth.class);
                            startActivity(i);

                        }
                    });


                }
            }

            @Override
            public void onCancel() {
                userInfo.getInstance().setLoggedIn(false);
                userInfo.getInstance().setLoggedInWith(null);
                Toast.makeText(getBaseContext(), "LoggedIn with " + userInfo.getInstance().getLoggedInWith(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                userInfo.getInstance().setLoggedIn(false);
                userInfo.getInstance().setLoggedInWith("NONE");
                Toast.makeText(getBaseContext(), "LoggedIn with " + userInfo.getInstance().getLoggedInWith(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (userInfo.getInstance().isLoggedIn()) {
            Intent i = new Intent(Auth.this, Intro.class);
            startActivity(i);
        }
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
            }
            this.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
