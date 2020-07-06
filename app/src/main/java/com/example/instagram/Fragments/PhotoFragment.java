package com.example.instagram.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.example.instagram.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends Fragment {
    private RecyclerView photo_recycler_view;
    private PostAdapter photoAdapter;
    private List<Post> postsList;
    String postId;
    String mine;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        postId = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("postId","none");

        photo_recycler_view = view.findViewById(R.id.photo_recycler_view);
        postsList = new ArrayList<>();
        photoAdapter = new PostAdapter(getContext(),postsList);
        photo_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        readPhotos();
        photo_recycler_view.setAdapter(photoAdapter);
        return view;
    }

    private void readPhotos() {
        postsList.clear();
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Post post = snapshot.getValue(Post.class);
                        postsList.add(post);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
}