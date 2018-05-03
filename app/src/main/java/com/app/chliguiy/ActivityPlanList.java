package com.app.chliguiy;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.chliguiy.adapter.AdapterClient;
import com.app.chliguiy.connection.API;
import com.app.chliguiy.connection.RestAdapter;
import com.app.chliguiy.connection.callbacks.CallbackClient;
import com.app.chliguiy.connection.callbacks.CallbackPlan;
import com.app.chliguiy.data.Constant;
import com.app.chliguiy.data.SharedPref;
import com.app.chliguiy.model.Client;
import com.app.chliguiy.model.Plan;
import com.app.chliguiy.utils.NetworkCheck;
import com.app.chliguiy.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPlanList extends AppCompatActivity {

    private View parent_view;
    private ListView recyclerView;
    private AdapterClient mAdapter;
    private SwipeRefreshLayout swipe_refresh;
    private Call<CallbackPlan> callbackCall = null;
    private SharedPref sharedPref;
    static List<HashMap<String, String>> list_Client = new ArrayList<HashMap<String, String>>();
    private int post_total = 0;
    private int failed_page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        parent_view = findViewById(android.R.id.content);

        sharedPref = new SharedPref(this);

        initToolbar();
        iniComponent();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.title_activity_plan);
        Tools.systemBarLolipop(this);
    }

    public void iniComponent() {

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (ListView) findViewById(R.id.listclient);
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set data and list adapterlistclient
        //mAdapter = new  AdapterClient(this, recyclerView, new ArrayList<Client>());
        //  recyclerView.setAdapter(mAdapter);
        //Log.i("Set adapter", "");
        ///recyclerView.setAdapter(adapt);

        // on item list clicked
       /* mAdapter.setOnItemClickListener(new AdapterClient.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Client obj, int position) {
                ActivityNewsInfoDetails.navigate(ActivityClient.this, obj.id, false);
            }

        });*/

        // detect when scroll reach bottom
     /*   adapt.setOnLoadMoreListener(new AdapterClient.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (post_total > mAdapter.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    mAdapter.setLoaded();
                }
            }
        });*/

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                //     mAdapter.resetListData();
///                requestAction(1);
                swipeProgress(false);

            }
        });

        requestAction(1);
    }

    private void displayApiResult(final List<Plan> items) {

        SimpleAdapter adapt = new SimpleAdapter (getApplicationContext(), list_Client, R.layout.item_client,
                new String[] {"date","client", "commande","contact","phone","remarque"}, new int[] {R.id.date, R.id.client, R.id.commande,R.id.contact,R.id.phone,R.id.remarque});//R.id.txt_four
        //Log.i("Set adapter", "");
        LayoutInflater factory = LayoutInflater.from(this);
        View myView = factory.inflate(R.layout.item_plan_header, null);

        recyclerView.addHeaderView(myView);
        recyclerView.setAdapter(adapt);
        swipeProgress(false);
        if (items.size() == 0) {
            showNoItemView(true);
        }
    }

    private void requestListNewsInfo(final int page_no) {
        list_Client.clear();
        API api = RestAdapter.createAPI();
        String id=sharedPref.getIdUser();
        if(id==null){id=null;}
        callbackCall = api.getlistPlan(page_no, Constant.NEWS_PER_REQUEST, null,id);
        callbackCall.enqueue(new Callback<CallbackPlan>() {
            @Override
            public void onResponse(Call<CallbackPlan> call, Response<CallbackPlan> response) {
                CallbackPlan resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    post_total = resp.count_total;


                    for(int i=0;i<post_total;i++){
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("client",resp.plan.get(i).client);
                        map.put("commande",resp.plan.get(i).commande);
                        map.put("contact",resp.plan.get(i).contact);
                        map.put("phone",resp.plan.get(i).phone);
                        map.put("date",resp.plan.get(i).date);
                        map.put("remarque",resp.plan.get(i).remarque);


                        list_Client.add(map);
                    }

                    displayApiResult(resp.plan);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<CallbackPlan> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        // mAdapter.setLoaded();
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            // mAdapter.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                requestListNewsInfo(page_no);
            }
        }, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = (View) findViewById(R.id.lyt_no_item);
        ((TextView) findViewById(R.id.no_item_message)).setText("no_post");
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
