package com.hakoru.tuner;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.uimanager.ViewManager;
import java.util.Collections;
import java.util.List;
import android.app.Application;
import android.content.Context;

public class HakoruTunerTwoPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(
            com.facebook.react.bridge.ReactApplicationContext reactContext) {
        return Collections.<NativeModule>singletonList(new HakoruTunerTwoModule(reactContext));
    }

    @Override
    public List<ViewManager> createViewManagers(
            com.facebook.react.bridge.ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}