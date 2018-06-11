package c.jpmc.qr_scanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dathu on 1/23/2018.
 */

public class Home extends Fragment {
    public Socket socket;
    JSONObject obj;
    private Button qr,sms;
    private String number;
    private EditText priceReq;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private IntentIntegrator qrScan;
    public static String statuscode;
    public String td;
    Intent i;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        socket_get app = (socket_get) this.getActivity().getApplication();
        socket = app.getSocket();
        qr=view.findViewById(R.id.button2);
        sms=view.findViewById(R.id.button);
        priceReq=view.findViewById(R.id.price);
        Boolean isFirstRun = getActivity().getApplicationContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        //getActivity().setContentView(R.layout.activity_main2);
        if(isFirstRun) {
            startActivity(new Intent(getContext(), RegisterActivity.class));
        }
        loadIMEI();
        number = getActivity().getApplicationContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("phone", null);
        qrScan = new IntentIntegrator(getActivity());
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanqr(view);
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sms.setBackgroundColor(Color.parseColor("#65f442"));
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("otp");
                myRef.child(number).child("status").setValue("Active");
                DatabaseReference sms1= myRef.child(number).child("status");
                sms1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                     String stat=dataSnapshot.getValue(String.class);
                     if(!stat.equals("Active")){
                       sms.setBackgroundColor(Color.parseColor("#ffffff"));
                     }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    public void scanqr(View v) {
        try {
            _createConnection();
            //Intent i = new Intent(this, QrCode_Scanner.class);
            //startActivity(i);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("bankdb");
            myRef.child(number).child("Requested Balance").setValue(priceReq.getText().toString());
            qrScan.initiateScan();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    String qrData=result.getContents().toString();
                    System.out.println("QR code Data:  " + qrData);
                    String[] parts = qrData.split(":");
                    JSONObject obj = new JSONObject();
                    obj.put("msg", parts[0]);
                    obj.put("wsid", parts[1]);
                    System.out.println("QRdata 200");
                    socket.emit("QRData", obj, new Ack() {
                        @Override
                        public void call(Object... args) {

                            String data = (String) args[0];
                            System.out.println("QRd200");
                            if (data.equals("200")) {
                                getInfo();
                            }

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            doPermissionGrantedStuffs();
        }
    }
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Request")
                    .setMessage(getString(R.string.permission_read_phone_state_rationale))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                doPermissionGrantedStuffs();
            } else {
                alertAlert(getString(R.string.permissions_not_granted_read_phone_state));
            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(getContext())
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })
                .show();
    }
    // public void clicked(View v) {
    public void doPermissionGrantedStuffs() {
        //Have an  object of TelephonyManager
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //Get IMEI Number of Phone  //////////////// for this example i only need the IMEI
        td = tm.getDeviceId().toString();
    }

    public void _createConnection() {
        try {


            System.out.println("hello");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    socket.emit("GenerateQR", new Ack() {
                        @Override
                        public void call(Object... args) {
                            System.out.println("fist::  " + args[0]);

                        }
                    });

                }


            }).on("event", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    System.out.println("second");
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    System.out.println("third");
                }

            });
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getInfo() {
// GET DEVICE ID
        final String deviceId = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);

// GET IMEI NUMBER
        JSONObject obj = new JSONObject();
        try {
            obj.put("device_id", deviceId);
            obj.put("name", deviceId);
            obj.put("imei", td);
            obj.put("number", number);
            obj.put("expire", "1");
            socket.emit("Auth0", obj);
            //currentStatus.concat("Auth0 Send");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
