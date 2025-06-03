package com.gr6.javocado;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.graphics.Color;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.graphics.drawable.Drawable;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;


public class MainActivity extends AppCompatActivity {

    private TextView seedsCount;
    private int logoTapCount = 0;
    private long lastTapTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // - CURRENT CHAPTER - //
        ImageView chapterIcon = findViewById(R.id.ChapterIcon);
        TextView chapterText = findViewById(R.id.ChapterText);
        TextView progressText = findViewById(R.id.ProgressText);
        ProgressBar progressBar = findViewById(R.id.progressBar);


        // - DEVELOPER OPTIONS - //
        ImageView logo = findViewById(R.id.jvcdo_lg);
        logo.setOnClickListener(v -> {
            long now = System.currentTimeMillis();

            if (now - lastTapTime > 1500) {
                logoTapCount = 0;
            }

            logoTapCount++;
            lastTapTime = now;

            if (logoTapCount == 5) {
                logoTapCount = 0;
                Toast.makeText(this, "Developer options unlocked!", Toast.LENGTH_LONG).show();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, new DeveloperOptionsFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                int tapsLeft = 5 - logoTapCount;
                Toast.makeText(MainActivity.this, "Tap " + tapsLeft + " more times to unlock developer options", Toast.LENGTH_SHORT).show();
            }
        });

        // - (SIDE) MENU - //
        ImageView menuIcon = findViewById(R.id.menuIcon);
        LinearLayout menu = findViewById(R.id.menu);
        FrameLayout menuOverlay = findViewById(R.id.menuOverlay);
        menuIcon.setOnClickListener(v -> {
            menuOverlay.setVisibility(View.VISIBLE);
            menu.setVisibility(View.VISIBLE);
            menu.setTranslationX(menu.getWidth());
            menu.animate()
                    .translationX(0)
                    .setDuration(300)
                    .start();
        });
        menuOverlay.setOnClickListener(v -> {
            menu.animate()
                    .translationX(menu.getWidth())
                    .setDuration(300)
                    .withEndAction(() -> {
                        menu.setVisibility(View.GONE);
                        menuOverlay.setVisibility(View.GONE);
                    })
                    .start();
        });

        // - UPDATE SEED COUNT - //
        seedsCount = findViewById(R.id.seedsCount);
        updateSeeds();

