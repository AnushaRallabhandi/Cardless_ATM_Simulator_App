package c.jpmc.qr_scanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.Matrix4f;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class CreatePassword extends AppCompatActivity {
    PatternLockView mPatternLockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
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
                SharedPreferences pref=getSharedPreferences("prefs",0);
                SharedPreferences.Editor ed=pref.edit();
                ed.putString("password", PatternLockUtils.patternToString(mPatternLockView,pattern));
                ed.commit();
                Intent in=new Intent(getApplicationContext(),Main2.class);
                startActivity(in);
                finish();
            }

            @Override
            public void onCleared() {

            }
        });
    }
}
