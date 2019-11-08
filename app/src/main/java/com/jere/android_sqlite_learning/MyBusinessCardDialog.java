package com.jere.android_sqlite_learning;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
    private CheckBox maleCb;
    private CheckBox femaleCb;

    MyBusinessCardDialog(Context context, boolean isUpdateCardInfo) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);
        this.isUpdateCardInfo = isUpdateCardInfo;
    }

    public void createDialogAndShow(BusinessCard businessCard, IGenerateBusinessCardListener listener) {
        this.mListener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.add_business_card_dialog, null);
        maleCb = view.findViewById(R.id.male_cb);
        femaleCb = view.findViewById(R.id.female_cb);
        maleCb.setOnClickListener(this);
        femaleCb.setOnClickListener(this);
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
                if (!maleCb.isChecked() && !femaleCb.isChecked()) {
                    Toast.makeText(context, "pls select gender!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (maleCb.isChecked()){
                    businessCard.setGender(true);
                } else {
                    businessCard.setGender(false);
                }
                businessCard.setName(nameEt.getText().toString());
                businessCard.setTelephone(telephoneEt.getText().toString());
                businessCard.setAddress(addressEt.getText().toString());
                businessCard.setAvatar(businessCard.getGender() ? R.drawable.male_avatar_icon : R.drawable.female_avatar_icon);

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
            case R.id.male_cb:
                //sure client only select one checkbox, can't select maleCb and female together
                if (maleCb.isChecked()) {
                    femaleCb.setChecked(false);
                }
                Toast.makeText(context, "maleCb isChecked: " + maleCb.isChecked(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.female_cb:
                if (femaleCb.isChecked()) {
                    maleCb.setChecked(false);
                }
                Toast.makeText(context, "maleCb isChecked: " + femaleCb.isChecked(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
