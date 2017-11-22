package il.co.yoman.yoman;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.WAKE_LOCK;
import static il.co.yoman.yoman.ContactUs.isCountactusShown;
import static il.co.yoman.yoman.accountsFrag.drawer;
import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;

public class welcomeScreen extends AppCompatActivity {
    TextView                                            nextPage;
    protected static String                             token, mobileNumber;
    private SharedPreferences                           prefs;
    private static final int                            PERMISSION_REQUEST_CODE  = 200;
    public static boolean                               isAccountsShown =false;
    public static boolean                               isWebViewShown = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation
        if (!checkPermission())
        requestPermission();

        if (isOnline() && checkIfUserHaveSavedToken() && checkPermission()) {  // if user already sign in --> move to accounts
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            accountsFragTransfer();
        } else {// full screen + visible keyboard
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.aawelcome_screen);
        nextPage        =        findViewById(R.id.continue1);
//        nextPage.setPaintFlags(nextPage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermission()) {
                    requestPermission();
                }else nextActivity();

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
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.welcomeContainer, fragInfo).
                commit();
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
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_WIFI_STATE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WAKE_LOCK);
//        int result4= ContextCompat.checkSelfPermission(getApplicationContext(), SYSTEM_ALERT_WINDOW);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED ;//&& result4 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{INTERNET, ACCESS_WIFI_STATE,ACCESS_NETWORK_STATE,
                WAKE_LOCK }, PERMISSION_REQUEST_CODE);
    }
    public static void setIsAccountsShown(boolean isAccountsShown) {
        welcomeScreen.isAccountsShown = isAccountsShown;
    }
    public static void setIsWebViewShown(boolean isWebViewShown) {
        welcomeScreen.isWebViewShown = isWebViewShown;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean internetAccapted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean wifiSAtateAccapted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean networkStateAccapted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean wakeAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
//                    boolean alertAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                    if (!(internetAccapted && wifiSAtateAccapted && networkStateAccapted && wakeAccepted )){//&&alertAccepted)){

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("יש לאשר את ההרשאות כדי להמשיך להשתמש באפליקציה",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{INTERNET, ACCESS_WIFI_STATE,ACCESS_NETWORK_STATE,WAKE_LOCK},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(welcomeScreen.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
        if (isWebViewShown || isCountactusShown){
            accountsFragTransfer();
        }

        else super.onBackPressed();
    }
}
