package com.android.appdevelopment;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ConcurrentModificationException;

public class SharedPreferenceConfig {
    SharedPreferences mSharedPreferences;
    Context mContext;

    public SharedPreferenceConfig(Context context) {
        this.mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(mContext.getResources().getString(R.string.shared_pref), Context.MODE_PRIVATE);
    }


    public void writeCurrentActivity(String activityName) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mContext.getResources().getString(R.string.pref_current_activity), activityName);
        editor.commit();
    }

    public String readCurrentActivity() {
        String currentActivityName = mSharedPreferences
                .getString(mContext.getResources()
                        .getString(R.string.pref_current_activity), "Not Started Yet!");

        return currentActivityName;

    }

    public void writeLessonID(String lesson_id) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mContext.getResources().getString(R.string.pref_lesson_id), lesson_id);
        editor.commit();
    }

    public void writePostID(String post_id) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mContext.getResources().getString(R.string.pref_post_id), post_id);
        editor.commit();
    }

    public String readLessonID() {
        String lesson_id = mSharedPreferences
                .getString(mContext.getResources()
                        .getString(R.string.pref_lesson_id), "0");
        return lesson_id;
    }

    public String readPostID() {
        String post_id = mSharedPreferences
                .getString(mContext.getResources()
                        .getString(R.string.pref_post_id), " ! ");
        return post_id;
    }

    public void writePostName(String post_name) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mContext.getResources().getString(R.string.pref_post_name), post_name);
        editor.commit();
    }

    public String readPostName() {
        String post_Name = mSharedPreferences
                .getString(mContext.getResources()
                        .getString(R.string.pref_post_name), "");
        return post_Name;
    }

}
