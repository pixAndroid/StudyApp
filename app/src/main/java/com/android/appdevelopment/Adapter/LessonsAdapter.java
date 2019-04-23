package com.android.appdevelopment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.appdevelopment.Model.Lessons;
import com.android.appdevelopment.PostsActivity;
import com.android.appdevelopment.R;
import com.android.appdevelopment.SharedPreferenceConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.lessonHolder> {
    private boolean isDataChanged;
    private List<Lessons> lessons = new ArrayList<>();
    private Context mContext;
    private int maxSteps = 0;
    private String mUid;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceToPostsID, databaseReferenceToLessonsID;
    final Map<String, String> map = new HashMap<>();

    private SharedPreferenceConfig preferenceConfig;

    public LessonsAdapter(List<Lessons> lessons, Context mContext, String uid) {
        this.lessons = lessons;
        this.mContext = mContext;
        preferenceConfig = new SharedPreferenceConfig(mContext);
        this.mUid = uid;
    }

    @NonNull
    @Override
    public LessonsAdapter.lessonHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_main_item, null, false);
        return new lessonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LessonsAdapter.lessonHolder lessonHolder, int i) {
//        final int tmp = 0;
        final int postsSize = lessons.get(i).getPosts().size();
//        final int totalStepsRead = 0;
        maxSteps = 0;
        map.clear();
        final int lessonPOS = i;
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        

        databaseReferenceToPostsID = firebaseDatabase.getReference()
                .child("classroom").child("android_development")
                .child("users").child(String.valueOf(mUid)).child("read_items")
                .child("lessons_id").child(String.valueOf(i))
                .child("posts_id");
        databaseReferenceToPostsID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        map.put(child.getKey(), child.getValue(String.class));
                        Log.d(child.toString(), "#TAG New Child()");

                        int totalStepsRead=0;
                        maxSteps = 0;
                        for (int c = 0; c < postsSize; c++) {
                            int tmpTotalSteps = lessons.get(lessonPOS).getPosts().get(c).getSteps().size();
                            maxSteps += tmpTotalSteps;
                            Log.d(String.valueOf(maxSteps), "#TAG max");
                            if (!map.isEmpty()){
                                try{
                                    int tmp =  Integer.parseInt(map.get(String.valueOf(c)));
                                    totalStepsRead += tmp + 1;
                                }catch (NullPointerException Ignored){}
                                catch (NumberFormatException e){
                                    e.printStackTrace();
                                }
                            }
                        }

                        lessonHolder.pb_lessons.setMax(maxSteps);
                        lessonHolder.pb_lessons.setProgress(totalStepsRead);
                        if (lessonHolder.pb_lessons.getProgress() == maxSteps){
                            //Completed
                            lessonHolder.txt_status.setText("Completed.");
                        }else if (lessonHolder.pb_lessons.getProgress() == 0){
                            //Not Started
                            lessonHolder.txt_status.setText("Not Started!");
                        }else{
                            //In Progress
                            lessonHolder.txt_status.setText("In Progress...");
                        }

                    }
                }

//                notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Picasso.get().load(lessons.get(i).getLessons_image_url()).into(lessonHolder.imgLessons);
        lessonHolder.txtTitle.setText(lessons.get(i).getLessons_title());
        lessonHolder.txtDescriptions.setText(lessons.get(i).getLessons_short_descriptions());
        lessonHolder.txt_lessons_number.setText("Lesson " + String.valueOf(i+1));
        lessonHolder.txt_activities.setText("Activities " + String.valueOf(postsSize));

        if (preferenceConfig.readLessonID().equals(String.valueOf(i))){
            lessonHolder.layoutListItem.setBackground(mContext.getResources().getDrawable(R.drawable.card_layout_green_background));
            lessonHolder.imgLessons.setBackground(mContext.getResources().getDrawable(R.drawable.circle_shape_green));
        } else {
            lessonHolder.layoutListItem.setBackground(mContext.getResources().getDrawable(R.drawable.card_layout_background));
            lessonHolder.imgLessons.setBackground(mContext.getResources().getDrawable(R.drawable.circle_shape));
        }


        lessonHolder.layoutListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceConfig.writeCurrentActivity(lessons.get(lessonPOS).getLessons_title());
                preferenceConfig.writeLessonID(String.valueOf(lessonPOS));
                preferenceConfig.writePostName("");
                Intent intent = new Intent(mContext, PostsActivity.class);
                final Bundle bundle = new Bundle();
                bundle.putString("uid", mUid);
                bundle.putInt("lessons_id", lessonPOS);
                bundle.putSerializable("posts", (Serializable) lessons.get(lessonPOS).getPosts());
                bundle.putString("action_bar_title", lessons.get(lessonPOS).getLessons_title());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class lessonHolder extends RecyclerView.ViewHolder{
        public TextView txtTitle;
        public TextView txtDescriptions;
        public TextView txt_status;
        public TextView txt_lessons_number;
        public TextView txt_activities;
        public ImageView imgLessons;
        public ProgressBar pb_lessons;
        public RelativeLayout layoutListItem;
        public lessonHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_main_item_title);
            txtDescriptions = itemView.findViewById(R.id.txt_main_item_descriptions);
            imgLessons = itemView.findViewById(R.id.img_lessons);
            txt_status = itemView.findViewById(R.id.txt_Status);
            pb_lessons = itemView.findViewById(R.id.pb_lessons);
            txt_lessons_number = itemView.findViewById(R.id.txt_lessons_number);
            txt_activities = itemView.findViewById(R.id.txt_activities_no);
            layoutListItem = itemView.findViewById(R.id.layout_main_list_item);
        }
    }
}
