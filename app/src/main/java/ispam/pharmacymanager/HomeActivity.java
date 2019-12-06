package ispam.pharmacymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import ispam.pharmacymanager.helpers.SQLiteHandler;
import ispam.pharmacymanager.helpers.SessionManager;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        changeFragment(new HomeFragment(), HomeFragment.class
                .getSimpleName());
    }


    public void changeFragment(Fragment fragment, String tagFragmentName) {

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment currentFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        for (Fragment frag : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().hide(frag).commit();
        }
        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName);

        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.container, fragmentTemp, tagFragmentName);
        } else {
            getSupportFragmentManager().beginTransaction().show(fragmentTemp).commit();

            fragmentTransaction.show(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    public void showHideFragment(final Fragment fragment){
        FragmentManager mFragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);

        if (fragment.isHidden()) {
            fragmentTransaction.show(fragment);
            Log.d("hidden","Show");
        } else {
            fragmentTransaction.hide(fragment);
            Log.d("Shown","Hide");
        }

        fragmentTransaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_navigation_item_home:
                            Toast.makeText(HomeActivity.this, "asddddd", Toast.LENGTH_SHORT).show();

                            changeFragment(new HomeFragment(), HomeFragment.class
                                    .getSimpleName());

                            return true;
                        case R.id.bottom_navigation_item_repo:
                            changeFragment(new RepoFragment(), RepoFragment.class
                                    .getSimpleName());
                            return true;
                        case R.id.bottom_navigation_item_profile:
                            changeFragment(new ProfileFragment(), ProfileFragment.class
                                    .getSimpleName());
                            return true;
                    }
                    return false;
                }
            };
}
