package com.example.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

        private Context mContext;
        private List<User> mUsers;
        private boolean isFragment;

        private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers,boolean isFragment){
            this.mContext = mContext;
            this.mUsers = mUsers;
            this.isFragment = isFragment;
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        holder.follow_btn.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.name.setText(user.getName());

        Picasso.get().load(user.getImageURL()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);

        isFollowed(user.getUid(), holder.follow_btn);
        if(user.getUid().equals(firebaseUser.getUid())){
            holder.follow_btn.setVisibility(View.GONE);
        }

        holder.follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.follow_btn.getText().toString().equals("Follow")){
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getUid().toString()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid().toString()).child("followers").child(firebaseUser.getUid()).setValue(true);

                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getUid().toString()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid().toString()).child("followers").child(firebaseUser.getUid()).removeValue();


                }
            }
        });


            



    }

    private void isFollowed(final String uid, final Button follow_btn) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists()){
                    follow_btn.setText("Following");
                }
                else{
                    follow_btn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageView;
        public TextView username;
        public TextView name;
        public Button follow_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.fullname);
            follow_btn = itemView.findViewById(R.id.follow_btn);
        }


    }
}
