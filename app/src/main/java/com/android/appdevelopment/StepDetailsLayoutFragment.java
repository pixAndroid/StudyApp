package com.android.appdevelopment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.appdevelopment.Model.Lessons;
import com.android.appdevelopment.Model.Posts;
import com.android.appdevelopment.Model.Steps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.OnCodeLineClickListener;
import io.github.kbiakov.codeview.adapters.Format;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import io.github.kbiakov.codeview.highlight.Font;
import io.github.kbiakov.codeview.highlight.FontCache;
import uk.co.senab.photoview.PhotoViewAttacher;

public class StepDetailsLayoutFragment extends Fragment {
    TextView header, body, body_before_image, body_after_image,body_before_code, body_after_code;
    io.github.kbiakov.codeview.CodeView body_code;
    String image_url;
    List<Steps> stepsList = new ArrayList<>();
    ImageView img_steps;
    int pos, posts_id, lessons_id;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
//    FirebaseAuth mAuth;
    String mUID;

    public StepDetailsLayoutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_details_for_fragment, container, false);

        header = rootView.findViewById(R.id.txt_header);
        body = rootView.findViewById(R.id.txt_body);
        body_before_image = rootView.findViewById(R.id.txt_before_image);
        body_after_image = rootView.findViewById(R.id.txt_after_image);
        body_before_code = rootView.findViewById(R.id.txt_before_code);
        body_code = rootView.findViewById(R.id.txt_step_code);
        body_after_code = rootView.findViewById(R.id.txt_after_code);
        img_steps = rootView.findViewById(R.id.img_step);

        header.setVisibility(View.GONE);
        body.setVisibility(View.GONE);
        body_before_image.setVisibility(View.GONE);
        body_after_image.setVisibility(View.GONE);
        body_before_code.setVisibility(View.GONE);
        body_code.setVisibility(View.GONE);
        body_after_code.setVisibility(View.GONE);
        img_steps.setVisibility(View.GONE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("classroom/android_development");
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();

        try{
            stepsList = (List<Steps>) getArguments().getSerializable("steps");
            pos = getArguments().getInt("mPos");
            mUID = getArguments().getString("mUid");
            posts_id = getArguments().getInt("posts_id");
            lessons_id = getArguments().getInt("lessons_id");
            Log.d("TAGlessons_id posts_id ", String.valueOf(lessons_id + "  " + posts_id));

            int listSize = stepsList.size();
            if (pos != 0) {
                Log.d("stepsPOS if", String.valueOf(pos));
                writeStepsToFirebase();
            }else if (pos == 0) {
                databaseReference.child("users")
                        .child(mUID)
                        .child("read_items")
                        .child("lessons_id")
                        .child(String.valueOf(lessons_id))
                        .child("posts_id")
                        .child(String.valueOf(posts_id))
                        .setValue(String.valueOf(pos));
            }

            //check Header
            if(stepsList.get(pos).getHeader() == null){
                header.setVisibility(View.GONE);
            }else{
                header.setVisibility(View.VISIBLE);
                header.setText(stepsList.get(pos).getHeader());
            }

            //check Body
            if(stepsList.get(pos).getBody() == null){
                body.setVisibility(View.GONE);
            }else{
                body.setVisibility(View.VISIBLE);
                body.setText(stepsList.get(pos).getBody());
            }

            //check body_before_image
            if(stepsList.get(pos).getBody_before_image() == null){
                body_before_image.setVisibility(View.GONE);
            }else{
                body_before_image.setText(stepsList.get(pos).getBody_before_image());
            }

            //check image_url
            if(stepsList.get(pos).getBody_image_url() == null){
                img_steps.setVisibility(View.GONE);
            }else{
                img_steps.setVisibility(View.VISIBLE);
                Picasso.get().load(stepsList.get(pos).getBody_image_url()).into(img_steps);
                img_steps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(img_steps);
                        photoViewAttacher.update();
                    }
                });
            }

            //check body_after_image
            if(stepsList.get(pos).getBody_after_image() == null){
                body_after_image.setVisibility(View.GONE);
            }else{
                body_after_image.setText(stepsList.get(pos).getBody_after_image());
                body_after_image.setVisibility(View.VISIBLE);
            }

            //check body_before_code
            if(stepsList.get(pos).getBody_before_code() == null){
                body_before_code.setVisibility(View.GONE);
            }else{
                body_before_code.setText(stepsList.get(pos).getBody_before_code());
                body_before_code.setVisibility(View.VISIBLE);
            }

            //check code
            if(stepsList.get(pos).getBody_code() == null){
                body_code.setVisibility(View.GONE);
            }else{
                body_code.setVisibility(View.VISIBLE);
                body_code.setCode(stepsList.get(pos).getBody_code());
                body_code.refreshDrawableState();
            }

            //check body_after_code
            if(stepsList.get(pos).getBody_after_code() == null){
                body_after_code.setVisibility(View.GONE);
            }else{
                body_after_code.setText(stepsList.get(pos).getBody_after_code());
                body_after_code.setVisibility(View.VISIBLE);
            }

        }catch (NullPointerException Ignored){}

        return rootView;
    }

    private void writeStepsToFirebase() {
        databaseReference.child("users")
                .child(mUID)
                .child("read_items")
                .child("lessons_id")
                .child(String.valueOf(lessons_id))
                .child("posts_id")
                .child(String.valueOf(posts_id))
                .setValue(String.valueOf(pos-1));
    }
}
