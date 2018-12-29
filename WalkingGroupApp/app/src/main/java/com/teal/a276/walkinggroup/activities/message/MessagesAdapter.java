package com.teal.a276.walkinggroup.activities.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teal.a276.walkinggroup.model.dataobjects.Message;

import java.util.List;

/**
 * Adapter for a RecyclerView.
 * General idea for the adapter from here: https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6
 */
class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private final List<Message> messages;

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        ViewHolder(TextView view) {
            super(view);
            textView = view;
        }
    }

    MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MessagesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {
        holder.textView.setText(messages.get(position).getText());
    }

    void removeAt(int position) {
        validateIndex(position);
        messages.remove(position);
        notifyItemRemoved(position);
    }

    void addMessageAt(Message message, int position) {
        validateIndex(position);
        messages.add(position, message);
        notifyItemInserted(position);
    }

    void addAll(List<Message> messages) {
       this.messages.addAll(messages);
       notifyItemRangeInserted(this.messages.size(), messages.size());
    }

    void clear() {
        int numberOfMessages = messages.size();
        this.messages.clear();
        notifyItemRangeRemoved(0, numberOfMessages);
    }

    private void validateIndex(int index) throws IllegalArgumentException {
        if (index >= messages.size() || index < 0) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
