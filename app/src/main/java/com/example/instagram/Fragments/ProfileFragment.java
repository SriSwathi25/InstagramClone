package com.example.instagram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.EditProfile;
import com.example.instagram.Home;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.example.instagram.Options;
import com.example.instagram.R;
import com.example.instagram.adapter.PhotoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView fullname;
    private TextView username;
    private TextView no_of_posts;
    private TextView no_of_followers;
    private TextView no_of_following;
    private TextView bio;
    private ImageView options;

    private CircleImageView profile_image;
    private Button editProfile;

    private ImageButton back;
    private ImageButton posts_grid;
    private ImageButton saved_grid;
    private RecyclerView posts;
    private RecyclerView saved_posts;
    private FirebaseUser firebaseUser;
        private String profileId;
    String data;

    private RecyclerView photos_recycler_view;
    private PhotoAdapter photoAdapter;
    private List<Post> photoList;

    private RecyclerView saved_photos_recycler_view;
    private PhotoAdapter savedPhotoAdapter;
    private List<Post> savedPhotoList;

    String mine;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences pref  = getActivity().getSharedPreferences("PROFILE", Context.MODE_PRIVATE);
        data = pref.getString("profileId","none");
        mine = pref.getString("mine","none");
        System.out.println(data);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        editProfile = view.findViewById(R.id.edit_profile_btn);
        options = view.findViewById(R.id.profile_options);

        fullname = view.findViewById(R.id.profile_fullname);
        username = view.findViewById(R.id.profile_username);
        bio = view.findViewById(R.id.profile_bio);
        no_of_posts = view.findViewById(R.id.no_of_posts);
        no_of_followers = view.findViewById(R.id.no_of_followers);
        no_of_following = view.findViewById(R.id.no_of_following);
        back = view.findViewById(R.id.back);
        posts_grid = view.findViewById(R.id.profile_posts);
        saved_grid = view.findViewById(R.id.profile_saved_posts);
        profile_image = view.findViewById(R.id.profile_image);

        posts = view.findViewById(R.id.profile_posts_recycler_view);

        saved_posts = view.findViewById(R.id.profile_saved_posts_recycler_view);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Home.class));
            }
        });
        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoList,getContext());
        photos_recycler_view = view.findViewById(R.id.profile_posts_recycler_view);
        photos_recycler_view.setLayoutManager(new GridLayoutManager(getContext(),3));
        photos_recycler_view.setAdapter(photoAdapter);

        saved_photos_recycler_view = view.findViewById(R.id.profile_saved_posts_recycler_view);
        savedPhotoList = new ArrayList<>();
        savedPhotoAdapter = new PhotoAdapter(savedPhotoList,getContext());
        saved_photos_recycler_view.setLayoutManager(new GridLayoutManager(getContext(),3));
        saved_photos_recycler_view.setAdapter(savedPhotoAdapter);

        readPhotos();
        readSavedPhotos();





        if(data.equals("none") || mine.equals("true")){
            profileId = firebaseUser.getUid();

            editProfile.setText("Edit Profile");
        }
        else{
            profileId = data;
            checkFollowOrNot();
        }

        readUserInfo();
        readFollowersAndFollowingCount();
        readPostsCount();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editProfile.getText().toString().equals("Edit Profile")){
                    Intent intent = new Intent(getContext(), EditProfile.class);
                    startActivity(intent);

                }
                else if(editProfile.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileId).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(firebaseUser.getUid()).setValue(true);

                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileId).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        posts_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photos_recycler_view.setVisibility(View.VISIBLE);
                saved_photos_recycler_view.setVisibility(View.GONE);

            }
        });
        saved_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photos_recycler_view.setVisibility(View.GONE);
                saved_photos_recycler_view.setVisibility(View.VISIBLE);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Options.class));
            }
        });


        return view;
    }

    private void readSavedPhotos() {
        savedPhotoList.clear();
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                final Post post = dataSnapshot.getValue(Post.class);
                FirebaseDatabase.getInstance().getReference().child("Saves").child(profileId).child(post.getPostId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                if(snapshot1.exists()){
                                    savedPhotoList.add(post);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
                savedPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photoList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)){
                        photoList.add(post);
                    }
                }
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowOrNot() {
        if(profileId.equals(firebaseUser.getUid())){
            editProfile.setText("Edit Profile");
        }
        else{
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    editProfile.setText("Following");
                }
                else{
                    editProfile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }

    }


    private void readUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageURL().equals("default")){
                    Picasso.get().load(R.mipmap.ic_launcher).into(profile_image);
                }
                else{
                    Picasso.get().load(user.getImageURL()).into(profile_image);
                }
                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readFollowersAndFollowingCount(){
        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                no_of_followers.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                no_of_following.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPostsCount(){
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        count++;
                    }
                }
                no_of_posts.setText(count+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}