package dev.ktroude.ft_hangout.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;

public class EditContactActivity extends AppCompatActivity {

    private TextInputEditText editFirstname, editLastname, editAddress, editEmail, editNumber;
    private ImageView imageViewProfile;
    private DatabaseHelper databaseHelper;
    private int contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        databaseHelper = new DatabaseHelper(this);

        contactId = getIntent().getIntExtra("contact_id", -1);
        if (contactId == -1) {
            Toast.makeText(this, "Erreur : Contact introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContactValues();

        imageViewProfile.setOnClickListener(view -> {
            Toast.makeText(this, getString(R.string.change_profil_picture), Toast.LENGTH_SHORT).show();
        });

        Button editButton = findViewById(R.id.outlined_button);
        editButton.setOnClickListener(view -> {
            if (!checkForm()) {
                Toast.makeText(this, getString(R.string.error_contact_added), Toast.LENGTH_SHORT).show();
            } else {
                updateContactInDatabase(getContact(contactId));
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private Boolean checkForm() {
        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String number = editNumber.getText().toString().trim();

        return (!(firstname.isEmpty() && lastname.isEmpty()) && !number.isEmpty());
    }

    private Contact getContact(Integer id) {
        return databaseHelper.getContactById(id);
    }

    private void updateContactInDatabase(Contact contact) {

        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String number = editNumber.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String picture = "";

        Contact updatedContact = new Contact(
                contact.getId(),
                firstname,
                lastname,
                email,
                address,
                number,
                picture
        );
        databaseHelper.updateContact(updatedContact);
    }

    private void setContactValues() {

        Contact contact = getContact(contactId);

        editFirstname = findViewById(R.id.editTextFirstname);
        editFirstname.setText(contact.getFirstname());

        editLastname = findViewById(R.id.editTextLastname);
        editLastname.setText(contact.getLastname());

        editAddress = findViewById(R.id.editTextAddress);
        editAddress.setText(contact.getAddress());

        editEmail = findViewById(R.id.editTextMail);
        editEmail.setText(contact.getEmail());

        editNumber = findViewById(R.id.editTextTelNumber);
        editNumber.setText(contact.getTelNumber());

        imageViewProfile = findViewById(R.id.imageViewProfile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
