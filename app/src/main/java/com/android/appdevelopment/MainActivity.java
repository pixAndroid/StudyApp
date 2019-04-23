package com.android.appdevelopment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.appdevelopment.Model.Lessons;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.kbiakov.codeview.classifier.CodeProcessor;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private Button mButtonGetStarted;
    private ProgressBar pbCourseStatus;
    private TextView txt_lessons_complete, txt_current_activity_lesson, txt_current_activity_post, txt_go_button;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseLessonReference, mDatabaseLessonsReadReference, mDatabaseReferenceUserInfo;
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
    private String mUid;
    final List<Lessons> lessonsList = new ArrayList<>();
    Map<String, String> map = new HashMap<>();
    private SharedPreferenceConfig sharedPreferenceConfig;
    private ImageView imgArrow2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonGetStarted = findViewById(R.id.button);
        pbCourseStatus = findViewById(R.id.pb_course_status);
        txt_lessons_complete = findViewById(R.id.txt_lessons_complete);
        txt_current_activity_lesson = findViewById(R.id.txt_latest_activity);
        imgArrow2 = findViewById(R.id.img_arrow2);
        txt_go_button = findViewById(R.id.txt_go_button);
        txt_current_activity_post = findViewById(R.id.txt_current_post);

        if (savedInstanceState == null) {
            CodeProcessor.init(this);
        }

        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseLessonReference = mFirebaseDatabase.getReference()
                .child("classroom").child("android_development").child("lessons");

        mDatabaseReferenceUserInfo = mFirebaseDatabase.getReference("classroom/android_development");


        sharedPreferenceConfig = new SharedPreferenceConfig(getApplicationContext());
//        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Learn Android App Development :) </font>"));


        mButtonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                attachAuthStateListener();
                Intent intent = new Intent(MainActivity.this, GetStartActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", mUid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if (sharedPreferenceConfig.readLessonID().equals("")) {
            txt_go_button.setEnabled(false);
            txt_go_button.setAlpha(.2f);
        } else {
            txt_go_button.setEnabled(true);
            txt_go_button.setAlpha(1f);
        }

        txt_go_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lesson_id = Integer.parseInt(sharedPreferenceConfig.readLessonID());
//                if (lessonsList.size() != 0){
                    sharedPreferenceConfig.writeCurrentActivity(lessonsList.get(lesson_id).getLessons_title());
                sharedPreferenceConfig.writeLessonID(String.valueOf(lesson_id));
                sharedPreferenceConfig.writePostName("");
                Intent intent = new Intent(MainActivity.this, PostsActivity.class);
                final Bundle bundle = new Bundle();
                bundle.putInt("lessons_id", lesson_id);
                bundle.putString("uid", mUid);
                bundle.putSerializable("posts", (Serializable) lessonsList.get(lesson_id).getPosts());
                bundle.putString("action_bar_title", lessonsList.get(lesson_id).getLessons_title());
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        attachAuthStateListener();
//        mAuthStateListener.onAuthStateChanged(mAuth);
    }


    private void attachAuthStateListener() {
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    mUid = user.getUid();
                    onSignInInitialize(user.getDisplayName());
                }else{
                    onSignOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignInInitialize(String displayName) {
        mUsername = displayName;
        attachDatabaseReadListener();
    }

    private void onSignOutCleanup() {
        mUsername = ANONYMOUS;
        detachDtatbaseReadListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mUsername = user.getDisplayName();
                mUid = user.getUid();
//                mAuthStateListener.onAuthStateChanged(mAuth);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"Signed in canceled!", LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

   /*  @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SearchActivity.JARGON, true);
        startSearch(null, false, appData, false);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_sign_out1:
                AuthUI.getInstance().signOut(getApplicationContext());
//                clearSharedPref();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getCurrentLessonName() {
        int x = 0;
        try {
            x = Integer.parseInt(sharedPreferenceConfig.readLessonID());
            x++;
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        String lessonIDString = String.valueOf(x);
        txt_current_activity_lesson.setText("Lesson " + lessonIDString + " - " + sharedPreferenceConfig.readCurrentActivity());
    }

    private void getCurrentPostName() {
        if (sharedPreferenceConfig.readPostName().equals("")) {
            txt_current_activity_post.setVisibility(View.INVISIBLE);
            imgArrow2.setVisibility(View.INVISIBLE);
        }else{
            txt_current_activity_post.setText(sharedPreferenceConfig.readPostName());
            txt_current_activity_post.setVisibility(View.VISIBLE);
            imgArrow2.setVisibility(View.VISIBLE);
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Lessons rLessons = dataSnapshot.getValue(Lessons.class);
                    lessonsList.add(rLessons);

                    int totalStepsInCourse = 0;
                    final int totalLessons = lessonsList.size();
                    final int[] totalReadSteps = {0};
                    for (int c = 0; c < totalLessons; c++) {
                        int totalPosts = lessonsList.get(c).getPosts().size();
                        for (int j = 0; j < totalPosts; j++) {
                            int tmp = lessonsList.get(c).getPosts().get(j).getSteps().size();
                            totalStepsInCourse += tmp;
                        }

                        map.clear();
                        mDatabaseLessonsReadReference = mFirebaseDatabase.getReference()
                                .child("classroom").child("android_development")
                                .child("users").child(mUid).child("read_items")
                                .child("lessons_id").child(String.valueOf(c))
                                .child("posts_id");
                        final int finalTotalStepsInCourse = totalStepsInCourse;

                        mValueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()){
                                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                                        map.put(child.getKey(), child.getValue(String.class));
                                    }

                                    for (int x = 0; x < map.size(); x++) {
                                        if (map.containsKey(String.valueOf(x))){

                                            int t = Integer.parseInt(map.get(String.valueOf(x)));
                                            totalReadSteps[0] += t + 1;
                                            Log.d(String.valueOf(totalReadSteps[0]), "mess" );
                                        }
                                    }

                                    pbCourseStatus.setMax(100);
                                    long percentage = (totalReadSteps[0] * 100)/ finalTotalStepsInCourse;
                                    txt_lessons_complete.setText(String.valueOf(percentage + "%"));
                                    pbCourseStatus.setProgress((int) percentage);
                                    map.clear();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };

                        mDatabaseLessonsReadReference.addValueEventListener(mValueEventListener);

                    }

                    pbCourseStatus.setMax(totalStepsInCourse);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDatabaseLessonReference.addChildEventListener(mChildEventListener);
        }

        getSupportActionBar().setTitle(mUsername);
    }

    private void detachDtatbaseReadListener() {
        if(mChildEventListener != null){
//            mDatabaseLessonsReadReference.removeEventListener(mValueEventListener);
            mDatabaseLessonReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
            mValueEventListener = null;
        }
    }
    /*@Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
//        mAuthStateListener.onAuthStateChanged(mAuth);
//        if (mAuthStateListener == null) {
            mAuth.addAuthStateListener(mAuthStateListener);
//        }
        getCurrentPostName();
        getCurrentLessonName();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDtatbaseReadListener();
    }

    public void clearSharedPref(){
        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.shared_pref), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
