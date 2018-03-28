package com.ttp.lstchatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.ttp.lstchatapp.Holder.QBUsersHolder;
import com.ttp.lstchatapp.R;

import java.util.ArrayList;

/**
 * Created by 0047TiTANplateform_ on 2018-01-16.
 */

public class ChatMessageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBChatMessage> qbChatMessages;

    public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View view = convertView;
       if(convertView == null) {
           LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

           if(qbChatMessages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId())){
               view = inflater.inflate(R.layout.list_send_message, null);
               BubbleTextView bubbleTextView = view.findViewById(R.id.message_content);
               bubbleTextView.setText(qbChatMessages.get(position).getBody());
           } else {
               view = inflater.inflate(R.layout.list_recv_message, null);
               BubbleTextView bubbleTextView = view.findViewById(R.id.message_content);
               bubbleTextView.setText(qbChatMessages.get(position).getBody());
               TextView txtName = view.findViewById(R.id.message_user);
               txtName.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getFullName());
           }

       }
       return view;
    }
}
