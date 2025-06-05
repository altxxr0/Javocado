package com.gr6.javocado;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultipleChoiceFragment extends Fragment {

    // - QUESTION TEXT - //
    private String question;

    // - CORRECT ANSWER STRING - //
    private String correctAnswer;

    // - TEXT CLUE TO DISPLAY ABOVE QUESTION - //
    private String textClue;

    // - FLAG TO TRACK IF ANSWER WAS CORRECT - //
    private boolean wasAnswerCorrect = false;

    // - INDEX OF THE CURRENT LESSON LEVEL - //
    private int currentLessonLevel;

    // - LIST OF ANSWER OPTIONS - //
    private List<String> options;

    // - LAYOUT TO CONTAIN QUESTION AND OPTIONS - //
    private LinearLayout lessonGround;

    // - SUBMIT BUTTON - //
    private MaterialButton submitButton;

    // - TEXTVIEW TO SHOW CORRECT/INCORRECT FEEDBACK - //
    private TextView feedbackView;

    // - CUSTOM FONT FOR UI TEXT - //
    private Typeface lilitaFont;

    // - USER'S CURRENTLY SELECTED ANSWER - //
    private final String[] selectedAnswer = {null};

    // - STORES REFERENCE TO ALL OPTION BUTTONS - //
    private List<MaterialButton> optionButtons = new ArrayList<>();

    // - TEXTVIEW TO DISPLAY CURRENT SEEDS COUNT - //
    private TextView seedsCountView;

    // - STATIC METHOD TO CREATE A NEW FRAGMENT INSTANCE WITH PARAMETERS - //
    public static MultipleChoiceFragment newInstance(String question, List<String> options, String correctAnswer, String textClue, int levelIndex) {
        MultipleChoiceFragment fragment = new MultipleChoiceFragment();
        Bundle args = new Bundle();
        args.putString("question", question);
        args.putString("correctAnswer", correctAnswer);
        args.putString("textClue", textClue);
        if (options == null) {
            options = new ArrayList<>();
        }
        args.putStringArrayList("options", new ArrayList<>(options));
        args.putInt("levelIndex", levelIndex);
        fragment.setArguments(args);
        return fragment;
    }

    // - INITIALIZE FRAGMENT VARIABLES FROM ARGUMENTS - //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString("question");
            correctAnswer = getArguments().getString("correctAnswer");
            textClue = getArguments().getString("textClue");
            options = getArguments().getStringArrayList("options");
            currentLessonLevel = getArguments().getInt("levelIndex", 0);
        }
    }

    // - CREATE AND RETURN THE FRAGMENT'S UI - //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiple_choice, container, false);

        // - EXIT BUTTON RETURNS TO MAIN MENU - //
        ImageView exitlevel = view.findViewById(R.id.exitlevel);
        exitlevel.setOnClickListener(V -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // - SETUP UI COMPONENTS - //
        lessonGround = view.findViewById(R.id.lesson_ground);
        submitButton = view.findViewById(R.id.submit_button);
        submitButton.setEnabled(false);
        lessonGround.setPadding(50, 50, 50, 50);
        lilitaFont = ResourcesCompat.getFont(requireContext(), R.font.lilitaoneregular);

        // - DEFAULT OPTIONS IF NONE PROVIDED - //
        if (options == null || options.isEmpty()) {
            options = Arrays.asList("Option A", "Option B", "Option C", "Option D");
        }

        // - BUILD UI DYNAMICALLY - //
        setupUI();

        // - DISPLAY CURRENT SEEDS COUNT - //
        TextView seedsCount = view.findViewById(R.id.seedsCount);
        int seeds = MainActivity.Memory.getSeeds(requireContext());
        seedsCount.setText(String.valueOf(seeds));

        // - "-1" FIX - //
        if (Integer.parseInt(seedsCount.getText().toString()) == -1){
            seedsCount.setText("0");
        }

        return view;
    }

    // - SETUP THE QUESTION UI, OPTIONS, CLUE AND SUBMIT LOGIC - //
    private void setupUI() {
        lessonGround.removeAllViews();
        selectedAnswer[0] = null;
        submitButton.setEnabled(false);
        submitButton.setText("Submit");
        submitButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        submitButton.setAlpha(0.6f);

        // - CREATE AND STYLE THE CLUE CAPSULE - //
        TextView clueView = new TextView(getContext());
        clueView.setText(textClue);
        clueView.setTextSize(30f);
        clueView.setTypeface(lilitaFont);
        clueView.setTextColor(Color.WHITE);
        clueView.setShadowLayer(4, 2, 2, Color.BLACK);

        LinearLayout clueCapsule = new LinearLayout(getContext());
        clueCapsule.setOrientation(LinearLayout.HORIZONTAL);
        clueCapsule.setPadding(32, 32, 32, 32);
        clueCapsule.setBackground(getRoundedDrawable(getResources().getColor(R.color.greenDark)));
        clueCapsule.setElevation(8 * getResources().getDisplayMetrics().density);
        clueCapsule.setGravity(Gravity.CENTER_VERTICAL);
        clueCapsule.addView(clueView);

        if (textClue == null || textClue.trim().isEmpty()) {
            clueCapsule.setVisibility(View.GONE);  // - HIDE IF CLUE IS EMPTY - //
        }

        lessonGround.addView(clueCapsule);

        // - ADD QUESTION TEXTVIEW - //
        TextView questionView = new TextView(getContext());
        questionView.setText(question);
        questionView.setTextSize(30f);
        questionView.setTextColor(Color.WHITE);
        questionView.setTypeface(lilitaFont);
        questionView.setShadowLayer(4, 2, 2, Color.BLACK);
        questionView.setPadding(0, 50, 0, 50);
        lessonGround.addView(questionView);

        // - CREATE AND STYLE OPTION BUTTONS - //
        optionButtons.clear();
        for (String option : options) {
            MaterialButton optionButton = new MaterialButton(getContext());
            optionButton.setText(option);
            optionButton.setTextSize(24f);
            optionButton.setTypeface(lilitaFont);
            optionButton.setTextColor(Color.WHITE);
            optionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greenDark)));
            optionButton.setCornerRadius(32);
            optionButton.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
            optionButton.setStrokeWidth(3);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 32);
            optionButton.setLayoutParams(params);

            // - HANDLE OPTION SELECTION - //
            optionButton.setOnClickListener(v -> {
                selectedAnswer[0] = option;
                for (MaterialButton btn : optionButtons) {
                    btn.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
                    btn.setBackgroundColor(getResources().getColor(R.color.greenDark));
                    btn.setTextColor(Color.WHITE);
                }
                optionButton.setStrokeColor(ColorStateList.valueOf(Color.YELLOW));
                submitButton.setEnabled(true);
                submitButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greenDark)));
                submitButton.setAlpha(1.0f);
            });

            optionButtons.add(optionButton);
            lessonGround.addView(optionButton);
        }

        // - ADD FEEDBACK TEXTVIEW (INITIALLY HIDDEN) - //
        feedbackView = new TextView(getContext());
        feedbackView.setTextSize(24f);
        feedbackView.setTypeface(lilitaFont);
        feedbackView.setTextColor(Color.WHITE);
        feedbackView.setPadding(32, 32, 32, 32);
        feedbackView.setShadowLayer(4, 2, 2, Color.BLACK);
        LinearLayout.LayoutParams feedbackParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        feedbackParams.setMargins(0, 50, 0, 50);
        feedbackView.setLayoutParams(feedbackParams);
        feedbackView.setVisibility(View.GONE);
        lessonGround.addView(feedbackView);

        // - HANDLE SUBMIT BUTTON CLICK - //
        submitButton.setOnClickListener(v -> onSubmit());
    }

    // - HANDLE LOGIC FOR SUBMITTING ANSWER - //
    private void onSubmit() {
        if (selectedAnswer[0] == null) return;

        boolean isCorrect = selectedAnswer[0].equalsIgnoreCase(correctAnswer);
        wasAnswerCorrect = isCorrect;

        for (MaterialButton btn : optionButtons) {
            btn.setEnabled(false);
        }

        if (isCorrect) {
            // - HIGHLIGHT CORRECT ANSWER IN GREEN - //
            for (MaterialButton btn : optionButtons) {
                if (btn.getText().toString().equalsIgnoreCase(correctAnswer)) {
                    btn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#3DBC21")));
                    btn.setBackgroundColor(Color.parseColor("#3DBC21"));
                    btn.setTextColor(Color.WHITE);
                } else {
                    btn.setAlpha(0.5f);
                }
            }

            // - DISPLAY SUCCESS FEEDBACK - //
            String[] successMessages = {"Nice Work!", "Good Job!", "Correct!"};
            int randomIndex = (int) (Math.random() * successMessages.length);
            feedbackView.setText(successMessages[randomIndex]);
            feedbackView.setBackground(getRoundedDrawable(Color.parseColor("#3DBC21")));
            feedbackView.setVisibility(View.VISIBLE);

            submitButton.setText("Continue to Next Lesson");
            submitButton.setOnClickListener(v -> ((LessonActivity) getActivity()).goToNextLevel());
        } else {
            // - HIGHLIGHT SELECTED WRONG ANSWER IN RED, CORRECT IN GREEN - //
            for (MaterialButton btn : optionButtons) {
                if (btn.getText().toString().equalsIgnoreCase(selectedAnswer[0])) {
                    btn.setStrokeColor(ColorStateList.valueOf(Color.RED));
                    btn.setTextColor(Color.RED);
                } else if (btn.getText().toString().equalsIgnoreCase(correctAnswer)) {
                    btn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#3DBC21")));
                    btn.setBackgroundColor(Color.parseColor("#3DBC21"));
                    btn.setTextColor(Color.WHITE);
                } else {
                    btn.setAlpha(0.5f);
                }
            }

            // - DISPLAY FAILURE FEEDBACK - //
            feedbackView.setText("Incorrect! Correct Answer: " + correctAnswer);
            feedbackView.setBackground(getRoundedDrawable(Color.RED));
            feedbackView.setVisibility(View.VISIBLE);

            submitButton.setText("Continue to Next Lesson");
            submitButton.setOnClickListener(v -> ((LessonActivity) getActivity()).onLevelFailed(currentLessonLevel));
        }
    }

    // - CREATE A ROUNDED RECTANGLE BACKGROUND WITH GIVEN COLOR - //
    private GradientDrawable getRoundedDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(24 * getResources().getDisplayMetrics().density);
        return drawable;
    }
}
