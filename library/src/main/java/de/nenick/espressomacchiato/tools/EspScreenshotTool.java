package de.nenick.espressomacchiato.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public class EspScreenshotTool {

    public static void takeWithName(String name) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        File sddir = new File(obtainScreenshotDirectory());
        if (!sddir.exists() && !sddir.mkdir()) {
            throw new IllegalStateException("screenshot folder does not exist: " + sddir.getAbsolutePath());
        }
        String screenshotName = name + ".png";
        File screenshotFile = new File(obtainScreenshotDirectory(), screenshotName);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            if (!device.takeScreenshot(screenshotFile)) {
                throw new IllegalStateException("take picture failed");
            }
        } else {
            try {
                new EspScreenshotPreJellyBeanMr2().takeScreenShot(screenshotFile);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static String obtainScreenshotDirectory() {
        File externalCacheDir = InstrumentationRegistry.getTargetContext().getExternalCacheDir();
        if (externalCacheDir == null) {
            throw new IllegalStateException("could not find external cache dir to store screenshot");
        }
        return externalCacheDir.getAbsolutePath() + "/test-screenshots/";
    }

    private static final String TAG = "SCREENSHOT_TAG";


}
