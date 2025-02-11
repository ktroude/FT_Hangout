package dev.ktroude.ft_hangout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;
import dev.ktroude.ft_hangout.utils.ImageHelper;

/**
 * Activity for editing an existing contact.
 * Allows users to modify contact details including name, email, phone number, and profile picture.
 */
public class EditContactActivity extends AppCompatActivity {

    private TextInputEditText editFirstname, editLastname, editAddress, editEmail, editNumber;
    private ImageView imageViewProfile;
    private DatabaseHelper databaseHelper;
    private int contactId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String encodedImage = "";

    /**
     * Called when the activity is created.
     * Initializes views, sets contact ID, and loads contact data.
     *
     * @param savedInstanceState The saved instance state from a previous session.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        setContactId();
        setViewsData();

        imageViewProfile.setOnClickListener(view -> selectImageFromGallery());
    }

    /**
     * Opens the image picker to select a profile picture from the gallery.
     */
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image selection from the gallery.
     * Converts the selected image to Base64 and updates the profile picture view.
     *
     * @param requestCode Request code identifying the action.
     * @param resultCode  Result code indicating success or failure.
     * @param data        The returned data containing the selected image URI.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                encodedImage = ImageHelper.handleImageResult(this, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            imageViewProfile.setImageBitmap(ImageHelper.decodeBase64ToImage(encodedImage));
        }
    }

    /**
     * Checks if the form is valid by ensuring required fields are not empty.
     *
     * @return true if form fields are valid, false otherwise.
     */
    private Boolean checkForm() {
        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String number = editNumber.getText().toString().trim();

        return (!(firstname.isEmpty() && lastname.isEmpty()) && !number.isEmpty());
    }

    /**
     * Retrieves a contact from the database using the given contact ID.
     *
     * @param id The contact ID.
     * @return The Contact object.
     */
    private Contact getContact(Integer id) {
        return databaseHelper.getContactById(id);
    }

    /**
     * Updates the contact information in the database with the new input values.
     *
     * @param contact The contact object to be updated.
     */
    private void updateContactInDatabase(Contact contact) {

        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String number = editNumber.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String picture = encodedImage.isEmpty() ? contact.getPicture() : encodedImage;

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

    /**
     * Retrieves the contact ID from the intent and validates it.
     * If the contact ID is invalid, the activity is closed with an error message.
     */
    private void setContactId() {
        contactId = getIntent().getIntExtra("contact_id", -1);
        if (contactId == -1) {
            Toast.makeText(this, getString(R.string.id_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Initializes views by binding UI elements from the layout.
     */
    private void initViews() {
        editFirstname = findViewById(R.id.editTextFirstname);
        editLastname = findViewById(R.id.editTextLastname);
        editAddress = findViewById(R.id.editTextAddress);
        editEmail = findViewById(R.id.editTextMail);
        editNumber = findViewById(R.id.editTextTelNumber);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        initButton();
    }

    /**
     * Populates the views with the current contact details.
     */
    private void setViewsData() {
        Contact contact = getContact(contactId);

        editFirstname.setText(contact.getFirstname());
        editLastname.setText(contact.getLastname());
        editAddress.setText(contact.getAddress());
        editEmail.setText(contact.getEmail());
        editNumber.setText(contact.getTelNumber());
        imageViewProfile.setImageBitmap(ImageHelper.decodeBase64ToImage(contact.getPicture()));
    }

    /**
     * Initializes the update button and sets its click listener.
     */
    private void initButton() {
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

    /**
     * Closes the database connection when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
