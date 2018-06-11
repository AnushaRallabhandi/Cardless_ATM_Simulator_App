package c.jpmc.qr_scanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class bottom_navigation extends AppCompatActivity {

    private TextView mTextMessage;
    private ActionBar toolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    toolbar.setTitle("Home");
                    fragment = new Home();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_dashboard:
                    toolbar.setTitle("dashboard");
                    fragment=new dashboard();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_notifications:
                    toolbar.setTitle("transaction");
                    fragment=new Transactions();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_maps:
                    toolbar.setTitle("Near by Atm's");
                    fragment=new maps();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Home");
        loadFragment(new Home());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}
