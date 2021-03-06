package com.example.macowner.flickr_api.activity;

/**
 * Created by Mikhail on 9/29/16.
 */

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.macowner.flickr_api.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {
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
