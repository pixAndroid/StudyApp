package com.android.appdevelopment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.appdevelopment.Adapter.PostsAdapter;
import com.android.appdevelopment.Model.Posts;

import java.util.ArrayList;
import java.util.List;

public class PostsActivity extends AppCompatActivity {
    List<Posts> postsList = new ArrayList<>();
    RecyclerView mRecyclerViewPosts;
    PostsAdapter mPostsAdapter;
    int lessons_id;
    private String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        mRecyclerViewPosts = findViewById(R.id.rv_posts);

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if( bundle != null){
            mUID = bundle.getString("uid");
            lessons_id = bundle.getInt("lessons_id");
            postsList = (List<Posts>) bundle.getSerializable("posts");
            getSupportActionBar().setTitle(bundle.getString("action_bar_title"));
        }

        mRecyclerViewPosts.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewPosts.setLayoutManager(gridLayoutManager);
        mPostsAdapter = new PostsAdapter(postsList, getApplicationContext(),mUID, lessons_id);
        mRecyclerViewPosts.setAdapter(mPostsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPostsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        getStartActivity();
        super.onBackPressed();
    }

    private void getStartActivity() {
        //        if (!isTaskRoot()){
        Intent intent = new Intent(PostsActivity.this, GetStartActivity.class);
        //BackStack CLear Top Activity
        Bundle bundle = new Bundle();
        bundle.putString("uid", mUID);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        }
    }
}
