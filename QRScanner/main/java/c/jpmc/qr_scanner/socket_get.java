package c.jpmc.qr_scanner;

/**
 * Created by Dathu on 15-04-2018.
 */
import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public  class socket_get extends Application {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://18.222.70.196:8000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
