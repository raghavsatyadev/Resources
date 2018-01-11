package com.support.utils;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.support.R;

public class FragmentUtils {

    private Bundle savedInstance = null;
    private FragmentManager fragmentManager;
    /**
     * make true to enable animations
     */
    private boolean SHOW_ANIMATION = false;

    private FragmentUtils(AppCompatActivity activity, Bundle savedInstance) {
        this.savedInstance = savedInstance;
        fragmentManager = activity.getSupportFragmentManager();
    }

    private FragmentUtils(Fragment nextFragment, Bundle savedInstance) {
        this.savedInstance = savedInstance;
        fragmentManager = nextFragment.getChildFragmentManager();
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
     * @param nextFragment {@link Fragment}
     * @return {@link FragmentUtils}
     */
    public static FragmentUtils get(Fragment nextFragment, @Nullable Bundle savedInstance) {
        return new FragmentUtils(nextFragment, savedInstance);
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
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

    /**
     * Call this method replace any fragment
     *
     * @param nextFragment {@link Fragment} object which will be placed upon current view
     * @param containerID  Container layout id
     */
    public void replace(Fragment nextFragment, @IdRes int containerID) {
        startFragment(null, nextFragment, containerID, false, SHOW_ANIMATION, null);
    }

    /**
     * Call this method add any fragment
     *
     * @param currentFragment {@link Fragment} object which is currently displayed
     * @param nextFragment    {@link Fragment} object which will be placed upon current view
     * @param containerID     Container layout id
     */
    public void add(Fragment currentFragment, Fragment nextFragment, @IdRes int containerID) {
        startFragment(currentFragment, nextFragment, containerID, true, SHOW_ANIMATION, null);
    }

    /**
     * Call this method replace any fragment with fragment animation
     *
     * @param nextFragment {@link Fragment} object which will be placed upon current view
     * @param containerID  Container layout id
     */
    public void replaceWithAnimation(Fragment nextFragment, @IdRes int containerID) {
        startFragment(null, nextFragment, containerID, false, true, null);
    }

    /**
     * Call this method add any fragment with fragment animation
     *
     * @param currentFragment {@link Fragment} object which is currently displayed
     * @param nextFragment    {@link Fragment} object which will be placed upon current view
     * @param containerID     Container layout id
     */
    public void addAnimation(Fragment currentFragment, Fragment nextFragment, @IdRes int containerID) {
        startFragment(currentFragment, nextFragment, containerID, true, true, null);
    }

    /**
     * Call this method replace any fragment with fragment animation and image shared transition
     *
     * @param nextFragment {@link Fragment} object which will be placed upon current view
     * @param containerID  Container layout id
     * @param animatedView {@link ImageView} object which will shared for transition
     */
    public void replaceWithAnimationTransition(Fragment nextFragment, @IdRes int containerID, ImageView animatedView) {
        startFragment(null, nextFragment, containerID, false, true, animatedView);
    }

    /**
     * Call this method replace any fragment with fragment animation and image shared transition
     *
     * @param currentFragment {@link Fragment} object which is currently displayed
     * @param nextFragment    {@link Fragment} object which will be placed upon current view
     * @param containerID     Container layout id
     * @param animatedView    {@link ImageView} object which will shared for transition
     */

    public void addWithAnimationTransition(Fragment currentFragment, Fragment nextFragment, @IdRes int containerID, ImageView animatedView) {
        startFragment(currentFragment, nextFragment, containerID, true, true, animatedView);
    }

    private void startFragment(Fragment currentFragment, Fragment nextFragment, int containerID, boolean addToBackState, boolean showAnimation, ImageView animatedView) {
        String tag = nextFragment.getClass().getCanonicalName();
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
            if (animatedView != null) {
                fragmentTransaction.addSharedElement(animatedView, ViewCompat.getTransitionName(animatedView));
            }
            fragmentTransaction.replace(containerID, nextFragment, tag);
            if (addToBackState)
                fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        }
    }
}
