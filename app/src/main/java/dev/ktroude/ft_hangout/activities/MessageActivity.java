package dev.ktroude.ft_hangout.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.adapters.MessageAdapter;
import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;
import dev.ktroude.ft_hangout.models.Message;
import dev.ktroude.ft_hangout.utils.PermissionAndResultHelper;

/**
 * MessageActivity manages the conversation screen, allowing users to send and receive messages.
 */
public class MessageActivity extends AppCompatActivity {

    private final DatabaseHelper dbHelper = new DatabaseHelper(this);
    private EditText editTextMessage;
    private TextView textViewContact;
    private ImageButton buttonSend;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private final List<Message> messageList = new ArrayList<>();
    private Contact contact;
    private int contactId;

    /**
     * Called when the activity is created. Initializes UI components and loads messages.
     * @param savedInstanceState The saved instance state from a previous session.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);

        requestPermissions();
        initContact();
        initViews();
        initRecyclerView();
        loadMessages();

        setViewsData();
        initButton();
        scrollToLastMessage();
    }

    /**
     * BroadcastReceiver that listens for incoming SMS messages and refreshes the message list.
     */
    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedContactId = intent.getIntExtra("contact_id", -1);
            if (receivedContactId == contactId) {
                loadMessages();
            }
        }
    };

    /**
     * Registers the SMS receiver when the activity is resumed.
     */
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

    /**
     * Unregisters the SMS receiver when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsReceiver);
    }

    /**
     * Initializes the contact by retrieving its ID from the intent and loading details from the database.
     */
    private void initContact() {
        contactId = getIntent().getIntExtra("contact_id", -1);
        if (contactId == -1) {
            Toast.makeText(this, getString(R.string.id_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        contact = dbHelper.getContactById(contactId);
    }

    /**
     * Initializes UI components.
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewMessages);
        textViewContact = findViewById(R.id.textViewContact);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
    }

    /**
     * Sets up UI elements with contact details and input behaviors.
     */
    private void setViewsData() {
        textViewContact.setText(String.format("%s %s", contact.getFirstname(), contact.getLastname()));
        buttonSend.setVisibility(View.INVISIBLE);

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSend.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Initializes the send button click behavior.
     */
    private void initButton() {
        buttonSend.setOnClickListener(view -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                editTextMessage.setText("");
                hideKeyboard();
            }
        });
    }


    /**
     * Scrolls the RecyclerView to display the last message.
     */
    private void scrollToLastMessage() {
        if (!messageList.isEmpty()) {
            recyclerView.post(() -> recyclerView.scrollToPosition(messageList.size() - 1));
        }
    }

    /**
     * Requests necessary permissions for sending and receiving SMS.
     */
    private void requestPermissions() {
        PermissionAndResultHelper.requestPermission(
                this,
                Manifest.permission.SEND_SMS,
                1,
                () -> Log.d("MessageActivity", "All permissions granted")
        );
        PermissionAndResultHelper.requestPermission(
                this,
                Manifest.permission.RECEIVE_SMS,
                1,
                () -> Log.d("MessageActivity", "All permissions granted")
        );
    }

    /**
     * Initializes the RecyclerView to display messages.
     */
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
    }

    /**
     * Sends a message by storing it in the database and sending it via SMS.
     * @param text The message content.
     */
    private void sendMessage(String text) {
        long currentTime = System.currentTimeMillis();
        Message newMessage = new Message(0, contactId, text, currentTime, true);
        dbHelper.addMessage(newMessage);
        loadMessages();

        String phoneNumber = contact.getTelNumber();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, text, null, null);
    }

    /**
     * Loads all messages from the database and refreshes the UI.
     */
    private void loadMessages() {
        messageList.clear();
        messageList.addAll(dbHelper.getAllMessageFromContact(contactId));
        messageAdapter.notifyDataSetChanged();
        scrollToLastMessage();
    }

    /**
     * Hides the keyboard after sending a message.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);
        }
    }


    /**
     * Closes the database when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.dbHelper.close();
    }
}
