package vn.kido.shop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import vn.kido.shop.R;

public class SpinnerAdapter extends ArrayAdapter {
    Context context;
    List items;
    boolean initial;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
    }

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List objects, boolean initial) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
        this.items.add(0, "...");
        this.initial = initial;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return common(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layout = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View row = layout.inflate(R.layout.row_spinner_dropdown, parent, false);

        TextView label = row.findViewById(R.id.text);
        try {
            Object item = items.get(position);
            if (item instanceof JSONObject) {
                label.setText(((JSONObject) item).getString("name"));
            } else if (item instanceof String) {
                label.setText(item + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return row;
    }

    View common(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setText("");
        try {
            Object item = items.get(position);
            if (item instanceof JSONObject) {
                label.setText(((JSONObject) item).getString("name"));
            }
            else if (item instanceof String) {
                label.setText(item + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }
}
