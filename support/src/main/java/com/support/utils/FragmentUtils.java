package com.support.utils;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.support.R;

public class FragmentUtils {

    private Bundle savedInstance = null;
    private FragmentManager fragmentManager;
    private boolean SHOW_ANIMATION = false;

    private FragmentUtils(AppCompatActivity activity, Bundle savedInstance) {
        this.savedInstance = savedInstance;
        fragmentManager = activity.getSupportFragmentManager();
    }

    private FragmentUtils(Fragment fragment, Bundle savedInstance) {
        this.savedInstance = savedInstance;
        fragmentManager = fragment.getChildFragmentManager();
    }

    /**
     * Create instance of {@link FragmentUtils}
     *
     * @param activity {@link android.app.Activity}
     * @return {@link FragmentUtils}
     */
    public static FragmentUtils get(AppCompatActivity activity, @Nullable Bundle savedInstance) {
        return new FragmentUtils(activity, savedInstance);
    }

    /**
     * Create instance of {@link FragmentUtils}
     *
     * @param fragment {@link Fragment}
     * @return {@link FragmentUtils}
     */
    public static FragmentUtils get(Fragment fragment, @Nullable Bundle savedInstance) {
        return new FragmentUtils(fragment, savedInstance);
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    /**
     * Call this method start any fragment
     *
     * @param fragment       {@link Fragment} object which will be placed upon current view
     * @param containerID    Container layout id
     * @param addToBackState whether to add to backstack or not
     */
    public void startFragment(Fragment fragment, @IdRes int containerID, boolean addToBackState) {
        // TODO: 25-09-2017 make show animation true to enable animations
        loadFragment(fragment, containerID, addToBackState, SHOW_ANIMATION);
    }

    public void startFragment(Fragment fragment, @IdRes int containerID, boolean addToBackState, boolean showAnimation) {
        loadFragment(fragment, containerID, addToBackState, showAnimation);
    }

    /**
     * pop any fragment from only this method while using {@link FragmentUtils}
     *
     * @return true if any fragment is popped.
     */
    public boolean popFragment() {
        boolean isPop = false;
        if (fragmentManager.getBackStackEntryCount() > 0) {
            isPop = true;
            fragmentManager.popBackStack();
        }
        return isPop;
    }

    /**
     * Clears full backstack
     */
    public void clearBackStack() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void loadFragment(Fragment fragment, int containerID, boolean addToBackState, boolean showAnimation) {
        String tag = fragment.getClass().getCanonicalName();
        if (fragmentManager.findFragmentByTag(tag) != null && savedInstance != null) {
            fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (savedInstance == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (showAnimation)
                fragmentTransaction.setCustomAnimations(R.animator.flip_right_in,
                        R.animator.flip_right_out,
                        R.animator.flip_left_in,
                        R.animator.flip_left_out);
//            if (showAnimation)
//                fragmentTransaction.setCustomAnimations(R.animator.cube_right_in,
//                        R.animator.cube_left_out,
//                        R.animator.cube_left_in,
//                        R.animator.cube_right_out);

            fragmentTransaction.replace(containerID, fragment, tag);
            if (addToBackState)
                fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}
