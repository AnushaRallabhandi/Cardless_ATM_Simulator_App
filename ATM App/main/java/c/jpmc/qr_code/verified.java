package c.jpmc.qr_code;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class verified extends AppCompatActivity {
    private EditText et;
    private TextView tv;
    private Button b1,b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified);
        et=findViewById(R.id.price);
        tv=findViewById(R.id.phone);
        Intent intent = getIntent();
        b1=findViewById(R.id.button2);
        String number = intent.getStringExtra("details");
        try {
            JSONObject jsonObj = new JSONObject(number);
            final String number1=jsonObj.getString("uid").toString();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("bankdb");
            DatabaseReference  k= myRef.child(number1).child("Requested Balance");
            k.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String amount=dataSnapshot.getValue(String.class);
                    et.setText(amount);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.w(TAG, "onCancelled", databaseError.toException());
                }
            });
            DatabaseReference  k1= myRef.child(number1).child("name");
            k1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name=dataSnapshot.getValue(String.class);
                    tv.setText("Hello "+name);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.w(TAG, "onCancelled", databaseError.toException());
                }
            });
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                    final DatabaseReference myRef1 = database1.getReference("bankdb");
                    DatabaseReference  k2= myRef1.child(number1).child("Balance");
                    final DatabaseReference myRef2 = database1.getReference("otp");
                    myRef2.child(number1).child("status").setValue("Inactive");
                    k2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String balance=dataSnapshot.getValue(String.class);
                            int bal=Integer.parseInt(balance);
                            int req=Integer.parseInt(et.getText().toString());
                            if(req>bal){
                                Toast.makeText(getApplicationContext(),"Insufficient Balance",Toast.LENGTH_LONG).show();
                            }
                            else{
                                int rem=bal-req;
                                String rem1 = Integer.toString(rem);
                                Intent i = new Intent(verified.this, balance.class);
                                i.putExtra("mode", "cash");
                                i.putExtra("bal",rem1);
                                i.putExtra("num",number1);
                                DatabaseReference db=myRef1.child(number1).child("transactions");
                                String id=db.push().getKey();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                db.child(id).child("bbal").setValue(Integer.toString(bal));
                                db.child(id).child("ramt").setValue(Integer.toString(req));
                                db.child(id).child("abal").setValue(rem1);
                                db.child(id).child("date").setValue(formatter.format(date));
                                startActivity(i);
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "onCancelled", databaseError.toException());
                        }
                    });
                }
            });
        }
        catch (JSONException ex){
            et.setText("error");
            Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public void onBackPressed() {
        // Simply Do noting!
    }
}
