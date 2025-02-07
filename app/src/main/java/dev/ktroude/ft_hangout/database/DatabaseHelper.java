package dev.ktroude.ft_hangout.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.ktroude.ft_hangout.models.Contact;
import dev.ktroude.ft_hangout.models.Message;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("firstname", contact.getFirstname());
        values.put("lastname", contact.getLastname());
        values.put("email", contact.getEmail());
        values.put("address", contact.getAddress());
        values.put("telNumber", contact.getTelNumber());
        values.put("picture", contact.getPicture());

        db.insert("contacts", null, values);
        db.close();
    }

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


    public void deleteContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();

        String contactId = String.valueOf(contact.getId());
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{contactId};

        db.delete("contacts", whereClause, whereArgs);
        db.close();
    }

    public void deleteContact(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();

        String contactId = String.valueOf(id);
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{contactId};

        db.delete("contacts", whereClause, whereArgs);
        db.close();
    }

    public void updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        String contactId = String.valueOf(contact.getId());
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{contactId};

        ContentValues values = new ContentValues();
        values.put("firstname", contact.getFirstname());
        values.put("lastname", contact.getLastname());
        values.put("email", contact.getEmail());
        values.put("address", contact.getAddress());
        values.put("telNumber", contact.getTelNumber());
        values.put("picture", contact.getPicture());

        db.update("contacts", values, whereClause, whereArgs);

        Contact updatedContact = getContactById(contact.getId());
        Log.d("db helper", updatedContact.toString());

        db.close();
    }

    public List<Contact> getAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Contact> contactList = new ArrayList<>();

        String query = "SELECT * FROM contacts";
        Cursor cursor = db.rawQuery(query, null);
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

    public List<Message> getAllMessageFromContact(Integer contactId){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Message> messageList = new ArrayList<>();

        String query = "SELECT * FROM messages WHERE contactId = " + contactId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(0),       // id
                        cursor.getInt(1),       // contactId
                        cursor.getString(2),    // msg
                        cursor.getLong(3),      // date
                        cursor.getInt(4) == 1    // isSend
                );
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messageList;
    }

    public Message getMessageById(Integer messageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Message message = null;

        String query = "SELECT * FROM contacts WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(messageId)});

        if (cursor.moveToFirst()) {
            message = new Message(
                    cursor.getInt(0),       // id
                    cursor.getInt(1),       // contactId
                    cursor.getString(2),    // msg
                    cursor.getLong(3),      // date
                    cursor.getInt(4) == 1    // isSend
            );
        }

        cursor.close();
        db.close();
        return message;
    }

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
