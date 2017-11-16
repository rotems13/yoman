package il.co.yoman.yoman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static il.co.yoman.yoman.accountsFrag.drawer;
import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;

public class welcomeScreen extends AppCompatActivity {
    TextView                            nextPage;
    protected static String             token, mobileNumber;
    private SharedPreferences           prefs;

    public static boolean isIsAccountsShown() {
        return isAccountsShown;
    }

    public static void setIsAccountsShown(boolean isAccountsShown) {
        welcomeScreen.isAccountsShown = isAccountsShown;
    }

    public static boolean               isAccountsShown =false;

    public static boolean isIsWebViewShown() {
        return isWebViewShown;
    }

    public static void setIsWebViewShown(boolean isWebViewShown) {
        welcomeScreen.isWebViewShown = isWebViewShown;
    }

    public static boolean isWebViewShown = false ;

//    private static String devieceToken;
//    private static String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation

        if (isOnline() && checkIfUserHaveSavedToken()) {  // if user already sign in --> move to accounts
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            accountsFragTransfer();
        } else {// full screen + visible keyboard
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome_screen);
        nextPage        =        findViewById(R.id.continue1);
        nextPage.setPaintFlags(nextPage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity();
            }
        });
    }
    private void nextActivity() {
        if (isOnline()) {
            if (checkIfUserHaveSavedToken()) {  // if user already sign in  --> move to accounts
                accountsFragTransfer();
            } else {// go to login class
                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                    startActivity(intent);
                    finish();
            }
        }
    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
    private void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(welcomeScreen.this);
        dialog.setCancelable(false);
        dialog.setTitle("חיבור אינטרנט");
        dialog.setMessage("שימוש באפליקציה דורש חיבור לאינטרנט");
        dialog.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Delete".
            }
        })
                .create();
        final AlertDialog alert = dialog.create();
        alert.show();
    }
    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    private boolean checkIfUserHaveSavedToken() {
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
    private void accountsFragTransfer() {
        Bundle bundle = new Bundle();
        bundle.putString("token", getToken());
        bundle.putString("mobileNumber", getMobileNumber());
        accountsFrag fragInfo = new accountsFrag();
        fragInfo.setArguments(bundle);
        isAccountsShown = true;
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.welcomeContainer, fragInfo).
                commit();
//        Intent i = new Intent(welcomeScreen.this, MainActivity.class);
//        i.putExtra("token", token);
//        i.putExtra("mobileNumber", mobileNumber);
//        startActivity(i);
//        finish();

    }

    public static String getMobileNumber() {
        return mobileNumber;
    }
    public static void setMobileNumber(String mobileNumber) {
        welcomeScreen.mobileNumber = mobileNumber;
    }
    public static String getToken() {
        return token;
    }
    public static void setToken(String token) {
        welcomeScreen.token = token;
    }

    @Override
    public void onBackPressed() {
        if (isAccountsShown){
            if (drawer.isDrawerVisible(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
            else {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            }
        }
        if (isWebViewShown){
            accountsFragTransfer();
        }
        else super.onBackPressed();
    }
}
