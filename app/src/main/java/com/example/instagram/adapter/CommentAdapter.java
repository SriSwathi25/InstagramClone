package com.example.instagram.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Home;
import com.example.instagram.Model.Comment;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> commentsList;
    private String postId;
    public CommentAdapter(Context mContext, List<Comment> commentsList, String postId){
        this.mContext = mContext;
        this.commentsList = commentsList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, int position) {
        final Comment comment = commentsList.get(position);
        holder.comment.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User publisher = snapshot.getValue(User.class);
                if(publisher.getImageURL().equals("default")){
                    Picasso.get().load(R.mipmap.ic_launcher).into(holder.publisher_image);

                }else {
                    Picasso.get().load(publisher.getImageURL()).into(holder.publisher_image);
                }
                holder.publisher_name.setText(publisher.getUsername().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.comment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(comment.getPublisher().equals(FirebaseAuth.getInstance().getUid())){
                    AlertDialog dialog = new AlertDialog.Builder(mContext).create();
                    dialog.setTitle("Delete this comment?");
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                             FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                                     .child(comment.getCommentId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful()){
                                         Toast.makeText(mContext, "Comment deleted.", Toast.LENGTH_SHORT).show();
                                         dialog.dismiss();
                                     }
                                 }
                             });

                        }
                    });
                    dialog.show();


            }
                return true;
        }});

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Home.class);
                    intent.putExtra("publisherId",comment.getPublisher());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
            holder.publisher_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Home.class);
                    intent.putExtra("publisherId",comment.getPublisher());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });


    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView publisher_image;
        public TextView publisher_name;
        public TextView comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            publisher_image = itemView.findViewById(R.id.publisher_image);
            publisher_name = itemView.findViewById(R.id.publisher_name);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
