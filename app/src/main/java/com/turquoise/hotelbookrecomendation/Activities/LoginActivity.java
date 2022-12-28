package com.turquoise.hotelbookrecomendation.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.turquoise.hotelbookrecomendation.R;


public class LoginActivity extends AppCompatActivity {

    private final int REQ_ONE_TAP = 12345;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private SignInClient oneTapClient;
    private boolean isLegacyCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // On click listener for `Sign in with Google` button
        findViewById(R.id.google).setOnClickListener(v -> legacySignUpWithGoogle());

        // On click listener for `Sign in with Facebook` button
        findViewById(R.id.facebook);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            startActivity(new Intent(this, MainActivity.class).putExtra("Credential", account));
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    private void legacySignUpWithGoogle() {
        System.out.println("legacySignUpWithGoogle called");
        this.isLegacyCalled = true;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_ONE_TAP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQ_ONE_TAP) {
            return;
        }

        if (!this.isLegacyCalled) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);

                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d(this.getClass().getName(), "Got ID token: " + idToken);
                }

                String username = credential.getId();
                if (!username.equals("")) {
                    Log.d(this.getClass().getName(), "Got name: " + username);
                }

                String password = credential.getPassword();
                if (password != null) {
                    Log.d(this.getClass().getName(), "Got password " + password);
                }

                startActivity(new Intent(this, MainActivity.class).putExtra("Credential", credential));
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED: {
                        Log.d(this.getClass().getName(), "One-tap dialog was closed.");
                        // Don't re-prompt the user.
                        break;
                    }

                    case CommonStatusCodes.NETWORK_ERROR: {
                        Log.d(this.getClass().getName(), "One-tap encountered a network error.");
                        // Try again or just ignore.
                        break;
                    }

                    default: {
                        Log.d(this.getClass().getName(), "Couldn't get credential from result." + e.getLocalizedMessage());
                    }
                }
            }
        } else {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Signed in successfully, show authenticated UI.
                startActivity(new Intent(this, MainActivity.class).putExtra("Credential", account));
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(this.getClass().getName(), "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }
}
