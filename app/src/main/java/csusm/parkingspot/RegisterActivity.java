package csusm.parkingspot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A registration screen that offers first time registration.
 */
public class RegisterActivity extends AppCompatActivity {
    private UserRegistrationTask mValidTask = null;

    // UI references.
    private EditText mIDView;
    private EditText mEmailView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPasswordView;
    private View mRegisterFormView;
    ProgressDialog progressDialog;
    ProgressDialog successDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //define Progress Dialog for validation
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Validating...");

        //define Progress Dialog for validation success
        successDialog = new ProgressDialog(RegisterActivity.this);
        successDialog.setIndeterminate(true);
        successDialog.setMessage("You have been registered");

        // Set up the registration form.
        mIDView = (EditText) findViewById(R.id.idInput);
        mIDView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mEmailView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mEmailView = (EditText) findViewById(R.id.emailInput);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mFirstNameView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mFirstNameView = (EditText) findViewById(R.id.firstNameInput);
        mFirstNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mLastNameView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mLastNameView = (EditText) findViewById(R.id.lastNameInput);
        mLastNameView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mPasswordView = (EditText) findViewById(R.id.passwordInput);
        mPasswordView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_GO) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.registerBtn);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptRegistration();
            }
        });

        Button mLoginButton = (Button) findViewById(R.id.loginTxtBtn);
        mLoginButton.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            }
                                        });

        mRegisterFormView = findViewById(R.id.register_form);
    }


    /**
     * Attempts to register the account specified by the registration form.
     * If there are form errors (invalid id, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void attemptRegistration() {
        if (mValidTask != null) {
            return;
        }

        // Reset errors.
        mIDView.setError(null);
        mEmailView.setError(null);
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String idString = mIDView.getText().toString();
        String email = mEmailView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid id .
        if (TextUtils.isEmpty(idString)) {
            mIDView.setError(getString(R.string.error_field_required));
            focusView = mIDView;
            cancel = true;
        } else if (!isIDValid(idString)) {
            mIDView.setError(getString(R.string.error_invalid_id));
            focusView = mIDView;
            cancel = true;
        }

        // Check for a valid email .
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid first name .
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        } else if (!isNameValid(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for a valid last name .
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        } else if (!isNameValid(lastName)) {
            mLastNameView.setError(getString(R.string.error_invalid_name));
            focusView = mLastNameView;
            cancel = true;
        }

        // Check for a valid password
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.show();
            mValidTask = new UserRegistrationTask(idString, email, firstName, lastName, password);
            mValidTask.execute((Void) null);
        }
    }

    private boolean isIDValid(String id) {

        return id.length() == 9 ;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace with better logic
        return email.contains("@");
    }

    private boolean isNameValid(String name) {

        return name.length() > 1 ;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace with better logic
        return password.length() > 4;
    }

    /**
     * Represents an asynchronous registration task used to validate
     * the user information.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final int mID;
        private final String mEmail;
        private final String mFirstName;
        private final String mLastName;
        private final String mPassword;
        private String mIDString;
        // if user already exists
        private int failureID;

        UserRegistrationTask(String idString, String email, String firstName, String lastName, String password) {
            mID = Integer.parseInt(idString);
            mEmail= email;
            mFirstName = firstName;
            mLastName = lastName;
            mPassword = password;
            //converting int back to string to remove leading zeros for the comparison
            mIDString = Integer.toString(mID);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            try {
                String linkID = "http://csusm-parkingspot.000webhostapp.com/getRegistrationID.php?studentid="+mIDString;
                String linkEmail = "http://csusm-parkingspot.000webhostapp.com/getRegistrationEmail.php?email="+mEmail;
                URL urlID = new URL(linkID);
                URL urlEmail = new URL(linkEmail);

                // connection for checking if id is already used
                conn = (HttpURLConnection) urlID.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLineID;
                inputLineID = in.readLine();
                if (inputLineID==null) {
                    inputLineID="";
                }

                in.close();
                conn.disconnect();

                // connection for checking if email is already used
                conn = (HttpURLConnection) urlEmail.openConnection();

                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLineEmail;
                inputLineEmail = in.readLine();
                if (inputLineEmail==null) {
                    inputLineEmail="";
                }

                in.close();


                if(inputLineID.equals(mIDString)) {
                    // 1 for id is used
                    failureID = 1;
                    return false;
                } else if (inputLineEmail.equals(mEmail)){
                    // 2 for email is used
                    failureID = 2;
                    return false;
                } else {
                    // nothing exists, all fine
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mValidTask = null;
            progressDialog.dismiss();
            if (success) {

                //show the success dialog

                successDialog.show();

                HttpURLConnection conn = null;
                try {
                    String link = "http://csusm-parkingspot.000webhostapp.com/addUser.php?studentid="+mIDString+"&email="+mEmail+"&firstname="+mFirstName+"&lastname="+mLastName+"&password="+mPassword;
                    URL url = new URL(link);

                    // connection for adding user to table
                    conn = (HttpURLConnection) url.openConnection();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    inputLine = in.readLine();

                    if (inputLine.equals("Success")) {
                        System.out.println("Successfully added user to database");
                    } else {
                        System.out.println("Error adding user to database");
                    }

                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }


                //log in the user
                //save student id into local android database to save the session
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("sessionID", mID);
                editor.commit();
                System.out.println("LoginActivity SessionID is "+settings.getInt("sessionID",-1));


                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                successDialog.dismiss();
                finish();

            } else {
                if (failureID==1) {
                    mIDView.setError(getString(R.string.error_id_used));
                    mIDView.requestFocus();
                } else if (failureID==2) {
                    mEmailView.setError(getString(R.string.error_email_used));
                    mEmailView.requestFocus();
                }
             }
        }

        @Override
        protected void onCancelled() {
            mValidTask = null;
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        finish();
    }
}

