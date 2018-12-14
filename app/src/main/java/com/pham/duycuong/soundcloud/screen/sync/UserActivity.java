package com.pham.duycuong.soundcloud.screen.sync;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.pham.duycuong.soundcloud.R;
import com.pham.duycuong.soundcloud.util.MySharedPreferences;

public class UserActivity extends AppCompatActivity {
    private TextView mTextViewUserName;
    private TextView mTextViewUserEmail;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private MySharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml(
                "<font color='#000000'>" + getString(R.string.title_user_info) + " </font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_left);
        upArrow.setColorFilter(getResources().getColor(R.color.color_black),
                PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        mTextViewUserName = findViewById(R.id.textUserName);
        mTextViewUserEmail = findViewById(R.id.textUserEmail);

        mSharedPreferences = new MySharedPreferences(UserActivity.this);
        String userName = mSharedPreferences.get(MySharedPreferences.USER_NAME, String.class);
        String userEmail = mSharedPreferences.get(MySharedPreferences.USER_EMAIL, String.class);

        mTextViewUserName.setText(getString(R.string.title_user_name) + ": " + userName);
        mTextViewUserEmail.setText(getString(R.string.title_user_email) + ": " + userEmail);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
                        getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClickButtonSignout(View view) {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        mSharedPreferences.put(MySharedPreferences.LOGGED_IN, false);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
