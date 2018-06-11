package c.jpmc.qr_scanner;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Belal on 1/23/2018.
 */

public class dashboard extends Fragment {
    FirebaseDatabase database;
    DatabaseReference myRef ;
    String number;
    private TextView balance;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return inflater.inflate(R.layout.fragment_dashboard, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        number = getActivity().getApplicationContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("phone", null);
        balance=view.findViewById(R.id.cash);
        myRef = database.getReference("bankdb").child(number).child("Balance");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String bal=dataSnapshot.getValue(String.class);
                balance.setText(bal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
