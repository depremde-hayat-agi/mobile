package com.deha.app.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtils {
    /**
     * @param fragmentManager use {@link AppCompatActivity#getSupportFragmentManager} if calling from activity,
     *                        {@link Fragment#getChildFragmentManager()} if calling from fragment
     * @param fragment        fragment to show
     * @param containerViewId id of the container view (e.g. binding.container.getId())
     * @param tag             provide a unique tag if you need this fragment to be added to back stack
     */
    public static void replaceFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment,
                                       int containerViewId, @Nullable String tag) {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerViewId, fragment, tag);
        if (tag != null) {
            transaction.addToBackStack(tag);
            transaction.commit();
        } else {
            transaction.commitNow();
        }
    }

}

