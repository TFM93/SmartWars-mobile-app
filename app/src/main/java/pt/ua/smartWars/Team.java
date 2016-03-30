package pt.ua.smartWars;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by drcc on 22/03/16.
 */
public class Team extends AppCompatActivity{

    //paintmonitor

    @InjectView(R.id.verify)
    Button _verify;
    @InjectView(R.id.linearLayout1)
    LinearLayout _linearLayout1;
    @InjectView(R.id.imageView)
    ImageView _imageLogo;
    @InjectView(R.id.red_team)
    Button _btn_red;

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
                _linearLayout1.setVisibility(View.VISIBLE);
                _imageLogo.setVisibility(View.VISIBLE);

                final ImageView iv = (ImageView) findViewById(R.id.imageView);
                final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
                final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);

                iv.startAnimation(an);
                an.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        iv.startAnimation(an2);
                        //finish();
                        //Intent i = new Intent(SplashActivity.this, Auth.class);
                        //startActivity(i);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });



        _btn_red.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent i = new Intent(Team.this, Gaming.class);

                startActivity(i);
                finish();
            }
        });



    }


}




