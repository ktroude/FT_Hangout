package dev.ktroude.ft_hangout.activities;

import static dev.ktroude.ft_hangout.utils.ImageHelper.resizeBitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
 * AddContactActivity handles the creation of a new contact.
 * It allows users to input contact details and optionally select a profile picture.
 */
public class AddContactActivity extends AppCompatActivity {

    private TextInputEditText editFirstname, editLastname, editAddress, editEmail, editNumber;
    private ImageView imageViewProfile;
    private DatabaseHelper databaseHelper;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String encodedImage = "";

    /**
     * Called when the activity is first created. Initializes UI components and event listeners.
     *
     * @param savedInstanceState The saved instance state from a previous session.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        initButton();
    }

    /**
     * Opens the image gallery to allow the user to select a profile picture.
     */
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image selection from the gallery.
     *
     * @param requestCode The request code passed when selecting an image.
     * @param resultCode  The result code indicating success or failure.
     * @param data        The intent containing the selected image data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                bitmap = resizeBitmap(bitmap, 300);
                encodedImage = ImageHelper.encodeImageToBase64(bitmap);
                imageViewProfile.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes UI components and sets up click listeners.
     */
    private void initViews() {
        editFirstname = findViewById(R.id.editTextFirstname);
        editLastname = findViewById(R.id.editTextLastname);
        editAddress = findViewById(R.id.editTextAddress);
        editEmail = findViewById(R.id.editTextMail);
        editNumber = findViewById(R.id.editTextTelNumber);
        imageViewProfile = findViewById(R.id.imageViewProfile);

        imageViewProfile.setOnClickListener(view -> selectImageFromGallery());
    }

    /**
     * Initializes the button for adding a new contact and sets up its click listener.
     */
    private void initButton() {
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

    /**
     * Validates the form input fields to ensure necessary details are provided.
     *
     * @return true if the form is valid, false otherwise.
     */
    private Boolean checkForm() {
        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String number = editNumber.getText().toString().trim();

        return (!(firstname.isEmpty() && lastname.isEmpty()) && !number.isEmpty());
    }

    /**
     * Creates a new Contact object with the provided user input.
     *
     * @return a Contact object containing the entered details.
     */
    private Contact createContact() {
        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String number = editNumber.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String picture = encodedImage;

        return new Contact(0, firstname, lastname, email, address, number, picture);
    }

    /**
     * Saves the newly created contact in the database.
     *
     * @param contact The Contact object to be added.
     */
    private void addContactToDatabase(Contact contact) {
        databaseHelper.addContact(contact);
        Toast.makeText(this, getString(R.string.contact_added), Toast.LENGTH_SHORT).show();
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
