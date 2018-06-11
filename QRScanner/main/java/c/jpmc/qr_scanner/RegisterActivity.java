package c.jpmc.qr_scanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity{
    private CardForm cardForm;
    private EditText name;
    private String cardno,cvv,countryCode,Phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .mobileNumberRequired(true)
                .actionLabel("Basic Info")
                .setup(this);
        name=findViewById(R.id.userName);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
    public void validate(View v){
            if(cardForm.isValid()){
                cardno=cardForm.getCardNumber();
                cvv=cardForm.getCvv();
                countryCode=cardForm.getCountryCode();
                Phone=cardForm.getMobileNumber();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("bankdb");
                myRef.child(Phone).child("Card Number").setValue(cardno);
                myRef.child(Phone).child("cvv").setValue(cvv);
                myRef.child(Phone).child("name").setValue(name.getText().toString());
                myRef.child(Phone).child("Balance").setValue("3000");
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("phone",Phone).commit();
                Intent i= new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(i);
            }
            else {
                cardForm.validate();
                Toast.makeText(RegisterActivity.this, "Please complete the form", Toast.LENGTH_LONG).show();
            }
    }
}
