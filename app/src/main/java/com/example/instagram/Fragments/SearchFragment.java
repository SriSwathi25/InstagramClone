package com.example.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.example.instagram.adapter.TagAdapter;
import com.example.instagram.adapter.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SocialAutoCompleteTextView searchBar;
    private List<User> mUsers;
    private UserAdapter mUserAdapter;

    private RecyclerView tagRecyclerView;
    private List<String> mTags;
    private List<String> mTagCount;
    private TagAdapter tagAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        mUserAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(mUserAdapter);

        searchBar = view.findViewById(R.id.search_bar);

        tagRecyclerView = view.findViewById(R.id.recyclerview_hashtags);
        tagRecyclerView.setHasFixedSize(true);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTags = new ArrayList<>();
        mTagCount = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(), mTags, mTagCount);
        tagRecyclerView.setAdapter(tagAdapter);




        readUsers();
        readTags();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        searchBar = view.findViewById(R.id.search_bar);
        return view;



    }



    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(searchBar.getText().toString())) {
                    mUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    mUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readTags() {
        FirebaseDatabase.getInstance().getReference().child("HashTags").
        addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mTagCount.clear();
                mTags.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    mTags.add(dataSnapshot.getKey());
                    mTagCount.add(dataSnapshot.getChildrenCount()+"");
                }
                System.out.println(mTags);
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void filter(String s){
        List<String> searchTags = new ArrayList<>();
        List<String> searchTagCount = new ArrayList<>();
        for(String str: mTags){
            if(str.contains(s)){
                searchTags.add(str);
                searchTagCount.add(mTagCount.get(mTags.indexOf(str)));
            }
        }
        tagAdapter.filter(searchTags, searchTagCount);
    }



    }
