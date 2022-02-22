package com.company.myday.main;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.company.myday.R;
import com.company.myday.adapters.ItemCustomAdapter;
import com.company.myday.dbhelpers.DBHelper;
import com.company.myday.models.Item;
import com.company.myday.models.SpinnerRaw;
import com.company.myday.notification.NotificationPublisher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    List<Item> list = new ArrayList<>();
    ListView listView;
    ItemCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        dbHelper = new DBHelper(this);
        list = dbHelper.getAllModel_item();
        dbHelper.close();

        listView = findViewById(R.id.list_view);
        adapter = new ItemCustomAdapter(this, list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> onClickPlus());

        createNotificationChannel();

    }



    private void onClickPlus() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final long[] date_var = {0};
        LayoutInflater inflater = LayoutInflater.from(this);
        View fragment = inflater.inflate(R.layout.fragment_item, null);

        final MaterialEditText task = fragment.findViewById(R.id.task);
        final CalendarView calendarView = fragment.findViewById(R.id.calendar);

        final HorizontalScrollView horizontalScrollView = fragment.findViewById(R.id.horizontal_scroll);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setVerticalScrollBarEnabled(false);

        TextView text9 = fragment.findViewById(R.id.text9);
        text9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        calendarView.setOnDateChangeListener((calendarView1, year, month, day) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            Date date = cal.getTime();
            date_var[0] = date.getTime();
        });

        View customTitle = inflater.inflate(R.layout.customtitlebar, null);
        dialog.setCustomTitle(customTitle)
                .setView(fragment)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    if (TextUtils.isEmpty(task.getText())) {
                        Toast.makeText(this, "Please, enter task", Toast.LENGTH_SHORT);
                        System.out.println("Please, enter task");
                        return;
                    }
                    Item modelItem = new Item();
                    if (date_var[0] == 0) {
                        modelItem = new Item(task.getText().toString(), calendarView.getDate(), false);
                    } else {
                        modelItem = new Item(task.getText().toString(), date_var[0], false);
                    }
                    long time = modelItem.getCalendar();
                    Date date = new Date(time);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, 20);
                    calendar.set(Calendar.MINUTE, 12);
                    calendar.set(Calendar.SECOND, 0);
                    date = calendar.getTime();

                    scheduleNotification(getNotification("You have scheduled a task for today", task.getText().toString()), date.getTime());

                    dbHelper.add_Model_item(modelItem);
                    list = dbHelper.getAllModel_item();
                    adapter = new ItemCustomAdapter(this, list);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                });

        AlertDialog dialog1 = dialog.show();
        dialog1.getWindow().setBackgroundDrawableResource(R.drawable.style_info);

        Button cancel = dialog1.getButton(AlertDialog.BUTTON_NEGATIVE);
        cancel.setTextColor(this.getResources().getColor(R.color.but_color));
        cancel.setTextSize(16);

        Button ok = dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setTextColor(this.getResources().getColor(R.color.but_color));
        ok.setTextSize(16);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ok.getLayoutParams();
        layoutParams.weight = 10;
        ok.setLayoutParams(layoutParams);
        cancel.setLayoutParams(layoutParams);
    }

    private void scheduleNotification(Notification notification, long delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // notificationId is a unique int for each notification that you must define
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
        System.out.println("yes");
    }

    private Notification getNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}