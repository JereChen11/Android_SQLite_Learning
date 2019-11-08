package com.jere.android_sqlite_learning;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author jere
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<BusinessCard> businessCardsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        businessCardsList = dataBaseHelper.getAllBusinessCards();
        mAdapter = new MyAdapter(this, businessCardsList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        ActionDialog actionDialog = new ActionDialog(MainActivity.this, position);
                        BusinessCard businessCard = businessCardsList.get(position);
                        actionDialog.createDialogAndShow(businessCard, new IGenerateBusinessCardListener() {
                            @Override
                            public void getBusinessCard(BusinessCard businessCard) {
                                if (businessCard != null) {
                                    businessCardsList.set(position, businessCard);
                                } else {
                                    businessCardsList.remove(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }));

        Button addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBusinessCardDialog myBusinessCardDialog =
                        new MyBusinessCardDialog(MainActivity.this, false);
                myBusinessCardDialog.createDialogAndShow(null, new IGenerateBusinessCardListener() {
                    @Override
                    public void getBusinessCard(BusinessCard businessCard) {
                        businessCardsList.add(businessCard);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
