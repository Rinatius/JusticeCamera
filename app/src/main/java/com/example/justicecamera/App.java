package com.example.justicecamera;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.backends.pipeline.Fresco;

public class App extends MultiDexApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context;
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheFilesCount(3)
                .build();
    }
}
