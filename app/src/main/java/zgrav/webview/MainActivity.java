package zgrav.webview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private WebView webView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); //deprecated af starting from api 23

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setupFullscreenMode();

        setContentView(R.layout.activity_main);

        RelativeLayout rl = (RelativeLayout)findViewById(R.id.activity_main_layout);
        rl.setBackgroundColor(Color.WHITE);

        editText = (EditText) findViewById(R.id.editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_SEND) {
                    editText.setVisibility(View.INVISIBLE);
                    editText.setFocusable(false);
                    editText.setFocusableInTouchMode(false);
                    editText.setClickable(false);

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    View decorView = getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    decorView.setSystemUiVisibility(uiOptions);

                    webView = (WebView) findViewById(R.id.activity_main_webview);
                    webView.setWebChromeClient(new WebChromeClient());
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setDomStorageEnabled(true);
                    webSettings.setAppCacheEnabled(false);
                    WebView.setWebContentsDebuggingEnabled(true);

                    webView.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                    View decorView = getWindow().getDecorView();
                                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                                    decorView.setSystemUiVisibility(uiOptions);

                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                    webView.setOnTouchListener(new View.OnTouchListener() {

                        public final static int FINGER_RELEASED = 0;
                        public final static int FINGER_TOUCHED = 1;
                        public final static int FINGER_DRAGGING = 2;
                        public final static int FINGER_UNDEFINED = 3;

                        private int fingerState = FINGER_RELEASED;


                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {

                            switch (motionEvent.getAction()) {

                                case MotionEvent.ACTION_DOWN:
                                    if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                                    else fingerState = FINGER_UNDEFINED;
                                    break;

                                case MotionEvent.ACTION_UP:
                                    if(fingerState != FINGER_DRAGGING) {
                                        fingerState = FINGER_RELEASED;

                                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                                        View decorView = getWindow().getDecorView();
                                        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                                        decorView.setSystemUiVisibility(uiOptions);

                                    }
                                    else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
                                    else fingerState = FINGER_UNDEFINED;
                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) fingerState = FINGER_DRAGGING;
                                    else fingerState = FINGER_UNDEFINED;
                                    break;

                                default:
                                    fingerState = FINGER_UNDEFINED;

                            }

                            return false;
                        }
                    });

                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(editText.getText().toString());

                    handled = true;
                }
                return handled;
            }
        });
    }

    private void setupFullscreenMode() {
        View decorView = setFullscreen();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                setFullscreen();
            }
        });
    }

    private View setFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        return decorView;
    }

    @Override
    public void onBackPressed() {
        //if(webView.canGoBack()) {
        //    webView.goBack();
        //}
        //else super.onBackPressed();
    }
}
