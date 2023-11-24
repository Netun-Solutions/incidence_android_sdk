package com.e510.commons.fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public interface OnFragmentInteractionListener {//implements Serializable {
    //private long serialVersionUID = 1L;

    void showDrawerArrowIcon();
    void showDrawerMenuIcon();
    void hideDrawerMenuIcon();

    void performBackPressed();

    void setTitleNavigationBar(int resourceId);
    void setTitleNavigationBar(String title);
    void setSubTitleNavigationBar(int resourceId);
    void setSubTitleNavigationBar(String subtitle);

    BaseFragment getActiveFragment();
    BaseFragment getPenultimFragment();
    BaseFragment getFragment(int position);
    FragmentManager getSupportFragmentManager();

    void checkUpdateTitleNavigationBar();
    void checkUpdateTitleNavigationBar(BaseFragment fragment);
    void showHideBackButton(boolean needBackButton);

    void showHud();
    void showHud(int title);
    void showHud(String title);
    void hideHud();

    void showInitialFragment(BaseFragment fragment);
    void showInitialFragment(int idLayout, BaseFragment fragment);
    void showInitialFragment(int idLayout, BaseFragment fragment, FragmentAnimation animation);
    void showInitialFragment(int idLayout, BaseFragment fragment, FragmentAnimation animation, boolean clean);
    void removeInitialFragment();
    void addFragment(int idLayout, Fragment fragment, FragmentAnimation animation);
    void addFragment(Fragment fragment);
    void addFragment(Fragment fragment, FragmentAnimation animation);
    void addFragment(Fragment fragment, boolean forced, FragmentAnimation animation);
    void addFragment(int idLayout, Fragment fragment, boolean fullScreen, boolean forced, FragmentAnimation animation);
    void addFragment(int idLayout, boolean addToBack, Fragment fragment, boolean fullScreen, boolean forced, FragmentAnimation animation);
    void addFragmentAnimated(Fragment fragment);
    void addFullFragment(Fragment fragment);
    void addFullFragment(Fragment fragment, FragmentAnimation animation);
    void addFullFragmentAnimated(Fragment fragment);
    void removeFragment(BaseFragment fragment);
    void removeFragment(BaseFragment fragment, boolean popBackStack);
    void removeLastFragments(int numberOfFragmentsRemove);
    void showFragment(Fragment fragment);


    void cleanAllBackStackEntries();
    void cleanAllBackStackEntries(ArrayList<String> classNames);
    void cleanAllFragmentsUpperThan(String classNamesException);
    void cleanAllFragmentsUpperThan(String classNamesException, boolean exceptionIncluded);
    void cleanAllFragmentsUpperThan(ArrayList<String> classNamesException, boolean exceptionIncluded);

    void checkAndCloseKeyboard();

    boolean checkPermission(final Context context, String permission);
}