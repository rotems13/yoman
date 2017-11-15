package il.co.yoman.yoman;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import il.co.yoman.yoman.DataSource.ServerReq;
import me.philio.pinentry.PinEntryView;

import static il.co.yoman.yoman.accountsFrag.drawer;
import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;
import static il.co.yoman.yoman.services.NetworkStateReceiver.makeToast;
import static il.co.yoman.yoman.welcomeScreen.getMobileNumber;
import static il.co.yoman.yoman.welcomeScreen.getToken;
import static il.co.yoman.yoman.welcomeScreen.isFragment1Shown;

public class verifyActivity extends AppCompatActivity {
    protected static String      verifyToken;
    private String               verifyNumber, msg ;
    private Button               verify;
    private TextView             counter;
    private SharedPreferences    prefs;
    private PinEntryView         edVerify;
    private ProgressBar          progressbar;
    private final String         verifyUrl = "http://mdev1.yoman.co.il/api/Client/verifyLogin";
    private ServerReq            client  = new ServerReq(verifyUrl);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) { //hide action bar
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_verify);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation

//            loginToken      =   getIntent().getStringExtra("token");
//            mobileNumber    =   getIntent().getStringExtra("mobileNumber");
            verify          =   findViewById(R.id.btnVerify);
            edVerify        =   findViewById(R.id.edVerifyNumber);
            progressbar     =   findViewById(R.id.progressBar);
            progressbar.setVisibility(View.INVISIBLE);
            counter         =   findViewById(R.id.counter);
            counter.setPaintFlags(counter.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            setCounter(counter);

        sendVerifyNumber();
        }

    public void sendVerifyNumber() {
        verify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isOnlineReciver && getVerifyNumber()) {
                    hideKeyboard();
                    verifyNumber = edVerify.getText().toString();
                    getVerifyNumber p = new getVerifyNumber();
                    p.start();
                }
            }
        });
    }
    private boolean getVerifyNumber() {
        verifyNumber = edVerify.getText().toString();
        if (verifyNumber.isEmpty() || verifyNumber.length() != 4) {
            makeToast("קוד אימות לא תקין",5 , getApplicationContext());
            return false;
        } else {
            progressbar.setVisibility(View.VISIBLE);
            verify.setError(null);
            return true;
        }
    }
    public void hideKeyboard(){
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void saveData(){
        prefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( verifyToken  , "verifyToken");
        editor.putString( getMobileNumber()  , "mobileNumber");
        editor.commit();
    }
    private void AccountsTransfer() {
        Bundle bundle = new Bundle();
        bundle.putString("token", verifyToken);
        bundle.putString("mobileNumber", getMobileNumber());
        accountsFrag fragInfo = new accountsFrag();
        fragInfo.setArguments(bundle);
        isFragment1Shown = true;
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.containerVerifyac, fragInfo).
                commit();


    }
    private String parseToken(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONObject data = root.getJSONObject("data");
        String token = data.getString("token");
        return token;
    }
    private String parseServerMsg(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        String msg = root.getString("Message");
        return msg;
    }
    @Override
    public void onBackPressed() {
        if (isFragment1Shown){
            if (drawer.isDrawerVisible(GravityCompat.START))
                drawer.closeDrawer(GravityCompat.START);
            else {
                startActivity(new Intent(verifyActivity.this, loginActivity.class));
                finish();
            }
        }

    }
    class getVerifyNumber extends Thread {
        @Override
        public void run() {
            try {
                client.AddHeader("TATtkn", getToken());
                client.AddParam("mobileNumber", getMobileNumber());
                client.AddParam("tempPass", verifyNumber);
                client.Execute(ServerReq.RequestMethod.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int responseCode = client.getResponseCode();
            if (responseCode == 200) {
                String response = client.getResponse();
                try {
                    msg = parseServerMsg(client.getResponse());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    setVerifyToken(parseToken(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                saveData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        getWindow().clearFlags(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


                    }
                });
                AccountsTransfer();

            } else  {
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.INVISIBLE);
                        makeToast(msg + ",\n נסה מאוחר יותר ", 10, getApplicationContext());
                    }
                }));
            }

        }
    }
    public void setCounter(final TextView counter1) {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter1.setText(millisUntilFinished / 1000 + " שניות");
            }
            public void onFinish() {
                counter1.setText("כעת");
                counter1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }.start();
    }
    public static String getVerifyToken() {
        return verifyToken;
    }
    public static void setVerifyToken(String verifyToken) {
        verifyActivity.verifyToken = verifyToken;
    }

}
