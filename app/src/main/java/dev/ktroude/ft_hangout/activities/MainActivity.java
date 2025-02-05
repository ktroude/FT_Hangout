package dev.ktroude.ft_hangout.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.adapters.ContactAdapter;
import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (db != null) {
            Log.d("DB_TEST", "Database created !");
            db.close();
        }

        Contact contact = new Contact(1, "Jean", "Dupont", "jean@email.com", "Paris", "0601020304", "");
        databaseHelper.addContact(contact);

        contact.setFirstname("Jean-Michel");
        databaseHelper.updateContact(contact);
        Log.d("DB_TEST", "Contact mis à jour: " + contact.getFirstname());

        List<Contact> contacts = databaseHelper.getAllContacts();
        for (Contact c : contacts) {
            Log.d("DB_CONTACTS", "ID: " + c.getId() + ", Nom: " + c.getFirstname() + " " + c.getLastname());
        }

        for (Contact c : contacts) {
            databaseHelper.deleteContact(c.getId());
            Log.d("DB_TEST", "Contact supprimé !");

        }

        List<Contact> contactsEmpty = databaseHelper.getAllContacts();
        for (Contact c : contactsEmpty) {
            Log.d("DB_CONTACTS", "ID: " + c.getId() + ", Nom: " + c.getFirstname() + " " + c.getLastname());
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener( view -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
                }
        );
    }
}
