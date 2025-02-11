package dev.ktroude.ft_hangout.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.activities.ContactDetailsActivity;
import dev.ktroude.ft_hangout.models.Contact;

/**
 * ContactAdapter is a RecyclerView adapter responsible for displaying a list of contacts
 * in a RecyclerView. It efficiently binds contact data to the corresponding views and
 * manages user interactions.

 * This adapter follows the ViewHolder pattern to improve performance by reusing views
 * instead of creating new ones each time an item is displayed.

 * Features:
 * - Displays contact names and phone numbers in a list format.
 * - Uses a ViewHolder to hold references to views for efficient recycling.
 * - Implements click handling to open ContactDetailsActivity when a contact is selected.
 *
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final List<Contact> contactList;

    /**
     * Constructor for ContactAdapter.
     *
     * @param contactList The list of contacts to be displayed.
     */
    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    /**
     * Creates a new ViewHolder instance for each item in the RecyclerView.
     *
     * @param parent   The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of ContactViewHolder.
     */
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    /**
     * Binds the data from a Contact object to the ViewHolder.
     *
     * @param holder   The ViewHolder that should be updated with the contact data.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.textViewName.setText(String.format("%s %s", contact.getFirstname(), contact.getLastname()));
        holder.textViewPhone.setText(contact.getTelNumber());

        // Handles click event for opening ContactDetailsActivity
        holder.itemView.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, ContactDetailsActivity.class);
            intent.putExtra("contact_id", contact.getId());
            context.startActivity(intent);
        });
    }

    /**
     * Returns the total number of items in the contact list.
     *
     * @return The number of contacts in the list.
     */
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    /**
     * ViewHolder class for Contact items.
     * Holds references to the views for each contact item.
     */
    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPhone;

        /**
         * Constructor for ContactViewHolder.
         *
         * @param itemView The view representing a single contact item.
         */
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
        }
    }
}

