package com.gr6.javocado;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SignupFragment extends Fragment {

    private View signUpButton;

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
        signupTitle = view.findViewById(R.id.signUpTitle);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        signupMessage = view.findViewById(R.id.signupMessage);
        emailInput = view.findViewById(R.id.emailInput);
        signUpButton = view.findViewById(R.id.button_signup);


        usernameInput.addTextChangedListener(inputWatcher);
        passwordInput.addTextChangedListener(inputWatcher);
        emailInput.addTextChangedListener(inputWatcher);

        loginRedirect.setOnClickListener(v -> goTologin());

        signUpButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
                addUser(username, password, email);
                goTologin();
            } else {
                signupMessage.setText("Please fill in all fields!");
            }
        });
        return view;
    }

    private Document getXmlDocument() {
        try {
            File file = new File(getActivity().getFilesDir(), "data.xml");

            if (!file.exists()) {
                return createNewXmlDocument();
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document createNewXmlDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("users");
            document.appendChild(root);

            saveXmlFile(document);  // Save initial empty XML file
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void addUser(String username, String password, String email) {
        try {
            Document document = getXmlDocument();
            if (document == null) return;

            Element root = document.getDocumentElement();

            Element newUser = document.createElement("user");

            Element usernameElement = document.createElement("username");
            usernameElement.setTextContent(username);

            Element passwordElement = document.createElement("password");
            passwordElement.setTextContent(password);

            Element emailElement = document.createElement("email");
            emailElement.setTextContent(email);

            newUser.appendChild(usernameElement);
            newUser.appendChild(passwordElement);
            newUser.appendChild(emailElement);

            root.appendChild(newUser);

            saveXmlFile(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveXmlFile(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);

            File file = new File(getActivity().getFilesDir(), "data.xml");
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
