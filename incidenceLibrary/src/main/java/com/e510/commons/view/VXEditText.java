package com.e510.commons.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.AppCompatEditText;

public class VXEditText extends AppCompatEditText {

    public boolean canClearFocus = true;

    public VXEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

    }

    public VXEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public VXEditText(Context context) {
        super(context);

    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
            this.clearFocus();

            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);

        if(actionCode == EditorInfo.IME_ACTION_DONE)
        {
            this.clearFocus();
        }
    }

    @Override
    public void clearFocus() {
        if (canClearFocus)
            super.clearFocus();
        else
            requestFocus();
    }
}
