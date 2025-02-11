package dev.ktroude.ft_hangout.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Utility class that simplifies handling of activity results and permission requests.
 * This class provides helper methods to:
 * Register an `ActivityResultLauncher` to handle results from activities (e.g., starting an intent and processing its result).
 * Request permissions at runtime and execute a callback if the permission is granted.
 * This is useful for reducing boilerplate code when handling common Android tasks like:
 * - Opening an activity for result (e.g., editing a contact).
 * - Requesting runtime permissions (e.g., sending SMS, making phone calls).
 */
public class PermissionAndResultHelper {

    /**
     * Registers a launcher for starting an activity and handling its result.
     * This method allows an activity to start another activity (e.g., EditContactActivity),
     * and process the result when the activity finishes. If the activity returns `RESULT_OK`,
     * the provided callback will be executed.
     *
     * @param activity The calling activity that needs to register the result listener.
     * @param callback The callback function that will be executed when the result is received.
     * @return ActivityResultLauncher<Intent> that can be used to launch activities.
     */
    public static ActivityResultLauncher<Intent> registerActivityResult(
            AppCompatActivity activity, Runnable callback) {
        return activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                callback.run();
            }
        });
    }

    /**
     * Requests a permission at runtime and executes a callback if the permission is granted.
     * This method simplifies permission handling by checking whether a permission is already granted.
     * If granted, the provided callback is executed immediately.
     * If not granted, the system displays a permission request dialog.
     *
     * @param activity    The calling activity that needs to request the permission.
     * @param permission  The specific permission being requested (e.g., Manifest.permission.SEND_SMS).
     * @param requestCode The request code associated with the permission request.
     * @param onGranted   The callback function to execute if the permission is granted.
     */
    public static void requestPermission(
            AppCompatActivity activity,
            String permission,
            int requestCode,
            Runnable onGranted) {

        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            // If permission is already granted, execute the callback immediately.
            onGranted.run();
        } else {
            // Otherwise, request the permission from the user.
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }
}

