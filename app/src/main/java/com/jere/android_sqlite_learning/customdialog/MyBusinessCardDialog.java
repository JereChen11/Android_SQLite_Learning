package com.jere.android_sqlite_learning.customdialog;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jere.android_sqlite_learning.DataBaseHelper;
import com.jere.android_sqlite_learning.IGenerateBusinessCardListener;
import com.jere.android_sqlite_learning.OperationTypeEnum;
import com.jere.android_sqlite_learning.R;
import com.jere.android_sqlite_learning.model.BusinessCard;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AlertDialog;

/**
 * @author jere
 */
public class MyBusinessCardDialog implements View.OnClickListener {
    private Context context;
    private AlertDialog alertDialog;
    private TextView titleTv;
    private EditText nameEt;
    private EditText telephoneEt;
    private EditText addressEt;
    private Button yesBtn;
    private Button noBtn;
    private IGenerateBusinessCardListener mListener;
    private BusinessCard oldBusinessCard;
    private CheckBox maleCb;
    private CheckBox femaleCb;
    private OperationTypeEnum operationType;

    public MyBusinessCardDialog(Context context,
                                OperationTypeEnum operationTypeEnum,
                                BusinessCard businessCard,
                                IGenerateBusinessCardListener listener) {
        this.context = context;
        operationType = operationTypeEnum;
        oldBusinessCard = businessCard;
        mListener = listener;
        createDialogAndShow();
    }

    public MyBusinessCardDialog(Context context,
                                OperationTypeEnum operationTypeEnum,
                                IGenerateBusinessCardListener listener) {
        this.context = context;
        operationType = operationTypeEnum;
        mListener = listener;
        createDialogAndShow();
    }

    public MyBusinessCardDialog(Context context,
                                OperationTypeEnum operationTypeEnum,
                                BusinessCard businessCard) {
        this.context = context;
        operationType = operationTypeEnum;
        oldBusinessCard = businessCard;
        createDialogAndShow();
    }

    public void createDialogAndShow() {
        View view = LayoutInflater.from(context).inflate(R.layout.add_business_card_dialog, null);
        findView(view);
        initViewType();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alertDialog = builder.create();
        if (operationType.equals(OperationTypeEnum.QUERY)) {
            alertDialog.setCanceledOnTouchOutside(true);
        } else {
            alertDialog.setCanceledOnTouchOutside(false);
        }
        alertDialog.show();
    }

    private void findView(View view) {
        maleCb = view.findViewById(R.id.male_cb);
        femaleCb = view.findViewById(R.id.female_cb);
        titleTv = view.findViewById(R.id.dialog_title_tv);
        nameEt = view.findViewById(R.id.name_et);
        telephoneEt = view.findViewById(R.id.telephone_et);
        addressEt = view.findViewById(R.id.address_et);
        yesBtn = view.findViewById(R.id.yes_btn);
        noBtn = view.findViewById(R.id.no_btn);
        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
        maleCb.setOnClickListener(this);
        femaleCb.setOnClickListener(this);
    }

    private void initViewType() {
        switch (operationType) {
            case INSERT:
                titleTv.setText("Add Business Card!");
                break;
            case UPDATE:
                titleTv.setText("Edit Business Card!");
                initViewContent();
                break;
            case QUERY:
                titleTv.setText("Query Business Card!");
                initViewContent();
                yesBtn.setVisibility(View.GONE);
                yesBtn.setEnabled(false);
                noBtn.setVisibility(View.GONE);
                noBtn.setEnabled(false);
                maleCb.setEnabled(false);
                femaleCb.setEnabled(false);
                nameEt.setEnabled(false);
                telephoneEt.setEnabled(false);
                addressEt.setEnabled(false);
                break;
            default:
                break;

        }
    }

