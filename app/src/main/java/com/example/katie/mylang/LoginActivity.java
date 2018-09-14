package com.example.katie.mylang;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * LoginActivity.java
 * Purpose: activity dealing with logging in existing users and registering new users using firebase
 *
 * @author katie
 * @version 1.0 4/10/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOGINTAG = "LOGIN";
    private static final String SIGNUPTAG = "SIGNUP";
    private static final String TAG = "AUTH";

    private static final int MAX_LENGTH = 15;
    private static final int MIN_LENGTH = 3;

    EditText emailText;
    EditText passwordText;
    Button loginButton;
    TextView createAccount;
    TextView title;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Database database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = new Database();

        //find and assign views
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        createAccount = (TextView) findViewById(R.id.link_createAccount);
        title = (TextView) findViewById(R.id.title);

        //set typefaces
        Typeface font = Typeface.createFromAsset(getAssets(), "bebas_neue/TTF's/BebasNeue Regular.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "roboto/Roboto-Regular.ttf");
        createAccount.setTypeface(font);
        loginButton.setTypeface(font);
        title.setTypeface(font);
        emailText.setTypeface(font2);
        passwordText.setTypeface(font2);

        //set listeners to reference custom onClick at bottom
        loginButton.setOnClickListener(this);
        createAccount.setOnClickListener(this);

        //set up firebase and assign an authstate listener to handle authenticated users views
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // User is signed in
                    //call dictionary activity
                    Intent intent = new Intent(LoginActivity.this, DictionaryActivity.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * adds authstate listener on start
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    /**
     * removes authstate listener on stop
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * sign up a user to the app given a username and passwords. validates a user, creates an auth
     * account for them and adds a user object in the user database.
     *
     * @param email - a string email
     * @param password  - a string password
     */
    public void signup(String email, String password) {
       final User user = new User(email, password);
        if (!validate()) {
            Toast.makeText(getBaseContext(), getString(R.string.signupFailed), Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "ADDING_USER" + user.getEmail());
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(SIGNUPTAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        database.addUserToFirebase(user);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    /**
     * logs a user in with an email and password using firebase auth.
     *
     * @param email - a string email
     * @param password - a stirng password
     */
    public void login(String email, String password) {
        if (!validate()) {
            Toast.makeText(getBaseContext(), getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
            return;
        }
        //use firebase sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOGINTAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOGINTAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * validating an email and password.. not totally relevant since firebase has its own validation
     * but this is more specific for password lengths, firebase handles "strength" of passwords
     *
     * @return a boolean if app is valid
     */
    public boolean validate() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        //email validationg
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getString(R.string.invalidEmail));
            return false;
        }
        //password validation
        if (password.isEmpty() || password.length() > MAX_LENGTH || password.length() < MIN_LENGTH) {
            passwordText.setError(getString(R.string.passwordError));
            return false;
        }
        return true;
    }

    /**
     * Code modified from function onClick obtained from:
     * https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/
     * firebase/quickstart/auth/EmailPasswordActivity.java
    */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.link_createAccount) {
            signup(emailText.getText().toString(), passwordText.getText().toString());
        } else if (i == R.id.btn_login) {
            login(emailText.getText().toString(), passwordText.getText().toString());
        }
    }

    @Override
    public void onBackPressed() {

    }
}
