package com.gr6.javocado;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LessonDoneFragment extends Fragment {

    // - REQUIRED EMPTY CONSTRUCTOR - //
    public LessonDoneFragment() {
    }

    // - CREATES A NEW INSTANCE OF THE FRAGMENT WITH FAILURE COUNT AND LESSON NUMBER - //
    public static LessonDoneFragment newInstance(int failureCount, String lessonNumber, String chapterId) {
        LessonDoneFragment fragment = new LessonDoneFragment();
        Bundle args = new Bundle();
        args.putInt("failureCount", failureCount);
        args.putString("lessonNumber", lessonNumber);
        args.putString("chapterId", chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    // - CALLED WHEN THE FRAGMENT'S UI IS CREATED - //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // - INFLATE THE LAYOUT FOR THIS FRAGMENT - //
        View view = inflater.inflate(R.layout.fragment_lesson_done, container, false);

        // - SETUP EXIT BUTTON TO RETURN TO MAIN MENU - //
        ImageView exitlevel = view.findViewById(R.id.exitlevel);
        exitlevel.setOnClickListener(V -> {
            if (getActivity() != null) {
                // - CREATE INTENT TO GO BACK TO MAINACTIVITY - //
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // - DISPLAY NUMBER OF FAILED ATTEMPTS DURING THE LESSON - //
        TextView failureCountView = view.findViewById(R.id.failure_count_capsule);
        int totalFailures = getArguments() != null ? getArguments().getInt("failureCount", 0) : 0;
        failureCountView.setText("Wrong Answers: " + totalFailures);

        // - MARK THE LESSON AS COMPLETED IN SHARED PREFERENCES - //
        String lessonNumber = getArguments() != null ? getArguments().getString("lessonNumber", null) : null;
        String chapterId = getArguments() != null ? getArguments().getString("chapterId", "unknown_chapter") : "unknown_chapter";

        if (lessonNumber != null) {
            String fullLessonKey = chapterId + "_" + lessonNumber;
            MainActivity.Memory.setLessonCompleted(requireContext(), fullLessonKey, true);
        }


        // - ADD REWARD SEEDS TO USER'S TOTAL AND DISPLAY IT - //
        int reward = LessonActivity.LessonLevel.Rewards.getReward();
        int currentSeeds = MainActivity.Memory.getSeeds(requireContext());
        int newTotal = currentSeeds + reward;
        MainActivity.Memory.setSeeds(requireContext(), newTotal);
        TextView seedsCount = view.findViewById(R.id.seedsCount);
        seedsCount.setText(String.valueOf(newTotal));

        // - SETUP BACK TO MENU BUTTON TO NAVIGATE TO MAINACTIVITY - //
        view.findViewById(R.id.back_to_menu).setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        return view;
    }

}