    private void initViewContent() {
        if (oldBusinessCard != null) {
            nameEt.setText(oldBusinessCard.getName());
            telephoneEt.setText(oldBusinessCard.getTelephone());
            addressEt.setText(oldBusinessCard.getAddress());
            if (oldBusinessCard.getGender()) {
                maleCb.setChecked(true);
            } else {
                femaleCb.setChecked(true);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yes_btn:
                BusinessCard newBusinessCard = generateNewBusinessCard();
                if (newBusinessCard == null) {
                    Toast.makeText(context, "pls select gender!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (operationType.equals(OperationTypeEnum.UPDATE)) {
                    updateDatabaseBusinessCardInfo(newBusinessCard);
                } else if (operationType.equals(OperationTypeEnum.INSERT)) {
                    insertBusinessCardDataToDatabase(newBusinessCard);
                }

                alertDialog.dismiss();
                break;
            case R.id.no_btn:
                alertDialog.dismiss();
                break;
            case R.id.male_cb:
                //sure client only select one checkbox, can't select maleCb and female together
                if (maleCb.isChecked()) {
                    femaleCb.setChecked(false);
                }
                break;
            case R.id.female_cb:
                //sure client only select one checkbox, can't select maleCb and female together
                if (femaleCb.isChecked()) {
                    maleCb.setChecked(false);
                }
                break;
            default:
                break;
        }
    }

    private BusinessCard generateNewBusinessCard() {
        BusinessCard businessCard = new BusinessCard();
        if (!maleCb.isChecked() && !femaleCb.isChecked()) {
            return null;
        } else if (maleCb.isChecked()) {
            businessCard.setGender(true);
        } else {
            businessCard.setGender(false);
        }
        businessCard.setName(nameEt.getText().toString());
        businessCard.setTelephone(telephoneEt.getText().toString());
        businessCard.setAddress(addressEt.getText().toString());
        businessCard.setAvatar(businessCard.getGender() ? R.drawable.male_avatar_icon : R.drawable.female_avatar_icon);

        return businessCard;
    }

    private void updateDatabaseBusinessCardInfo(BusinessCard businessCard) {
        UpdateBusinessCardAsyncTask updateBusinessCardAsyncTask =
                new UpdateBusinessCardAsyncTask(context, oldBusinessCard.getId(), mListener);
        updateBusinessCardAsyncTask.execute(businessCard);
    }

    private void insertBusinessCardDataToDatabase(BusinessCard businessCard) {
        InsertBusinessCardAsyncTask insertBusinessCardAsyncTask = new InsertBusinessCardAsyncTask(context, mListener);
        insertBusinessCardAsyncTask.execute(businessCard);
    }


    private static class UpdateBusinessCardAsyncTask extends AsyncTask<BusinessCard, Void, BusinessCard> {

        private WeakReference<Context> contextWeakReference;
        private IGenerateBusinessCardListener iGenerateBusinessCardListener;
        private int oldBusinessCardId;

        UpdateBusinessCardAsyncTask(Context context, int id, IGenerateBusinessCardListener listener) {
            contextWeakReference = new WeakReference<>(context);
            oldBusinessCardId = id;
            iGenerateBusinessCardListener = listener;
        }

        @Override
        protected BusinessCard doInBackground(BusinessCard... businessCards) {
            Context context = contextWeakReference.get();
            if (context != null) {
                DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
                int theNumberAfterUpdate = dataBaseHelper.updateBusinessCard(oldBusinessCardId, businessCards[0]);
                if (theNumberAfterUpdate > 0) {
                    return businessCards[0];
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(BusinessCard businessCard) {
            super.onPostExecute(businessCard);
            Context context = contextWeakReference.get();
            if (context != null) {
                if (businessCard != null) {
                    iGenerateBusinessCardListener.getBusinessCard(businessCard);
                } else {
                    Toast.makeText(context, "Insert BusinessCard Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static class InsertBusinessCardAsyncTask extends AsyncTask<BusinessCard, Void, BusinessCard> {
        private WeakReference<Context> contextWeakReference;
        private IGenerateBusinessCardListener iGenerateBusinessCardListener;

        InsertBusinessCardAsyncTask(Context context, IGenerateBusinessCardListener listener) {
            contextWeakReference = new WeakReference<>(context);
            iGenerateBusinessCardListener = listener;
        }

        @Override
        protected BusinessCard doInBackground(BusinessCard... businessCards) {
            BusinessCard businessCard = businessCards[0];
            long idReturnByInsert = -1;
            if (contextWeakReference.get() != null) {
                idReturnByInsert = new DataBaseHelper(contextWeakReference.get()).insertBusinessCard(businessCard);
            }
            if (idReturnByInsert > -1) {
                businessCard.setId((int) idReturnByInsert);
                return businessCard;
            }
            return null;
        }

        @Override
        protected void onPostExecute(BusinessCard businessCard) {
            super.onPostExecute(businessCard);
            if (contextWeakReference.get() != null) {
                if (businessCard != null) {
                    iGenerateBusinessCardListener.getBusinessCard(businessCard);
                } else {
                    Toast.makeText(contextWeakReference.get(), "Insert BusinessCard Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
