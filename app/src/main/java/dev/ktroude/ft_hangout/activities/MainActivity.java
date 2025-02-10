package dev.ktroude.ft_hangout.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.ktroude.ft_hangout.MainApplication;
import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.adapters.ContactAdapter;
import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private static final int PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_CODE);
        }

        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        loadSavedColor();
        loadContacts();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_change_color) {
            View menuItemView = findViewById(R.id.action_change_color);

            if (menuItemView != null) {
                TooltipCompat.setTooltipText(menuItemView, getString(R.string.change_color));

                menuItemView.setOnLongClickListener(v -> {
                    Toast tooltip = Toast.makeText(this, getString(R.string.change_color), Toast.LENGTH_SHORT);
                    View view = tooltip.getView();
                    assert view != null;
                    view.setBackgroundColor(Color.BLUE);
                        TextView text = view.findViewById(android.R.id.message);
                        if (text != null) {
                            text.setTextColor(Color.WHITE);
                        }
                    tooltip.show();
                    return true;
                });
            }

            showColorPickerDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();

        boolean wasInBackground = MainApplication.getLifecycleTracker().wasInBackground();
        Log.d("DEBUG_APP", "wasInBackground: " + wasInBackground);

        if (wasInBackground) {
            SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
            long lastExitTime = preferences.getLong("last_exit_time", 0);

            if (lastExitTime != 0) {
                long diffInMillis = System.currentTimeMillis() - lastExitTime;
                long diffInSeconds = diffInMillis / 1000;

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(new Date(lastExitTime));

                String toastMsg = formattedDate + "\n" + getString(R.string.toast_pause) + " " + diffInSeconds + " sec";
                Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
            }
        }

        IntentFilter filter = new IntentFilter("dev.ktroude.ft_hangout.NEW_SMS_RECEIVED");
        ContextCompat.registerReceiver(
                this,
                smsReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission SMS accordée", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Permission SMS refusée", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadContacts();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("last_exit_time", System.currentTimeMillis());
        editor.apply();
    }

    private void showColorPickerDialog() {
        final String[] colors = {
                getString(R.string.red),
                getString(R.string.blue),
                getString(R.string.green),
                getString(R.string.yellow),
                getString(R.string.magenta)
        };

        final int[] colorValues = {
                ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_error),
                ContextCompat.getColor(this, R.color.purple_700),
                ContextCompat.getColor(this, R.color.teal_700),
                ContextCompat.getColor(this, R.color.yellow_custom),
                ContextCompat.getColor(this, R.color.purple_200)
        };
        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, com.google.android.material.R.style.ThemeOverlay_Material3_Light);

        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
        builder.setTitle(getString(R.string.select_color))
                .setItems(colors, (dialog, which) -> {
                    int selectedColor = colorValues[which];
                    changeHeaderColor(selectedColor);
                    saveColorPreference(selectedColor);
                });
        builder.create().show();
    }


    private void changeHeaderColor(int color) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(color);
    }

    private void saveColorPreference(int color) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("header_color", color);
        editor.apply();
    }

    private void loadSavedColor() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int savedColor = prefs.getInt("header_color", R.color.purple_700);
        changeHeaderColor(savedColor);
    }

    private void loadContacts() {
        List<Contact> contacts = databaseHelper.getAllContacts();
        ContactAdapter contactAdapter = new ContactAdapter(contacts);
        recyclerView.setAdapter(contactAdapter);

        if (contacts.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_contact), Toast.LENGTH_SHORT).show();
        }
    }
}
