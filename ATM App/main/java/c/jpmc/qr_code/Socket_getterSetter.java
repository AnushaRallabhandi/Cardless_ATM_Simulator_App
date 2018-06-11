package c.jpmc.qr_code;

import android.app.Application;
import android.provider.SyncStateContract;

import java.net.URISyntaxException;
import java.util.logging.SocketHandler;

import io.socket.client.IO;
import io.socket.client.Socket;

public  class Socket_getterSetter extends Application {
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