        // - EXPAND CHAPTERS - //
        ImageView expandChapters = findViewById(R.id.expandChapters);
        expandChapters.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, new ChaptersFragment())
                    .addToBackStack(null)
                    .commit();
        });



        // - GET & SET CURRENT CHAPTER DATA - //
        Prefabs.ChapterData currentChapterData = initPrefabs();
        if (currentChapterData != null) {
            chapterText.setText(currentChapterData.title);
            int drawableResId = Prefabs.getDrawableIdByName(this, currentChapterData.drawableRes);
            chapterIcon.setImageResource(drawableResId);

            int completed = 0;
            for (Prefabs.LessonData lesson : currentChapterData.lessons) {
                if (Memory.isLessonCompleted(this, lesson.id)) {
                    completed++;
                }
            }

            int total = currentChapterData.lessons.size();
            int percent = (int) ((completed / (float) total) * 100);

            progressText.setText(completed + " of " + total + " lessons completed");
            progressBar.setProgress(percent);
        }
    }

    ///// - GET PREFABS - /////
    private Prefabs.ChapterData initPrefabs() {
        // - GET CHAPTER COMPONENTS - //
        LinearLayout lessonsContainer = findViewById(R.id.lessons);
        TextView chapterText = findViewById(R.id.ChapterText);
        ImageView chapterIcon = findViewById(R.id.ChapterIcon);
        TextView progressText = findViewById(R.id.ProgressText);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // - GET CHAPTER DATA - //
        List<Prefabs.ChapterData> chapters = Prefabs.getChapters(this);

        // - RESET LESSONS - //
        lessonsContainer.removeAllViews();

        // - GET CURRENT CHAPTER FROM MEMORY - //
        String currentChapter = Memory.getCurrentChapter(this);
        if (currentChapter == null && !chapters.isEmpty()) {
            Memory.setCurrentChapter(this, chapters.get(0).title);
            currentChapter = chapters.get(0).title;
        }

        // - RESET - //
        Prefabs.ChapterData activeChapterData = null;

        // - GET & SET CHAPTER DATA FROM PREFABS - //
        for (Prefabs.ChapterData chapter : chapters) {
            if (currentChapter != null && currentChapter.equals(chapter.title)) {
                activeChapterData = chapter;

                chapterText.setText(chapter.title);

                int chapterDrawableId = Prefabs.getDrawableIdByName(this, chapter.drawableRes);
                chapterIcon.setImageResource(chapterDrawableId);

                int completed = 0;
                for (Prefabs.LessonData lesson : chapter.lessons) {
                    if (Memory.isLessonCompleted(this, lesson.id)) {
                        completed++;
                    }
                }

                int total = chapter.lessons.size();
                int percent = (int) ((completed / (float) total) * 100);

                progressText.setText(completed + " of " + total + " lessons completed");
                progressBar.setProgress(percent);

                // - GET & SET LESSON DATA FROM CHAPTER DATA - //
                for (Prefabs.LessonData lesson : chapter.lessons) {
                    int lessonDrawableId = Prefabs.getDrawableIdByName(this, lesson.drawableRes);
                    createLesson(this, lessonsContainer,
                            lesson.id,
                            lesson.title,
                            lesson.description,
                            lesson.seeds,
                            lesson.difficulty,
                            lessonDrawableId,
                            lesson.levels);
                }
                break;
            }
        }
        return activeChapterData;
    }

    // - UPDATES SEEDS - //
    public void updateSeeds() {
        int seeds = Memory.getSeeds(this);
        if (seeds < 0) seeds = 0;
        seedsCount.setText(String.valueOf(seeds));
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart called");
    }

    // - UPDATE ON RETURN - //
    @Override
    protected void onResume() {
        super.onResume();
        updateSeeds();

        // Re-initialize the chapters and lessons view to reflect any progress changes
        initPrefabs();

        // Force redraw on the lessons container (if needed)
        LinearLayout lessonsContainer = findViewById(R.id.lessons);
        lessonsContainer.invalidate();
        lessonsContainer.requestLayout();
        Log.d("MainActivity", "onResume called - UI updated");

    }

    // - MEMORY FOR IMPORTANT DATA - //
    public static class Memory {

        // - USER PREFERENCE NAME - //
        private static final String PREF = "jvcdo";

        // - SET THE CURRENT CHAPTER - //
        public static void setCurrentChapter(@NonNull Context context, String chapterTitle) {
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                    .edit()
                    .putString("current_chapter", chapterTitle)
                    .apply();
        }

        // - GET THE CURRENT CHAPTER - //
        public static String getCurrentChapter(@NonNull Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            return prefs.getString("current_chapter", null);
        }

        // - RESET (ALL) DATA IN MEMORY - //
        public static void reset(@NonNull Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
        }

        // - GET SEED COUNT - //
        public static int getSeeds(@NonNull Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            if (!prefs.contains("seeds")) return -1;
            return prefs.getInt("seeds", 0);
        }

        // - SET SEED COUNT - //
        public static void setSeeds(@NonNull Context context, int seeds) {
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                    .edit()
                    .putInt("seeds", seeds)
                    .apply();
        }

        // - TRACK COMPLETED LESSONS - //
        public static boolean isLessonCompleted(@NonNull Context context, String lessonNumber) {
            SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            return prefs.getBoolean("lesson_completed_" + lessonNumber, false);
        }

        // - SET LESSON COMPLETE - //
        public static void setLessonCompleted(@NonNull Context context, String lessonNumber, boolean completed) {
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("lesson_completed_" + lessonNumber, completed)
                    .apply();
        }
    }

    // - LESSON CREATOR - //
    public void createLesson(Context context, LinearLayout container, String lessonNumber, String lessonTitle,
                             String description, int reward, String difficulty, int codeImageResId,
                             List<LessonActivity.LessonLevel> levels) {

        // - SET DATA - //
        LessonActivity.LessonLevel.Rewards.setReward(reward);
        Typeface lilita = ResourcesCompat.getFont(context, R.font.lilitaoneregular);
        int currentLesson = Integer.parseInt(lessonNumber);
        int previousLesson = currentLesson - 1;

        // - ROOT - //
        LinearLayout capsuleLayout = new LinearLayout(context);
        capsuleLayout.setOrientation(LinearLayout.HORIZONTAL);
        capsuleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        capsuleLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.capsule_left));
        capsuleLayout.setGravity(Gravity.CENTER_VERTICAL);

        // - TITLE - //
        TextView title = new TextView(context);
        title.setText("Lesson " + lessonNumber + " – " + lessonTitle);
        title.setTextColor(Color.BLACK);
        title.setTypeface(lilita);
        title.setTextSize(16);
        title.setPadding(30, 20, 10, 20);
        title.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // - LESSON EXPANSION - //
        TextView expandButton = new TextView(context);
        expandButton.setText("Loading...");
        expandButton.setTextColor(Color.BLACK);
        expandButton.setTypeface(lilita);
        expandButton.setTextSize(16);
        expandButton.setPadding(20, 20, 20, 20);
        expandButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // - SET VIEW TO ROOT - //
        capsuleLayout.addView(title);
        capsuleLayout.addView(expandButton);

        // - SET BACKGROUND - //
        Drawable background = ContextCompat.getDrawable(context, R.drawable.capsule_right);
        Drawable wrapped = DrawableCompat.wrap(background.mutate());

        // - LESSON BUTTON VARIATIONS - //
        if (currentLesson > 1 && !Memory.isLessonCompleted(context, String.valueOf(previousLesson))) {
            DrawableCompat.setTint(wrapped, Color.parseColor("#858585"));
            expandButton.setBackground(wrapped);
            expandButton.setText("Lesson Locked 🔒");
            expandButton.setTextColor(Color.WHITE);
        } else {
            if (Memory.isLessonCompleted(context, lessonNumber)) {
                expandButton.setText("Review Lesson 📚");
                DrawableCompat.setTint(wrapped, Color.parseColor("#B2FF59"));
                expandButton.setBackground(wrapped);
                expandButton.setTextColor(Color.BLACK);
            } else {
                expandButton.setText("Continue Lesson 📚");
                DrawableCompat.setTint(wrapped, Color.parseColor("#FFEB3B"));
                expandButton.setBackground(wrapped);
                expandButton.setTextColor(Color.BLACK);
            }
        }

        // - SET LESSONS VIEW - //
        final View[] lesson = new View[1];

        lesson[0] = createLesson(context, lilita, lessonNumber, lessonTitle,
                description, reward, difficulty, codeImageResId,
                levels,
                v -> {
                    lesson[0].setVisibility(View.GONE);
                    capsuleLayout.setVisibility(View.VISIBLE);
                });
        lesson[0].setVisibility(View.GONE);

        // - SET LESSON EXPANSION VIEW - //
        final List<View> allLessons = new ArrayList<>();
        allLessons.add(lesson[0]);

        expandButton.setOnClickListener(v -> {
            if (currentLesson > 1 && !Memory.isLessonCompleted(context, String.valueOf(previousLesson))) {
                Toast.makeText(context, "Complete Lesson " + previousLesson + " first!", Toast.LENGTH_SHORT).show();
            } else {
                for (View card : allLessons) {
                    card.setVisibility(View.GONE);
                }
                capsuleLayout.setVisibility(View.GONE);
                lesson[0].setVisibility(View.VISIBLE);
            }
        });

        // - REMOVE BROKEN VIEWS - //
        if (capsuleLayout.getParent() != null) {
            ((ViewGroup) capsuleLayout.getParent()).removeView(capsuleLayout);
        }
        if (lesson[0].getParent() != null) {
            ((ViewGroup) lesson[0].getParent()).removeView(lesson[0]);
        }

        // - SET MARGINS TO EACH VIEW - //
        LinearLayout.LayoutParams capsuleLayoutParams = (LinearLayout.LayoutParams) capsuleLayout.getLayoutParams();
        capsuleLayoutParams.setMargins(0, 0, 0, 20);

        FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) lesson[0].getLayoutParams();
        cardViewLayoutParams.setMargins(0, 20, 0, 20);

        // - SET TO MAIN - //
        container.addView(capsuleLayout);
        container.addView(lesson[0]);
    }

    // - LESSON CREATOR (OVERLOAD) - //
    public View createLesson(Context context, Typeface lilita,
                             String lessonNumber, String lessonTitle,
                             String description, int sReward,
                             String difficulty, int codeImageResId,
                             List<LessonActivity.LessonLevel> levels,
                             View.OnClickListener onCancelClick) {

        // - ROOT - //
        CardView lesson = new CardView(context);
        lesson.setRadius(24f);
        lesson.setCardElevation(8f);
        lesson.setUseCompatPadding(true);
        lesson.setCardBackgroundColor(Color.parseColor("#D4FFB2"));
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(20, 20, 20, 20);
        lesson.setLayoutParams(cardParams);

        // - CONTENT - //
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(30, 30, 30, 30);

        // - TITLE - //
        TextView title = new TextView(context);
        title.setText("Lesson " + lessonNumber + " - " + lessonTitle);
        title.setTypeface(lilita);
        title.setTextSize(20);
        title.setPadding(0, 0, 0, 20);
        title.setTextColor(Color.BLACK);

        // - DESCRIPTION - //
        TextView desc = new TextView(context);
        desc.setText(description);
        desc.setTypeface(lilita);
        desc.setTextSize(16);
        desc.setPadding(0, 0, 0, 20);
        desc.setTextColor(Color.DKGRAY);

        // - CODE / EXAMPLE IMAGE - //
        ImageView codeImage = new ImageView(context);
        codeImage.setImageResource(codeImageResId);
        codeImage.setAdjustViewBounds(true);
        codeImage.setPadding(0, 0, 0, 20);
        codeImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // - META DATA - //
        TextView meta = new TextView(context);
        meta.setText("Reward: +" + sReward + " Seeds\nDifficulty: " + difficulty);
        meta.setTypeface(lilita);
        meta.setTextColor(Color.BLACK);
        meta.setPadding(0, 0, 0, 20);
        meta.setTextSize(16);

        // - LESSON START - //
        Button startBtn = new Button(context);
        startBtn.setText("Start Lesson ▶");
        startBtn.setTypeface(lilita);
        startBtn.setTextColor(Color.BLACK);
        startBtn.setAllCaps(false);
        startBtn.setPadding(20, 0, 20, 0);
        startBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_yellow));
        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, LessonActivity.class);
            intent.putExtra("lessonTitle", lessonTitle);
            intent.putExtra("lessonNumber", lessonNumber);
            intent.putExtra("levels", new Gson().toJson(levels));
            context.startActivity(intent);
        });

        // - CANCEL BUTTON -  //
        ImageView cancelBtn = new ImageView(context);
        cancelBtn.setImageResource(R.drawable.ic_dropdown);
        FrameLayout.LayoutParams cancelParams = new FrameLayout.LayoutParams(
                80, 80, Gravity.END | Gravity.TOP);
        cancelParams.setMargins(0, 10, 20, 20);
        cancelBtn.setLayoutParams(cancelParams);
        cancelBtn.setOnClickListener(onCancelClick);

        // - SET VIEWS TO CONTENT - //
        contentLayout.addView(title);
        contentLayout.addView(desc);
        contentLayout.addView(codeImage);
        contentLayout.addView(meta);
        contentLayout.addView(startBtn);

        // - WRAP VIEW INTO LAYOUT - //
        FrameLayout wrapper = new FrameLayout(context);
        wrapper.addView(contentLayout);
        wrapper.addView(cancelBtn);
        lesson.addView(wrapper);
        return lesson;
    }
}