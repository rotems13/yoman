package il.co.yoman.yoman.Account;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import il.co.yoman.yoman.R;
import il.co.yoman.yoman.accountsFrag;

import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;
import static il.co.yoman.yoman.welcomeScreen.getMobileNumber;
import static il.co.yoman.yoman.welcomeScreen.getToken;

/**
 * A simple {@link Fragment} subclass.
 */
public class webViewFrag extends Fragment {
    private WebView          webView;
    private TextView         title, count, meetingsTag, bottom_Line, futureEvents, descriptionFrag;
    private String           link, strTitle, strFuture, nick, token,mobileNumber;
    public  webViewFrag      newInstance(String link, String title, String description, String future, String nick ) {

        Bundle args = new Bundle();
        args.putString("link", link);
        args.putString("title", title);
        args.putString("description", description);
        args.putString("future", future);
        args.putString("nick", nick);

        webViewFrag fragment = new webViewFrag();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        link             =        getArguments().getString("link");
        strTitle         =        this.getArguments().getString("title");
        strFuture        =        this.getArguments().getString("future");
        nick             =        this.getArguments().getString("nick");
        mobileNumber     =        getMobileNumber();
        token            =        getToken();

        View v           =        inflater.inflate(R.layout.fragment_web_view, container, false);
        webView          =        v.findViewById(R.id.webView);
        title            =        v.findViewById(R.id.titleAccount);
        count            =        v.findViewById(R.id.countAccount);
        futureEvents     =        v.findViewById(R.id.futureEvents);
        descriptionFrag  =        v.findViewById(R.id.businessDescription);
        meetingsTag      =        v.findViewById(R.id.webViewTag);
        bottom_Line      =        v.findViewById(R.id.bottom_Line);

        title.setText(strTitle);
        meetingsTag.setTextColor(Color.WHITE);
        bottom_Line.setBackground( getResources().getDrawable(R.drawable.shadow) );
        count.setText("מס׳ אירועים: " + strFuture);
        futureEvents.setText("הפגישות שלי " + "(" + strFuture +")");


        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isOnlineReciver) {
                    return true;
                }else
                return false;
            }
        });

        config(webView);
        webView.loadUrl(link);
        futureEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToFutureEvents();
                }
            });
        descriptionFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToDescription();
            }
        });
        return v;
    }
    private void config(final WebView webView) {
        //Enable JavaScript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                webView.animate().rotation(0);
//                webView.setBackgroundColor(0xfff);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
              //  webView.animate().rotation(360);
                super.onPageStarted(view, url, favicon);
//                webView.setBackgroundColor(0x0f0);
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                webView.loadUrl(url);
                return true;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    getFragmentManager().
                            beginTransaction().
                            replace(R.id.webContainer, new accountsFrag()).
                            commit();

                    return true;
                }
                return false;
            }
        });
    }
    public void moveToFutureEvents(){
        Bundle bundle = new Bundle();
        bundle.putString("title",strTitle);
        bundle.putString("link",link);
        bundle.putString("token", getToken());
        bundle.putString("nick", nick);
        bundle.putString("future", strFuture);
        bundle.putString("token", token);
        bundle.putString("mobileNumber", mobileNumber);

        bundle.putString("mobileNumber", getMobileNumber());

        futureEvents fragInfo = new futureEvents();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.webContainer, fragInfo).
                commit();


    }
    public void moveToDescription(){
        Bundle bundle = new Bundle();
        bundle.putString("title",strTitle);
        bundle.putString("link",link);
        bundle.putString("nick",nick);
        bundle.putString("token", token);
        bundle.putString("future", strFuture);
        bundle.putString("mobileNumber", mobileNumber);

        il.co.yoman.yoman.Account.description fragInfo = new description();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.webContainer, fragInfo).
                commit();
    }
}
