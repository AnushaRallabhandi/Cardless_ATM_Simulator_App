package c.jpmc.qr_code;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import org.json.JSONException;
import org.json.JSONObject;

public class phoneauth extends AppCompatActivity {
    Pinview p;
    Intent k;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneauth);
        Intent intent = getIntent();
        final String otp = intent.getStringExtra("pin");
        final String number=intent.getStringExtra("num");
        //Toast.makeText(getApplicationContext(), otp, Toast.LENGTH_LONG).show();
        p=findViewById(R.id.pinview);
        p.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                //Make api calls here or what not
                //Toast.makeText(phoneauth.this, p.getValue(), Toast.LENGTH_SHORT).show();
                if(otp.equals(p.getValue().toString())){
                    k = new Intent(phoneauth.this, verified.class);
                    try {
                        JSONObject s = new JSONObject();
                        s.put("uid", number);
                        k.putExtra("details",s.toString());
                        startActivity(k);
                        finish();
                    }
                    catch(Exception JSONException){
                        Toast.makeText(phoneauth.this,"Error", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(phoneauth.this,"Incorrect Password", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        // Simply Do noting!
    }
}
