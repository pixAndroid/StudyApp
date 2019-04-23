package com.android.appdevelopment;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.appdevelopment.Adapter.LessonsAdapter;
import com.android.appdevelopment.Model.Lessons;
import com.android.appdevelopment.Model.Steps;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetStartActivity extends AppCompatActivity {
    private MaterialSearchView searchView;
    final Map<String, String> map = new HashMap<>();
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseLessonReference;
    ChildEventListener mChildEventListener;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference databaseReferenceToPostsID;
    private SharedPreferenceConfig preferenceConfig;
    final List<Lessons> lessonsList = new ArrayList<>();
    RecyclerView recyclerViewLessons;
    LessonsAdapter mLessonsAdapter;
    Lessons mLessons;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_start);
        recyclerViewLessons = findViewById(R.id.recyclerViewLessons);
        searchView = findViewById(R.id.search_view);

        setupToolbar();

        preferenceConfig = new SharedPreferenceConfig(this);
//        searchView.setVoiceSearch(true);
//        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Learn Android App Development </font>"));

//        if (mFirebaseDatabase == null){
            mFirebaseDatabase = FirebaseDatabase.getInstance();
//            mFirebaseDatabase.setPersistenceEnabled(true);

//        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        mUid = bundle.getString("uid");

        mDatabaseLessonReference = mFirebaseDatabase.getReference().child("classroom").child("android_development").child("lessons");
        mDatabaseLessonReference.keepSynced(true);

        setupLessonsAdapter();
//        mLessonsAdapter.notifyDataSetChanged();

    }

    private void setupLessonsAdapter() {
        recyclerViewLessons.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewLessons.setLayoutManager(layoutManager);

        // specify an adapter
        mLessonsAdapter = new LessonsAdapter(lessonsList, getApplicationContext(), mUid);
        recyclerViewLessons.setAdapter(mLessonsAdapter);
        getListOfLessons();
    }

    private void getListOfLessons() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Lessons rLessons = dataSnapshot.getValue(Lessons.class);
                lessonsList.add(rLessons);
                mLessonsAdapter.notifyDataSetChanged();
                findViewById(R.id.progress_loading).setVisibility(View.GONE);
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

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menu_search:
                        searchView.setMenuItem(menuItem);
                        searchNow(searchView);
                        return true;
                    case R.id.menu_sign_out:
                        AuthUI.getInstance().signOut(getApplicationContext());
//                clearSharedPref();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        mLessonsAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        searchView.setMenuItem(item);
        return true;
    }

    private void searchNow(MaterialSearchView searchView) {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchOnFirebase(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*if (!newText.isEmpty()){
                    searchOnFirebase(newText);
                } else {
                    setupLessonsAdapter();
                }
*/
                searchOnFirebase(newText);
                return false;
            }
        });
    }

    private void searchOnFirebase(final String strSearch) {
        final List<Lessons> searchList = new ArrayList<>();
        mLessonsAdapter = new LessonsAdapter(searchList, this, mUid);
        Query mQuery = FirebaseDatabase.getInstance().getReference("classroom/android_development/lessons")
                .orderByChild("lessons_title")
                .startAt(strSearch.toUpperCase())
                .endAt(strSearch.toLowerCase() + "\uf8ff");

        ChildEventListener searchChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                searchList.clear();
                Lessons rLessons = dataSnapshot.getValue(Lessons.class);
//                findViewById(R.id.progress_loading).setVisibility(View.GONE);

                if (rLessons.getLessons_title().toLowerCase().contains(strSearch.toLowerCase())) {
                    searchList.add(rLessons);
                    mLessonsAdapter.notifyDataSetChanged();
                }
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

        mQuery.addChildEventListener(searchChildEventListener);
        recyclerViewLessons.setAdapter(mLessonsAdapter);
//        updateFirebaseDatabaseUI(mQuery);

    }

    private void updateFirebaseDatabaseUI(Query myQuery) {
        FirebaseRecyclerOptions<Lessons> options = new FirebaseRecyclerOptions.Builder<Lessons>()
                .setQuery(myQuery, Lessons.class)
                .build();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewLessons.setLayoutManager(layoutManager);
        recyclerViewLessons.setHasFixedSize(true);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Lessons, LessonsAdapter.lessonHolder>(options) {
            @NonNull
            @Override
            public LessonsAdapter.lessonHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.layout_main_item, viewGroup, false);
                return new LessonsAdapter.lessonHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final LessonsAdapter.lessonHolder holder, int i, @NonNull Lessons model) {
                final int postsSize = lessonsList.get(i).getPosts().size();
                final int[] maxSteps = {0};
                map.clear();
                final int lessonPOS = i;
                mFirebaseDatabase = FirebaseDatabase.getInstance();

                databaseReferenceToPostsID = mFirebaseDatabase.getReference()
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
                                maxSteps[0] = 0;
                                for (int c = 0; c < postsSize; c++) {
                                    int tmpTotalSteps = lessonsList.get(lessonPOS).getPosts().get(c).getSteps().size();
                                    maxSteps[0] += tmpTotalSteps;
                                    Log.d(String.valueOf(maxSteps[0]), "#TAG max");
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

                                holder.pb_lessons.setMax(maxSteps[0]);
                                holder.pb_lessons.setProgress(totalStepsRead);
                                if (holder.pb_lessons.getProgress() == maxSteps[0]){
                                    //Completed
                                    holder.txt_status.setText("Completed.");
                                }else if (holder.pb_lessons.getProgress() == 0){
                                    //Not Started
                                    holder.txt_status.setText("Not Started!");
                                }else{
                                    //In Progress
                                    holder.txt_status.setText("In Progress...");
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                Picasso.get().load(lessonsList.get(i).getLessons_image_url()).into(holder.imgLessons);
                holder.txtTitle.setText(lessonsList.get(i).getLessons_title());
                holder.txtDescriptions.setText(lessonsList.get(i).getLessons_short_descriptions());
                holder.txt_lessons_number.setText("Lesson " + String.valueOf(i+1));
                holder.txt_activities.setText("Activities " + String.valueOf(postsSize));

                if (preferenceConfig.readLessonID().equals(String.valueOf(i))){
                    holder.layoutListItem.setBackground(getApplication().getResources().getDrawable(R.drawable.card_layout_green_background));
                    holder.imgLessons.setBackground(getResources().getDrawable(R.drawable.circle_shape_green));
                } else {
                    holder.layoutListItem.setBackground(getResources().getDrawable(R.drawable.card_layout_background));
                    holder.imgLessons.setBackground(getResources().getDrawable(R.drawable.circle_shape));
                }

                holder.layoutListItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preferenceConfig.writeCurrentActivity(lessonsList.get(lessonPOS).getLessons_title());
                        preferenceConfig.writeLessonID(String.valueOf(lessonPOS));
                        preferenceConfig.writePostName("");
                        Intent intent = new Intent(getApplicationContext(), PostsActivity.class);
                        final Bundle bundle = new Bundle();
                        bundle.putString("uid", mUid);
                        bundle.putInt("lessons_id", lessonPOS);
                        bundle.putSerializable("posts", (Serializable) lessonsList.get(lessonPOS).getPosts());
                        bundle.putString("action_bar_title", lessonsList.get(lessonPOS).getLessons_title());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerViewLessons.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        firebaseRecyclerAdapter.stopListening();
    }
}
