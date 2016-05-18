package pt.ua.smartWars.menus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.ua.smartWars.OnGameData.FirePlayers;
import pt.ua.smartWars.R;
import pt.ua.smartWars.playing.Gaming;
import userData.userInfo;

public class NewGame extends AppCompatActivity {

    //teste

    @InjectView(R.id.textView4)
    TextView _code_text;

    @InjectView(R.id.button4)
    Button _goButton;

    String randomNum;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        Firebase.setAndroidContext(this);
        ButterKnife.inject(this);
        _goButton.setClickable(false);
        try {
            randomNum = "" + 1 + (int)(Math.random() * 9999);
            createFirebaseGame(randomNum,"RED");
            generateQrCode(randomNum);
            _code_text.setText(randomNum);
            _goButton.setClickable(true);
            _goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(NewGame.this, Gaming.class);
                    startActivity(i);
                }
            });
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void createFirebaseGame(String randomNum,String team) {
        String path = "https://pei.firebaseio.com/Game/" +randomNum+"/" +team+"/"+userInfo.getInstance().getUid()+"/";
        Firebase ref = new Firebase(path);
        FirePlayers.getInstance().setTeam_pos(userInfo.getInstance().getUid(), 0, 0, 0);
        FirePlayers.getInstance().setTeam(team);
        FirePlayers.getInstance().setMatch_id(randomNum);
        ref.setValue(FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()));

        //create the flag on databse
        String path2 = "https://pei.firebaseio.com/Game/" +randomNum+"/flag/";
        Firebase ref2 = new Firebase(path2);
        ref2.setValue("0");
    }



    public void generateQrCode(String myCodeText) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(myCodeText, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}