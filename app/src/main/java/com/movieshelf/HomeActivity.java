package com.movieshelf;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.movieshelf.network.DataRetriever;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements DataRetriever.DataListener, NavigationView.OnNavigationItemSelectedListener {

    User mUser;
    CircleImageView circleImageView;

    private String mUsername;
    private String mUserMailId;
    private String mProfileImgUrl;
    private TextView mtextUsername;
    private TextView mTextUserMailId;
    private CardContainer mCardContainer;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View handle = navigationView.getHeaderView(0);
        circleImageView = (CircleImageView) handle.findViewById(R.id.imageView);
        mtextUsername = (TextView) handle.findViewById(R.id.username);
        mTextUserMailId = (TextView) handle.findViewById(R.id.userEmailId);

        mUser = (User) getIntent().getExtras().get("user");
        if (mUser != null)
            fillData();

        mCardContainer = (CardContainer) findViewById(R.id.mainCardContainer);
        mCardContainer.setOrientation(Orientations.Orientation.Ordered);

        CardModel card = new CardModel("Title1", "Description goes here", getDrawable(R.drawable.picture1));

        card.setOnClickListener(new CardModel.OnClickListener() {
            @Override
            public void OnClickListener() {
                Log.i("Swappable Cards", "I am pressing the card");
            }
        });

        card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
            @Override
            public void onLike() {
                Log.d("Swappable Card", "I liked it");
            }

            @Override
            public void onDislike() {
                Log.d("Swappable Card", "I did not liked it");
            }
        });

        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);
        adapter.add(new CardModel("Title1", "Description goes here", getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title2", "Description goes here", getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title3", "Description goes here", getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title4", "Description goes here", getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title5", "Description goes here", getDrawable(R.drawable.picture1)));
        mCardContainer.setAdapter(adapter);

    }

    private void fillData() {
        if (mUser.getLoginType().equals(LoginActivity.LOGIN_TYPE.GOOGLE.toString())) {
            mtextUsername.setText(mUser.getGpName());
            mTextUserMailId.setText(mUser.getGpEmailId());
        } else if (mUser.getLoginType().equals(LoginActivity.LOGIN_TYPE.FACEBOOK.toString())) {
            mtextUsername.setText(mUser.getFbName());
            mTextUserMailId.setText(mUser.getFbEmailId());
        } else if (mUser.getLoginType().equals(LoginActivity.LOGIN_TYPE.FACEBOOK.toString())) {
            mtextUsername.setText(mUser.getName());
            mTextUserMailId.setText(mUser.getId());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void requestStart() {

    }

    @Override
    public void dataReceived(JSONObject jsonObject) {

    }

    @Override
    public void imageReceived(ImageLoader.ImageContainer imageContainer) {

    }

    @Override
    public void error(String error) {

    }
}
