package com.example.songo.livechat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextView mProfileName, mDProfileStatus, mProfileFriendsCount;
    private ImageView mProfileImage;
    private Button mProfileSendReqBtn, mProfileDeclineReqBtn;

    private DatabaseReference mUserDatabase;

    private ProgressDialog mProgress;

    private String mCurrent_state;

    private DatabaseReference mFriendsReqDatabase;

    private DatabaseReference mFriendDatabase;

    private DatabaseReference mNotificationDatabase;

    private FirebaseUser mCurrent_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friend");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileName = (TextView)findViewById(R.id.profile_displayName);
        mProfileImage = (ImageView)findViewById(R.id.profile_image);
        mDProfileStatus = (TextView)findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView)findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button)findViewById(R.id.profile_send_req_btn);
        mProfileDeclineReqBtn = (Button)findViewById(R.id.profile_decline_req_btn);

        mCurrent_state = "not_friends";

        //dialogo que se muestra mientras carga los datos
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading User Data");
        mProgress.setMessage("Please wait while we load the user data.");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                //esconde el boton decline cuando inicie la actividad
                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                mProfileDeclineReqBtn.setEnabled(false);

                //mostramos el nombre
                mProfileName.setText(display_name);
                //mostramos el estado
                mDProfileStatus.setText(status);
                //mostramos la imagen
                Picasso.get().load(image).placeholder(R.mipmap.defaultim).into(mProfileImage);


                //-------------- FRIEND LIST / REQUEST FEATURE -----------
                mFriendsReqDatabase.child(mCurrent_user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type")
                                    .getValue().toString();
                            //verifica si tiene una solicitud recibida
                            if (req_type.equals("received")){

                                //comprueba el estado
                                mCurrent_state = "req_received";
                                //cambia el texto del boton
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                //muestra el moton decline
                                mProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineReqBtn.setEnabled(true);
                                //verifica si tiene una solicitud enviada
                            }else if (req_type.equals("sent")){
                                //comprueba el estado
                                mCurrent_state = "req_sent";
                                //cambia el texto del boton
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                //esconde el boton decline
                                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineReqBtn.setEnabled(false);
                            }
                            //cierra dialogo cuando datos esten cargados
                            mProgress.dismiss();
                        }else {
                            //comprueba si ya son amigos
                            mFriendDatabase.child(mCurrent_user.getUid()).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)){
                                        mCurrent_state = "friends";
                                        //cambia el texto del boton
                                        mProfileSendReqBtn.setText("Unfriend this person");
                                        //esconde el boton decline
                                        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineReqBtn.setEnabled(false);
                                    }
                                    //cierra dialogo cuando datos esten cargados
                                    mProgress.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);
///------------------------------------ NOT FRIENDS STATE -----------------------------
                if (mCurrent_state.equals("not_friends")){

                    mFriendsReqDatabase.child(mCurrent_user.getUid()).child(user_id).
                            child("request_type").setValue("sent").addOnCompleteListener
                            (new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //envia la solicitud de amistad
                                mFriendsReqDatabase.child(user_id).child(mCurrent_user.getUid())
                                        .child("request_type").setValue("received")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //datos que mandaremos en la notificacion
                                        HashMap<String, String>notificationData = new HashMap<>();
                                        notificationData.put("from",mCurrent_user.getUid());
                                        notificationData.put("type", "request");

                                        mNotificationDatabase.child(user_id).push().setValue(notificationData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //comprueba el estado
                                                        mCurrent_state = "req_sent";
                                                        //cambia el texto del boton
                                                        mProfileSendReqBtn.setText("Cancel Friend Request");

                                                        //esconde el boton decline
                                                        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                        mProfileDeclineReqBtn.setEnabled(false);
                                                    }
                                                });



                                        /*Toast.makeText(ProfileActivity.this, "Request Sent Successfully",
                                                Toast.LENGTH_LONG).show();*/
                                    }
                                });
                            }else {
                                Toast.makeText(ProfileActivity.this, "Failed Sending Request",
                                        Toast.LENGTH_LONG).show();
                            }
                            //cambia el estado del boton a enable
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }
                //----------------- CANCEL REQUEST STATE
                if (mCurrent_state.equals("req_sent")){
                    mFriendsReqDatabase.child(mCurrent_user.getUid()).child(user_id).
                            removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendsReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //cambia el estado del boton a enable
                                            mProfileSendReqBtn.setEnabled(true);
                                            //comprueba el estado
                                            mCurrent_state = "not_friends";
                                            //cambia el texto del boton
                                            mProfileSendReqBtn.setText("Send Friend Request");
                                            //esconde el boton decline
                                            mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                            mProfileDeclineReqBtn.setEnabled(false);
                                        }
                                    });

                        }
                    });

                }
                //------------- REQ FRIEND STATE ----------------------
                if (mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).setValue(currentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mFriendsReqDatabase.child(mCurrent_user.getUid()).child(user_id).
                                                            removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            mFriendsReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            //cambia el estado del boton a enable
                                                                            mProfileSendReqBtn.setEnabled(true);
                                                                            //comprueba el estado
                                                                            mCurrent_state = "friends";
                                                                            //cambia el texto del boton
                                                                            mProfileSendReqBtn.setText("Unfriend this person");

                                                                            //esconde el boton decline
                                                                            mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                                            mProfileDeclineReqBtn.setEnabled(false);
                                                                        }
                                                                    });

                                                        }
                                                    });
                                                }
                                            });
                                }
                            });
                }
                //------------------------ Unfriend-----------------------
                if (mCurrent_state.equals("friends")){
                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).
                            removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //cambia el estado del boton a enable
                                            mProfileSendReqBtn.setEnabled(true);
                                            //comprueba el estado
                                            mCurrent_state = "not_friends";
                                            //cambia el texto del boton
                                            mProfileSendReqBtn.setText("Send Friend Request");
                                        }
                                    });

                        }
                    });

                }
            }
        });
    }
}
