package com.example.musiqueue.Activities.users_only;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musiqueue.HelperClasses.BackgroundWorker;
import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.HelperClasses.HubsListItem;
import com.example.musiqueue.HelperClasses.SearchHubResponse;
import com.example.musiqueue.HelperClasses.adapters.HubsListAdapter;
import com.example.musiqueue.R;
import com.google.gson.Gson;

import java.util.Vector;

public class RecentHubsFragment extends Fragment {
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    //protected RadioButton mLinearLayoutRadioButton;
    //protected RadioButton mGridLayoutRadioButton;

    protected RecyclerView mRecyclerView;
    protected HubsListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset;
    HubsListAdapter.OnItemClickListener callback;
    Vector<HubsListItem> hubs;
    SearchHubResponse r;

    // newInstance constructor for creating fragment with arguments
    public static RecentHubsFragment newInstance(int page, String title) {
        RecentHubsFragment fragmentFirst = new RecentHubsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.

        callback = new HubsListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(HubsListItem hub) {
                selectHub(hub);
            }
        };
        hubs = new Vector<>();

    }

    protected void selectHub(HubsListItem hub) {
        if(hub.getHub_pin_required()) {
            final Intent i = new Intent(getActivity(), JoinHub.class);
            i.putExtra("hubName", hub.getHub_name());
            startActivity(i);
        }else{
            final Intent i = new Intent(getActivity(), ConnectToHubActivity.class);
            i.putExtra("hubName", hub.getHub_name());
            i.putExtra("hubPin","");
            startActivity(i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hubs_fragment, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.hubs_list);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        r = new SearchHubResponse();
        r.result = new Vector<>();
        mAdapter = new HubsListAdapter(getActivity(), r.result, callback);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Gson gson = new Gson();
                r = gson.fromJson(result, SearchHubResponse.class);

                setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
                mAdapter = new HubsListAdapter(getActivity(), r.result, callback);
                mRecyclerView.setLayoutManager(mLayoutManager);
                // Set CustomAdapter as the adapter for RecyclerView.
                mRecyclerView.setAdapter(mAdapter);            }
        });

        //String userId = getArguments().getString("userId");
        String userId = HubSingleton.getInstance().getUserID();

        backgroundWorker.execute("recentHubs", userId);
        /*mLinearLayoutRadioButton = (RadioButton) rootView.findViewById(R.id.linear_layout_rb);
        mLinearLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
            }
        });

        mGridLayoutRadioButton = (RadioButton) rootView.findViewById(R.id.grid_layout_rb);
        mGridLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
            }
        });*/

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }
}
