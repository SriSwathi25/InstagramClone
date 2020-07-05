package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.Model.Comment;
import com.example.instagram.Model.User;
import com.example.instagram.adapter.CommentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActvity extends AppCompatActivity {
    private CircleImageView profile_image;
    private CircleImageView author_image;
    private RecyclerView comments_recycler_view;
    private CommentAdapter commentAdapter;
    private List<Comment> commentsList;
    private ImageView back;

    private EditText addComment;
    private TextView post;

    String postId;
    String publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_actvity);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        publisher = intent.getStringExtra("publisher");
        profile_image = findViewById(R.id.profile_image);
        author_image = findViewById(R.id.author_image);
        comments_recycler_view  = findViewById(R.id.comments_recycler_view);
        addComment = findViewById(R.id.comment_added);
        post = findViewById(R.id.post_comment);
        back=  findViewById(R.id.back);



        upload_Image("user");
        upload_Image("publisher");

        comments_recycler_view = findViewById(R.id.comments_recycler_view);
        commentsList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getApplicationContext(),commentsList,postId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        comments_recycler_view.setLayoutManager(linearLayoutManager);
        comments_recycler_view.setAdapter(commentAdapter);

        readComments();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommentActvity.this,Home.class));
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString())){
                    Toast.makeText(CommentActvity.this, "No Comment Added", Toast.LENGTH_SHORT).show();
                }
                else{
                    postComment();
                }
            }
        });

    }

    private void readComments() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentsList.add(comment);

                }
                System.out.println(commentsList);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postComment() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String commentId = ref.push().getKey();
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("commentId",commentId);
        hm.put("comment",addComment.getText().toString());
        hm.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child(commentId).setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    addComment.setText("");
                    Toast.makeText(CommentActvity.this, "Comment Added", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActvity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void upload_Image(String person) {
        if (person.equals("publisher")) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(publisher).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user.getImageURL().equals("default")) {
                        Picasso.get().load(R.mipmap.ic_launcher).into(author_image);
                    } else {
                        Picasso.get().load(user.getImageURL()).into(author_image);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user.getImageURL().equals("default")) {
                        Picasso.get().load(R.mipmap.ic_launcher).into(profile_image);
                    } else {
                        Picasso.get().load(user.getImageURL()).into(profile_image);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        }
    }
}