package de.nenick.espressomacchiato.elements;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.app.ActivityCompat;

import de.nenick.espressomacchiato.tools.EspPermissionsTool;
import de.nenick.espressomacchiato.tools.EspWait;

public class EspPermissionDialog {

    /**
     * wait below 1000ms was sometimes not enough for permissions update on circle ci emulator
     */
    public static int DELAY_FOR_UPDATE_PERMISSION_STATE = 1000;

    public static EspPermissionDialog build() {
        return new EspPermissionDialog();
    }

    public void allow() {
        click("Allow");
        waitUntilPermissionIsChanged();
    }

    /**
     * Warning: revoke an already granted permission would force your app to restart (test fail).
     * <p>
     * Deny tests should be the first executed tests.
     * Before test run ensure that all permission are revoked.
     * <p>
     * 1. Option: ordered test execution to force deny test first @FixMethodOrder(MethodSorters.NAME_ASCENDING) to avoid revoke
     * <p>
     * 2. Option: reset all permissions {@link EspPermissionsTool#resetAllPermission()}
     */
    public void deny() {
        avoidAppCrashWhenDenyGrantedPermission();
        click("Deny");
        waitUntilPermissionIsChanged();
    }

    protected void click(String target) {
        // permissions handling only available since android marshmallow
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject button = device.findObject(new UiSelector().text(target));

        try {
            button.waitForExists(3000);
            button.click();
        } catch (UiObjectNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private void avoidAppCrashWhenDenyGrantedPermission() {
        if (ActivityCompat.checkSelfPermission(InstrumentationRegistry.getTargetContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_DENIED) {
            throw new IllegalStateException("Deny would revoke permission and restart app. This would let all following tests fail. See documentation for details.");
        }
    }

    private void waitUntilPermissionIsChanged() {
        // need to wait some time until permission is changed
        EspWait.forDelay(DELAY_FOR_UPDATE_PERMISSION_STATE);
    }
}
