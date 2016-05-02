package com.sample.picker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.picker.controller.BaseActivity;
import com.picker.utils.Utils;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Login screen that offers login via facebook .
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatButton mFacebookLogin;
    private AppCompatButton mFacebookLogout;
    private AppCompatButton mBrowse;
    private View mParent;

    private CallbackManager mFacebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolBar(getString(R.string.title_activity_login), true);
        getToolbar().setNavigationIcon(R.drawable.ic_close);
        initComponents();
        initListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                doFacebookLogin();
                break;
            case R.id.logout:
                LoginManager.getInstance().logOut();
                controlUI();
                break;
            case R.id.browse:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        controlUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initComponents() {
        mParent = findViewById(R.id.parent);
        mFacebookLogin = (AppCompatButton) findViewById(R.id.login);
        mFacebookLogout = (AppCompatButton) findViewById(R.id.logout);
        mBrowse = (AppCompatButton) findViewById(R.id.browse);

        mFacebookCallbackManager = CallbackManager.Factory.create();
    }

    private void initListeners() {
        mFacebookLogin.setOnClickListener(this);
        mFacebookLogout.setOnClickListener(this);
        mBrowse.setOnClickListener(this);
    }

    private void controlUI() {
        boolean status = Utils.isLoggedIn();
        mFacebookLogin.setVisibility(status ? View.GONE : View.VISIBLE);
        mFacebookLogout.setVisibility(status ? View.VISIBLE : View.GONE);
        mBrowse.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    /**
     * Can be used to login with Facebook
     */
    private void doFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(getResources().getStringArray(R.array.facebook_permissions)));
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Login success, now request for user details
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {

                                        if (response.getError() != null) {
                                            // failed to fetch user details
                                            Snackbar.make(mParent, response.getError().getErrorUserMessage(), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            /* Successfully retrieved user details
                                                   email,first_name,gender,birthday */
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "email,first_name,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Snackbar.make(mParent, exception.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

}

