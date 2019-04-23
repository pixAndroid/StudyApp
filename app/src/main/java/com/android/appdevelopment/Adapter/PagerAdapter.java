package com.android.appdevelopment.Adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.appdevelopment.Model.Steps;
import com.android.appdevelopment.StepDetailsLayoutFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    List<Steps> stepsList = new ArrayList<>();
    private int mPosition, posts_id, lessons_id;
    private String mUID;
    private Context mContext;

    public PagerAdapter(FragmentManager fm, List<Steps> stepsList, int post_id, int lesson_id, String mUid, Context ctx) {
        super(fm);
        this.stepsList = stepsList;
        this.posts_id = post_id;
        this.lessons_id = lesson_id;
        this.mUID = mUid;
        this.mContext = ctx;
    }

    @Override
    public Fragment getItem(int pos) {
        Log.d("pagerPOS ", String.valueOf(pos));
        Bundle arguments = new Bundle();
        arguments.putSerializable("steps", (Serializable) stepsList);
        arguments.putString("mUid", mUID);
        arguments.putInt("mPos", pos);
        arguments.putInt("lessons_id", lessons_id);
        arguments.putInt("posts_id", posts_id);
        StepDetailsLayoutFragment fragment = new StepDetailsLayoutFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public int getCount() {
        return stepsList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        mPosition = position;
        return String.valueOf(mPosition);
    }

  /*  @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        container.removeView((View) object);
        container.getChildCount();
    }*/
}





