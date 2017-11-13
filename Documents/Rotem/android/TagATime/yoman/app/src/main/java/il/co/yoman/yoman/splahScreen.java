package il.co.yoman.yoman;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class splahScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGHT = 2; // seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        if (getSupportActionBar() != null) { //hide action bar
            getSupportActionBar().hide();
        }
        setContentView(R.layout.aactivity_splah_screen);

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(1000*SPLASH_DISPLAY_LENGHT);
                    Intent intent = new Intent(getApplicationContext(), welcomeScreen.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void fullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

}

