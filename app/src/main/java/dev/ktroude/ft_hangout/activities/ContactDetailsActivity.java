package dev.ktroude.ft_hangout.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;

public class ContactDetailsActivity extends AppCompatActivity {

    private TextView textViewName, textViewPhone, textViewEmail, textViewAddress;
    private ImageView imageViewProfile;
    private FloatingActionButton buttonCall, buttonMessage, buttonEdit, buttonDelete;
    private DatabaseHelper databaseHelper;
    private int contactId;

    private final ActivityResultLauncher<Intent> editContactLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadContactData();
                }
            });

    private final ActivityResultLauncher<Intent> messageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadContactData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_details);
        databaseHelper = new DatabaseHelper(this);

        contactId = getIntent().getIntExtra("contact_id", -1);
        if (contactId == -1) {
            Toast.makeText(this, getString(R.string.id_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textViewName = findViewById(R.id.textViewName);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewAddress = findViewById(R.id.textViewAddress);
        imageViewProfile = findViewById(R.id.imageViewProfile);

        buttonCall = findViewById(R.id.fabCall);
        buttonMessage = findViewById(R.id.fabMessage);
        buttonEdit = findViewById(R.id.fabEdit);
        buttonDelete = findViewById(R.id.fabDelete);

        loadContactData();

        buttonCall.setOnClickListener(v -> makeCall());
        buttonMessage.setOnClickListener(v -> sendMessage());
        buttonEdit.setOnClickListener(v -> editContact(v));
        buttonDelete.setOnClickListener(v -> deleteContact());
    }

    private void loadContactData(){
        try {
            Contact contact = databaseHelper.getContactById(contactId);
            textViewName.setText(String.format("%s %s", contact.getFirstname(), contact.getLastname()));
            textViewPhone.setText(parseNumberToDisplay(contact.getTelNumber()));
            textViewEmail.setText(contact.getEmail());
            textViewAddress.setText(contact.getAddress());
//            imageViewProfile.setImage(contact.getPicture());
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.id_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private String parseNumberToDisplay(String number) {
        StringBuilder sb = new StringBuilder(number);

        for (int i = 0; i < sb.length(); i += 3) {
            sb.insert(i, " ");
        }
        return sb.toString();
    }

    private void makeCall() {

    }

    private void sendMessage() {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("contact_id", contactId);
        messageLauncher.launch(intent);
    }

    private void editContact(View view) {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra("contact_id", contactId);
        editContactLauncher.launch(intent);
    }

    private void deleteContact() {
        databaseHelper.deleteContact(contactId);
        finish();
    }

}