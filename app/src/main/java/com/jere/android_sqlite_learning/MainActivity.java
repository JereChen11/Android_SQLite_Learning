package com.jere.android_sqlite_learning;

import android.app.Activity;
import android.os.AsyncTask;
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

import java.lang.ref.WeakReference;
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
        GetAllBusinessCardsAsyncTask getAllBusinessCardsAsyncTask = new GetAllBusinessCardsAsyncTask(this);
        getAllBusinessCardsAsyncTask.execute();
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
        QueryByBusinessCardNameAsyncTask queryByBusinessCardNameAsyncTask = new QueryByBusinessCardNameAsyncTask(this);
        queryByBusinessCardNameAsyncTask.execute(inputSearchNameEt.getText().toString());
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBaseHelper.close();
    }

    private static class GetAllBusinessCardsAsyncTask extends AsyncTask<Void, Void, ArrayList<BusinessCard>> {

        private WeakReference<MainActivity> activityWeakReference;
        private RecyclerView recyclerView;

        GetAllBusinessCardsAsyncTask(MainActivity context, RecyclerView recyclerView) {
            activityWeakReference = new WeakReference<>(context);
            this.recyclerView = recyclerView;
        }

        @Override
        protected ArrayList<BusinessCard> doInBackground(Void... voids) {
//            if (mainActivity != null && !mainActivity.isFinishing()) {
//            }
            ArrayList<BusinessCard> businessCards = new ArrayList<>();

            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null) {
                businessCards = new DataBaseHelper(mainActivity).getAllBusinessCards();
            }
            return businessCards;
        }

        @Override
        protected void onPostExecute(ArrayList<BusinessCard> businessCards) {
            super.onPostExecute(businessCards);
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null && !mainActivity.isFinishing()) {
                mainActivity.businessCardsList = businessCards;
                mainActivity.mAdapter = new MyRecyclerViewAdapter(businessCards);
                recyclerView.setAdapter();
            }
        }
    }

    private static class QueryByBusinessCardNameAsyncTask extends AsyncTask<String, Void, BusinessCard> {

        private WeakReference<MainActivity> activityWeakReference;

        QueryByBusinessCardNameAsyncTask(MainActivity context) {
            activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected BusinessCard doInBackground(String... strings) {
            String inputSearchNameString = strings[0];
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null && !mainActivity.isFinishing()) {
                return new DataBaseHelper(mainActivity).getBusinessCardQueryByName(inputSearchNameString);
            }
            return null;
        }

        @Override
        protected void onPostExecute(BusinessCard businessCard) {
            super.onPostExecute(businessCard);
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity != null && !mainActivity.isFinishing()) {
                if (businessCard != null) {
                    new MyBusinessCardDialog(mainActivity, OperationTypeEnum.QUERY, businessCard);
                    mainActivity.inputSearchNameEt.setText("");
                } else {
                    Toast.makeText(mainActivity,
                            "The people not survive in your list",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                mainActivity.hideSoftKeyboard();
            }
        }
    }

}
