package com.example.starbucksook;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ConnectionService extends Service {
    public ConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}