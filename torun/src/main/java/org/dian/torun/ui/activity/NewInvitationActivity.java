package org.dian.torun.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.dian.torun.R;
import org.dian.torun.bean.User;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.vendor.TorunApi;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class NewInvitationActivity extends BaseActivity {

    private User toUser;
    private String mPlace;
    private String mContent;
    private Calendar mCalendar = Calendar.getInstance();

    private Button mTimePicker;
    private Button mDatePicker;
    private EditText mPlaceEdit;

    private EditText mContentEdit;
    private Button mConfirmBtn;

    private Button mCancelBtn;

    private boolean sending = false;

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.invitation_cancel:
                    finish();
                    break;
                case R.id.invitation_confirm:
                    sendInvitation();
                    break;

                case R.id.invitation_datepicker:
                    setDate();
                    break;

                case R.id.invitation_timepicker:
                    setTime();
                    break;
            }
        }
    };

    private void setTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Date date = mCalendar.getTime();
                date.setMinutes(minute);
                date.setHours(hourOfDay);
                mCalendar.setTime(date);
                mTimePicker.setText(hourOfDay + ":" + minute);
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void setDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(year, monthOfYear, dayOfMonth);
                mDatePicker.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }



    private void sendInvitation() {
        if (sending) return;
        sending = true;

        String from_id = AccountKeeper.getUid(this);
        long time = mCalendar.getTimeInMillis();
        mPlace = String.valueOf(mPlaceEdit.getText());
        if (TextUtils.isEmpty(mPlace)) {
            Toast.makeText(this, "约定地点不能为空", Toast.LENGTH_LONG).show();
            sending = false;
            return;
        }
        mContent = String.valueOf(mContentEdit.getText());


        TorunApi.createInvitation(this, from_id, toUser.getUid(), time, mPlace, mContent, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("to run", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("result") && jsonObject.optInt("result") == 1) {
                        showSendResult(true);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showSendResult(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSendResult(false);
            }
        });
    }

    private void showSendResult(boolean result) {
        sending = false;

        String s = result ? "发送成功" : "发送失败";
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invitation);

        mCalendar.setTimeInMillis(System.currentTimeMillis());

        if (savedInstanceState != null) {

        }

        String userStr = getIntent().getStringExtra("user");
        if (TextUtils.isEmpty(userStr)) {
            throw new IllegalArgumentException("the user is null");
        }
        toUser = User.fromJsonByGson(userStr);

        initView();
    }

    private void initView() {
        mPlaceEdit = (EditText) findViewById(R.id.invitation_place);
        mContentEdit = (EditText) findViewById(R.id.invitation_content);
        mDatePicker = (Button) findViewById(R.id.invitation_datepicker);
        mTimePicker = (Button) findViewById(R.id.invitation_timepicker);

        mConfirmBtn = (Button) findViewById(R.id.invitation_confirm);
        mCancelBtn = (Button) findViewById(R.id.invitation_cancel);

        mDatePicker.setOnClickListener(mClickListener);
        mTimePicker.setOnClickListener(mClickListener);
        mConfirmBtn.setOnClickListener(mClickListener);
        mCancelBtn.setOnClickListener(mClickListener);
    }

}
