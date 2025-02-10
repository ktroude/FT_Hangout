package dev.ktroude.ft_hangout.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import dev.ktroude.ft_hangout.database.DatabaseHelper;
import dev.ktroude.ft_hangout.models.Contact;
import dev.ktroude.ft_hangout.models.Message;

public class SmsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        if (pdus != null) {
            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = sms.getOriginatingAddress();
                assert sender != null;
                sender = sender.replaceFirst("^\\+33", "0");
                String text = sms.getMessageBody();
                long currentTime = System.currentTimeMillis();

                Contact contact = dbHelper.getContactByNumber(sender);

                if (contact == null) {
                    contact = new Contact(0, sender, "", "", "", sender, "");
                    Integer contactId = dbHelper.addContact(contact);
                    contact.setId(contactId);
                    Intent broadcastIntentNewSms = new Intent("dev.ktroude.ft_hangout.NEW_UNKNOWN_SMS_RECEIVED");
                    context.sendBroadcast(broadcastIntentNewSms);
                }

                Message message = new Message(0, contact.getId(), text, currentTime, false);
                dbHelper.addMessage(message);

                Intent broadcastIntentNewSms = new Intent("dev.ktroude.ft_hangout.NEW_SMS_RECEIVED");
                broadcastIntentNewSms.putExtra("contact_id", contact.getId());
                context.sendBroadcast(broadcastIntentNewSms);
            }
        }
        dbHelper.close();
    }
}
