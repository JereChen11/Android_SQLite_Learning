package com.jere.android_sqlite_learning;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author jere
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BusinessCard> businessCardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.my_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        BusinessCard jereBs = generateBusinessCard(R.drawable.ic_launcher_background,
                "jere", "110110", "chine");
        businessCardList.add(jereBs);
        BusinessCard xiaoMingBs = generateBusinessCard(R.drawable.ic_launcher_background,
                "小明", "1234567", "chine");
        businessCardList.add(xiaoMingBs);
        BusinessCard xiaoXiaoBs = generateBusinessCard(R.drawable.ic_launcher_background,
                "xiaoxiao", "110110", "chine");
        businessCardList.add(xiaoXiaoBs);

        BusinessCard jereBs1 = generateBusinessCard(R.drawable.ic_launcher_background,
                "jere", "110110", "chine");
        businessCardList.add(jereBs1);
        BusinessCard xiaoMingBs1 = generateBusinessCard(R.drawable.ic_launcher_background,
                "小明", "1234567", "chine");
        businessCardList.add(xiaoMingBs1);
        BusinessCard xiaoXiaoBs1 = generateBusinessCard(R.drawable.ic_launcher_background,
                "xiaoxiao", "110110", "chine");
        businessCardList.add(xiaoXiaoBs1);

        mAdapter = new MyAdapter(businessCardList);
        recyclerView.setAdapter(mAdapter);

        Button addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBusinessCard();
            }
        });
    }

    private void addBusinessCard() {
        View view = LayoutInflater.from(this).inflate(R.layout.add_business_card_dialog, null);
        final EditText nameEt = view.findViewById(R.id.name_et);
        final EditText telephoneEt = view.findViewById(R.id.telephone_et);
        final EditText addressEt = view.findViewById(R.id.address_et);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Add BusinessCard")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        BusinessCard businessCard = new BusinessCard();
                        businessCard.setName(nameEt.getText().toString());
                        businessCard.setTelephont(telephoneEt.getText().toString());
                        businessCard.setAddress(addressEt.getText().toString());
                        businessCardList.add(businessCard);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private BusinessCard generateBusinessCard(int portrait, String name, String telephone, String address) {
        BusinessCard businessCard = new BusinessCard();
        businessCard.setPortrait(portrait);
        businessCard.setName(name);
        businessCard.setTelephont(telephone);
        businessCard.setAddress(address);
        return businessCard;
    }
}
