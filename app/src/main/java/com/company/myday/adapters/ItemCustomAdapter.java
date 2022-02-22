package com.company.myday.adapters;


import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.company.myday.R;
import com.company.myday.dbhelpers.DBHelper;
import com.company.myday.models.Item;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemCustomAdapter extends ArrayAdapter<Item> {

    private List<Item> list;
    private final Activity context;
    private MaterialCheckBox materialCheckBox;
    private String [] timeArray;

    public ItemCustomAdapter(Activity context, List<Item> list) {
        super(context, R.layout.list_activity, list);
        this.context = context;
        this.list = list;
    }

    public ItemCustomAdapter(Activity context, List<Item> list, String[] timeAr) {
        super(context, R.layout.list_activity, list);
        this.context = context;
        this.list = list;
        this.timeArray = timeAr;
    }

    static class ViewHolder {
        protected TextView textView;
        protected TextView date_text;
        protected CheckBox checkBox;
        private Long id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.textView = view.findViewById(R.id.item_text);
            viewHolder.date_text = view.findViewById(R.id.date_text);
            viewHolder.checkBox = view.findViewById(R.id.item_checkbox);
            viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Item element = (Item) viewHolder.checkBox
                        .getTag();
                element.setSelected(buttonView.isChecked());
            });
            view.setTag(viewHolder);
            viewHolder.checkBox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkBox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.textView.setText(list.get(position).getName());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
        String date = sdf.format(list.get(position).getCalendar());
        holder.date_text.setText(date);
        holder.id = (list.get(position).getId());

        final Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                remove(getItem(position));
                DBHelper dbHelper = new DBHelper(context);
                dbHelper.delete_Model_item_by_id(holder.id);
                dbHelper.close();
                notifyDataSetChanged();
            }
        });

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b == true) {
                view.startAnimation(animation);
            }
        });
        view.setOnClickListener(this::onClick);

        return view;
    }

    public void onClick(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        ViewHolder holder = (ViewHolder) view.getTag();
        LayoutInflater inflater = LayoutInflater.from(context);
        View fragment = inflater.inflate(R.layout.fragment_item, null);

        final MaterialEditText task = fragment.findViewById(R.id.task);
        final CalendarView calendarView = fragment.findViewById(R.id.calendar);



        DBHelper dbHelper = new DBHelper(context);

        final MaterialCheckBox box = view.findViewById(R.id.item_checkbox);

        Item modelItem = dbHelper.get_Model_item_by_id(holder.id);
        System.out.println("DATE BEFORE: " + modelItem.getCalendar());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView1, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                Date date = cal.getTime();
                modelItem.setCalendar(date.getTime());
            }
        });

        if (modelItem != null) {
            task.setText(modelItem.getName());
            box.setSelected(modelItem.isSelected());
            calendarView.setDate(modelItem.getCalendar());
        }

        View customTitle = inflater.inflate(R.layout.customtitlebar, null);
        dialog.setCustomTitle(customTitle)
                .setView(fragment)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    if (TextUtils.isEmpty(task.getText())) {
                        Toast.makeText(context, "Please, enter task", Toast.LENGTH_SHORT);
                        System.out.println("Please, enter task");
                        return;
                    }
                    materialCheckBox = view.findViewById(R.id.item_checkbox);

                    modelItem.setName(task.getText().toString());
                    dbHelper.update_Model_item(modelItem);
                    list = dbHelper.getAllModel_item();
                    notifyDataSetChanged();
                });

        dbHelper.close();


        AlertDialog dialog1 = dialog.show();
        dialog1.getWindow().setBackgroundDrawableResource(R.drawable.style_info);
        Button cancel = dialog1.getButton(AlertDialog.BUTTON_NEGATIVE);
        cancel.setTextColor(context.getResources().getColor(R.color.but_color));
        cancel.setTextSize(16);

        Button ok = dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setTextColor(context.getResources().getColor(R.color.but_color));
        ok.setTextSize(16);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ok.getLayoutParams();
        layoutParams.weight = 10;
        ok.setLayoutParams(layoutParams);
        cancel.setLayoutParams(layoutParams);

    }
}
