package com.siristechnology.surya.app;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class SearchEditText extends EditText {
    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            ((Activity)getContext()).finish();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) { //not being called on search, need to fix
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

}
