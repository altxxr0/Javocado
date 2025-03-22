package com.gr6.javocado;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.animation.ValueAnimator;

public class SignupFragment extends Fragment {

    public SignupFragment() {
        // Required empty public constructor
    }
    private EditText usernameInput, passwordInput, emailInput;
    private TextView signupMessage, signupTitle;
    private ImageView logoImage;
    private Handler handler = new Handler();
    private Runnable resetLogoRunnable;
    private Runnable resetUIRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup, container, false);

        TextView loginRedirect = view.findViewById(R.id.loginRedirect);
        loginRedirect.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        logoImage = view.findViewById(R.id.logoImage);
        signupTitle = view.findViewById(R.id.signupTitle);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        signupMessage = view.findViewById(R.id.signupMessage);

        usernameInput.addTextChangedListener(inputWatcher);
        passwordInput.addTextChangedListener(inputWatcher);
        emailInput.addTextChangedListener(inputWatcher);

        Button signUpButton = view.findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(v -> goTologin());

        return view;
    }

    private void goTologin() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    private final TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            shrinkLogoAndHideTitle();
        }

        @Override
        public void afterTextChanged(Editable s) {
            resetUIWithDelay();
        }
    };

    private void shrinkLogoAndHideTitle() {
        int newSize = dpToPx(150); // Adjust based on your scaling needs
        resizeView(logoImage, newSize, newSize); // Shrink logo

        adjustTopMargin(logoImage, dpToPx(-100)); // Move up

        signupTitle.animate()
                .translationY(-signupTitle.getHeight()) // Move up by its height
                .alpha(0f) // Fade out
                .setDuration(200)
                .withEndAction(() -> signupTitle.setVisibility(View.GONE)) // Hide after animation
                .start();

        if (resetUIRunnable != null) {
            handler.removeCallbacks(resetUIRunnable);
        }
    }

    private void resetUIWithDelay() {
        resetUIRunnable = () -> {
            resizeView(logoImage, dpToPx(250), dpToPx(250)); // Restore original size
            adjustTopMargin(logoImage, dpToPx(0)); // Reset margin

            signupTitle.setVisibility(View.VISIBLE);
            signupTitle.setAlpha(0f);
            signupTitle.setTranslationY(-signupTitle.getHeight());

            signupTitle.animate()
                    .translationY(0) // Move back to original position
                    .alpha(1f) // Fade in
                    .setDuration(200)
                    .start();
        };
        handler.postDelayed(resetUIRunnable, 1000);
    }

    // Converts dp to pixels
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // Helper method to animate top margin change
    private void adjustTopMargin(View view, int newMarginPx) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int startMargin = params.topMargin;

        ValueAnimator animator = ValueAnimator.ofInt(startMargin, newMarginPx);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            params.topMargin = (int) animation.getAnimatedValue();
            view.setLayoutParams(params);
        });
        animator.start();
    }

    // Helper method to resize the view properly
    private void resizeView(View view, int newWidth, int newHeight) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        view.setLayoutParams(params);
    }

}
