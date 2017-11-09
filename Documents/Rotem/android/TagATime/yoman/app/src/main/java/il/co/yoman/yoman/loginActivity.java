package il.co.yoman.yoman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import il.co.yoman.yoman.DataSource.ServerReq;

import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;
import static il.co.yoman.yoman.services.NetworkStateReceiver.makeToast;

public class loginActivity extends AppCompatActivity {
    private String              mobileNumber, token, devieceToken, android_id, msg;
    private Button              submit;
    private EditText            edPhoneNumber;
    private ProgressBar         progressbar;
    private SharedPreferences   prefs;
    private final String        Loginurl = "http://mdev1.yoman.co.il/api/Client/Login";
    private ServerReq           client = new ServerReq(Loginurl);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) { //hide action bar
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);

        if (isOnline() && verifyUser()) {  // if user already sign in --> move to accounts
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            accountsFragTransfer();
        } else {// full screen + visible keyboard
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        setContentView(R.layout.activity_login);
        progressbar      =   findViewById(R.id.progressBar);
        edPhoneNumber    =   findViewById(R.id.edPhoneNumber);
        submit           =   findViewById(R.id.btnLogin);
        devieceToken     =   FirebaseInstanceId.getInstance().getToken();
        android_id       =   Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        progressbar.setVisibility(View.INVISIBLE);

        sendNumber();

        // sendRegistrationToServer(devieceToken);
    }
    public void sendNumber() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOnline() && isVerifyMobileNumber()) {
                    getToken p = new getToken();
                    p.start();
                    hideKeyboard();
                }
            }
        });
    }
    private boolean isVerifyMobileNumber() {
        mobileNumber = edPhoneNumber.getText().toString();
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
                    token = parseToken(response);
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
        }
    }
    private void accountsFragTransfer() {
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        bundle.putString("mobileNumber", mobileNumber);
        accountsFrag fragInfo = new accountsFrag();
        fragInfo.setArguments(bundle);


        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.containerLogin, fragInfo).
                commit();

    }
    private boolean verifyUser() {
        prefs = getSharedPreferences("pref", 0);
        Map<String, String> hmap;

        hmap = (Map<String, String>) prefs.getAll();
        Set set2 = hmap.entrySet();
        Iterator iterator2 = set2.iterator();
        if (iterator2.hasNext()) {

            String tempphone = iterator2.next().toString();
            String[] parts = tempphone.split("=");
            mobileNumber = parts[0];
            String temptoken = iterator2.next().toString();
            String[] partst = temptoken.split("=");
            token = partst[0];

        }

        if (mobileNumber != null && token != null)
            return true;
        else
            return false;


    }
    private void verifyFragTransfer() {
        Intent i = new Intent(loginActivity.this, verifyActivity.class);

        i.putExtra("token", token);
        i.putExtra("mobileNumber", mobileNumber);
        startActivity(i);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            isOnlineReciver = true;
            return true;
        } else {
            showDialog();
            isOnlineReciver = false;
            return false;
        }
    }
    public void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(loginActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("No internet connection");
        dialog.setMessage("check your connection.");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Delete".
            }
        })
                .create();

        final AlertDialog alert = dialog.create();
        alert.show();


    }
    private String parseServerMsg(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        String msg = root.getString("Message");
        return msg;
    }
    public void hideKeyboard(){
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }



}