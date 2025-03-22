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

public class LoginFragment extends Fragment {

    private EditText usernameInput, passwordInput;
    private TextView loginMessage, loginTitle;
    private ImageView logoImage;
    private Handler handler = new Handler();
    private Runnable resetLogoRunnable;
    private Runnable resetUIRunnable;

    public LoginFragment() {
        // Required empty public constructor
    }

    private void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Clear previous messages
        loginMessage.setVisibility(View.GONE);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showMessage("Please enter both username and password", true);
            return;
        }

        // Hardcoded credentials check
        if (username.equals("admin") && password.equals("1234")) {
            showMessage("Login successful!", false);

            // Delay transition slightly to allow message to appear
            loginMessage.postDelayed(() -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();
            }, 500); // 500ms delay
        } else {
            showMessage("Invalid username or password", true);
        }
    }


    private void goToSignUp() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new SignupFragment())
                .addToBackStack(null)
                .commit();
    }

    private void showMessage(String message, boolean isError) {
        loginMessage.setText(message);
        loginMessage.setVisibility(View.VISIBLE);
        loginMessage.setTextColor(isError ? 0xFFFF0000 : 0xFF4CAF50); // Red for errors, Green for success
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);

        logoImage = view.findViewById(R.id.logoImage);
        loginTitle = view.findViewById(R.id.loginTitle);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginMessage = view.findViewById(R.id.loginMessage);

        usernameInput.addTextChangedListener(inputWatcher);
        passwordInput.addTextChangedListener(inputWatcher);

        Button signUpButton = view.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> goToSignUp());

        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> loginUser());


        return view;
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

    private int dpToPx(int dpv) {
        return (int) (dpv * getResources().getDisplayMetrics().density);
    }

    private void shrinkLogoAndHideTitle() {
        int newSize = dpToPx(150); // Adjust based on your scaling needs
        resizeView(logoImage, newSize, newSize); // Shrink logo

        adjustTopMargin(logoImage, dpToPx(-100)); // Move up

        loginTitle.animate()
                .translationY(-loginTitle.getHeight()) // Move up by its height
                .alpha(0f) // Fade out
                .setDuration(200)
                .withEndAction(() -> loginTitle.setVisibility(View.GONE)) // Hide after animation
                .start();

        if (resetUIRunnable != null) {
            handler.removeCallbacks(resetUIRunnable);
        }
    }


    private void resetUIWithDelay() {
        resetUIRunnable = () -> {
            resizeView(logoImage, dpToPx(250), dpToPx(250)); // Restore original size
            adjustTopMargin(logoImage, dpToPx(0)); // Reset margin

            loginTitle.setVisibility(View.VISIBLE);
            loginTitle.setAlpha(0f);
            loginTitle.setTranslationY(-loginTitle.getHeight());

            loginTitle.animate()
                    .translationY(0) // Move back to original position
                    .alpha(1f) // Fade in
                    .setDuration(200)
                    .start();
        };
        handler.postDelayed(resetUIRunnable, 1000);
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
