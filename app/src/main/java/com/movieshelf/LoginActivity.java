package com.movieshelf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.movieshelf.network.DataRetriever;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, DataRetriever.DataListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "temp@gmail.com:temp0", "temp2@gmail.com:temp0"
    };

    static enum LOGIN_TYPE {
        GOOGLE,
        FACEBOOK,
        NORMAL
    }

    private AlertDialog mAlertDialog;

    private static final int RC_SIGN_IN = 999;
    private static final String TAG = "LoginActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton signInButton;
    private CallbackManager callbackManager;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    mUser = new User();
                    mUser.setLoginType(LOGIN_TYPE.NORMAL.toString());
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "user_friends", "email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("facebook login success ");
                System.out.println("fb access token :" + loginResult.getAccessToken().getToken().length());
                System.out.println("fb access user ID :" + loginResult.getAccessToken().getUserId());
                System.out.println("fb access permission :" + loginResult.getAccessToken().getPermissions());

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        hideProgressDialog();
                        mUser = new User();
                        System.out.println("new graph request completed");
                        System.out.println("gsph json : " + response.toString());
                        try {
                            mUser.setFbId(object.getString("id").toString());
                            mUser.setFbName(object.getString("name").toString());
                            mUser.setFbEmailId(object.getString("email").toString());
                            mUser.setLoginType(LOGIN_TYPE.FACEBOOK.toString());
                            attemptLogin();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                Bundle parametres = new Bundle();
                parametres.putString("fields", "id,name,email");
                graphRequest.setParameters(parametres);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("facebook login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("facebook login error : " + error.toString());
                showAlertDialog("Facebook login error");
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .requestIdToken(getString(R.string.server_client_id))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_AUTO);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void populateAutoComplete() {

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     *
     * @paramnormal
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        if (mUser.getLoginType().equals(LOGIN_TYPE.NORMAL.toString())) {
            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);

            // Store values at the time of the login attempt.
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgressDialog();
                if (mUser == null)
                    mUser = new User();
                mUser.setId(email);
                mUser.setName("User");
                mUser.setPassword(password);
                mUser.setLoginType(LOGIN_TYPE.NORMAL.toString());

                mAuthTask = new UserLoginTask(mUser);
                mAuthTask.execute((Void) null);
            }
        } else /*if (mUser.getLoginType().equals(LOGIN_TYPE.GOOGLE.toString())) {

            showProgressDialog();
            mAuthTask = new UserLoginTask(mUser);
            mAuthTask.execute((Void) null);

        } else if (mUser.getLoginType().equals(LOGIN_TYPE.FACEBOOK.toString()))*/ {
            showProgressDialog();
            mAuthTask = new UserLoginTask(mUser);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "google connection Failed");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            //this will call loginbutton callback in on create method..
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        hideProgressDialog();
        StringBuffer stringBuffer = new StringBuffer();
        if (result.isSuccess()) {
            mUser = new User();
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.i(TAG, "Acc name : " + acct.getDisplayName());
            Log.i(TAG, "Acc email : " + acct.getEmail());
            Log.i(TAG, "Acc id : " + acct.getId());
            Log.i(TAG, "Acc name : " + acct.getPhotoUrl());
            Log.i(TAG, "Acc scopes : " + acct.getGrantedScopes());
            stringBuffer.append(acct.getIdToken());
            Log.i(TAG, "token in string buffer : " + stringBuffer.toString());
            Log.i(TAG, "size of token in string buffer : " + stringBuffer.toString().length());

            if (acct.getEmail() != null && isEmailValid(acct.getEmail())) {
                //Email id is present and valid
                mUser.setLoginType(LOGIN_TYPE.GOOGLE.toString());
                mUser.setGpEmailId(acct.getEmail());
                mUser.setGpName(acct.getDisplayName());
                mUser.setGpId(acct.getId());
                mUser.setGpProfileImage(acct.getPhotoUrl().toString());
                attemptLogin();
            } else {
                showAlertDialog("Google Login error: email not found");
            }

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void showAlertDialog(String msg) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this).create();
            mAlertDialog.setTitle("Error");
            mAlertDialog.setMessage(msg);
            mAlertDialog.setCancelable(false);
            mAlertDialog.setIcon(android.R.drawable.stat_notify_error);
            mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "error dialog cancelled");
                    mAlertDialog.hide();
                }
            });
        }
        mAlertDialog.show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setTitle("Fetching Data");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private User user;
        private String mEmail;
        private final String mPassword;
        private DataRetriever retriever;

        UserLoginTask(User user) {
            this.user = user;
            if (user.getLoginType().equals(LOGIN_TYPE.NORMAL.toString()))
                mEmail = user.getId();

            mPassword = user.getPassword();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            retriever = new DataRetriever(LoginActivity.this, Constants.User_api + Constants.User);
            retriever.makeRequest();
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here, open register user activity
            System.out.println("Please register new account");
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            hideProgressDialog();

            if (success) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            hideProgressDialog();
        }
    }

    @Override
    public void requestStart() {
        Log.i(TAG, "starting network request");
    }

    @Override
    public void dataReceived(JSONObject jsonObject) {
        Log.i(TAG, "data recieved from network : " + jsonObject.toString());
    }

    @Override
    public void imageReceived(ImageLoader.ImageContainer imageContainer) {
        Log.i(TAG, "image recieved from url: " + imageContainer.getRequestUrl());
    }

    @Override
    public void error(String error) {
        Log.i(TAG, "error in getting data");
    }

}

