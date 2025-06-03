package com.gr6.javocado;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class OutOfSeedsFragment extends Fragment {

    // - BACK TO MENU BUTTON INSTANCE - //
    private Button back_to_menu;

    // - STATIC FACTORY METHOD TO CREATE A NEW INSTANCE OF OutOfSeedsFragment - //
    public static OutOfSeedsFragment newInstance() {
        return new OutOfSeedsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // - INFLATE THE LAYOUT FOR THIS FRAGMENT - //
        View view = inflater.inflate(R.layout.fragment_out_of_seeds, container, false);

        // - GET REFERENCE TO THE SEEDS COUNT TEXTVIEW - //
        TextView seedsCount = view.findViewById(R.id.seedsCount);

        // - RETRIEVE CURRENT SEEDS COUNT FROM MEMORY - //
        int seeds = MainActivity.Memory.getSeeds(requireContext());

        // - ENSURE SEEDS COUNT IS NOT NEGATIVE; RESET TO 0 IF IT IS - //
        if (seeds < 0) {
            MainActivity.Memory.setSeeds(requireContext(), 0);
            seeds = 0;
        }

        // - UPDATE SEEDS COUNT TEXTVIEW TO DISPLAY CURRENT SEEDS - //
        seedsCount.setText(String.valueOf(seeds));

        // - SETUP EXIT LEVEL IMAGEVIEW AND CLICK HANDLER TO RETURN TO MAIN ACTIVITY - //
        ImageView exitlevel = view.findViewById(R.id.exitlevel);
        exitlevel.setOnClickListener(V -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // - INITIALIZE BACK TO MENU BUTTON AND SET CLICK HANDLER TO RETURN TO MAIN ACTIVITY - //
        back_to_menu = view.findViewById(R.id.back_to_menu);
        back_to_menu.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // - RETURN THE INFLATED VIEW - //
        return view;
    }
}
