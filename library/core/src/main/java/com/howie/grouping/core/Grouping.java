package com.howie.grouping.core;


import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Grouping {

    private Map<Class, Object> serviceMap = new HashMap<>();
    private Map<Class<? extends IAppLike>, IAppLike> groupMap = new HashMap<>();

    public <T, Impl extends T> void addService(@NonNull Class<T> serviceClazz, @NonNull Impl impl) {
        serviceMap.put(serviceClazz, impl);
    }

    public <T> T getService(@NonNull Class<T> clazz) {
        return (T) serviceMap.get(clazz);
    }

    public static void register(@NonNull IAppLike appLike) {
        Class<? extends IAppLike> key = appLike.getClass();
        Map<Class<? extends IAppLike>, IAppLike> groupMap = Instance.INSTANCE.groupMap;
        if (groupMap.containsKey(key)) {
            return;
        }
        appLike.onCreate();
        groupMap.put(key, appLike);
    }

    public static void unregister(@NonNull Class<? extends IAppLike> clazz) {
        IAppLike appLike;
        // Java 的赋值语句是有返回值的
        if ((appLike = getInstance().groupMap.remove(clazz)) == null) {
            return;
        }
        appLike.onDestroy();
    }

    public static Grouping getInstance() {
        return Instance.INSTANCE;
    }

    private Grouping() {
    }

    private static class Instance {
        static final Grouping INSTANCE = new Grouping();
    }
}
