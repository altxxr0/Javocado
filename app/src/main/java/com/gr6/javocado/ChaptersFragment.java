package com.gr6.javocado;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.content.res.ColorStateList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;


public class ChaptersFragment extends Fragment {
    private Context context;
    private final List<ChapterViewHolder> chapterViews = new ArrayList<>();


    // - DEFAULT - //
    public ChaptersFragment() {
        super(R.layout.fragment_chapters);
    }

    // - INFLATE LAYOUT - //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chapters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = requireContext();

        // - GET & SET CURRENT SEEDS - //
        TextView seedsCount = view.findViewById(R.id.seedsCount);
        int seeds = MainActivity.Memory.getSeeds(requireContext());
        seedsCount.setText(String.valueOf(seeds));

        // - "-1" FIX - //
        if (Integer.parseInt(seedsCount.getText().toString()) == -1){
            seedsCount.setText("0");
        }

        view.findViewById(R.id.exitlevel).setOnClickListener(v -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, new ChaptersFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // - CHAPTER EXPANSION - //
        ImageView chapterActionIcon = view.findViewById(R.id.expandChapters);
        chapterActionIcon.setScaleX(-1f);
        chapterActionIcon.setOnClickListener(v -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, new ChaptersFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // - INITIALIZE COMPONENTS - //
        ImageView chapterIcon = view.findViewById(R.id.ChapterIcon);
        TextView chapterText = view.findViewById(R.id.ChapterText);
        TextView progressText = view.findViewById(R.id.ProgressText);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        // - GET & SET CURRENT CHAPTER DATA - //
        Prefabs.ChapterData currentChapterData = initPrefabs();

        if (currentChapterData != null) {
            chapterText.setText(currentChapterData.title);
            if (currentChapterData.drawableRes != null) {
                int resId = context.getResources().getIdentifier(currentChapterData.drawableRes, "drawable", context.getPackageName());
                if (resId != 0) {
                    chapterIcon.setImageResource(resId);
                }
            } else {
                chapterIcon.setImageResource(R.drawable.ic_javocado_logo);
            }
            int completed = 0;
            if (currentChapterData.lessons != null) {
                for (Prefabs.LessonData lesson : currentChapterData.lessons) {
                    if (MainActivity.Memory.isLessonCompleted(requireContext(), lesson.id)) {
                        completed++;
                    }
                }
            }

            // - GET PROGRESS PERCENTAGE - //
            int total = currentChapterData.lessons.size();
            int percent = total == 0 ? 0 : (int)((completed / (float) total) * 100);

            // - SET PROGRESS - //
            progressText.setText(completed + " of " + total + " lessons completed");
            progressBar.setProgress(percent);
        }

        // - SET ALL CHAPTERS - //
        loadChapters(view);
    }

    // - GET FIRST CHAPTER - //
    private Prefabs.ChapterData initPrefabs() {
        List<Prefabs.ChapterData> chapters = Prefabs.getChapters(context);
        String currentChapterTitle = MainActivity.Memory.getCurrentChapter(context);

        if (currentChapterTitle != null) {
            // Find the chapter matching the saved current chapter title
            for (Prefabs.ChapterData chapter : chapters) {
                if (chapter.title.equals(currentChapterTitle)) {
                    return chapter;
                }
            }
        }
        // fallback to first chapter if none found
        if (!chapters.isEmpty()) {
            return chapters.get(0);
        }
        return null;
    }


    // - LOAD CHAPTERS- //
    private void loadChapters(View view) {
        LinearLayout chaptersContainer = view.findViewById(R.id.chapters);
        Context context = requireContext();

        List<Prefabs.ChapterData> chapters = Prefabs.getChapters(context);
        String currentChapter = MainActivity.Memory.getCurrentChapter(context);

        // - CLEAR EXISTING VIEWS - //
        chaptersContainer.removeAllViews();

        for (Prefabs.ChapterData chapter : chapters) {
            // - SKIP CURRENTLY ACTIVE CHAPTER - //
            if (currentChapter != null && currentChapter.equals(chapter.title)) {
                continue;
            }

            // - CREATE CONTAINER FOR CHAPTER ITEM - //
            LinearLayout chapterCapsule = new LinearLayout(context);
            chapterCapsule.setOrientation(LinearLayout.HORIZONTAL);
            chapterCapsule.setClickable(true);
            chapterCapsule.setFocusable(true);
            chapterCapsule.setBackgroundResource(R.drawable.rounded_card_bg);
            chapterCapsule.setElevation(8f);
            chapterCapsule.setGravity(Gravity.CENTER_VERTICAL);

            // - APPLY MARGINS - //
            int marginVertical = (int) (8 * context.getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, marginVertical, 0, marginVertical);
            chapterCapsule.setLayoutParams(params);

            // - APPLY PADDING AND BACKGROUND COLOR - //
            int paddingPx = (int) (16 * context.getResources().getDisplayMetrics().density);
            chapterCapsule.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            ColorStateList tint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.avocadoLight));
            ViewCompat.setBackgroundTintList(chapterCapsule, tint);

            // - CREATE CHAPTER ICON - //
            ImageView chapterIcon = new ImageView(context);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    (int)(64 * context.getResources().getDisplayMetrics().density),
                    (int)(64 * context.getResources().getDisplayMetrics().density));
            iconParams.setMarginEnd((int)(16 * context.getResources().getDisplayMetrics().density));
            chapterIcon.setLayoutParams(iconParams);
            if (chapter.drawableRes != null) {
                int resId = context.getResources().getIdentifier(chapter.drawableRes, "drawable", context.getPackageName());
                if (resId != 0) {
                    chapterIcon.setImageResource(resId);
                } else {
                    chapterIcon.setImageResource(R.drawable.ic_javocado_logo); // fallback if not found
                }
            } else {
                chapterIcon.setImageResource(R.drawable.ic_javocado_logo); // fallback if null
            }

