package com.aircall.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aircall.app.Adapter.ReceiptNoShowAdapter;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Model.NoShowNotification.NoShowPaymentData;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReceiptNoShowActivity extends Activity {

    @Bind(R.id.tvName)
    TextView tvName;

    @Bind(R.id.tvEmail)
    TextView tvEmail;

    @Bind(R.id.tvTotalAmount)
    TextView tvTotalAmount;

    @Bind(R.id.tvDashboard)
    TextView tvDashboard;

    @Bind(R.id.rvReceipt)
    RecyclerView rvReceipt;

    private NoShowPaymentData receipt = new NoShowPaymentData();
    public GlobalClass globalClass;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private ReceiptNoShowAdapter receiptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_no_show);
        ButterKnife.bind(this);
        init();
        clickEvent();
    }

    private void init() {
        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        Intent intent = getIntent();
        String jsonReceipt = intent.getStringExtra("Receipt");
        Gson gson = new Gson();
        receipt = gson.fromJson(jsonReceipt, NoShowPaymentData.class);
        tvName.setText(receipt.FirstName + " " + receipt.LastName);
        tvEmail.setText(receipt.Email);
        tvTotalAmount.setText("$"+receipt.NoShowAmount);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvReceipt.setLayoutManager(llm);
        receiptAdapter = new ReceiptNoShowAdapter(this, receipt);
        rvReceipt.setAdapter(receiptAdapter);
    }

    private void clickEvent() {
        tvDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiptNoShowActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
