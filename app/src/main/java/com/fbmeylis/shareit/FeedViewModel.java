package com.fbmeylis.shareit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Map;

public class FeedViewModel extends ViewModel {

    MutableLiveData<FeedItem> item = new MutableLiveData<>();
    MutableLiveData<String> error = new MutableLiveData<>();

    private FirebaseAuth firebaseAuth;

    void init(FirebaseAuth firebaseAuth, FirebaseFirestore firestore) {
        this.firebaseAuth = firebaseAuth;
        firestore
                .collection("posts")
                .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                FeedViewModel.this.error.postValue(error.getLocalizedMessage());
            }

            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                Map<String, Object> data = documentSnapshot.getData();

                String comment = (String) data.get("comment");
                String downloadurl = (String) data.get("downloadurl");
                String usermail = (String) data.get("usermail");

                FeedItem feedItem = new FeedItem(comment, downloadurl, usermail);
                item.setValue(feedItem);
            }
        });
    }

    void signOut() {
        firebaseAuth.signOut();
    }


}
