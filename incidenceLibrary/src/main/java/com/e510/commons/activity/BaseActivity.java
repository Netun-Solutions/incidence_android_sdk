package com.e510.commons.activity;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.e510.commons.fragment.BaseFragment;
import com.e510.commons.fragment.FragmentAnimation;
import com.e510.commons.fragment.OnFragmentInteractionListener;
import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.commons.utils.config.E510Resources;
import com.e510.commons.view.Hud;
import com.e510.incidencelibrary.R;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import es.incidence.library.IncidenceLibraryManager;

public class BaseActivity extends AppCompatActivity implements OnFragmentInteractionListener, View.OnClickListener {
    private static final String TAG = makeLogTag(BaseActivity.class);

    public static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    public static final int PERMISSION_WRITE_REQUEST_CODE = 2;
    public static final int PERMISSION_CAMERA = 3;
    public static final int PERMISSION_READ_CONTACTS = 4;
    public static final int PERMISSION_RECORD_AUDIO_CODE = 5;
    public static final int PERMISSION_CALL_PHONE = 6;

    private E510Resources res;

    private Toolbar toolbar;
    private Hud hud;

    private long timeOpenFragment;
    private boolean firstResume;

    private int lastInitialLayout = R.id.fragment_container;
    public int getLastInitialLayout() {
        return lastInitialLayout;
    }
    public void setLastInitialLayout(int lastInitialLayout) {
        this.lastInitialLayout = lastInitialLayout;
    }

    /*
    public BaseApplication getBaseApplication()
    {
        return ((BaseApplication) getApplication());
    }
    */

