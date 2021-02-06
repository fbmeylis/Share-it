package com.fbmeylis.shareit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.fbmeylis.shareit.R.menu.shareit_options_menu;

public class FeedActivity extends AppCompatActivity {

    FeedRecycleAdapter feedRecycleAdapter;
    FeedViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(shareit_options_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post){
            Intent intent = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.logout){
            viewModel.signOut();
            Intent intent  = new Intent(FeedActivity.this,SignUpActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(FeedViewModel.class);

        viewModel.init(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecycleAdapter = new FeedRecycleAdapter();
        recyclerView.setAdapter(feedRecycleAdapter);

        viewModel.item.observe(this, feedItem -> {
            Log.d("viridis", "view: "+feedItem);
            feedRecycleAdapter.addItem(feedItem);
        });

        viewModel.error.observe(this, message -> {
            Toast.makeText(FeedActivity.this, "fb: "+message, Toast.LENGTH_LONG).show();
        });
    }
}