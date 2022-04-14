package com.example.ev.stations.evchargingstation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.List;

public class StationListAdapter extends ArrayAdapter<Station> {
    private final int resourceLayout;
    private final Context mContext;

    public StationListAdapter(Context context, int resource, List<Station> objects) {
        super(context, resource, objects);
        resourceLayout = resource;
        mContext = context;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) view = LayoutInflater.from(mContext).inflate(resourceLayout, null);
        Station item = getItem(position);
        ((TextView) view.findViewById(R.id.title)).setText(item.name);
        ((TextView) view.findViewById(R.id.subtitle)).setText(String.format("%.2f km", item.distance));
        ((TextView) view.findViewById(R.id.trailing)).setText(String.format("%d", item.plugsAvailable));
        ((CardView) view.findViewById(R.id.trailing_avatar))
                .setCardBackgroundColor(item.plugsAvailable > 1 ? Colors.green400 : item.plugsAvailable > 0 ? Colors.orange400 : Colors.red400);
        return view;
    }
}
