package il.co.yoman.yoman.Account;


import android.graphics.Bitmap;
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

import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;


/**
 * A simple {@link Fragment} subclass.
 */
public class fullWebView extends Fragment {
    private WebView          webView;
    private TextView         topRightTitleAccount;
    private String           link;
    private AccountDataSource.Account CurrentAccount;

    public  fullWebView   newInstance(String link, AccountDataSource.Account CurrentAccount ) {
        Bundle args = new Bundle();
        args.putString("link", link);
        args.putParcelable("Account", CurrentAccount);
        fullWebView fragment = new fullWebView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (container != null) {
                container.removeAllViews();
            }
            CurrentAccount              =        this.getArguments().getParcelable("Account");
            link                        =        getArguments().getString("link");
            //upper layer
            View v                      =        inflater.inflate(R.layout.fragment_full_web_view, container, false);
            topRightTitleAccount        =        v.findViewById(R.id.topRightTitleAccount);
            webView                     =        v.findViewById(R.id.fullwebView);

            topRightTitleAccount.setText(CurrentAccount.getTitle());
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
                    moveToFutureEvents();
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
                replace(R.id.fullwebContainer, fragInfo).
                commit();


    }

}
