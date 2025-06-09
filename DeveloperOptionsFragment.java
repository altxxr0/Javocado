package com.gr6.javocado;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DeveloperOptionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // - INFLATE LAYOUT - //
        View view = inflater.inflate(R.layout.fragment_developer_options, container, false);

        // - INITIALIZE COMPONENTS - //
        EditText seedInput = view.findViewById(R.id.seed_input);
        Button setSeedsButton = view.findViewById(R.id.set_seeds);
        TextView seedsCount = view.findViewById(R.id.seedsCount);
        Button resetProgressButton = view.findViewById(R.id.reset_progress);

        // - RESET BUTTON - //
        resetProgressButton.setOnClickListener(v -> {
            // - RESET SEEDS & PROGRESS - //
            MainActivity.Memory.setSeeds(requireContext(), 5);
            MainActivity.Memory.reset(requireContext());

            // - RESET - //
            seedsCount.setText("5");
            seedInput.setText("");
            Toast.makeText(requireContext(), "Progress reset.", Toast.LENGTH_SHORT).show();

            // - RESTART APP - //
            Intent intent = requireActivity().getPackageManager()
                    .getLaunchIntentForPackage(requireActivity().getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // - EXIT BUTTON - //
        ImageView exitdev = view.findViewById(R.id.exitdev);
        exitdev.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // - SET INITIAL SEEDS - //
        int currentSeeds = MainActivity.Memory.getSeeds(requireContext());
        if (currentSeeds >= 0) {
            seedsCount.setText(String.valueOf(currentSeeds));
        } else {
            seedsCount.setText("0");
        }

        // - "-1" FIX - //
        if (Integer.parseInt(seedsCount.getText().toString()) == -1){
            seedsCount.setText("0");
        }

        // - SET SEEDS BUTTON - //
        setSeedsButton.setOnClickListener(v -> {
            String input = seedInput.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    int seeds = Integer.parseInt(input);
                    if (seeds < 0) seeds = 0;
                    MainActivity.Memory.setSeeds(requireContext(), seeds);
                    seedsCount.setText(String.valueOf(seeds));
                } catch (NumberFormatException e) {
                    // - HANDLE INVALID INPUT - //
                    seedInput.setError("Please enter a valid number");
                }
            }
        });
        return view;
    }
}
