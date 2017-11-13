package il.co.yoman.yoman;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import il.co.yoman.yoman.DataSource.ServerReq;

import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;
import static il.co.yoman.yoman.services.NetworkStateReceiver.makeToast;
import static il.co.yoman.yoman.welcomeScreen.mobileNumber;
import static il.co.yoman.yoman.welcomeScreen.setMobileNumber;
import static il.co.yoman.yoman.welcomeScreen.setToken;
import static il.co.yoman.yoman.welcomeScreen.token;

public class loginActivity extends AppCompatActivity {
    private String              devieceToken, android_id, msg;
    private Button              submit;
    private EditText            edPhoneNumber;
    private ProgressBar         progressbar;
    private final String        Loginurl = "http://mdev1.yoman.co.il/api/Client/Login";
    private ServerReq           client = new ServerReq(Loginurl);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation
        if (getSupportActionBar() != null) { //hide action bar
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        setContentView(R.layout.activity_login);
        progressbar      =   findViewById(R.id.progressBar);
        edPhoneNumber    =   findViewById(R.id.edPhoneNumber);
        submit           =   findViewById(R.id.btnLogin);
        devieceToken     =   FirebaseInstanceId.getInstance().getToken();
        android_id       =   Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        progressbar.setVisibility(View.INVISIBLE);

        sendNumber();
    }
    public void sendNumber() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOnlineReciver && isVerifyMobileNumber()) {
                    getToken p = new getToken();
                    p.start();
                    hideKeyboard();
                }
            }
        });
    }
    private boolean isVerifyMobileNumber() {
        setMobileNumber(edPhoneNumber.getText().toString());
        if (mobileNumber.isEmpty() || mobileNumber.length() != 10) {
            edPhoneNumber.setError("מספר טלפון לא תקין");
            edPhoneNumber.setText(null);
            return false;
        } else {
            progressbar.setVisibility(View.VISIBLE);
            edPhoneNumber.setError(null);
            edPhoneNumber.setText(null);
            return true;
        }
    }
    private void verifyFragTransfer() {
        Intent i = new Intent(loginActivity.this, verifyActivity.class);
        i.putExtra("token", token);
        i.putExtra("mobileNumber", mobileNumber);
        startActivity(i);
        finish();
    }
    private String parseServerMsg(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        String msg = root.getString("Message");
        return msg;
    }
    private void hideKeyboard(){
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
    class getToken extends Thread {
        @Override
        public void run() {
            try {
                client.AddParam("mobileNumber", mobileNumber);
                client.Execute(ServerReq.RequestMethod.POST);
            } catch (Exception e) {
                makeToast("התרחשה שגיאה, נסה שנית..", 10,getApplicationContext());
                e.printStackTrace();
                return;
            }
            int responseCode = client.getResponseCode();
            try {
                msg = parseServerMsg(client.getResponse());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                String response = client.getResponse();
                try {
                    setToken(parseToken(response));
                    verifyFragTransfer();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.INVISIBLE);
                        makeToast(msg + ",\n נסה מאוחר יותר ", 10, getApplicationContext());

                    }
                }));
            }
        }
        private String parseToken(String json) throws JSONException {
            JSONObject root = new JSONObject(json);
            JSONObject data = root.getJSONObject("data");
            String token = data.getString("token");
            return token;
        }}
}