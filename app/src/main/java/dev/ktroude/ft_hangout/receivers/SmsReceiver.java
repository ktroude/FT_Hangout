package dev.ktroude.ft_hangout.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;
import dev.ktroude.ft_hangout.models.Message;


/**
 * SmsReceiver is a BroadcastReceiver that listens for incoming SMS messages.
 * It extracts message data, checks if the sender is known, saves the message to the database,
 * and broadcasts an event to update the UI.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        processReceivedSms(context, intent, dbHelper);

        dbHelper.close();
    }

    /**
     * Extracts and processes SMS messages from the received intent.
     *
     * @param context  The application context.
     * @param intent   The intent containing the SMS data.
     * @param dbHelper Instance of DatabaseHelper for database operations.
     */
    private void processReceivedSms(Context context, Intent intent, DatabaseHelper dbHelper) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        if (pdus != null) {
            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = sms.getOriginatingAddress();

                if (sender == null) {
                    continue;
                }

                sender = sender.replaceFirst("^\\+33", "0");
                String text = sms.getMessageBody();
                long timestamp = System.currentTimeMillis();

                Contact contact = handleUnknownSender(context, sender, dbHelper);

                saveMessageAndBroadcast(context, contact, text, timestamp, dbHelper);
            }
        }
    }

    /**
     * Checks if the sender exists in the database. If not, creates a new contact.
     *
     * @param context  The application context.
     * @param sender   The phone number of the sender.
     * @param dbHelper Instance of DatabaseHelper for database operations.
     * @return The contact associated with the sender.
     */
    private Contact handleUnknownSender(Context context, String sender, DatabaseHelper dbHelper) {
        Contact contact = dbHelper.getContactByNumber(sender);

        if (contact == null) {
            contact = new Contact(0, sender, "", "", "", sender, "");
            Integer contactId = dbHelper.addContact(contact);
            contact.setId(contactId);

            Intent newContactIntent = new Intent("dev.ktroude.ft_hangout.NEW_UNKNOWN_SMS_RECEIVED");
            context.sendBroadcast(newContactIntent);
        }
        return contact;
    }

    /**
     * Saves the received message to the database and broadcasts an update event.
     *
     * @param context  The application context.
     * @param contact  The contact associated with the message.
     * @param text     The content of the SMS.
     * @param timestamp The timestamp of the message.
     * @param dbHelper Instance of DatabaseHelper for database operations.
     */
    private void saveMessageAndBroadcast(Context context, Contact contact, String text, long timestamp, DatabaseHelper dbHelper) {
        Message message = new Message(0, contact.getId(), text, timestamp, false);
        dbHelper.addMessage(message);

        // Broadcast an event to notify the UI that a new SMS has been received
        Intent smsReceivedIntent = new Intent("dev.ktroude.ft_hangout.NEW_SMS_RECEIVED");
        smsReceivedIntent.putExtra("contact_id", contact.getId());
        context.sendBroadcast(smsReceivedIntent);
    }
}
