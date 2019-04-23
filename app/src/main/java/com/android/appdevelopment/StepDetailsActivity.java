package com.android.appdevelopment;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.appdevelopment.Adapter.PagerAdapter;
import com.android.appdevelopment.Model.Steps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StepDetailsActivity extends FragmentActivity {
    List<Steps> stepsList = new ArrayList<>();
    Button btn_continue;
    int mPosition, mMaxPosition, posts_id, lessons_id;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private String mUID;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);
        btn_continue = findViewById(R.id.btn_continue);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("classroom/android_development");

//        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle mBundle = intent.getExtras();
            stepsList = (List<Steps>) mBundle.getSerializable("steps");
            mUID = mBundle.getString("mUid");
            mMaxPosition = stepsList.size() -1;
            posts_id = mBundle.getInt("posts_id");
            lessons_id = mBundle.getInt("lessons_id");

//            getSupportActionBar().setTitle(mBundle.getString("action_bar_title"));
            pagerAdapter = new PagerAdapter(getSupportFragmentManager(), stepsList, posts_id, lessons_id, mUID, getApplicationContext());
            viewPager.setAdapter(pagerAdapter);

        } else {

        }

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() < stepsList.size() - 1) {
                    btn_continue.setEnabled(true);
                    btn_continue.setAlpha(1f);
                } else {
                    btn_continue.setEnabled(false);
                    btn_continue.setAlpha(.2f);
                }
                writeStepsToFirebase(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                writeStepsToFirebase(tab.getPosition());
            }
        });

//        viewPager.setCurrentItem(mPosition);
//        onTabSelectedListener(viewPager);
       /* if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putSerializable("steps", (Serializable) stepsList);
            arguments.putInt("posts_id", posts_id);
            arguments.putInt("lessons_id", lessons_id);
            StepDetailsLayoutFragment layoutFragment = new StepDetailsLayoutFragment();
            layoutFragment.setArguments(arguments);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, layoutFragment)
                    .commit();
        }
*/
       if (mPosition == mMaxPosition){
            btn_continue.setAlpha(.2f);
            btn_continue.setEnabled(false);
        } else {
            btn_continue.setAlpha(1f);
            btn_continue.setEnabled(true);
        }

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = viewPager.getCurrentItem()+1;
                if (mPosition == stepsList.size()-1){
                    Log.d("stepsPOS if", String.valueOf(mPosition));
                    writeStepsToFirebase(mPosition);
                }else{
                    Log.d("stepsPOS else", String.valueOf(mPosition));
                }
//                tabLayout.getSelectedTabPosition();
                viewPager.setCurrentItem(mPosition);
//                refreshStepFragment();
                btn_continue.setEnabled(mPosition != mMaxPosition);
                if (mPosition == mMaxPosition){
                    btn_continue.setAlpha(.2f);
                }else{
                    btn_continue.setAlpha(1f);
                }
            }
        });
    }

    private void writeStepsToFirebase(int mPos) {
        databaseReference.child("users")
                .child(mUID)
                .child("read_items")
                .child("lessons_id")
                .child(String.valueOf(lessons_id))
                .child("posts_id")
                .child(String.valueOf(posts_id))
                .setValue(String.valueOf(mPos));
    }

    @Override
    public void onBackPressed() {
        // if (viewPager.getCurrentItem() == 0) { //If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        // } else {
            // Otherwise, select the previous step.
            // viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        // }
    }

/*
    @Override
    public void onBackPressed() {
//        getStartActivity();
        super.onBackPressed();
    }

    private void onTabSelectedListener(final ViewPager viewPager) {

        new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };
    }

    public void refreshStepFragment() {
        Bundle arguments = new Bundle();
        arguments.putSerializable("steps", (Serializable) stepsList);
        arguments.putInt("pos", mPosition);
        arguments.putInt("lessons_id", lessons_id);
        arguments.putInt("posts_id", posts_id);
        StepDetailsLayoutFragment fragment = new StepDetailsLayoutFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitNow();
    }

    private void getStartActivity() {
        //        if (!isTaskRoot()){
        Intent intent = new Intent(StepDetailsActivity.this, PostsActivity.class);
        //BackStack CLear Top Activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        }
    }
*/

}
