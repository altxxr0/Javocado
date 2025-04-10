// Generated by view binder compiler. Do not edit!
package com.gr6.javocado.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.gr6.javocado.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class HomeBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ProgressBar currentProgress;

  @NonNull
  public final ImageView footer;

  @NonNull
  public final RelativeLayout home;

  @NonNull
  public final ImageView homeButton;

  @NonNull
  public final ImageView profileButton;

  private HomeBinding(@NonNull RelativeLayout rootView, @NonNull ProgressBar currentProgress,
      @NonNull ImageView footer, @NonNull RelativeLayout home, @NonNull ImageView homeButton,
      @NonNull ImageView profileButton) {
    this.rootView = rootView;
    this.currentProgress = currentProgress;
    this.footer = footer;
    this.home = home;
    this.homeButton = homeButton;
    this.profileButton = profileButton;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static HomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static HomeBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static HomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.current_progress;
      ProgressBar currentProgress = ViewBindings.findChildViewById(rootView, id);
      if (currentProgress == null) {
        break missingId;
      }

      id = R.id.footer;
      ImageView footer = ViewBindings.findChildViewById(rootView, id);
      if (footer == null) {
        break missingId;
      }

      RelativeLayout home = (RelativeLayout) rootView;

      id = R.id.home_button;
      ImageView homeButton = ViewBindings.findChildViewById(rootView, id);
      if (homeButton == null) {
        break missingId;
      }

      id = R.id.profile_button;
      ImageView profileButton = ViewBindings.findChildViewById(rootView, id);
      if (profileButton == null) {
        break missingId;
      }

      return new HomeBinding((RelativeLayout) rootView, currentProgress, footer, home, homeButton,
          profileButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
