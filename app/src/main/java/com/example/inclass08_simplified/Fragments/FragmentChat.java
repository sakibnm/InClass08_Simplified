package com.example.inclass08_simplified.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inclass08_simplified.Models.ChatRecord;
import com.example.inclass08_simplified.Models.Message;
import com.example.inclass08_simplified.Models.User;
import com.example.inclass08_simplified.R;
import com.example.inclass08_simplified.Tags;
import com.example.inclass08_simplified.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FragmentChat extends Fragment {

    private FirebaseFirestore db;
    private ChatRecord currentChat;
    private User currentLocalUser;
    private ArrayList<String> chatEmails;

    private ArrayList<User> chatUsers;
    private ArrayList<Message> messages;
    private TextView textViewChatUsers;
    private RecyclerView recyclerViewChat;
    private RecyclerView.LayoutManager recyclerViewChatLayoutManager;
    private RecyclerView.Adapter recyclerViewChatAdapter;
    private EditText editText_Chat;
    private ImageButton imageButtonSelectPhoto;
    private ImageButton imageButtonSend;

    public FragmentChat(User currentLocalUser, ArrayList<String> chatEmails){
        this.currentLocalUser = currentLocalUser;
        this.chatEmails = chatEmails;
    };

    public FragmentChat() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        textViewChatUsers = rootView.findViewById(R.id.textView_chatUserNames);
        editText_Chat = rootView.findViewById(R.id.editText_TextMessage);
        imageButtonSelectPhoto = rootView.findViewById(R.id.imageButton_SelectPhoto);
        imageButtonSend = rootView.findViewById(R.id.imageButton_Send);


        imageButtonSelectPhoto.setOnClickListener(this::onButtonSelectPhotoClicked);
        imageButtonSend.setOnClickListener(this::onButtonSendClicked);
//        Fetch the current messages for this chat......
        fetchCurrentMessagesForThisChat(chatEmails);
//        Fetch all the users in this chat.....
//        fetchUsersInThisChat(chatEmails);

        return rootView;
    }



    private void fetchCurrentMessagesForThisChat(ArrayList<String> chatEmails) {
        String chatRecordID = Utils.generateUniqueID(chatEmails);
        DocumentReference chatRef = db.collection("users")
                .document(currentLocalUser.getEmail())
                .collection("chats")
                .document(chatRecordID);
//fetch the chat record ID......
        chatRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentChat = documentSnapshot.toObject(ChatRecord.class);
//                        Now, fetch all the messages from that chat room.....
                        fetchCurrentMessages();
                    }
                });
    }

    private void fetchCurrentMessages() {
        CollectionReference messagesCollection = db.collection("chats")
                .document(currentChat.getDocumentReference())
                .collection("messages");

        messagesCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.e(Tags.TAG, "onEvent: "+ error.getMessage());
                }else{
                    for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                        messages.add(documentSnapshot.toObject(Message.class));
                    }
//                    recyclerViewChatAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void onButtonSendClicked(View view) {
        String text = editText_Chat.getText().toString().trim();
        if( text != ""){
            Message message = new Message(text);
            uploadMessageToFirebase(message);
        }else{
            editText_Chat.setError("Can't be empty!");
        }

    }

    private void uploadMessageToFirebase(Message message) {
        
    }

    private void onButtonSelectPhotoClicked(View view) {
        Toast.makeText(view.getContext(), "To be implemented!", Toast.LENGTH_SHORT).show();
    }
}