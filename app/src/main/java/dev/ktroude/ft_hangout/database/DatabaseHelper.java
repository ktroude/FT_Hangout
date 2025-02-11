package dev.ktroude.ft_hangout.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.ktroude.ft_hangout.models.Contact;
import dev.ktroude.ft_hangout.models.Message;

/**
 * DatabaseHelper is a SQLite database helper class for managing the storage of contacts and messages.
 * It provides methods to create, update, delete, and retrieve contacts and messages.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 2;

    /**
     * Constructor for DatabaseHelper.
     *
     * @param context The context of the application.
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the database schema when the database is first initialized.
     *
     * @param db The database instance.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE contacts (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "firstname TEXT NOT NULL, " +
                        "lastname TEXT, " +
                        "email TEXT, " +
                        "address TEXT, " +
                        "telNumber TEXT NOT NULL, " +
                        "picture TEXT)";
        db.execSQL(createTable);

        String createMessagesTable =
                "CREATE TABLE messages (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "contactId INTEGER NOT NULL, " +
                        "msg TEXT NOT NULL, " +
                        "date INTEGER NOT NULL, " +
                        "isSend INTEGER NOT NULL, " +
                        "FOREIGN KEY(contactId) REFERENCES contacts(id))";
        db.execSQL(createMessagesTable);
    }

    /**
     * Handles database upgrades by dropping and recreating the tables.
     *
     * @param db         The database instance.
     * @param oldVersion The previous database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    /**
     * Adds a new contact to the database.
     *
     * @param contact The contact object to be added.
     * @return The ID of the newly added contact or 0 if the insertion failed.
     */
    public Integer addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("firstname", contact.getFirstname());
        values.put("lastname", contact.getLastname());
        values.put("email", contact.getEmail());
        values.put("address", contact.getAddress());
        values.put("telNumber", contact.getTelNumber());
        values.put("picture", contact.getPicture());

        long contactId = db.insert("contacts", null, values);
        db.close();
        return (contactId != -1) ? (int) contactId : 0;
    }

    /**
     * Retrieves a contact by its ID.
     *
     * @param id The ID of the contact.
     * @return The corresponding Contact object or null if not found.
     */
    public Contact getContactById(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contact contact = null;

        String query = "SELECT * FROM contacts WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            contact = new Contact(
                    cursor.getInt(0),    // id
                    cursor.getString(1), // firstname
                    cursor.getString(2), // lastname
                    cursor.getString(3), // email
                    cursor.getString(4), // address
                    cursor.getString(5), // telNumber
                    cursor.getString(6)  // picture (Base64)
            );
        }

        cursor.close();
        db.close();
        return contact;
    }

    /**
     * Retrieves a contact by their phone number.
     *
     * @param telNumber The phone number of the contact.
     * @return The corresponding Contact object or null if not found.
     */
    public Contact getContactByNumber(String telNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contact contact = null;

        String query = "SELECT * FROM contacts WHERE telNumber = ?";
        Cursor cursor = db.rawQuery(query, new String[]{telNumber});

        if (cursor.moveToFirst()) {
            contact = new Contact(
                    cursor.getInt(0),    // id
                    cursor.getString(1), // firstname
                    cursor.getString(2), // lastname
                    cursor.getString(3), // email
                    cursor.getString(4), // address
                    cursor.getString(5), // telNumber
                    cursor.getString(6)  // picture (Base64)
            );
        }
        cursor.close();
        db.close();
        return contact;
    }


    /**
     * Deletes a contact by ID.
     *
     * @param id The ID of the contact to be deleted.
     */
    public void deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contacts", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Updates an existing contact in the database.
     *
     * @param contact The updated contact object.
     */
    public void updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("firstname", contact.getFirstname());
        values.put("lastname", contact.getLastname());
        values.put("email", contact.getEmail());
        values.put("address", contact.getAddress());
        values.put("telNumber", contact.getTelNumber());
        values.put("picture", contact.getPicture());

        db.update("contacts", values, "id = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    /**
     * Retrieves all contacts from the database.
     *
     * @return A list of all contacts.
     */
    public List<Contact> getAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Contact> contactList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM contacts", null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                        cursor.getInt(0),  // id
                        cursor.getString(1), // firstname
                        cursor.getString(2), // lastname
                        cursor.getString(3), // email
                        cursor.getString(4), // address
                        cursor.getString(5), // telNumber
                        cursor.getString(6)  // picture (Base64)
                );
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    /**
     * Retrieves all messages for a specific contact.
     *
     * @param contactId The ID of the contact.
     * @return A list of messages associated with the contact.
     */
    public List<Message> getAllMessageFromContact(Integer contactId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Message> messageList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM messages WHERE contactId = ?", new String[]{String.valueOf(contactId)});
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(0),       // id
                        cursor.getInt(1),       // contactId
                        cursor.getString(2),    // msg
                        cursor.getLong(3),      // date
                        cursor.getInt(4) == 1   // isSend
                );
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messageList;
    }

    /**
     * Adds a new message to the database.
     *
     * @param message The message object to be stored.
     */
    public void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("contactId", message.getContactId());
        values.put("msg", message.getMsg());
        values.put("date", message.getDate());
        values.put("isSend", message.isSend());

        db.insert("messages", null, values);
        db.close();
    }
}

