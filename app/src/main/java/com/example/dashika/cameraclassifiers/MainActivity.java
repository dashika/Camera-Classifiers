package com.example.dashika.cameraclassifiers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.dashika.cameraclassifiers.View.Fragment.LoadingFragment;

public class MainActivity extends BaseActivity {

    public final static int MY_PERMISSIONS_REQUEST_CAMERA = 123;
    static FragmentManager fragmentManager;

    public static void replaceFragment(Fragment fragment, boolean addBackStack, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment, tag);
        if (addBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentByTag("camera");
        if (fragment == null) replaceFragment(new LoadingFragment(), false, "Loading");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                break;
        }
    }
}
