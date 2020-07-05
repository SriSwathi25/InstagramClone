package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    String profileId = FirebaseAuth.getInstance().getUid();
    private ImageButton back;
    private Button save;
    private Button edit_image;
    private CircleImageView profile_image;
    private EditText txt_username;
    private EditText txt_fullname;
    private EditText txt_bio;
    private String ImageUrl;
    private String fullName;
    private String username;
    private String bio;
    private String email;
    private String password;
    private String uid;
    private Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        back = findViewById(R.id.back);
        edit_image = findViewById(R.id.edit_image);
        profile_image = findViewById(R.id.profile_image);
        txt_username = findViewById(R.id.username);
        txt_fullname = findViewById(R.id.fullname);
        txt_bio = findViewById(R.id.bio);
        save = findViewById(R.id.save_btn);

    FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            User user  = snapshot.getValue(User.class);
            if(user.getImageURL().equals("default")){
                Picasso.get().load(R.mipmap.ic_launcher).into(profile_image);

            }
            else {
                Picasso.get().load(user.getImageURL()).into(profile_image);
            }
            ImageUrl = user.getImageURL();
            fullName = user.getName();
            username = user.getUsername();
            bio = user.getBio();
            email = user.getEmail();
            password = user.getPassword();
            uid = user.getUid();
            txt_username.setText(username);
            txt_fullname.setText(fullName);
            txt_bio.setText(bio);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(EditProfile.this);
            }
        });
        System.out.println(getFileExtension(Uri.parse("abcd.jpg")));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, Home.class));
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StorageReference ref =  FirebaseStorage.getInstance().getReference("ProfilePics").child(System.currentTimeMillis() + ".jpg");

                StorageTask uploadTask = ref.putFile(imageURI);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Uri> task) {
                                                                           Uri downloadUrl = task.getResult();
                                                                           ImageUrl = downloadUrl.toString();
                                                                           HashMap<String, Object> hm = new HashMap<>();
                                                                           username = txt_username.getText().toString();
                                                                           fullName = txt_fullname.getText().toString();
                                                                           bio = txt_bio.getText().toString();
                                                                           hm.put("email",email);
                                                                           hm.put("password",password);
                                                                           hm.put("username",username);
                                                                           hm.put("name",fullName);
                                                                           hm.put("bio",bio);
                                                                           Log.e("URL NOWWWWW", ImageUrl);
                                                                           hm.put("imageURL",ImageUrl);
                                                                           hm.put("uid",uid);
                                                                           FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).setValue(hm);
                                                                           Toast.makeText(EditProfile.this, "Profile Updated.", Toast.LENGTH_SHORT).show();
                                                                           startActivity(new Intent(EditProfile.this, Home.class));
                                                                       }
                                                                   });





            }
        });


    }
    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri ImageUri = result.getUri();
            imageURI = ImageUri;
            Picasso.get().load(ImageUri.toString()).into(profile_image);



        } else {
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfile.this, Home.class));
            finish();
        }
    }

}