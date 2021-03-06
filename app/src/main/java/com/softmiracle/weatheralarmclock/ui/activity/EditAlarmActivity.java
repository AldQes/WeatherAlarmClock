package com.softmiracle.weatheralarmclock.ui.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.softmiracle.weatheralarmclock.R;
import com.softmiracle.weatheralarmclock.alarm.AlarmClockBuilder;
import com.softmiracle.weatheralarmclock.alarm.AlarmClockLab;
import com.softmiracle.weatheralarmclock.alarm.AlarmManagerHelper;
import com.softmiracle.weatheralarmclock.alarm.db.AlarmDBUtils;
import com.softmiracle.weatheralarmclock.models.alarm.AlarmModel;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Denys on 22.01.2017.
 */
public class EditAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.alarm_cv_repeat)
    CardView cvRepeat;
    @Bind(R.id.repeat_content)
    TextView tvRepeat;
    @Bind(R.id.alarm_cv_ring)
    CardView cvRing;
    @Bind(R.id.ringtones_content)
    TextView tvRingtones;
    @Bind(R.id.alarm_cv_remind)
    CardView cvRemind;
    @Bind(R.id.remind_content)
    TextView tvRemind;
    @Bind(R.id.switch_vibration)
    SwitchCompat switchVibration;
    @Bind(R.id.switch_weather)
    SwitchCompat switchWeather;

    public static TextView tvHours;
    public static TextView tvMin;

    public static final String ALARM_CLOCK = "alarm_clock";

    private static AlarmClockLab alarmClockLab;

    public static Intent newIntent(Context context, AlarmModel alarm) {
        Intent intent = new Intent(context, EditAlarmActivity.class);
        intent.putExtra(ALARM_CLOCK, alarm);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(R.string.editAlarmActivityTitle);

        tvHours = (TextView) findViewById(R.id.alarm_time_hours);
        tvMin = (TextView) findViewById(R.id.alarm_time_min);

        AlarmModel alarm = (AlarmModel) getIntent().getSerializableExtra(ALARM_CLOCK);
        alarmClockLab = new AlarmClockBuilder().builderLab(0);
        alarmClockLab.setId(alarm.id);
        alarmClockLab.setEnable(alarm.enable);
        alarmClockLab.setHour(alarm.hour);
        alarmClockLab.setMinute(alarm.minute);
        alarmClockLab.setRepeat(alarm.repeat);
        alarmClockLab.setSunday(alarm.sunday);
        alarmClockLab.setMonday(alarm.monday);
        alarmClockLab.setTuesday(alarm.tuesday);
        alarmClockLab.setWednesday(alarm.wednesday);
        alarmClockLab.setThursday(alarm.thursday);
        alarmClockLab.setFriday(alarm.friday);
        alarmClockLab.setSaturday(alarm.saturday);
        alarmClockLab.setRingPosition(alarm.ringPosition);
        alarmClockLab.setRing(alarm.ring);
        alarmClockLab.setVolume(alarm.volume);
        alarmClockLab.setVibrate(alarm.vibrate);
        alarmClockLab.setRemind(alarm.remind);
        alarmClockLab.setVibrate(alarm.weather);

        tvRingtones.setText(alarmClockLab.ring);
        switchVibration.setChecked(alarmClockLab.vibrate);
        cvRepeat.setOnClickListener(this);
        cvRing.setOnClickListener(this);
        cvRemind.setOnClickListener(this);
        switchWeather.setChecked(alarmClockLab.weather);

        int hour = alarmClockLab.hour;
        int minute = alarmClockLab.minute;

        String h = String.valueOf(hour);
        String m = String.valueOf(minute);

        if (minute < 10) {
            m = "0" + minute;
        }
        if (hour < 10) {
            h = "0" + hour;
        }
        tvHours.setText(h);
        tvMin.setText(m);

        switchVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarmClockLab.setVibrate(isChecked);
            }
        });

        switchWeather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarmClockLab.setWeather(isChecked);
            }
        });
    }

    @OnClick(R.id.alarm_cv_time)
    public void OnTimeClick() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @OnClick(R.id.floating_action_btn2)
    public void OnFAB2Click() {
        AlarmDBUtils.updateAlarmClock(EditAlarmActivity.this, alarmClockLab);
        if (alarmClockLab.enable) {
            AlarmManagerHelper.startAlarmClock(EditAlarmActivity.this, alarmClockLab);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvRingtones.setText(alarmClockLab.ring);
        tvRepeat.setText(alarmClockLab.repeat);
        tvRemind.setText(getRemindString(alarmClockLab.remind));
    }

    private String getRemindString(int remind) {
        String remindString = "";
        if (remind == 3) {
            remindString = getString(R.string.remindThreeMinutes);
        } else if (remind == 5) {
            remindString = getString(R.string.remindFiveMinutes);
        } else if (remind == 10) {
            remindString = getString(R.string.remindTenMinutes);
        } else if (remind == 20) {
            remindString = getString(R.string.remindTwentyMinutes);
        } else if (remind == 30) {
            remindString = getString(R.string.remindHalfHour);
        }
        return remindString;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_cv_repeat:
                startActivity(new Intent(RepeatActivity.newIntent(this)));
                break;
            case R.id.alarm_cv_ring:
                startActivity(RingActivity.newIntent(this));
                break;
            case R.id.alarm_cv_remind:
                startActivity(new Intent(this, RemindActivity.class));
                break;
            default:
                break;
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        public TimePickerFragment() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String min = String.valueOf(minute);
            String hour = String.valueOf(hourOfDay);

            tvHours.setText(hour);
            tvMin.setText(min);
            alarmClockLab.setMinute(minute);
            alarmClockLab.setHour(hourOfDay);


        }
    }
}
