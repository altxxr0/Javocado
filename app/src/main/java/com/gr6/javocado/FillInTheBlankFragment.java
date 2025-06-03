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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.concurrent.atomic.AtomicReference;

public class FillInTheBlankFragment extends Fragment {

    // - STORES THE QUESTION, CORRECT ANSWER, OPTIONAL TEXT CLUE, AND TRACKING VARIABLES - //
    private String question;
    private String correctAnswer;
    private String textClue = null;
    private boolean wasAnswerCorrect = false;
    private int currentLessonLevel = 0;

    // - FACTORY METHOD TO CREATE A NEW INSTANCE OF THIS FRAGMENT WITH PROVIDED DATA - //
    public static FillInTheBlankFragment newInstance(String question,
                                                     String correctAnswer,
                                                     String textClue,
                                                     int lessonLevel) {
        FillInTheBlankFragment fragment = new FillInTheBlankFragment();
        Bundle args = new Bundle();
        args.putString("question", question);
        args.putString("answer", correctAnswer);
        args.putString("textClue", textClue);
        args.putInt("lessonLevel", lessonLevel);
        fragment.setArguments(args);
        return fragment;
    }

    // - INITIALIZE VARIABLES FROM THE ARGUMENTS PASSED TO THE FRAGMENT - //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString("question");
            correctAnswer = getArguments().getString("answer");
            textClue = getArguments().getString("textClue");
            currentLessonLevel = getArguments().getInt("lessonLevel", 0);
        }
    }

    // - CREATES AND CONFIGURES THE FRAGMENT UI - //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_in_the_blank, container, false);

        // - EXIT BUTTON TO RETURN TO MAIN ACTIVITY - //
        ImageView exitlevel = view.findViewById(R.id.exitlevel);
        exitlevel.setOnClickListener(V -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // - REFERENCES TO LAYOUT AND SUBMIT BUTTON - //
        AtomicReference<LinearLayout> lessonGround = new AtomicReference<>(view.findViewById(R.id.lesson_ground));
        MaterialButton submitButton = view.findViewById(R.id.submit_button);
        submitButton.setEnabled(false);
        submitButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

        // - STYLE SETUP FOR LAYOUT - //
        lessonGround.get().setPadding(50, 50, 50, 50);
        Typeface lilitaFont = ResourcesCompat.getFont(requireContext(), R.font.lilitaoneregular);

        // - CREATE A CLUE CAPSULE IF CLUE TEXT IS PRESENT - //
        LinearLayout clueCapsule = new LinearLayout(getContext());
        clueCapsule.setOrientation(LinearLayout.HORIZONTAL);
        clueCapsule.setPadding(32, 32, 32, 32);
        clueCapsule.setBackground(getRoundedDrawable(getResources().getColor(R.color.greenDark)));
        clueCapsule.setElevation(8 * getResources().getDisplayMetrics().density);
        clueCapsule.setGravity(Gravity.CENTER_VERTICAL);

        TextView clueView = new TextView(getContext());
        clueView.setText(textClue);
        clueView.setTextSize(30f);
        clueView.setTypeface(lilitaFont);
        clueView.setTextColor(Color.WHITE);
        clueView.setShadowLayer(4, 2, 2, Color.BLACK);
        clueCapsule.addView(clueView);
        lessonGround.get().addView(clueCapsule);

        // - ADD QUESTION TEXT VIEW - //
        TextView questionView = new TextView(getContext());
        questionView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        questionView.setText(question);
        questionView.setTextSize(30f);
        questionView.setTextColor(Color.WHITE);
        questionView.setShadowLayer(4f, 2f, 2f, Color.BLACK);
        questionView.setTypeface(lilitaFont);
        questionView.setPadding(0, 50, 0, 50);
        lessonGround.get().addView(questionView);

        // - INPUT CONTAINER FOR ANSWER - //
        LinearLayout inputContainer = new LinearLayout(getContext());
        inputContainer.setOrientation(LinearLayout.HORIZONTAL);
        inputContainer.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams inputContainerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        inputContainerParams.setMargins(0, 0, 0, 100);
        inputContainer.setLayoutParams(inputContainerParams);

        // - ANSWER INPUT FIELD - //
        EditText answerInput = new EditText(getContext());
        answerInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));
        answerInput.setHint("Type your Answer");
        answerInput.setTextColor(Color.WHITE);
        answerInput.setLinkTextColor(Color.WHITE);
        answerInput.setHighlightColor(Color.WHITE);
        answerInput.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        answerInput.setMaxLines(1);
        answerInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        answerInput.setTypeface(lilitaFont);
        answerInput.setTextSize(30f);
        answerInput.setSingleLine(true);

        // - ENABLE SUBMIT BUTTON IF TEXT IS ENTERED - //
        answerInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s.toString().trim().length() > 0;
                submitButton.setEnabled(hasText);
                submitButton.setBackgroundTintList(ColorStateList.valueOf(
                        hasText ? getResources().getColor(R.color.greenDark) : Color.GRAY));
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // - X/✓ ICON TO SHOW RESULT - //
        TextView xIcon = new TextView(getContext());
        xIcon.setText("✖");
        xIcon.setTextSize(28f);
        xIcon.setTextColor(Color.RED);
        xIcon.setVisibility(View.GONE);
        xIcon.setTypeface(lilitaFont);
        xIcon.setPadding(24, 0, 0, 0);

        inputContainer.addView(answerInput);
        inputContainer.addView(xIcon);
        lessonGround.get().addView(inputContainer);

        // - BOOLEAN TO PREVENT MULTIPLE ANSWERS - //
        final boolean[] hasAnswered = {false};

        // - FEEDBACK MESSAGE AFTER SUBMISSION - //
        TextView feedbackView = new TextView(getContext());
        feedbackView.setTextSize(24f);
        feedbackView.setTypeface(lilitaFont);
        feedbackView.setTextColor(Color.WHITE);
        feedbackView.setPadding(32, 32, 32, 32);
        feedbackView.setShadowLayer(4, 2, 2, Color.BLACK);
        LinearLayout.LayoutParams feedbackParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        feedbackParams.setMargins(0, 10, 0, 100);
        feedbackView.setLayoutParams(feedbackParams);
        feedbackView.setVisibility(View.GONE);
        lessonGround.get().addView(feedbackView);

        // - SUBMIT BUTTON LOGIC - //
        submitButton.setOnClickListener(v -> {
            if (!hasAnswered[0]) {
                hasAnswered[0] = true;
                String userInput = answerInput.getText().toString().trim();
                boolean isCorrect = userInput.equalsIgnoreCase(correctAnswer);
                wasAnswerCorrect = isCorrect;
                submitButton.setText("Continue to Next Lesson");

                if (isCorrect) {
                    // - DISPLAY CORRECT STATE - //
                    xIcon.setVisibility(View.VISIBLE);
                    xIcon.setText("✓");
                    xIcon.setTextColor(Color.parseColor("#3DBC21"));
                    answerInput.setTextColor(Color.parseColor("#3DBC21"));
                    answerInput.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3DBC21")));
                    feedbackView.setText(getRandomSuccessMessage());
                    feedbackView.setBackground(getRoundedDrawable(Color.parseColor("#3DBC21")));
                } else {
                    // - DISPLAY INCORRECT STATE WITHOUT FAILING LEVEL YET - //
                    xIcon.setVisibility(View.VISIBLE);
                    xIcon.setText("✖");
                    xIcon.setTextColor(Color.RED);
                    answerInput.setTextColor(Color.RED);
                    answerInput.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                    int failureCount = 0;
                    LessonActivity activity = (LessonActivity) getActivity();
                    if (activity != null) {
                        LessonActivity.LessonLevel level = activity.getLevels().get(currentLessonLevel);
                        failureCount = level.getFailureCount(); // - ONLY FETCH FAILURE COUNT - //
                    }

                    feedbackView.setText("Incorrect! Correct Answer: " + correctAnswer);
                    feedbackView.setBackground(getRoundedDrawable(Color.RED));
                }

                feedbackView.setVisibility(View.VISIBLE);

            } else {
                // - IF ALREADY ANSWERED, GO TO NEXT LEVEL OR FAIL CURRENT - //
                if (wasAnswerCorrect) {
                    ((LessonActivity) getActivity()).goToNextLevel();
                } else {
                    ((LessonActivity) getActivity()).onLevelFailed(currentLessonLevel);
                }
            }
        });

        // - DISPLAY CURRENT SEEDS - //
        TextView seedsCount = view.findViewById(R.id.seedsCount);
        int seeds = MainActivity.Memory.getSeeds(requireContext());
        seedsCount.setText(String.valueOf(seeds));
        return view;
    }

    // - RETURNS A RANDOM SUCCESS MESSAGE - //
    private String getRandomSuccessMessage() {
        String[] successMessages = {"Nice Work!", "Good Job!", "Correct!"};
        int randomIndex = (int) (Math.random() * successMessages.length);
        return successMessages[randomIndex];
    }

    // - RETURNS A ROUNDED RECTANGLE BACKGROUND DRAWABLE - //
    private GradientDrawable getRoundedDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(24 * getResources().getDisplayMetrics().density);
        return drawable;
    }
}
