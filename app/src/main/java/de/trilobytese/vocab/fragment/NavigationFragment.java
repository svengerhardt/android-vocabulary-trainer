package de.trilobytese.vocab.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.adapter.NavigationAdapter;
import de.trilobytese.vocab.data.NavigationDataProvider;

public class NavigationFragment extends Fragment implements NavigationAdapter.OnItemClickListener {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position, boolean fromSavedInstanceState);
        void onNavigationDrawerItemClicked(int position);
    }

    private NavigationDrawerCallbacks mCallbacks;

    private NavigationAdapter mAdapter;
    private NavigationDataProvider mDataProvider;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;

    private int mPosition;
    private boolean mFromSavedInstanceState;
    private int mClickType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_navigation, container, false);
        mDrawerListView = (RecyclerView) rootView.findViewById(R.id.list_view);
        mDrawerListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDataProvider = new NavigationDataProvider();
        mAdapter = new NavigationAdapter(mDataProvider, this);
        mDrawerListView.setAdapter(mAdapter);
        mAdapter.setItemChecked(mCurrentSelectedPosition);

        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedPosition, savedInstanceState != null);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        NavigationDataProvider.Data data = mDataProvider.getItem(position);
        switch (data.getType()) {
            case NavigationDataProvider.ITEM_TYPE_CATEGORY:
                selectItem(position, false);
                break;
            case NavigationDataProvider.ITEM_TYPE_STARRED:
                selectItem(position, false);
                break;
            default:
                clickItem(position);
                break;
        }
    }

    public NavigationDataProvider getDataProvider() {
        return mDataProvider;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
                // disable drawer icon animation
                super.onDrawerSlide(drawerView, 0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
                if (mCallbacks != null) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallbacks != null) {
                                switch (mClickType) {
                                    case 1:
                                        mCallbacks.onNavigationDrawerItemSelected(mPosition, mFromSavedInstanceState);
                                        mClickType = 0;
                                        break;
                                    case 2:
                                        mCallbacks.onNavigationDrawerItemClicked(mPosition);
                                        mClickType = 0;
                                        break;
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // disable drawer icon animation
                super.onDrawerSlide(drawerView, 0);
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(final int position, final boolean fromSavedInstanceState) {

        mCurrentSelectedPosition = position;
        mFromSavedInstanceState = fromSavedInstanceState;
        mPosition = position;
        mClickType = 1;

        if (mDrawerListView != null) {
            mAdapter.setItemChecked(position);
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    private void clickItem(final int position) {

        mPosition = position;
        mClickType = 2;

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
