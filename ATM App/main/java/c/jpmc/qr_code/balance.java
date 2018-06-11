package c.jpmc.qr_code;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class balance extends AppCompatActivity {
    public TextView bal;
    private ImageView checkView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        Intent intent = getIntent();
        final String mode = intent.getStringExtra("mode");
        final String number=intent.getStringExtra("num");
        final String balance=intent.getStringExtra("bal");
        bal=findViewById(R.id.balance);
        bal.setText(balance);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("bankdb");
        myRef.child(number).child("Balance").setValue(balance);
        checkView = (ImageView) findViewById(R.id.check);
        ((Animatable) checkView.getDrawable()).start();
    }
    @Override
    public void onBackPressed() {
        // Simply Do noting!
        finish();
    }
}
