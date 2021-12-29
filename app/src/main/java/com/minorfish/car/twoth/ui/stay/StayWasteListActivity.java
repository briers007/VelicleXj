package com.minorfish.car.twoth.ui.stay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.tangjd.common.abs.JsonApiBase;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StayWasteListActivity extends XBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {

    @Bind(R.id.srl)
    SwipeRefreshLayout mSrl;

    private static final int size = 50;
    private int page = 0;

    @Bind(R.id.rvList)
    RvStay rvList;

  @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_staywaste_list);

        ButterKnife.bind(this);

        enableBackFinish();

        setToolbarTitle("垃圾入库");
      mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
              getDataOrRefresh();
          }
      });

//      rvList.addOnItemTouchListener(new OnItemClickListener() {
//          @Override
//          public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//          }
//      });
      rvList.setOnEmptyViewClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              getDataOrRefresh();
          }
      });

      getDataOrRefresh();
    }

    @Override
    public void onLoadMoreRequested() {
        mSrl.setEnabled(false);
        page++;
        Api.getSupplyList(size, page, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    rvList.onLoadMoreSuccess(((JSONObject) result.mData).optBoolean("hasNextPage"), StayBean.parse(result.mData));
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                showShortToast(error + "");
                rvList.onLoadMoreFail();
            }

            @Override
            public void onFinish(boolean withoutException) {
                mSrl.setEnabled(true);
            }
        });
    }


    private void getDataOrRefresh() {
        page = 1;
        rvList.showLoadingView();
        Api.getSupplyList(size, page, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Result result = Result.parse(jsonObject);

                if(result.isSuccess()) {
                    rvList.onGetDataSuccess(((JSONObject) result.mData).optBoolean("hasNextPage"), StayBean.parse(result.mData), StayWasteListActivity.this);
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String s) {
                rvList.onGetDataFail(s+"", true);
                showTipDialog(s);
            }

            @Override
            public void onFinish(boolean b) {
                mSrl.setRefreshing(false);
            }
        });
    }
}
