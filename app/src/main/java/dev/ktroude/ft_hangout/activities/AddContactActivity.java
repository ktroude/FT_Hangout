package dev.ktroude.ft_hangout.activities;

import android.os.Bundle;
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

public class AddContactActivity extends AppCompatActivity {

    private TextInputEditText editFirstname, editLastname, editAddress, editEmail, editNumber;
    private ImageView imageViewProfile;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        databaseHelper = new DatabaseHelper(this);

        editFirstname = findViewById(R.id.editTextFirstname);
        editLastname = findViewById(R.id.editTextLastname);
        editAddress = findViewById(R.id.editTextAddress);
        editEmail = findViewById(R.id.editTextMail);
        editNumber = findViewById(R.id.editTextTelNumber);
        imageViewProfile = findViewById(R.id.imageViewProfile);

        imageViewProfile.setOnClickListener(view -> {
            Toast.makeText(this, getString(R.string.change_profil_picture), Toast.LENGTH_SHORT).show();
        });

        Button addButton = findViewById(R.id.outlined_button);
        addButton.setOnClickListener(view -> {
            if (!checkForm()) {
                Toast.makeText(this, getString(R.string.error_contact_added), Toast.LENGTH_SHORT).show();
            } else {
                addContactToDatabase(createContact());
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

    private Contact createContact() {
        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String number = editNumber.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String picture = "";

        return new Contact(0, firstname, lastname, email, address, number, picture);
    }

    private void addContactToDatabase(Contact contact) {
        databaseHelper.addContact(contact);
        Toast.makeText(this, getString(R.string.contact_added), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
