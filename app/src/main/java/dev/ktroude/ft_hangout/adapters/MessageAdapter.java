package dev.ktroude.ft_hangout.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.ktroude.ft_hangout.R;
import dev.ktroude.ft_hangout.models.Message;

/**
 * MessageAdapter is a RecyclerView adapter responsible for displaying a list of messages
 * in a chat-like format. It efficiently binds message data to the corresponding views
 * and differentiates between sent and received messages with distinct alignments and styles.
 *
 * Features:
 * - Displays messages with timestamps in a structured conversation format.
 * - Uses the ViewHolder pattern to optimize performance by recycling views.
 * - Differentiates between sent and received messages:
 *   - Sent messages are aligned to the right with a distinct background color.
 *   - Received messages are aligned to the left with a different background.
 *
 * @author Your Name
 * @version 1.0
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messageList;

    /**
     * Constructor for MessageAdapter.
     *
     * @param messages The list of messages to be displayed in the RecyclerView.
     */
    public MessageAdapter(List<Message> messages) {
        this.messageList = messages;
    }

    /**
     * Inflates the layout for individual message items and creates a new ViewHolder.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The type of view (not used in this case).
     * @return A new MessageViewHolder containing the inflated layout.
     */
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    /**
     * Binds data from a Message object to the ViewHolder at the given position.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the message in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.textViewMessage.setText(message.getMsg());

        // Formatting the date into a readable format (dd/MM/yyyy HH:mm)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(message.getDate()));
        holder.textViewDate.setText(formattedDate);

        // Adjusting layout parameters dynamically based on whether the message is sent or received
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.cardView.getLayoutParams();

        if (message.isSend()) {
            // Align sent messages to the right
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.startToStart = ConstraintLayout.LayoutParams.UNSET;
            holder.textViewMessage.setBackgroundResource(R.drawable.bg_message_sent);
            holder.textViewMessage.setTextColor(Color.WHITE);
        } else {
            // Align received messages to the left
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.textViewMessage.setBackgroundResource(R.drawable.bg_message_received);
            holder.textViewMessage.setTextColor(Color.BLACK);
        }

        holder.cardView.setLayoutParams(params);
    }

    /**
     * Returns the total number of messages in the list.
     *
     * @return The number of messages.
     */
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /**
     * ViewHolder class that holds references to the views for each message item.
     * This helps optimize performance by reducing redundant view lookups.
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewDate;
        CardView cardView;

        /**
         * Constructor for MessageViewHolder.
         *
         * @param itemView The view representing a single message item.
         */
        public MessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

