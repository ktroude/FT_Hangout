package dev.ktroude.ft_hangout.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.adapters.MessageAdapter;
import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;
import dev.ktroude.ft_hangout.models.Message;

public class MessageActivity extends AppCompatActivity {

    private final DatabaseHelper dbHelper = new DatabaseHelper(this);
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private int contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);

        contactId = getIntent().getIntExtra("contact_id", -1);
        if (contactId == -1) {
            Toast.makeText(this, getString(R.string.id_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, 2);
        }

        messageList = dbHelper.getAllMessageFromContact(contactId);
        Contact contact = dbHelper.getContactById(contactId);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        TextView textViewContact = findViewById(R.id.textViewContact);
        textViewContact.setText(String.format("%s %s", contact.getFirstname(), contact.getLastname()));

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        buttonSend.setVisibility(View.INVISIBLE);

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    buttonSend.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonSend.setOnClickListener(view -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                editTextMessage.setText("");
            }
        });

        if (!messageList.isEmpty()) {
            recyclerView.post(() -> recyclerView.scrollToPosition(messageList.size() - 1));
        }
    }

    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedContactId = intent.getIntExtra("contact_id", -1);
            if (receivedContactId == contactId) {
                refreshDatabase();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("dev.ktroude.ft_hangout.NEW_SMS_RECEIVED");
        ContextCompat.registerReceiver(
                this,
                smsReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsReceiver);
    }


    private void sendMessage(String text) {

        long currentTime = System.currentTimeMillis();
        Message newMessage = new Message(0, contactId, text, currentTime, true);
        dbHelper.addMessage(newMessage);

        refreshDatabase();

        Contact contact = dbHelper.getContactById(contactId);
        String phoneNumber = contact.getTelNumber();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        } catch (Exception e) {
            Log.e("MessageActivity", e.toString());
        }
    }

    private void refreshDatabase(){
        messageList.clear();
        messageList.addAll(dbHelper.getAllMessageFromContact(contactId));

        messageAdapter.notifyDataSetChanged();

        recyclerView.scrollToPosition(messageList.size() - 1);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.dbHelper.close();
    }
}
