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

import il.co.yoman.yoman.DataSource.AccountDataSource;
import il.co.yoman.yoman.R;
import il.co.yoman.yoman.accountsFrag;

import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;
import static il.co.yoman.yoman.welcomeScreen.setIsAccountsShown;
import static il.co.yoman.yoman.welcomeScreen.setIsWebViewShown;

/**
 * A simple {@link Fragment} subclass.
 */
public class webViewFrag extends Fragment {
    private WebView          webView;
    private TextView         topRightTitleAccount, webViewFragTitle, bottom_Line, futureEventsFragTitle, descriptionFragTitle;
    private AccountDataSource.Account CurrentAccount;

    public  webViewFrag      newInstance(AccountDataSource.Account CurrentAccount ) {

        Bundle args = new Bundle();
        args.putParcelable("Account", CurrentAccount);

        webViewFrag fragment = new webViewFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setIsAccountsShown(false);
        setIsWebViewShown(true);

        if (container != null) {
            container.removeAllViews();
        }
        CurrentAccount              =        this.getArguments().getParcelable("Account");
        View v                      =        inflater.inflate(R.layout.fragment_web_view, container, false);
        //upper layer
        webView                     =        v.findViewById(R.id.webView);
        topRightTitleAccount        =        v.findViewById(R.id.topRightTitleAccount);
        webViewFragTitle            =        v.findViewById(R.id.webViewFragTitle);
        futureEventsFragTitle       =        v.findViewById(R.id.futureEventsFragTitle);
        descriptionFragTitle        =        v.findViewById(R.id.descriptionFragTitle);
        bottom_Line                 =        v.findViewById(R.id.bottom_line);

        topRightTitleAccount.setText(CurrentAccount.getTitle());
        webViewFragTitle.setTextColor(Color.WHITE);
        bottom_Line.setBackground( getResources().getDrawable(R.drawable.shadow) );
        futureEventsFragTitle.setText("פגישות עתידיות " + "(" + CurrentAccount.getFutureEvents() +")");

        //webView
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
        webView.loadUrl(CurrentAccount.getSiteURL());
        futureEventsFragTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIsWebViewShown(false);
                moveToFutureEvents();
                }
            });
        descriptionFragTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIsWebViewShown(false);
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
        bundle.putParcelable("Account", CurrentAccount);
        futureEvents fragInfo = new futureEvents();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.webContainer, fragInfo).
                commit();

    }
    public void moveToDescription(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("Account", CurrentAccount);
        il.co.yoman.yoman.Account.description fragInfo = new description();
        fragInfo.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.webContainer, fragInfo).commit();
    }
}
