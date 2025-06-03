package com.gr6.javocado;

import android.os.Bundle;
import android.view.View;
import android.text.Spanned;
import android.view.Gravity;
import android.content.Intent;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.content.res.ColorStateList;
import android.text.SpannableStringBuilder;
import android.graphics.drawable.GradientDrawable;

import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CodePatternFragment extends Fragment {

    // - VARIABLES TO STORE QUESTION, ANSWERS, CLUE, AND OPTIONS - //
    private String question;
    private List<String> correctAnswers;
    private String textClue;
    private boolean wasAnswerCorrect = false;
    private int currentLessonLevel;
    private List<String> options;

    // - UI COMPONENT REFERENCES - //
    private LinearLayout lessonGround;
    private MaterialButton submitButton;
    private TextView feedbackView;
    private Typeface lilitaFont;
    private List<String> selectedAnswers = new ArrayList<>();
    private List<MaterialButton> optionButtons = new ArrayList<>();
    private TextView questionView;

    // - FACTORY METHOD TO CREATE A NEW INSTANCE OF THE FRAGMENT WITH PARAMETERS - //
    public static CodePatternFragment newInstance(String question, List<String> options, List<String> correctAnswers, String textClue, int levelIndex) {
        CodePatternFragment fragment = new CodePatternFragment();
        Bundle args = new Bundle();
        args.putString("question", question);
        args.putStringArrayList("correctAnswers", new ArrayList<>(correctAnswers));
        args.putString("textClue", textClue);
        args.putStringArrayList("options", new ArrayList<>(options));
        args.putInt("levelIndex", levelIndex);
        fragment.setArguments(args);
        return fragment;
    }

    // - INITIALIZE VALUES FROM ARGUMENTS - //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString("question");
            correctAnswers = getArguments().getStringArrayList("correctAnswers");
            textClue = getArguments().getString("textClue");
            options = getArguments().getStringArrayList("options");
            currentLessonLevel = getArguments().getInt("levelIndex", 0);
        }
    }

    // - CREATE AND SETUP THE VIEW HIERARCHY - //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_pattern, container, false);

        // - EXIT BUTTON TO RETURN TO MAIN ACTIVITY - //
        ImageView exitlevel = view.findViewById(R.id.exitlevel);
        exitlevel.setOnClickListener(V -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // - SETUP UI ELEMENT REFERENCES - //
        lessonGround = view.findViewById(R.id.lesson_ground);
        submitButton = view.findViewById(R.id.submit_button);
        submitButton.setEnabled(false);
        lilitaFont = ResourcesCompat.getFont(requireContext(), R.font.lilitaoneregular);

        // - SET SEEDS COUNT - //
        TextView seedsCount = view.findViewById(R.id.seedsCount);
        int seeds = MainActivity.Memory.getSeeds(requireContext());
        seedsCount.setText(String.valueOf(seeds));

        // - BUILD THE LESSON UI - //
        setupUI();
        return view;
    }

    // - UPDATE THE QUESTION DISPLAY WITH SELECTED ANSWERS - //
    private void updateQuestionView() {
        String[] parts = question.split("_____");
        SpannableStringBuilder builder = new SpannableStringBuilder();

        int filled = 0;
        for (int i = 0; i < parts.length; i++) {
            builder.append(parts[i]);

            if (filled < selectedAnswers.size()) {
                String answer = selectedAnswers.get(filled);
                SpannableString underlined = new SpannableString(answer);
                underlined.setSpan(new UnderlineSpan(), 0, answer.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(underlined);
                filled++;
            } else if (i < parts.length - 1) {
                builder.append("_____");
            }
        }

        questionView.setText(builder);
    }

    // - COUNT NUMBER OF BLANKS IN THE QUESTION - //
    private int countBlanks(String text) {
        return text.split("_____").length - 1;
    }

    // - BUILD ALL UI ELEMENTS DYNAMICALLY FOR THE LESSON - //
    private void setupUI() {
        lessonGround.removeAllViews();
        selectedAnswers.clear();
        optionButtons.clear();
        submitButton.setEnabled(false);
        submitButton.setText("Submit");

        // - CLUE TEXT BUBBLE - //
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

        lessonGround.addView(clueCapsule);

        // - DISPLAY THE QUESTION WITH BLANKS - //
        questionView = new TextView(getContext());
        questionView.setTextSize(15f);
        questionView.setTextColor(Color.WHITE);
        questionView.setTypeface(lilitaFont);
        questionView.setShadowLayer(4, 2, 2, Color.BLACK);
        questionView.setPadding(0, 50, 0, 50);
        lessonGround.addView(questionView);

        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        lessonGround.setPadding(padding, padding, padding, padding);
        updateQuestionView();

        // - CREATE OPTION BUTTONS - //
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

            // - HANDLE OPTION SELECTION / DESELECTION - //
            optionButton.setOnClickListener(v -> {
                String opt = optionButton.getText().toString();
                if (selectedAnswers.contains(opt)) {
                    selectedAnswers.remove(opt);
                    optionButton.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
                } else {
                    int maxBlanks = countBlanks(question);
                    if (selectedAnswers.size() < maxBlanks) {
                        selectedAnswers.add(opt);
                        optionButton.setStrokeColor(ColorStateList.valueOf(Color.YELLOW));
                    }
                }
                updateQuestionView();
                submitButton.setEnabled(selectedAnswers.size() == countBlanks(question));
            });

            optionButtons.add(optionButton);
            lessonGround.addView(optionButton);
        }

        // - FEEDBACK MESSAGE TEXTVIEW - //
        feedbackView = new TextView(getContext());
        feedbackView.setTextSize(24f);
        feedbackView.setTypeface(lilitaFont);
        feedbackView.setTextColor(Color.WHITE);
        feedbackView.setPadding(32, 32, 32, 32);
        feedbackView.setShadowLayer(4, 2, 2, Color.BLACK);
        feedbackView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        feedbackView.setVisibility(View.GONE);
        lessonGround.addView(feedbackView);
        submitButton.setOnClickListener(v -> onSubmit());
    }

    // - HANDLE SUBMISSION AND SHOW RESULTS - //
    private void onSubmit() {
        List<String> userAnswer = new ArrayList<>(selectedAnswers);
        List<String> correct = new ArrayList<>(correctAnswers);

        boolean isCorrect = userAnswer.size() == correct.size()
                && userAnswer.containsAll(correct);

        wasAnswerCorrect = isCorrect;

        for (MaterialButton btn : optionButtons) {
            btn.setEnabled(false);
        }

        if (isCorrect) {
            for (MaterialButton btn : optionButtons) {
                if (correct.contains(btn.getText().toString())) {
                    btn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#3DBC21")));
                    btn.setBackgroundColor(Color.parseColor("#3DBC21"));
                } else {
                    btn.setAlpha(0.5f);
                }
            }
            feedbackView.setText("Nice Work!");
            feedbackView.setBackground(getRoundedDrawable(Color.parseColor("#3DBC21")));
            feedbackView.setVisibility(View.VISIBLE);
            submitButton.setText("Continue to Next Lesson");
            submitButton.setOnClickListener(v -> ((LessonActivity) getActivity()).goToNextLevel());
        } else {
            for (MaterialButton btn : optionButtons) {
                if (selectedAnswers.contains(btn.getText().toString()) && !correct.contains(btn.getText().toString())) {
                    btn.setStrokeColor(ColorStateList.valueOf(Color.RED));
                    btn.setTextColor(Color.RED);
                } else if (correct.contains(btn.getText().toString())) {
                    btn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#3DBC21")));
                    btn.setBackgroundColor(Color.parseColor("#3DBC21"));
                }
            }
            feedbackView.setText("Incorrect!\nCorrect Answers: " + String.join(", ", correct));
            feedbackView.setBackground(getRoundedDrawable(Color.RED));
            feedbackView.setVisibility(View.VISIBLE);
            submitButton.setText("Continue to Next Lesson");
            submitButton.setOnClickListener(v -> {
                ((LessonActivity) getActivity()).onLevelFailed(currentLessonLevel);
            });
        }
    }

    // - CREATE A ROUNDED BACKGROUND SHAPE WITH SPECIFIED COLOR - //
    private GradientDrawable getRoundedDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(24 * getResources().getDisplayMetrics().density);
        return drawable;
    }
}
