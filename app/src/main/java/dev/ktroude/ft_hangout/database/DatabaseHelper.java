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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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


}
