package pt.ua.smartWars.menus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.firebase.client.Firebase;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.ua.smartWars.OnGameData.FirePlayers;
import pt.ua.smartWars.R;
import pt.ua.smartWars.playing.Gaming;
import userData.userInfo;

/**
 * Created by drcc on 22/03/16.
 */
public class Team extends AppCompatActivity{

    //pei

    @InjectView(R.id.verify)
    Button _verify;
    @InjectView(R.id.linearLayout1)
    LinearLayout _linearLayout1;
    @InjectView(R.id.imageView)
    ImageView _imageLogo;
    @InjectView(R.id.blue_team)
    Button _btn_blue;
    @InjectView(R.id.red_team)
    Button _btn_red;
    @InjectView(R.id.btn_qr)
    Button _btn_qr;
    @InjectView(R.id.code_game)
    EditText _code_editText;


    String pathToFirebase;
    String userLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        ButterKnife.inject(this);


        //Batch.Push.setGCMSenderId("40170863066");

        //Batch.setConfig(new Config("DEV56D70ED21459D8880EF3180D288"));

       /* Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );

        Button button = (Button)findViewById(R.id.buttonLike);
        button.setTypeface(font);

*/


        _verify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

               if (!_code_editText.getText().toString().equals(""))
                {

                    _linearLayout1.setVisibility(View.VISIBLE);
                    _imageLogo.setVisibility(View.VISIBLE);


                    //pathToFirebase = "https://paintmonitor.firebaseio.com/games/" + _code_editText.getText();
                    FirePlayers.getInstance().setMatch_id(_code_editText.getText().toString());


                    final ImageView iv = (ImageView) findViewById(R.id.imageView);
                    final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
                    //final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);

                    iv.startAnimation(an);
                    an.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            iv.startAnimation(an);
                            //finish();
                            //Intent i = new Intent(SplashActivity.this, Auth.class);
                            //startActivity(i);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }

            }
        });



        _btn_red.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                  FirePlayers.getInstance().setTeam("RED");
                  addMeToGameFirebase();
                // Start the Signup activity
                Intent i = new Intent(Team.this, Gaming.class);

                startActivity(i);
                //finish();
            }
        });


        _btn_blue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FirePlayers.getInstance().setTeam("BLUE");
                addMeToGameFirebase();


                // Start the Signup activity
                Intent i = new Intent(Team.this, Gaming.class);

                startActivity(i);
                finish();
            }
        });

        _btn_qr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                try {

                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                    startActivityForResult(intent, 0);

                } catch (Exception e) {

                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);

                }
            }
        });

    }

    private void addMeToGameFirebase() {
        Log.d("MATCH_ID",FirePlayers.getInstance().getMatch_id());
        String path = "https://pei.firebaseio.com/Game/" +FirePlayers.getInstance().getMatch_id()+"/" +FirePlayers.getInstance().getTeam()+"/"+userInfo.getInstance().getUid()+"/";
        Firebase ref = new Firebase(path);
        FirePlayers.getInstance().setTeam_pos(userInfo.getInstance().getUid(), 0, 0, 0);
        ref.setValue(FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                _code_editText.setText(contents);
                FirePlayers.getInstance().setMatch_id(contents);

                Button button_verify = (Button) findViewById(R.id.verify);
                button_verify.performClick();

            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }




}




