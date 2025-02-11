package dev.ktroude.ft_hangout.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;
import dev.ktroude.ft_hangout.utils.ImageHelper;
import dev.ktroude.ft_hangout.utils.PermissionAndResultHelper;
import dev.ktroude.ft_hangout.utils.Utils;

/**
 * ContactDetailsActivity handles the display and interactions of a single contact's details.
 * It allows users to view, edit, delete, or call the selected contact.
 */
public class ContactDetailsActivity extends AppCompatActivity {

    private TextView textViewName, textViewPhone, textViewEmail, textViewAddress;
    private ImageView imageViewProfile;
    private FloatingActionButton buttonCall, buttonMessage, buttonEdit, buttonDelete;
    private DatabaseHelper databaseHelper;
    private int contactId;
    private Contact contact;
    private ActivityResultLauncher<Intent> editContactLauncher;
    private ActivityResultLauncher<Intent> messageLauncher;

    /**
     * Called when the activity is created. Initializes UI components, sets up event handlers,
     * and loads contact details.
     *
     * @param savedInstanceState The saved instance state from a previous session.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_details);

        databaseHelper = new DatabaseHelper(this);
        initContactId();

        editContactLauncher = PermissionAndResultHelper.registerActivityResult(this, this::setViewsData);
        messageLauncher = PermissionAndResultHelper.registerActivityResult(this, this::setViewsData);

        initViews();
        setViewsData();
        setButtonFunctions();
    }

    /**
     * Retrieves the contact ID from the intent extras and ensures a valid ID is provided.
     * Displays an error message and exits if the ID is invalid.
     */
    private void initContactId() {
        contactId = getIntent().getIntExtra("contact_id", -1);
        if (contactId == -1) {
            Toast.makeText(this, getString(R.string.id_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        contact = databaseHelper.getContactById(contactId);
    }

    /**
     * Initializes all UI components such as TextViews, ImageView, and Buttons.
     */
    private void initViews() {
        textViewName = findViewById(R.id.textViewName);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewAddress = findViewById(R.id.textViewAddress);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonCall = findViewById(R.id.fabCall);
        buttonMessage = findViewById(R.id.fabMessage);
        buttonEdit = findViewById(R.id.fabEdit);
        buttonDelete = findViewById(R.id.fabDelete);
    }

    /**
     * Retrieves contact details from the database and updates the UI.
     */
    private void setViewsData() {
        contact = databaseHelper.getContactById(contactId);

        textViewName.setText(String.format("%s %s", contact.getFirstname(), contact.getLastname()));
        textViewPhone.setText(Utils.parseNumberToDisplay(contact.getTelNumber()));
        textViewEmail.setText(contact.getEmail());
        textViewAddress.setText(contact.getAddress());

        imageViewProfile.setImageBitmap(ImageHelper.decodeBase64ToImage(contact.getPicture()));
    }

    /**
     * Assigns event listeners to buttons for calling, messaging, editing, and deleting the contact.
     */
    private void setButtonFunctions() {
        buttonCall.setOnClickListener(v -> makeCall());
        buttonMessage.setOnClickListener(v -> sendMessage());
        buttonEdit.setOnClickListener(v -> editContact());
        buttonDelete.setOnClickListener(v -> deleteContact());
    }

    /**
     * Handles permission request results. If call permission is granted, initiates the call.
     *
     * @param requestCode  The request code passed when requesting permission.
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        }
    }

    /**
     * Initiates a phone call to the contact if the phone number exists.
     * Requests CALL_PHONE permission if not already granted.
     */
    private void makeCall() {
        String phoneNumber = contact.getTelNumber();

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return;
        }

        PermissionAndResultHelper.requestPermission(this, Manifest.permission.CALL_PHONE, 1, () -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        });
    }

    /**
     * Opens the messaging screen for the contact.
     */
    private void sendMessage() {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("contact_id", contactId);
        messageLauncher.launch(intent);
    }

    /**
     * Opens the edit contact screen.
     */
    private void editContact() {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra("contact_id", contactId);
        editContactLauncher.launch(intent);
    }

    /**
     * Deletes the contact from the database and closes the activity.
     */
    private void deleteContact() {
        databaseHelper.deleteContact(contactId);
        finish();
    }
}
