package de.trilobytese.vocab.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.data.NavigationDataProvider;
import de.trilobytese.vocab.fragment.*;
import de.trilobytese.vocab.util.Utilities;

public class MainActivity extends BaseActivity implements NavigationFragment.NavigationDrawerCallbacks {

    private NavigationFragment mNavigationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation);
        mNavigationFragment.setUp(R.id.fragment_navigation, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        setNavigationDrawerWidth();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        boolean drawerOpen = mNavigationFragment.isDrawerOpen();

        for(int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(!drawerOpen);
        }

        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, boolean fromSavedInstanceState) {
        if (!fromSavedInstanceState) {
            NavigationDataProvider provider = mNavigationFragment.getDataProvider();
            NavigationDataProvider.Data data = provider.getItem(position);

            FragmentManager fragmentManager = getSupportFragmentManager();

            switch (data.getType()) {
                case NavigationDataProvider.ITEM_TYPE_CATEGORY:
                    long categoryId = data.getId();
                    FragmentTransaction ftCat = fragmentManager.beginTransaction();
                    ftCat.replace(R.id.container, DeckCategoryFragment.newInstance(categoryId), "deck");
                    ftCat.commit();
                    break;
                case NavigationDataProvider.ITEM_TYPE_STARRED:
                    FragmentTransaction ftStarred = fragmentManager.beginTransaction();
                    ftStarred.replace(R.id.container, DeckStarredFragment.newInstance(), "deck");
                    ftStarred.commit();
                    break;
            }
        }
    }

    @Override
    public void onNavigationDrawerItemClicked(int position) {
        NavigationDataProvider provider = mNavigationFragment.getDataProvider();
        NavigationDataProvider.Data data = provider.getItem(position);
        switch (data.getType()) {
            case NavigationDataProvider.ITEM_TYPE_HELP:
                Intent intentHelp = new Intent(this, HelpActivity.class);
                startActivity(intentHelp);
                break;
            case NavigationDataProvider.ITEM_TYPE_SETTINGS:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
        }
    }

    private void setNavigationDrawerWidth() {
        View view = mNavigationFragment.getView();
        if (view != null) {
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams)view.getLayoutParams();

            if (Utilities.isTablet()) {
                if (Utilities.isLandscape()) {
                    params.width = (int)Utilities.convertDpToPixels(320);
                } else {
                    params.width = (int)Utilities.convertDpToPixels(320);
                }
            } else {
                if (Utilities.isLandscape()) {
                    params.width = Utilities.getDisplayMetrics().widthPixels / 2;
                } else {
                    params.width = Utilities.getDisplayMetrics().widthPixels - (int)Utilities.convertDpToPixels(56);
                }
            }

            view.setLayoutParams(params);
        }
    }

}

