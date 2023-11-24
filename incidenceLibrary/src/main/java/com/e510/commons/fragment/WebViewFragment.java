package com.e510.commons.fragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.e510.incidencelibrary.R;
import com.e510.commons.activity.BaseActivity;
import com.e510.commons.utils.StringUtils;
import com.e510.commons.view.CustomWebView;

import static com.e510.commons.utils.LogUtil.makeLogTag;

public class WebViewFragment extends BaseFragment
{

    public static final int WEBVIEW_TYPE_REGULAR = 1;
    public static final int WEBVIEW_TYPE_HIDDEN_NAVIGATION = 2;
    public static final int WEBVIEW_TYPE_ANIMATED_NAVIGATION = 3;
    private static final String TAG = makeLogTag(WebViewFragment.class);

    private CustomWebView webView;
    public static final String KEY_URL = "url";
    public static final String KEY_TYPE = "type";

    public String url;
    public int type;
    private static final String PDF_EXTENSION = ".pdf";

    @Override
    public int getTitleId()
    {
        return R.string.empty;
    }

    public static WebViewFragment newInstance(String url, int type)
    {
        WebViewFragment fragment = new WebViewFragment();
        fragment.url = url;
        fragment.type = type;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putInt(KEY_TYPE, type);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            url = getArguments().getString(KEY_URL);
            type = getArguments().getInt(KEY_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        webView = rootView.findViewById(R.id.webview);

        switch (type) {
            case WEBVIEW_TYPE_HIDDEN_NAVIGATION:
                BaseActivity activity = getBaseActivity();
                activity.getSupportActionBar().hide();
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
            case WEBVIEW_TYPE_ANIMATED_NAVIGATION:
                webView.setGestureDetector(new GestureDetector(getActivity(), new CustomeGestureDetector()));
                break;
            default:
                break;
        }
        loadWeb();
    }
    
    private void loadWeb()
    {
        String preURL = "";

        if (url != null)
        {
            url = StringUtils.checkAndReturnUrlFormat(url);

            WebSettings settings = webView.getSettings();

            if(isPdfUrl(url)) preURL = "https://docs.google.com/gview?embedded=true&url=";
            else settings.setBuiltInZoomControls(true);

            webView.setWebViewClient(new SSLTolerentWebViewClient());

            settings.setJavaScriptEnabled(true);
            webView.loadUrl(preURL+url);

            webView.setWebViewClient(new WebViewClient()
            {
                public void onPageStarted(WebView view, String url, Bitmap favicon)
                {
                    showHud();

                }
                public void onPageFinished(WebView view, String url)
                {
                    hideHud();
                }
            });
        }
    }

    private boolean isPdfUrl(String url)
    {
        if (!TextUtils.isEmpty(url))
        {
            url = url.trim();
            int lastIndex = url.toLowerCase().lastIndexOf(PDF_EXTENSION);
            if (lastIndex != -1)
            {
                return url.substring(lastIndex).equalsIgnoreCase(PDF_EXTENSION);
            }
        }
        return false;
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            AlertDialog alertDialog = builder.create();
            String message = getString(R.string.ssl_certificate_error);
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = getString(R.string.the_certificate_authority_is_not_trusted);
                    break;
                case SslError.SSL_EXPIRED:
                    message = getString(R.string.the_certificate_has_expired);
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = getString(R.string.the_certificate_hostname_mismatch);
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = getString(R.string.the_certificate_is_not_yet_valid);
                    break;
            }

            message += getString(R.string.do_you_want_to_continue_anyway);
            alertDialog.setTitle(getString(R.string.ssl_certificate_error));
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ws_response_default_ok_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Ignore SSL certificate errors
                    handler.proceed();
                }
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.ws_response_default_cancel_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    handler.cancel();
                }
            });
            alertDialog.show();
        }
    }

    private class CustomeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1 == null || e2 == null) return false;
            if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
            else {
                try {
                    if(e1.getY() - e2.getY() > 20 ) {
                        BaseActivity activity = getBaseActivity();
                        activity.getSupportActionBar().hide();
                        return false;
                    }
                    else if (e2.getY() - e1.getY() > 20 ) {
                        BaseActivity activity = getBaseActivity();
                        activity.getSupportActionBar().show();
                        return false;
                    }

                } catch (Exception e) {

                }
                return false;
            }


        }
    }

}