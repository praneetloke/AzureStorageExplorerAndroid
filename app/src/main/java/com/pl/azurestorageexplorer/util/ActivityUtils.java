package com.pl.azurestorageexplorer.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;
import java.util.Stack;

/**
 * Created by Praneet Loke on 4/15/2016.
 */
public class ActivityUtils {
    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameId, String fragmentTag) {
        fragmentManager
                .beginTransaction()
                .add(frameId, fragment, fragmentTag)
                .commit();
    }

    public static void addFragmentStacked(FragmentManager fragmentManager, Fragment fragment, int frameId, String fragmentTag, Stack<Fragment> fragmentStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment, fragmentTag);
        fragmentStack.lastElement().onPause();
        transaction.hide(fragmentStack.lastElement());
        fragmentStack.push(fragment);
        transaction.commit();
    }

    public static void popPreviousFragmentFromStack(FragmentManager fragmentManager, Stack<Fragment> fragmentStack) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        fragmentStack.lastElement().onPause();
        ft.remove(fragmentStack.pop());
        fragmentStack.lastElement().onResume();
        ft.show(fragmentStack.lastElement());
        ft.commit();
    }

    public static void replaceFragment(FragmentManager fragmentManager, int frameId, Fragment fragmentToShow, String fragmentTag, Stack<Fragment> fragmentStack) {
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();

            if (!fragmentStack.empty()) {
                fragmentStack.pop();
            }
        }

        fragmentManager
                .beginTransaction()
                .add(frameId, fragmentToShow, fragmentTag)
                .commit();

        fragmentStack.push(fragmentToShow);
    }
}
