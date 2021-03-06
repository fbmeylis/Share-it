package com.fbmeylis.shareit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    Bitmap selectedImage;
    ImageView imageView;
    EditText commentText;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri imageuri;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imageView = findViewById(R.id.selectimage);
        commentText = findViewById(R.id.commenttext);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public void selectImage(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            Intent intenttoGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intenttoGallery,2);
        }
    }

    public void uploadImage(View view) {
        ProgressDialog progressDialog= ProgressDialog.show(UploadActivity.this, "Upload", "Loading. Please wait...", true);

        if (imageuri != null){
            UUID uuid = UUID.randomUUID();
            String imageid ="images/"+ uuid + ".jpg";
            storageReference.child(imageid).putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UploadActivity.this,"Succes",Toast.LENGTH_LONG).show();

                    StorageReference reference = FirebaseStorage.getInstance().getReference(imageid);
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadedurl = uri.toString();

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String usermail = firebaseUser.getEmail();
                            String comment = commentText.getText().toString();

                            HashMap<String,Object> postData = new HashMap<>();
                            postData.put("usermail",usermail);
                            postData.put("downloadurl",downloadedurl);
                            postData.put("comment",comment);

                            postData.put("date", FieldValue.serverTimestamp());

                            firestore.collection("posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Intent intent = new Intent(UploadActivity.this,FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Toast.makeText(UploadActivity.this,"Uploaded Succesfully",Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                               Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
      if (requestCode ==1){
          if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
              Intent intenttoGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
              startActivityForResult(intenttoGallery,2);
          }
      }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
       if (requestCode ==2 && resultCode ==RESULT_OK && data != null){
           imageuri = data.getData();
           try {
               if (Build.VERSION.SDK_INT >= 28){
                   ByteArrayOutputStream out = new ByteArrayOutputStream();
                   ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageuri);
                   selectedImage = ImageDecoder.decodeBitmap(source);
                   selectedImage.compress(Bitmap.CompressFormat.JPEG, 30, out);
                   Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                   imageView.setImageBitmap(decoded);
               }else{
               selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageuri);
               imageView.setImageBitmap(selectedImage);}
           } catch (IOException e) {
               e.printStackTrace();
           }


       }
        super.onActivityResult(requestCode, resultCode, data);
    }
}