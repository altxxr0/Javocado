package com.gr6.javocado;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Prefabs {

    // - DATA CLASS REPRESENTING A CHAPTER WITH ID, TITLE, DRAWABLE RESOURCE, AND A LIST OF LESSONS - //
    public static class ChapterData {
        public String id;
        public String title;
        public String drawableRes;
        public List<LessonData> lessons = new ArrayList<>();
    }

    // - DATA CLASS REPRESENTING A LESSON WITH VARIOUS PROPERTIES INCLUDING LEVELS - //
    public static class LessonData {
        public String id;
        public String title;
        public String description;
        public int seeds;
        public String difficulty;
        public String drawableRes;
        public List<LessonActivity.LessonLevel> levels;
    }

    // - DATA CLASS REPRESENTING A LESSON LEVEL WITH QUESTION, OPTIONS, CORRECT ANSWER AND CLUE - //
    public static class LessonLevel {
        public String id;
        public String type;
        public String question;
        public List<String> options;
        public String correctAnswer;
        public List<String> answerList;
        public String textClue;
    }

    // - STATIC HELPER METHOD TO GET DRAWABLE RESOURCE ID BY NAME USING CONTEXT - //
    public static int getDrawableIdByName(Context context, String drawableName) {
        return context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
    }

    // - CACHE TO STORE LOADED CHAPTERS TO AVOID RELOADING MULTIPLE TIMES - //
    private static List<ChapterData> cachedChapters;

    // - PUBLIC METHOD TO GET CHAPTERS, LOADS FROM JSON IF NOT ALREADY LOADED - //
    public static List<ChapterData> getChapters(Context context) {
        if (cachedChapters == null) {
            cachedChapters = loadChapters(context);
            if (cachedChapters != null) {
                android.util.Log.d("Prefabs", "Loaded chapters count: " + cachedChapters.size());
            }
        }
        return cachedChapters;
    }

    // - PRIVATE METHOD TO LOAD CHAPTERS FROM RAW JSON RESOURCE USING GSON - //
    private static List<ChapterData> loadChapters(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.chapters);
            Reader reader = new InputStreamReader(is);
            return new Gson().fromJson(reader, new TypeToken<List<ChapterData>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
