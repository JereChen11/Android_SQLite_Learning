package com.jere.android_sqlite_learning;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jere.android_sqlite_learning.customdialog.ActionDialog;
import com.jere.android_sqlite_learning.customdialog.MyBusinessCardDialog;
import com.jere.android_sqlite_learning.model.BusinessCard;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author jere
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<BusinessCard> businessCardsList = new ArrayList<>();
    private EditText inputSearchNameEt;
    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataBaseHelper = new DataBaseHelper(this);
        businessCardsList = dataBaseHelper.getAllBusinessCards();
        mAdapter = new MyRecyclerViewAdapter(businessCardsList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        ActionDialog actionDialog = new ActionDialog(MainActivity.this);
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
        addBtn.setOnClickListener(this);
        Button startSearchBtn = findViewById(R.id.start_search_btn);
        startSearchBtn.setOnClickListener(this);
        inputSearchNameEt = findViewById(R.id.search_input_name_et);
        inputSearchNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //handle user input enter action on inputSearchNameEt
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    startQueryDatabase();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                new MyBusinessCardDialog(MainActivity.this, OperationTypeEnum.INSERT,
                        new IGenerateBusinessCardListener() {
                            @Override
                            public void getBusinessCard(BusinessCard businessCard) {
                                businessCardsList.add(businessCard);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                break;
            case R.id.start_search_btn:
                //query database by the name input by user.
                startQueryDatabase();
                break;
            default:
                break;
        }
    }

    private void startQueryDatabase() {
        BusinessCard businessCard =
                dataBaseHelper.getBusinessCardQueryByName(inputSearchNameEt.getText().toString());
        if (businessCard == null) {
            Toast.makeText(MainActivity.this,
                    "The people not survive in your list",
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            new MyBusinessCardDialog(MainActivity.this, OperationTypeEnum.QUERY, businessCard);
            inputSearchNameEt.setText("");
        }
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}
