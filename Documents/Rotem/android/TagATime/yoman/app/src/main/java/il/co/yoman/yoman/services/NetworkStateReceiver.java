package il.co.yoman.yoman.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import il.co.yoman.yoman.R;


/**
 * Created by rotems on 26/10/2017.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    static Snackbar snackbar; //make it as global
    public static boolean isOnlineReciver = true;
    private AlertDialog alertDialog;
    public NetworkStateReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null) {
            isOnlineReciver = false;
            makeToast("לא קיים חיבור לאינטרנט", 10, context);

        } else
            isOnlineReciver = true;


    }
    public static void makeToast(String text,int durationInSecond, Context context){
        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        Spannable centeredText = new SpannableString(text);
        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0, text.length() - 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        View vieew = toast.getView();
        vieew.setBackgroundResource(R.drawable.toast);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(vieew);


        int toastDurationInMilliSeconds = durationInSecond * 1000;  //10 sec
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            public void onFinish() {
                if (isOnlineReciver = true)
                    toast.cancel();
            }
        };

        // Show the toast and starts the countdown
        toast.show();
        toastCountDown.start();

    }


}




