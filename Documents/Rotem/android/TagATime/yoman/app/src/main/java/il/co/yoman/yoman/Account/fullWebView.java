package il.co.yoman.yoman.Account;


import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import il.co.yoman.yoman.R;

import static il.co.yoman.yoman.welcomeScreen.getMobileNumber;
import static il.co.yoman.yoman.welcomeScreen.getToken;


/**
 * A simple {@link Fragment} subclass.
 */
public class fullWebView extends Fragment {
    private WebView          webView;
    private TextView         title;
    private String           link, strTitle, strFuture, description, token,mobileNumber, nick;

    public  fullWebView      newInstance(String link, String title, String description, String future, String nick ) {
        Bundle args = new Bundle();
        args.putString("link", link);
        args.putString("title", title);
        args.putString("description", description);
        args.putString("future", future);
        args.putString("nick", nick);

        fullWebView fragment = new fullWebView();
        fragment.setArguments(args);
        return fragment;
    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            if (container != null) {
                container.removeAllViews();
            }
            link             =        getArguments().getString("link");
            strTitle         =        this.getArguments().getString("title");
            strFuture        =        this.getArguments().getString("future");
            nick             =        this.getArguments().getString("nick");
            mobileNumber     =        getMobileNumber();
            token            =        getToken();

        // Inflate the layout for this fragment
        View v           =        inflater.inflate(R.layout.fragment_full_web_view, container, false);
        webView          =        v.findViewById(R.id.fullwebView);
        title            =        v.findViewById(R.id.titleAccount);
        title.setText(strTitle);

        config(webView);
        webView.loadUrl(link);

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
                    moveToFutureEvents();
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
        bundle.putString("mobileNumber", getMobileNumber());

        futureEvents fragInfo = new futureEvents();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.fullwebContainer, fragInfo).
                commit();


    }
}
