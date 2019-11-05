package com.jere.android_sqlite_learning;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

/**
 * @author jere
 */
public class MyBusinessCardDialog implements View.OnClickListener {
    private static final String TAG = "MyBusinessCardDialog";
    private Context context;
    private AlertDialog alertDialog;
    private EditText nameEt;
    private EditText telephoneEt;
    private EditText addressEt;
    private DataBaseHelper dataBaseHelper;
    private boolean isUpdateCardInfo = false;
    private IGenerateBusinessCardListener mListener;
    private BusinessCard oldBusinessCard;

    MyBusinessCardDialog(Context context, boolean isUpdateCardInfo) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);
        this.isUpdateCardInfo = isUpdateCardInfo;
    }

    public void createDialogAndShow(BusinessCard businessCard, IGenerateBusinessCardListener listener) {
        this.mListener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.add_business_card_dialog, null);
        nameEt = view.findViewById(R.id.name_et);
        telephoneEt = view.findViewById(R.id.telephone_et);
        addressEt = view.findViewById(R.id.address_et);
        if (businessCard != null) {
            oldBusinessCard = businessCard;
            nameEt.setText(businessCard.getName());
            telephoneEt.setText(businessCard.getTelephone());
            addressEt.setText(businessCard.getAddress());
        }
        Button yesBtn = view.findViewById(R.id.yes_btn);
        yesBtn.setOnClickListener(this);
        Button noBtn = view.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yes_btn:
                BusinessCard businessCard = new BusinessCard();
                businessCard.setName(nameEt.getText().toString());
                businessCard.setTelephont(telephoneEt.getText().toString());
                businessCard.setAddress(addressEt.getText().toString());

                //todo insert or update database and notifyDataSetChanged
//                businessCardList.add(businessCard);
//                mAdapter.notifyDataSetChanged();
                if (isUpdateCardInfo) {
                    int oldBusinessCardId = dataBaseHelper.getBusinessCardId(oldBusinessCard);
                    long id = dataBaseHelper.updateBusinessCard(oldBusinessCardId, businessCard);
                    mListener.getBusinessCard(businessCard);
                    Log.d(TAG, "onClick: updateBusinessCard id = " + id);
                } else {
                    long id = dataBaseHelper.insertBusinessCard(businessCard);
                    mListener.getBusinessCard(businessCard);
                    Log.d(TAG, "onClick: insertBusinessCard id = " + id);
                }

                alertDialog.dismiss();
                break;
            case R.id.no_btn:
                //todo no action
                alertDialog.dismiss();
                break;
            default:
                break;
        }
    }
}