            chapterIcon.setContentDescription("Chapter Icon");

            chapterCapsule.addView(chapterIcon);

            // - CREATE CENTER LAYOUT FOR TEXT AND PROGRESS - //
            LinearLayout centerLayout = new LinearLayout(context);
            centerLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams centerParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f);
            centerLayout.setLayoutParams(centerParams);

            // - CHAPTER TITLE TEXT - //
            TextView chapterText = new TextView(context);
            chapterText.setText(chapter.title);
            chapterText.setTextSize(20);
            chapterText.setTextColor(context.getResources().getColor(R.color.black));
            chapterText.setTypeface(ResourcesCompat.getFont(context, R.font.lilitaoneregular));
            centerLayout.addView(chapterText);

            // - PROGRESS TEXT - //
            TextView progressText = new TextView(context);
            int completed = 0;
            for (Prefabs.LessonData lesson : chapter.lessons) {
                if (MainActivity.Memory.isLessonCompleted(requireContext(), lesson.id)) {
                    completed++;
                }
            }

            int total = chapter.lessons.size();
            int percent = total == 0 ? 0 : (int)((completed / (float) total) * 100);

            progressText.setText(completed + " of " + total + " lessons completed");
            progressText.setTextSize(14);
            progressText.setTextColor(context.getResources().getColor(R.color.grayDark));
            progressText.setTypeface(ResourcesCompat.getFont(context, R.font.lilitaoneregular));
            centerLayout.addView(progressText);

            // - PROGRESS BAR - //
            ProgressBar progressBar = new ProgressBar(context, null,
                    android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int)(8 * context.getResources().getDisplayMetrics().density));
            progressParams.topMargin = (int)(5 * context.getResources().getDisplayMetrics().density);
            progressParams.rightMargin = (int)(5 * context.getResources().getDisplayMetrics().density);
            progressBar.setLayoutParams(progressParams);
            progressBar.setMax(100);
            progressBar.setProgress(percent);
            progressBar.setProgressTintList(ColorStateList.valueOf(
                    context.getResources().getColor(R.color.javaFlame)));
            progressBar.setBackgroundTintList(ColorStateList.valueOf(
                    context.getResources().getColor(R.color.javaFlameLight)));
            centerLayout.addView(progressBar);

            // - ADD CENTER LAYOUT TO CAPSULE - //
            chapterCapsule.addView(centerLayout);

            // - RIGHT ACTION ICON - //
            ImageView actionIcon = new ImageView(context);
            LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                    (int)(32 * context.getResources().getDisplayMetrics().density),
                    (int)(32 * context.getResources().getDisplayMetrics().density));
            actionParams.setMarginStart((int)(16 * context.getResources().getDisplayMetrics().density));
            actionIcon.setLayoutParams(actionParams);
            actionIcon.setImageResource(R.drawable.ic_other_chapters);
            actionIcon.setContentDescription("Go to Chapter");

            chapterCapsule.addView(actionIcon);

            // - ONCLICK BEHAVIOR: SET CHAPTER AND RETURN TO MAIN - //
            actionIcon.setOnClickListener(v -> {
                MainActivity.Memory.setCurrentChapter(context, chapter.title);
                Bundle result = new Bundle();
                result.putString("chapter", chapter.title);
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish(); // Optional: close current instance

            });

            // - ADD CAPSULE TO CHAPTERS CONTAINER - //
            ChapterViewHolder holder = new ChapterViewHolder(
                    chapter.title, chapterIcon, progressText, progressBar
            );
            chapterViews.add(holder);
            chaptersContainer.addView(chapterCapsule);
        }
    }
    private static class ChapterViewHolder {
        public String chapterTitle;
        public ImageView icon;
        public TextView progressText;
        public ProgressBar progressBar;

        public ChapterViewHolder(String title, ImageView icon, TextView progressText, ProgressBar progressBar) {
            this.chapterTitle = title;
            this.icon = icon;
            this.progressText = progressText;
            this.progressBar = progressBar;
        }
    }

}
