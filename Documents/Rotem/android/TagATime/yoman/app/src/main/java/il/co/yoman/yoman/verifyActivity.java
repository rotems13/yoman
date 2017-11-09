package il.co.yoman.yoman;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import il.co.yoman.yoman.DataSource.ServerReq;

import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;
import static il.co.yoman.yoman.services.NetworkStateReceiver.makeToast;

public class verifyActivity extends AppCompatActivity {
    private String               loginToken, mobileNumber, verifyNum, verifyToken, msg ;
    private Button               verify;
    private SharedPreferences    prefs;
    private EditText             edVerify;
    private ProgressBar          progressbar;
    private final String         verifyUrl = "http://mdev1.yoman.co.il/api/Client/verifyLogin";
    private ServerReq client = new ServerReq(verifyUrl);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) { //hide action bar
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_verify);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            loginToken      =   getIntent().getStringExtra("token");
            mobileNumber    =   getIntent().getStringExtra("mobileNumber");
            verify          =   findViewById(R.id.btnVerify);
            edVerify        =   findViewById(R.id.edVerifyNumber);
            progressbar     =   findViewById(R.id.progressBar);
            progressbar.setVisibility(View.INVISIBLE);

            sendVerifyNumber();
        }
    public void sendVerifyNumber() {
        verify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isOnlineReciver) {
                    verifyNum = edVerify.getText().toString();
                    getVerifyNumber p = new getVerifyNumber();
                    p.start();
                    hideKeyboard();
                }
            }
        });
    }
    class getVerifyNumber extends Thread {
        @Override
        public void run() {
            try {
                client.AddHeader("TATtkn", loginToken);
                client.AddParam("mobileNumber", mobileNumber);
                client.AddParam("tempPass", verifyNum);
                client.Execute(ServerReq.RequestMethod.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int responseCode = client.getResponseCode();
            if (responseCode == 200) {
                progressbar.setVisibility(View.INVISIBLE);
                String response = client.getResponse();
                try {
                    msg = parseServerMsg(client.getResponse());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    verifyToken = parseToken(response);
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
        editor.putString( mobileNumber  , "mobileNumber");
        editor.commit();
    }
    private void AccountsTransfer() {
        Bundle bundle = new Bundle();
        bundle.putString("token", verifyToken);
        bundle.putString("mobileNumber", mobileNumber);
        accountsFrag fragInfo = new accountsFrag();
        fragInfo.setArguments(bundle);
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
        startActivity(new Intent(verifyActivity.this, loginActivity.class));
        finish();
    }
}
