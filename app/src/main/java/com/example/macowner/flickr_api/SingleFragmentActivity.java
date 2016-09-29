package com.example.macowner.flickr_api;

/**
 * Created by macowner on 9/29/16.
 */

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
  protected abstract Fragment createFragment();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fragment);
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager.findFragmentById(R.id.fragment_container);

    if (fragment == null) {
      fragment = createFragment();
      manager.beginTransaction()
          .add(R.id.fragment_container, fragment)
          .commit();
    }
  }
}
