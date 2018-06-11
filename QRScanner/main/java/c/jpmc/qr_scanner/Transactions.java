package c.jpmc.qr_scanner;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Belal on 1/23/2018.
 */

public class Transactions extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef ;
    List<FireModel> list;
    RecyclerView recycle;
    String number;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return inflater.inflate(R.layout.fragment_transactions, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycle = (RecyclerView) view.findViewById(R.id.recycle);
        database = FirebaseDatabase.getInstance();
        number = getActivity().getApplicationContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("phone", null);
        myRef = database.getReference("bankdb").child(number).child("transactions");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                list = new ArrayList<FireModel>();
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    FireModel value = dataSnapshot1.getValue(FireModel.class);
                    //String
                    //fire.setName(name);
                    //fire.setEmail(email);
                    //fire.setAddress(address);
                    list.add(value);

                }
                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list,getContext());
                RecyclerView.LayoutManager recyce = new GridLayoutManager(getContext(),1);
                recycle.setLayoutManager(recyce);
                recycle.setItemAnimator( new DefaultItemAnimator());
                recycle.setAdapter(recyclerAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

    }
}
