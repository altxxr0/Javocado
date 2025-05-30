// Generated by view binder compiler. Do not edit!
package com.gr6.javocado.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.gr6.javocado.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class SignupBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonSignup;

  @NonNull
  public final EditText emailInput;

  @NonNull
  public final TextView loginRedirect;

  @NonNull
  public final ImageView logoImage;

  @NonNull
  public final EditText passwordInput;

  @NonNull
  public final TextView signUpTitle;

  @NonNull
  public final TextView signupMessage;

  @NonNull
  public final EditText usernameInput;

  private SignupBinding(@NonNull LinearLayout rootView, @NonNull Button buttonSignup,
      @NonNull EditText emailInput, @NonNull TextView loginRedirect, @NonNull ImageView logoImage,
      @NonNull EditText passwordInput, @NonNull TextView signUpTitle,
      @NonNull TextView signupMessage, @NonNull EditText usernameInput) {
    this.rootView = rootView;
    this.buttonSignup = buttonSignup;
    this.emailInput = emailInput;
    this.loginRedirect = loginRedirect;
    this.logoImage = logoImage;
    this.passwordInput = passwordInput;
    this.signUpTitle = signUpTitle;
    this.signupMessage = signupMessage;
    this.usernameInput = usernameInput;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static SignupBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static SignupBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.signup, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static SignupBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_signup;
      Button buttonSignup = ViewBindings.findChildViewById(rootView, id);
      if (buttonSignup == null) {
        break missingId;
      }

      id = R.id.emailInput;
      EditText emailInput = ViewBindings.findChildViewById(rootView, id);
      if (emailInput == null) {
        break missingId;
      }

      id = R.id.loginRedirect;
      TextView loginRedirect = ViewBindings.findChildViewById(rootView, id);
      if (loginRedirect == null) {
        break missingId;
      }

      id = R.id.logoImage;
      ImageView logoImage = ViewBindings.findChildViewById(rootView, id);
      if (logoImage == null) {
        break missingId;
      }

      id = R.id.passwordInput;
      EditText passwordInput = ViewBindings.findChildViewById(rootView, id);
      if (passwordInput == null) {
        break missingId;
      }

      id = R.id.signUpTitle;
      TextView signUpTitle = ViewBindings.findChildViewById(rootView, id);
      if (signUpTitle == null) {
        break missingId;
      }

      id = R.id.signupMessage;
      TextView signupMessage = ViewBindings.findChildViewById(rootView, id);
      if (signupMessage == null) {
        break missingId;
      }

      id = R.id.usernameInput;
      EditText usernameInput = ViewBindings.findChildViewById(rootView, id);
      if (usernameInput == null) {
        break missingId;
      }

      return new SignupBinding((LinearLayout) rootView, buttonSignup, emailInput, loginRedirect,
          logoImage, passwordInput, signUpTitle, signupMessage, usernameInput);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
