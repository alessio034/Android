package com.example.songo.livechat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserList;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar)findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mUserList = (RecyclerView)findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();
        startListening();

    }
    //metodo para obtener los datos en el recicler view
    public void startListening(){
        //variable que jalara los datos de la base de datos
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        //creamos un Firebaserecicler Options para guardar lo obtenido de la consulta
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();
        //cramos un adaptador que llevara las consulta anterior
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                //mostramos la actividad todos los usuarios con el layout de cada usuario
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);
                return new UserViewHolder(view);
            }

            //muestra los datos obtenidos por la consulta
            @Override
            protected void onBindViewHolder(UserViewHolder userViewHolder, int position, Users users) {
                // Bind the Chat object to the ChatHolder
                userViewHolder.setName(users.getName());
                userViewHolder.setStatus(users.getStatus());
                userViewHolder.setImage(users.getThumb_image());

                final String user_id = getRef(position).getKey();

                //abrimos los chats
                userViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);

                    }
                });
            }

        };
        //llamamos el adaptador y lo ponemos a escuchar
        mUserList.setAdapter(adapter);
        adapter.startListening();
    }
    //creamos un metodo que trabajara usuarios por items y mostrara informacion
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        //muestra el loyaut
        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        //muestra los nombres de las personas
        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        //muestra los estados de las personas
        public void setStatus(String status){
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }
        //muestra las imagenes de perfil de las personas
        public void setImage(String thumbImage){
            CircleImageView userThumbView = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumbImage).placeholder(R.mipmap.defaultim).into(userThumbView);
        }
    }


}


