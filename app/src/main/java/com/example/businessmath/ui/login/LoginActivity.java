package com.example.businessmath.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.businessmath.MainActivity;
import com.example.businessmath.R;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import  com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final  String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private LoginViewModel loginViewModel;

    int AUTHUI_REQUEST_CODE = 10001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            this.finish();
        }
    }

    public void handleLoginRegister(View view) {
        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                .setLogo(R.drawable.common_google_signin_btn_icon_dark)
                .build();
        startActivityForResult(intent, AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHUI_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // we have signed in the iser or we have a new user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: "+ user.getEmail());
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    Toast.makeText(this, "Welcome new User", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Welcome back again", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                // Sign in failed
                IdpResponse  response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    Log.d(TAG, "onActivityResult: the user has canceled the sign in request");
                } else {
                    Log.d(TAG, "onActivityResult: ", response.getError());
                }
            }

        }
    }


    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}