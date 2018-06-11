package c.jpmc.qr_code;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
public class MainActivity extends AppCompatActivity {
    public Socket socket;
    ImageView imageView;
    Button button;
    EditText editText;
    private Runnable runnable;
    private Handler handler;
    String otp,stat ;
    private Button b1;
    private EditText et;
    Intent i;
    public String number;
    public final static int QRcodeWidth = 500 ;
    Bitmap decodedBitmap ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        i = getIntent();
        imageView = (ImageView)findViewById(R.id.imageView);
        Socket_getterSetter app = (Socket_getterSetter) this.getApplication();
        socket = app.getSocket();
        try {
            _createConnection();
            runOnUiThread(new Runnable() // start actions in UI thread
            {

                @Override
                public void run() {
                    socket.emit("GenerateQR", new Ack() {
                        @Override
                        public void call(Object... args) {
                            String enx = args[0].toString();
                            final String pureBase64Encoded = enx.substring(enx.indexOf(",") + 1);
                            System.out.println(pureBase64Encoded);
                            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                            decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        }
                    });
                    //System.out.println("inside"+decodedBitmap.toString());
                    setImg(decodedBitmap);
                }
            });
                    handler = new Handler();
                    runnable = new Runnable() {
                        public void run() {
                            socket.emit("GenerateQR", new Ack() {
                                @Override
                                public void call(Object... args) {
                                    String enx = args[0].toString();
                                    final String pureBase64Encoded = enx.substring(enx.indexOf(",") + 1);
                                    System.out.println(pureBase64Encoded);
                                    final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                                    decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                }
                            });
                            //System.out.println("inside"+decodedBitmap.toString());
                            setImg(decodedBitmap);
                            handler.postDelayed(this, 30000);
                        }
                    };
                    runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        et=findViewById(R.id.editText3);
        b1=findViewById(R.id.button3);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number=et.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("otp");
                DatabaseReference k = myRef.child(number).child("status");
                k.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        stat = dataSnapshot.getValue(String.class);
                        //   System.out.println(stat);
                        if (stat.equals("Active")) {
                            Toast.makeText(getApplicationContext(), "Active", Toast.LENGTH_LONG).show();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("otp");
                            otp = getSaltString();
                            myRef.child(number).child("pin").setValue(otp);
                            myRef.child(number).child("timestamp").setValue(System.currentTimeMillis());
                           final  String msg = "Your one time password is " + otp;
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL url = new URL("https://www.smsgatewayhub.com/api/mt/SendSMS?APIKey=dv5OnVjlnE2kAq9erToGew&senderid=TESTIN&channel=2&DCS=0&flashsms=0&number=91" + number + "&text=" + msg);
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.setDoOutput(true);
                                        conn.setRequestMethod("POST");
                                        // Toast.makeText(getApplicationContext(), "Otp is:"+otp,Toast.LENGTH_LONG).show();
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                        StringBuilder sb = new StringBuilder();
                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            // Append server response in string
                                            sb.append(line + "\n");
                                        }
                                        //Toast.makeText(getApplicationContext(),sb,Toast.LENGTH_LONG).show();
                                        i = new Intent(MainActivity.this, phoneauth.class);
                                        i.putExtra("pin", otp);
                                        i.putExtra("num",number);
                                        startActivity(i);
                                        et.setText("");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Thread t= new Thread(runnable);
                            t.start();

                        } else {
                            Toast.makeText(getApplicationContext(), "Inactive", Toast.LENGTH_LONG).show();
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
    protected String getSaltString() {
        String SALTCHARS = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 4) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
public void setImg(Bitmap k){
    imageView.setImageBitmap(k);
}
public void _createConnection() {
        try {
            System.out.println("hello");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.emit("message", new Ack() {
                        @Override
                        public void call(Object... args) {
                            System.out.println("fist::  " + args[0]);

                        }
                        //   Log.i()"helloo"+args);


                    });

                }


            }).on("event", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    System.out.println("second");
                }
            }).on("token",new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    i=new Intent(MainActivity.this,verified.class);
                    i.putExtra("details",args[0].toString());
                    startActivity(i);
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
    @Override
    public void onBackPressed() {
        // Simply Do noting!
    }
}
