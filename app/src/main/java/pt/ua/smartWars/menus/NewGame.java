package pt.ua.smartWars.menus;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        _goButton.setClickable(false);
        Firebase.setAndroidContext(this);
        ButterKnife.inject(this);

        try {
            randomNum = "" + 1000 + (int)(Math.random() * 9999);
            createFirebaseGame(randomNum,"RED");
            generateQrCode(randomNum);
            _code_text.setText(randomNum);
            _goButton.setClickable(true);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void createFirebaseGame(String randomNum,String team) {
        String path = "https://paintmonitor.firebaseio.com/Game/" +randomNum+"/" +team+"/";
        Firebase ref = new Firebase(path);
        FirePlayers.getInstance().setTeam_pos(userInfo.getInstance().getUid(),0,0);
        FirePlayers.getInstance().setTeam(team);
        FirePlayers.getInstance().setMatch_id(randomNum);
        ref.setValue(FirePlayers.getInstance().getTeam_pos(userInfo.getInstance().getUid()));

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