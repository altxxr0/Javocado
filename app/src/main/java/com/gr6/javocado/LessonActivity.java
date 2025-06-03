package com.gr6.javocado;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class LessonActivity extends AppCompatActivity {

    // - TRACK USER'S SEED CURRENCY - //
    private int seeds;

    // - LIST OF LEVELS IN THIS LESSON - //
    private List<LessonLevel> levels;

    // - CURRENT LEVEL INDEX - //
    private int currentIndex = 0;

    // - TOTAL NUMBER OF FAILURES IN THIS LESSON - //
    private int failureCount = 0;

    // - RETRIEVE TOTAL FAILURES - //
    public int getFailureCount() { return failureCount; }

    // - INCREMENT TOTAL FAILURES - //
    public void incrementFailureCount() { failureCount++; }

    // - ENUM TO REPRESENT LEVEL TYPES - //
    public enum LevelType {
        @SerializedName("MULTIPLE_CHOICE")
        MULTIPLE_CHOICE,
        @SerializedName("FILL_IN_THE_BLANK")
        FILL_IN_THE_BLANK,
        @SerializedName("CODE_PATTERN")
        CODE_PATTERN,
    }

    // - ACCESSOR FOR LEVEL LIST - //
    public List<LessonLevel> getLevels() {
        return levels;
    }

    // - UNUSED BUT RESERVED FOR EXPANSION - //
    private List<String> correctAnswers;

    // - INNER CLASS TO REPRESENT A SINGLE LEVEL - //
    public static class LessonLevel implements Serializable {
        private String id;
        private LevelType type;
        private String question;
        private List<String> options;
        private String correctAnswer;
        private List<String> correctAnswers;
        private String textClue;
        private int failureCount = 0;

        // - STATIC REWARD CLASS FOR THIS LEVEL - //
        public static class Rewards {
            private static int reward;

            public static int getReward() {
                return reward;
            }

            public static void setReward(int newReward) {
                reward = newReward;
            }
        }

        // - CONSTRUCTOR FOR SINGLE CORRECT ANSWER LEVELS - //
        public LessonLevel(String id,
                           LevelType type,
                           String question,
                           List<String> options,
                           String correctAnswer,
                           String textClue) {
            this.id = id;
            this.type = type;
            this.question = question;
            this.options = options;
            this.correctAnswer = correctAnswer;
            this.textClue = textClue;
        }

        // - CONSTRUCTOR FOR MULTIPLE CORRECT ANSWERS (E.G., CODE PATTERN) - //
        public LessonLevel(String id,
                           LevelType type,
                           String question,
                           List<String> options,
                           List<String> correctAnswers,
                           String textClue) {
            this.id = id;
            this.type = type;
            this.question = question;
            this.options = options;
            this.correctAnswers = correctAnswers;
            this.textClue = textClue;
        }

        // - GETTERS FOR LEVEL PROPERTIES - //
        public String getId() { return id; }
        public LevelType getType() { return type; }
        public String getQuestion() { return question; }
        public String getCorrectAnswer() { return correctAnswer; }
        public List<String> getCorrectAnswers() { return correctAnswers; }
        public String getTextClue() { return textClue; }
        public List<String> getOptions() { return options; }

        // - TRACK INDIVIDUAL LEVEL FAILURES - //
        public void incrementFailureCount() {
            failureCount++;
        }

        public int getFailureCount() {
            return failureCount;
        }
    }

    // - SAVE APP STATE (SEEDS, INDEX, FAILURES) - //
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seeds", seeds);
        outState.putInt("currentIndex", currentIndex);
        outState.putInt("failureCount", failureCount);
    }

    // - CURRENT LESSON NUMBER PASSED FROM MAIN - //
    private String currentLessonNumber;

    // - INITIAL SETUP FOR LESSON ACTIVITY - //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        // - LOAD LEVELS FOR THIS LESSON - //
        levels = loadLevels();

        // - GET LESSON NUMBER FROM INTENT - //
        currentLessonNumber = getIntent().getStringExtra("lessonNumber");

        // - SHOW FIRST LEVEL - //
        showLevel(currentIndex);

        // - LOAD SEEDS FROM MEMORY - //
        seeds = MainActivity.Memory.getSeeds(this);
    }

    // - LOAD LEVELS FROM INTENT JSON OR FALLBACK TO DEFAULT - //
    private List<LessonLevel> loadLevels() {
        String jsonLevels = getIntent().getStringExtra("levels");

        // - GET LEVELS FROM JSON - //
        if (jsonLevels != null) {
            try {
                Type listType = new TypeToken<List<LessonLevel>>() {}.getType();
                return new Gson().fromJson(jsonLevels, listType);
            } catch (Exception e) {
                Log.e("< LessonActivity >", "Failed to parse levels from JSON", e);
            }
        }

        // - FALLBACK DEFAULT LEVELS IF NONE PROVIDED - //
        List<LessonLevel> defaultLevels = new ArrayList<>();
        defaultLevels.add(new LessonLevel(
                "4",
                LevelType.CODE_PATTERN,
                "public _____ void _____()",
                Arrays.asList("int", "static", "class", "main", "void"),
                Arrays.asList("static", "main"),
                "Fill in correct method signature: "
        ));
        defaultLevels.add(new LessonLevel(
                "1",
                LevelType.MULTIPLE_CHOICE,
                "What is Java?",
                Arrays.asList("Microsoft", "Oracle", "Sun Microsystems"),
                "Oracle",
                "Which Company Developed Java?"
        ));
        defaultLevels.add(new LessonLevel(
                "2",
                LevelType.FILL_IN_THE_BLANK,
                "Java is a programming _________ used by millions around the world",
                null,
                "Language",
                "Fill the blank with the correct word"
        ));
        return defaultLevels;
    }

    // - HANDLE WHEN USER FAILS A LEVEL - //
    public void onLevelFailed(int failedIndex) {
        if (failedIndex < 0 || failedIndex >= levels.size()) return;

        LessonLevel failedLevel = levels.get(failedIndex);

        // - UPDATE FAILURE STATS - //
        failedLevel.incrementFailureCount();
        incrementFailureCount();

        // - DECREMENT SEEDS AND UPDATE MEMORY - //
        if (seeds > 0) {
            seeds--;
            MainActivity.Memory.setSeeds(this, seeds);
        }

        // - IF OUT OF SEEDS, SHOW OUTOFSEEDS FRAGMENT - //
        if (seeds <= 0) {
            Fragment outOfSeedsFragment = OutOfSeedsFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, outOfSeedsFragment)
                    .commit();
            return;
        }

        // - MOVE FAILED LEVEL TO END OF LIST - //
        levels.remove(failedIndex);
        levels.add(failedLevel);

        // - RESET INDEX IF NECESSARY - //
        if (currentIndex >= levels.size()) {
            currentIndex = 0;
        }

        // - SHOW LEVEL AGAIN - //
        showLevel(currentIndex);
    }

    // - DISPLAY THE CURRENT LEVEL BASED ON TYPE - //
    private void showLevel(int index) {
        LessonLevel level = levels.get(index);
        Fragment fragment;

        // - DEBUG LOGGING FOR LEVEL DATA - //
        Log.d("LessonActivity", "Showing level: " + level.getId());
        Log.d("LessonActivity", "Type: " + level.getType());
        Log.d("LessonActivity", "Question: " + level.getQuestion());
        Log.d("LessonActivity", "Options: " + level.getOptions());
        Log.d("LessonActivity", "CorrectAnswer: " + level.getCorrectAnswer());

        // - INSTANTIATE FRAGMENT BASED ON LEVEL TYPE - //
        if (level.getType() == LevelType.MULTIPLE_CHOICE) {
            fragment = MultipleChoiceFragment.newInstance(
                    level.getQuestion(),
                    level.getOptions(),
                    level.getCorrectAnswer(),
                    level.getTextClue(),
                    index
            );
        } else if (level.getType() == LevelType.FILL_IN_THE_BLANK) {
            fragment = FillInTheBlankFragment.newInstance(
                    level.getQuestion(),
                    level.getCorrectAnswer(),
                    level.getTextClue(),
                    index
            );
        } else if (level.getType() == LevelType.CODE_PATTERN) {
            fragment = CodePatternFragment.newInstance(
                    level.getQuestion(),
                    level.getOptions(),
                    level.getCorrectAnswers(),
                    level.getTextClue(),
                    index
            );
        } else {
            throw new IllegalArgumentException("Unsupported level type: " + level.getType());
        }

        // - PASS LEVEL INDEX TO FRAGMENT - //
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putInt("levelIndex", index);
        fragment.setArguments(args);

        // - REPLACE FRAGMENT CONTAINER WITH LEVEL FRAGMENT - //
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // - MOVE TO NEXT LEVEL OR SHOW LESSON DONE FRAGMENT - //
    public void goToNextLevel() {
        if (currentIndex < levels.size() - 1) {
            currentIndex++;
            showLevel(currentIndex);
        } else {
            Fragment doneFragment = LessonDoneFragment.newInstance(getFailureCount(), currentLessonNumber);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, doneFragment)
                    .commit();
        }
    }
}