    public void hideHomeImage() {
        RelativeLayout homeImageViewLayout = findViewById(R.id.imgBackgroundLayout);
        homeImageViewLayout.setVisibility(View.GONE);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            FontUtils.setTypeValueText(toolbar, FontUtils.SECONDARY_REGULAR, this);

            //par que se muestren los acentos de las mayusculas
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            TextView toolbarSubTitle = toolbar.findViewById(R.id.toolbar_subtitle);
            toolbarTitle.setIncludeFontPadding(true);
            toolbarSubTitle.setIncludeFontPadding(true);
        }
    }

    public void prepareToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);
        int backgroundColor = Color.parseColor("#" + AppConfiguration.getInstance().appearance.colors.navigationBar);
        int textColor = Color.parseColor("#" + AppConfiguration.getInstance().appearance.colors.navigationTitle);
        boolean statusTextDark = AppConfiguration.getInstance().appearance.statusBar.equals("darkContent");
        setToolbarColor(backgroundColor, textColor, statusTextDark);
    }

    public void hideToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null)
            toolbar.setVisibility(View.GONE);
    }

    public void showToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null)
            toolbar.setVisibility(View.VISIBLE);
    }

    public void setToolbarColor(int backgroundColor, int textColor, boolean statusTextDark)
    {
        if (toolbar != null)
        {
            toolbar.setBackgroundColor(backgroundColor);
            getWindow().setStatusBarColor(backgroundColor);

            int state = getWindow().getDecorView().getSystemUiVisibility();
            if (statusTextDark)
            {
                //Dark Text to show up on your light status bar
                state |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            else
            {
                //Light Text to show up on your dark status bar
                state &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            getWindow().getDecorView().setSystemUiVisibility(state);

            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            if(toolbarTitle != null){
                toolbarTitle.setTextColor(textColor);
            }
            TextView toolbarSubTitle = toolbar.findViewById(R.id.toolbar_subtitle);
            if(toolbarSubTitle != null){
                toolbarSubTitle.setTextColor(textColor);
            }

            Drawable backArrow = getResources().getDrawable(R.drawable.ic_back);
            DrawableCompat.setTint(backArrow, textColor);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String code){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        IncidenceLibraryManager.instance.activityCreated(this);

        firstResume = true;
        registerEventBus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IncidenceLibraryManager.instance.activityDestroyed(this);
        unRegisterEventBus();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        hud = findViewById(R.id.hud);
        /*if (hud != null) {
            hud.setOpaque();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firstResume) {
            firstResume = false;
            loadData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IncidenceLibraryManager.instance.activityStarted();
    }

    @Override
    public void onStop() {
        super.onStop();
        IncidenceLibraryManager.instance.activityStopped();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                performBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setResources(E510Resources resources)
    {
        res = resources;
    }

    @Override
    public Resources getResources() {
        if (res == null) {
            res = new E510Resources(super.getResources());
        }
        return res;
    }

    public void loadData()
    {
    }

    @Override
    public void setTitleNavigationBar(int resourceId)
    {
        try
        {
            setTitleNavigationBar(getResources().getString(resourceId));
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public void setTitleNavigationBar(String title)
    {
        try
        {
            if (title != null)
            {
                setToolbarTitle(title);
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "setTitleNavigationBar: " + e.getMessage());
        }
    }

    @Override
    public void setSubTitleNavigationBar(int resourceId)
    {
        try
        {
            setSubTitleNavigationBar(getResources().getString(resourceId));
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "setTitleNavigationBar: " + e.getMessage());
        }
    }

    @Override
    public void setSubTitleNavigationBar(String subtitle)
    {
        try
        {
            if (subtitle != null)
            {
                if (toolbar != null)
                {
                    TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_subtitle);
                    if(toolbarTitle != null){
                        if (subtitle.equals(""))
                        {
                            toolbarTitle.setVisibility(View.GONE);
                        }
                        else
                        {
                            toolbarTitle.setVisibility(View.VISIBLE);
                            toolbarTitle.setText(subtitle);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "setTitleNavigationBar: " + e.getMessage());
        }
    }

    public void setToolbarTitle(String title) {
        if (toolbar != null)
        {
            toolbar.setTitle("");
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            if(toolbarTitle != null){
                toolbarTitle.setText(title);
            }
        }
    }

    @Override
    public void checkUpdateTitleNavigationBar()
    {
        BaseFragment newActiveFragment = getActiveFragment();
        checkUpdateTitleNavigationBar(newActiveFragment);
    }

    @Override
    public void checkUpdateTitleNavigationBar(BaseFragment newActiveFragment)
    {
        try
        {
            if (newActiveFragment != null)
            {
                setTitleNavigationBar(newActiveFragment.getTitleId());
            }
            else
            {
                BaseFragment baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (baseFragment != null)
                {
                    setTitleNavigationBar(baseFragment.getTitleId());
                }
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, e.getMessage());
        }
    }

    public Hud getHud()
    {
        return hud;
    }

    @Override
    public void showHud() {
        if (hud != null) {
            hud.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showHud(int title) {
        if (hud != null) {
            hud.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showHud(String title) {
        if (hud != null) {
            hud.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideHud() {
        if (hud != null) {
            hud.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed()
    {
        performBackPressed();
    }

    public void superOnBackPressed()
    {
        super.onBackPressed();
    }

    public void performBackPressed()
    {
        boolean eventConsumed = false;

        BaseFragment fragment = getActiveFragment();
        BaseFragment penultimFragment = getPenultimFragment();

        if (fragment != null)
        {
            eventConsumed = fragment.onBackPressed();
        }
        else
        {
            BaseFragment initialFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(getLastInitialLayout());
            if (initialFragment != null)
            {
                eventConsumed = initialFragment.onBackPressed();
            }
        }

        if (!eventConsumed)
        {
            checkAndCloseKeyboard();

            superOnBackPressed();

            checkUpdateTitleNavigationBar();

            if (penultimFragment != null)
            {
                if (!penultimFragment.isVisible())
                {
                    showFragment(penultimFragment);
                }

                penultimFragment.onBecomeFrontFromBackPressed();
                showHideBackButton(penultimFragment.isSecondLevel());
            }
            else
            {
                BaseFragment initialFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(getLastInitialLayout());

                if (initialFragment != null)
                {
                    if (!initialFragment.isVisible())
                    {
                        showFragment(initialFragment);
                    }

                    initialFragment.onBecomeFrontFromBackPressed();
                }

                showHideBackButton(false);
            }
        }
    }

    public void showHideBackButton(boolean needBackButton)
    {
        /* no se hace, al tener drawer
        if (needBackButton)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    BaseActivity.this.onBackPressed();
                }
            });
        }
        else
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }*/

        if (needBackButton)
        {
            showDrawerArrowIcon();
        }
        else
        {
            showDrawerMenuIcon();
        }
    }

    public FragmentManager getBaseSupportFragmentManager() {

        return getSupportFragmentManager();
    }

    @Override
    public BaseFragment getPenultimFragment()
    {
        return getFragment(2);
    }

    @Override
    public BaseFragment getActiveFragment()
    {
        return getFragment(1);
    }

    @Override
    public BaseFragment getFragment(int position) {
        try
        {
            if (getBaseSupportFragmentManager().getBackStackEntryCount() == 0)
            {
                return null;
            }

            int posFrag = getBaseSupportFragmentManager().getBackStackEntryCount() - position;

            if (posFrag >= 0)
            {
                String tag = getBaseSupportFragmentManager().getBackStackEntryAt(posFrag).getName();
                return (BaseFragment) getBaseSupportFragmentManager().findFragmentByTag(tag);
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void cleanAllBackStackEntries()
    {
        cleanAllBackStackEntries(null);
    }

    @Override
    public void cleanAllBackStackEntries(ArrayList<String> classNames)
    {
        try
        {
            ArrayList<BaseFragment> baseFragmentList = new ArrayList<>();

            for (int i = 0; i < getBaseSupportFragmentManager().getBackStackEntryCount(); i++)
            {
                String tag = getBaseSupportFragmentManager().getBackStackEntryAt(i).getName();

                BaseFragment fragment = (BaseFragment) getBaseSupportFragmentManager().findFragmentByTag(tag);

                if (fragment != null)
                {
                    boolean canRemove = false;

                    if (classNames != null)
                    {
                        if (classNames.contains(fragment.getClass().getName()))
                        {
                            canRemove = true;
                        }
                    }
                    else
                    {
                        canRemove = true;
                    }

                    //HLog.i(TAG, "Fragment cleaned: " + fragment.getClass().getName());
                    if (canRemove)
                        baseFragmentList.add(fragment);
                }
            }

            if (classNames == null)
            {
                FragmentTransaction transaction = getBaseSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);

                for (int i = (baseFragmentList.size()-1); i >= 0; i--)
                {
                    BaseFragment baseFragment = baseFragmentList.get(i);
                    transaction.remove(baseFragment);
                }

                transaction.commit();
                getBaseSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            else
            {
                for (int i = (baseFragmentList.size()-1); i >= 0; i--)
                {
                    BaseFragment baseFragment = baseFragmentList.get(i);
                    baseFragment.closeThis();
                }
            }

            baseFragmentList.clear();
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void cleanAllFragmentsUpperThan(String classNamesException)
    {
        cleanAllFragmentsUpperThan(classNamesException, false);
    }


    @Override
    public void cleanAllFragmentsUpperThan(String classNamesException, boolean exceptionIncluded)
    {
        ArrayList<String> list = new ArrayList<>();
        list.add(classNamesException);
        cleanAllFragmentsUpperThan(list, exceptionIncluded);
    }

    @Override
    public void cleanAllFragmentsUpperThan(ArrayList<String> classNamesException, boolean exceptionIncluded)
    {
        try
        {
            boolean classFounded = false;
            ArrayList<BaseFragment> baseFragmentList = new ArrayList<>();

            for (int i = getSupportFragmentManager().getBackStackEntryCount() -1; i >= 0; i--)
            {
                String tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();

                BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);

                if (fragment != null)
                {
                    boolean canRemove = true;

                    if (classNamesException != null)
                    {
                        if (classNamesException.contains(fragment.getClass().getName()))
                        {
                            classFounded = true;

                            if (exceptionIncluded)
                            {
                                if (fragment instanceof BaseFragment)
                                {
                                    baseFragmentList.add(fragment);
                                }
                            }

                            canRemove = false;
                            break;
                        }
                    }

                    if (canRemove && fragment instanceof BaseFragment)
                    {
                        baseFragmentList.add(fragment);
                    }
                }
            }

            if (classFounded)
            {
                for (int i = (baseFragmentList.size()-1); i >= 0; i--)
                {
                    BaseFragment baseFragment = baseFragmentList.get(i);
                    baseFragment.closeThis();
                }
            }

            baseFragmentList.clear();
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "cleanAllFragments: " + e.getMessage(), e);
        }
    }

    @Override
    public void showInitialFragment(BaseFragment fragment)
    {
        showInitialFragment(R.id.fragment_container, fragment);
    }

    @Override
    public void showInitialFragment(int idLayout, BaseFragment fragment)
    {
        showInitialFragment(idLayout, fragment, FragmentAnimation.NONE);
    }

    @Override
    public void showInitialFragment(int idLayout, BaseFragment fragment, FragmentAnimation animation)
    {
        showInitialFragment(idLayout, fragment, animation, true);
    }

    @Override
    public void showInitialFragment(int idLayout, BaseFragment fragment, FragmentAnimation animation, boolean clean)
    {
        try
        {
            checkAndCloseKeyboard();
            BaseFragment lastInitialFragment = null;
            if (clean) {
                cleanAllBackStackEntries();
                if (lastInitialLayout != idLayout)
                {
                    lastInitialFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(lastInitialLayout);
                }
                setLastInitialLayout(idLayout);
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (animation != FragmentAnimation.NONE)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    if (animation == FragmentAnimation.PUSH)
                    {
                        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    }
                    else if (animation == FragmentAnimation.MODAL)
                    {
                        transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom);
                    }
                    else if (animation == FragmentAnimation.ALPHA)
                    {
                        transaction.setCustomAnimations(R.anim.wait, R.anim.alpha_0_to_100, R.anim.wait, R.anim.alpha_0_to_100);
                    }
                }
            }

            if (lastInitialFragment != null)
            {
                transaction.remove(lastInitialFragment);
            }

            transaction.replace(idLayout, fragment).commit();

            setTitleNavigationBar(fragment.getTitleId());
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "showInitialFragment" + e.getMessage(), e);
        }
    }

    @Override
    public void removeInitialFragment()
    {
        removeInitialFragment(R.id.fragment_container);
    }

    public void removeInitialFragment(int idFragment)
    {
        try
        {
            checkAndCloseKeyboard();

            cleanAllBackStackEntries();

            BaseFragment initialFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(idFragment);

            if (initialFragment != null)
            {
                getSupportFragmentManager().beginTransaction().remove(initialFragment).commit();
            }

            toolbar.setTitle(getString(R.string.incidence_key_app_name));

        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "removeInitialFragment" + e.getMessage(), e);
        }
    }

    @Override
    public void addFragment(Fragment fragment)
    {
        addFragment(R.id.fragment_container, fragment, false, false, FragmentAnimation.NONE);
    }

    @Override
    public void addFragment(Fragment fragment, boolean forced, FragmentAnimation animation)
    {
        addFragment(R.id.fragment_container, fragment, false, forced, animation);
    }

    @Override
    public void addFragment(int idLayout, Fragment fragment, FragmentAnimation animation)
    {
        addFragment(idLayout, fragment, false, false, animation);
    }

    @Override
    public void addFragment(Fragment fragment, FragmentAnimation animation)
    {
        addFragment(R.id.fragment_container, fragment, false, false, animation);
    }

    @Override
    public void addFragmentAnimated(Fragment fragment)
    {
        addFragment(R.id.fragment_container, fragment, false, false, FragmentAnimation.PUSH);
    }

    @Override
    public void addFullFragment(Fragment fragment)
    {
        addFragment(R.id.layout_activity_main, fragment, true, false, FragmentAnimation.NONE);
    }

    @Override
    public void addFullFragment(Fragment fragment, FragmentAnimation animation)
    {
        addFragment(R.id.layout_activity_main, fragment, true, false, animation);
    }

    @Override
    public void addFullFragmentAnimated(Fragment fragment)
    {
        addFragment(R.id.layout_activity_main, fragment, true, false, FragmentAnimation.PUSH);
    }

    public void addFragment(Fragment fragment, boolean fullScreen)
    {
        addFragment(R.id.fragment_container, fragment,  fullScreen, false, FragmentAnimation.NONE);
    }

    @Override
    public void addFragment(int idLayout, Fragment fragment, boolean fullScreen, boolean forced, FragmentAnimation animation)
    {
        addFragment(idLayout, true, fragment, fullScreen, forced, animation);
    }

    @Override
    public void addFragment(int idLayout, boolean addToBack, Fragment fragment, boolean fullScreen, boolean forced, FragmentAnimation animation)
    {
        try
        {
            if (forced || canAddFragment(fragment))
            {
                checkAndCloseKeyboard();

                /*if (disableMenu)
                    disableMenuClicable();*/

                if (!fragment.isAdded())
                {
                    String nameTag = fragment.getClass().getSimpleName();

                    FragmentTransaction transaction = fullScreen ? getSupportFragmentManager().beginTransaction() : getBaseSupportFragmentManager().beginTransaction();
                    if (animation != FragmentAnimation.NONE)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        {
                            if (animation == FragmentAnimation.PUSH)
                            {
                                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                            }
                            else if (animation == FragmentAnimation.MODAL)
                            {
                                transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom);
                            }
                            else if (animation == FragmentAnimation.NONE_WAIT)
                            {
                                transaction.setCustomAnimations(0, R.anim.wait, 0, R.anim.wait);
                            }
                            else if (animation == FragmentAnimation.NONE_PUSH)
                            {
                                transaction.setCustomAnimations(0, R.anim.exit_to_right, 0, R.anim.exit_to_right);
                            }
                            else if (animation == FragmentAnimation.ALPHA)
                            {
                                transaction.setCustomAnimations(R.anim.alpha_0_to_100, R.anim.alpha_100_to_0, R.anim.alpha_0_to_100, R.anim.alpha_100_to_0);
                            }
                        }
                    }

                    if (addToBack)
                        transaction.addToBackStack(nameTag);

                    if (fullScreen)
                    {
                        transaction.add(R.id.layout_activity_main, fragment, nameTag).commit();
                    }
                    else
                    {
                        transaction.add(idLayout, fragment, nameTag).commit();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            LogUtil.logE(TAG, "addFragment: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void removeLastFragments(int numberOfFragmentsToRemove)
    {
        if (numberOfFragmentsToRemove > 0)
        {
            try
            {
                ArrayList<BaseFragment> fragmentsList = new ArrayList<>();
                BaseFragment newActiveFragment = null;

                FragmentManager fm = getSupportFragmentManager();
                int totalFragments = fm.getBackStackEntryCount();

                if (totalFragments > 0 && totalFragments >= numberOfFragmentsToRemove)
                {
                    while (numberOfFragmentsToRemove > 0)
                    {
                        String tag = fm.getBackStackEntryAt(numberOfFragmentsToRemove).getName();
                        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);

                        if (fragment != null)
                        {
                            fragmentsList.add(fragment);
                        }

                        numberOfFragmentsToRemove--;
                    }

                    //Obtenemos el fragment actual
                    try
                    {
                        String tag = fm.getBackStackEntryAt(0).getName();
                        newActiveFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
                    }
                    catch (Exception e)
                    {
                        newActiveFragment = null;
                    }
                }

                for(int i = 0; i < fragmentsList.size(); i++)
                {
                    removeFragment(fragmentsList.get(i), true);
                }

                //actualizamos titulo navigation bar e icono
                if (newActiveFragment != null)
                {
                    checkUpdateTitleNavigationBar(newActiveFragment);
                }
            }
            catch (Exception e)
            {
                LogUtil.logE(TAG, "removeFragment: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void showFragment(Fragment fragment)
    {
        if (fragment != null)
        {
            try
            {
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        //.setCustomAnimations(R.anim.enter_from_left, 0)
                        .show(fragment)
                        .commit();
            }
            catch (Exception e)
            {
                LogUtil.logE(TAG, "showFragment: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void removeFragment(BaseFragment fragment)
    {
        removeFragment(fragment, false);
    }
    @Override
    public void removeFragment(BaseFragment fragment, boolean popBackStack)
    {
        if (fragment != null)
        {
            try
            {
                FragmentManager fm = getSupportFragmentManager();

                FragmentTransaction trans = fm.beginTransaction();
                /*if (fragment.removeAnimated())
                    trans.setCustomAnimations(0, R.anim.exit_to_right);*/
                trans.remove(fragment);
                trans.commit();

                if (popBackStack)
                    fm.popBackStack();
            }
            catch (Exception e)
            {
                LogUtil.logE(TAG, "removeFragment: " + e.getMessage(), e);
            }
        }
    }

    private boolean canAddFragment(Fragment fragment)
    {
        String nameFrag = fragment != null ? fragment.getClass().getName() : "-";
        boolean res = false;
        long now = System.currentTimeMillis();
        long secondAgo = now - 500;

        if (timeOpenFragment < secondAgo)
        {
            //LogUtil.logE(TAG, "Time to add Fragment " + nameFrag);
            timeOpenFragment = System.currentTimeMillis();
            res = true;
        }
        else
        {
            LogUtil.logE(TAG, "Time can't add Fragment " + nameFrag);
        }
        return res;
    }

    public void checkAndCloseKeyboard()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    if(KeyboardVisibilityEvent.INSTANCE.isKeyboardVisible(BaseActivity.this))
                    {
                        View view = getCurrentFocus();
                        if (view != null)
                        {
                            view.clearFocus();
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                }
                catch (Exception e)
                {
                    LogUtil.logE(TAG, "checkAndCloseKeyboard" + e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void showDrawerArrowIcon()
    {
        /*
        try {
            MenuManager.getInstance().getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            MenuManager.getInstance().disableDrawer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public void showDrawerMenuIcon()
    {/*
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            MenuManager.getInstance().getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
            MenuManager.getInstance().enableDrawer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
    @Override
    public void hideDrawerMenuIcon()
    {/*
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            MenuManager.getInstance().getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
            MenuManager.getInstance().disableDrawer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }

    public void showAlert(String message)
    {
        showAlert(getString(R.string.incidence_key_app_name), message);
    }
    public void showAlert(String title, String message)
    {
        try
        {
            if (message != null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setNegativeButton(R.string.incidence_key_ws_response_default_ok_button, new DialogInterface.OnClickListener()
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

    public void showAlert(String title, String message, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        builder.setNegativeButton(R.string.incidence_key_ws_response_default_ok_button, listener);
        builder.create().show();
    }

    //Función repetida en BaseActivity y algunos adapters
    public void registerEventBus()
    {
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

    //Función repetida en BaseActivity y algunos adapters
    public void unRegisterEventBus()
    {
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }

    public boolean checkPermission(final Context context, String permission)
    {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        BaseFragment baseFragment = getActiveFragment();
        if (baseFragment != null) {
            baseFragment.onBaseRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            BaseFragment initialFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(getLastInitialLayout());
            if (initialFragment != null) {
                initialFragment.onBaseRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        checkDispatchTouchEvent();
        return super.dispatchTouchEvent(ev);
    }

    public void checkDispatchTouchEvent()
    {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        View current = getCurrentFocus();
        if (current != null) current.clearFocus();
    }

    @Override
    public void onClick(View v) {
    }
}