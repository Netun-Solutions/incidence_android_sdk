package com.e510.commons.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.e510.commons.activity.BaseActivity;
import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.Utils;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.commons.utils.config.Pages;
import com.e510.commons.view.Hud;
import com.e510.incidencelibrary.R;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BaseFragment extends GlobalFragmentParcelable {

    private static final String TAG = makeLogTag(BaseFragment.class);
    public OnFragmentInteractionListener mListener;
    public BaseFragmentListener baseFragmentListener;
    public boolean firstResume;
    private boolean callOptionsMenu = true;
    public boolean secondLevel = false;

    public BaseFragment() {
        // Required empty public constructor
    }

    public BaseFragment renew()
    {
        BaseFragment baseFragment = this;

        try
        {
            Method methodToFind = null;
            try {
                methodToFind = getClass().getMethod("newInstance", (Class<?>[]) null);
            } catch (NoSuchMethodException | SecurityException e) {
                // Your exception handling goes here
            }

            if(methodToFind == null) {
                // Method not found.
            } else {
                // Method found. You can invoke the method like
                try {
                    baseFragment = (BaseFragment) methodToFind.invoke(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return baseFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (needEventBus())
        {
            registerEventBus();
        }

        if (isSecondLevel())
        {
            mListener.showHideBackButton(true);
        }

        if (callOptionsMenu)
            setHasOptionsMenu(true);

        firstResume = true;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (needEventBus())
        {
            unRegisterEventBus();
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (firstResume) {
            firstResume = false;
            loadData();
        }
    }

    public void onBaseRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    }

    public BaseActivity getBaseActivity()
    {
        return ((BaseActivity) getActivity());
    }

    public void setupUI(View rootView)
    {
        FontUtils.setTypeValueText(rootView, getContext());
    }

    public int getColor(int id) {
        return Utils.getColor(getActivity(), id);
    }

    public void loadData()
    {
    }

    public void reloadData()
    {
    }

    public void onBecomeFrontFromBackPressed()
    {
        if (baseFragmentListener != null)
        {
            baseFragmentListener.onBecomeFrontFromBackPressed();
        }
    }

    public void setCallOptionsMenu(boolean call) {
        callOptionsMenu = call;
    }

    public void setTitleNavigationBar(int resourceId)
    {
        if (mListener != null)
        {
            mListener.setTitleNavigationBar(resourceId);
        }
    }

    public void setTitleNavigationBar(String title)
    {
        if (mListener != null)
        {
            mListener.setTitleNavigationBar(title);
        }
    }

    public int getTitleId()
    {
        return R.string.app_name;
    }

    public int getLayoutRootId()
    {
        return getView().getId();
    }

    public void showDrawerMenuIcon()
    {
        if (mListener != null)
            mListener.showDrawerMenuIcon();
    }

    public void showDrawerArrowIcon()
    {
        if (mListener != null)
            mListener.showDrawerArrowIcon();
    }

    public void closeThis() {
        if (mListener != null)
            mListener.performBackPressed();
    }

    public boolean onBackPressed()
    {
        if (baseFragmentListener!= null)
            baseFragmentListener.onPerformBackPressed(null);

        return false;
    }

    public void showAlertConfirm(String title, String message, DialogInterface.OnClickListener listenerPositive, String titlePositive, DialogInterface.OnClickListener listenerNegative, String titleNegative)
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setTitle(title);
            builder.setMessage(message);
            if (listenerNegative != null)
                builder.setNegativeButton(titleNegative, listenerNegative);
            if (listenerPositive != null)
                builder.setPositiveButton(titlePositive, listenerPositive);
            builder.create().show();
        }
        catch (Exception e)
        {
            LogUtil.logE(getClass().getName(), e.getMessage(), e);
        }
    }

    public void showAlertConfirm(String title, String message, DialogInterface.OnClickListener listenerPositive, DialogInterface.OnClickListener listenerNegative)
    {
        showAlertConfirm(title, message, listenerPositive, getString(R.string.action_accept), listenerNegative, getString(R.string.action_denied));
    }

    public void showAlert(int messageId)
    {
        showAlert(getString(R.string.app_name), getString(messageId));
    }

    public void showAlert(int titleId, int messageId)
    {
        showAlert(getString(titleId), getString(messageId));
    }

    public void showAlert(String message)
    {
        showAlert(getString(R.string.app_name), message);
    }

    public void showAlert(String title, String message) {
        try
        {
            if (message != null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(title);
                builder.setCancelable(false);
                builder.setMessage(message);
                builder.setNegativeButton(R.string.ws_response_default_ok_button, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(getClass().getName(), e.getMessage(), e);
        }
    }

    public void showAlert(int titleId, int messageId, DialogInterface.OnClickListener listener)
    {
        showAlert(getString(titleId), getString(messageId), listener, R.string.ws_response_default_ok_button);
    }

    public void showAlert(String title, String message, DialogInterface.OnClickListener listener)
    {
        showAlert(title, message, listener, R.string.ws_response_default_ok_button);
    }

    public void showAlert(String title, String message, DialogInterface.OnClickListener listener, int listenerTitle)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        if (listener == null)
        {
            listener = new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                }
            };
        }

        builder.setNegativeButton(listenerTitle, listener);
        builder.create().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        try
        {
            menu.clear();
            super.onCreateOptionsMenu(menu, inflater);
        }
        catch (Exception e)
        {
            LogUtil.logE(getClass().getSimpleName().substring(0, 22), "Error: " + e.getMessage(), e);
        }
    }

    public void hideKeyboard()
    {
        try
        {
            if (mListener != null)
                mListener.checkAndCloseKeyboard();
        }
        catch (Exception e)
        {
            LogUtil.logE(getClass().getName(), "hideKeyboard: " + e.getMessage(), e);
        }
    }

    public void showKeyboard(final Context context, final View view)
    {
        try
        {
            new Handler().post(
                    new Runnable() {
                        public void run() {
                            InputMethodManager inputMethodManager =  (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                            view.requestFocus();
                        }
                    });
        }
        catch (Exception e)
        {
            LogUtil.logE(getClass().getName(), "hideKeyboard: " + e.getMessage(), e);
        }
    }

    public void showHud()
    {
        try
        {
            Hud hud = getView().findViewById(R.id.hud);
            if (hud != null)
            {
                hud.show();
            }
            else if (mListener != null)
                mListener.showHud();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void hideHud()
    {
        try
        {
            Hud hud = getView().findViewById(R.id.hud);
            if (hud != null)
            {
                hud.hide();
            }
            if (mListener != null)
                mListener.hideHud();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean needEventBus()
    {
        return false;
    }

    public boolean needHideToolbar()
    {
        return false;
    }

    public boolean isSecondLevel()
    {
        return secondLevel;
    }

    //Función repetida en BaseActivity y algunos adapters
    private void registerEventBus()
    {
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

    //Función repetida en BaseActivity y algunos adapters
    private void unRegisterEventBus()
    {
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onSpannableURLClick(String url) {
    }

    public void onClickRow(Object object)
    {
        if (baseFragmentListener!= null)
            baseFragmentListener.onClickRow(object);
    }

    public void onClickRow(int idRow, Object object)
    {
    }

    public void onLongClickRow(int viewId)
    {
    }
    public void onLongClickRow(int viewId, Object object)
    {
    }

    public Pages getPageConfiguration(String pageID) {
        ArrayList<Pages> pages = AppConfiguration.getInstance().getModule(AppConfiguration.MODULE_SHOP).pages;
        if (pages != null) {
            for (Pages page : AppConfiguration.getInstance().getModule(AppConfiguration.MODULE_SHOP).pages) {
                if (page.id.equals(pageID)) {
                    return page;
                }
            }
        }
        return null;
    }
}
