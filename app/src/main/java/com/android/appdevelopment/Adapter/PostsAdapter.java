package com.android.appdevelopment.Adapter;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.appdevelopment.Model.Posts;
import com.android.appdevelopment.ModelReadStatus.Lessons_ID;
import com.android.appdevelopment.ModelReadStatus.Posts_ID;
import com.android.appdevelopment.ModelReadStatus.Steps_ID;
import com.android.appdevelopment.R;
import com.android.appdevelopment.SharedPreferenceConfig;
import com.android.appdevelopment.StepDetailsActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Pair;

import java.io.Serializable;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyHolder> {
    List<Posts> postsList = new ArrayList<>();
    Context mContext;
    int mCurrentItemPosition, mNextItemPosition;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    List<Steps_ID> steps_idList = new ArrayList<>();
    List<Boolean> booleans = new ArrayList<>();
    private String mUID;
    int lessons_id;
    Map<String, String> map = new HashMap<>();
    private SharedPreferenceConfig preferenceConfig;

    public PostsAdapter(List<Posts> postsList, Context mContext,String uid, int lessonsID) {
        this.postsList = postsList;
        this.mContext = mContext;
        this.lessons_id = lessonsID;
        preferenceConfig = new SharedPreferenceConfig(mContext);
        this.mUID = uid;
    }

    @NonNull
    @Override
    public PostsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_posts, null, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostsAdapter.MyHolder myHolder, final int i) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference()
                .child("classroom").child("android_development")
                .child("users").child(String.valueOf(mUID)).child("read_items")
                .child("lessons_id").child(String.valueOf(lessons_id))
                .child("posts_id");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int stepsSize = postsList.get(i).getSteps().size();
                for (DataSnapshot child: dataSnapshot.getChildren()){
//                    Log.d(child.getValue().toString(), "#TAG");

                   map.put(child.getKey(), child.getValue(String.class));

                    if (map.containsKey(String.valueOf(i))
                            && map.get(String.valueOf(i)).equals(String.valueOf(stepsSize-1))) {
                        //All Data Seen
                        myHolder.layoutPostItem.setBackgroundResource(R.color.colorGreen);
                    } else if (map.containsKey(String.valueOf(i)) && !map.get(String.valueOf(i)).equals(String.valueOf(stepsSize-1))){
                        myHolder.layoutPostItem.setBackgroundResource(R.color.colorOrange);
                    } else {
                        myHolder.layoutPostItem.setBackgroundResource(R.color.colorGray);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        myHolder.txtPostsTitle.setText(postsList.get(i).getPosts_title());
        myHolder.txtNoOfPosts.setText(i+1 + "/" + postsList.size());
        myHolder.txtNoOfSteps.setText(postsList.get(i).getSteps().size() + " steps to follow.");
        mCurrentItemPosition = i;
        mNextItemPosition = mCurrentItemPosition +1;

//        databaseReference.child(String.valueOf(i));
//        myHolder.layoutPostItem.setEnabled(false);

        myHolder.layoutPostItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceConfig.writePostName(postsList.get(i).getPosts_title());
//                myHolder.layoutPostItem.setBackgroundResource(R.color.colorGreen);
                Intent intent = new Intent(mContext, StepDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mUid", mUID);
                bundle.putInt("lessons_id", lessons_id);
                bundle.putInt("posts_id", i);
                bundle.putSerializable("steps", (Serializable) postsList.get(i).getSteps());
                bundle.putString("action_bar_title", postsList.get(i).getPosts_title());
                intent.putExtras(bundle);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView txtPostsTitle, txtNoOfPosts, txtNoOfSteps;
        RelativeLayout layoutPostItem;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            txtPostsTitle = itemView.findViewById(R.id.txt_posts_title);
            layoutPostItem = itemView.findViewById(R.id.layout_posts_item);
            txtNoOfPosts = itemView.findViewById(R.id.txt_no_of_posts);
            txtNoOfSteps = itemView.findViewById(R.id.txt_no_of_steps);
        }
    }
}
