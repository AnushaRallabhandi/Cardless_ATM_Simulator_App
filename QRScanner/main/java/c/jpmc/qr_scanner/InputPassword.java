package c.jpmc.qr_scanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class InputPassword extends AppCompatActivity {
    PatternLockView mPatternLockView;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);
        SharedPreferences pref=getSharedPreferences("prefs",0);
        password=pref.getString("password","0");
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if(password.equals(PatternLockUtils.patternToString(mPatternLockView,pattern))) {
                    Intent in = new Intent(getApplicationContext(), bottom_navigation.class);
                    startActivity(in);
                    finish();
                }
            }

            @Override
            public void onCleared() {

            }
        });
    }
}