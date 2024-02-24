package vn.kido.shop.Adapter;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.List;

import vn.kido.shop.Bean.BeanRetail;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.Order.Change.ConfirmRetailOrderFragment;
import vn.kido.shop.Fragment.Order.Change.RetailOrderFragment;
import vn.kido.shop.Fragment.Order.Change.UpdateQuantityRetailFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class UpdateRetailAdapter extends RecyclerView.Adapter<UpdateRetailAdapter.MyViewHolder> {

    Fragment fragment;
    RecyclerView recyclerView;
    List<BeanRetail> retails;
    int size;
    ConfirmRetailOrderFragment confirmRetailOrderFragment;

    public UpdateRetailAdapter(Fragment fragment, RecyclerView recyclerView, List<BeanRetail> retails) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.retails = retails;
        size = CmmFunc.convertDpToPx(fragment.getActivity(), 240);
        confirmRetailOrderFragment = (ConfirmRetailOrderFragment) fragment.getParentFragment();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_update_retail, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            final BeanRetail beanRetail = retails.get(position);
            if (beanRetail != null) {
                if (!TextUtils.isEmpty(beanRetail.getImage())) {
                    Picasso.get().load(beanRetail.getImage()).resize(size, size).centerInside().into(holder.thumbnail);
                }
                holder.name.setText(beanRetail.getName() + "");
                holder.minUnit.setText(beanRetail.getMinUnit() + "");
                holder.quantity.setText(beanRetail.getQuantity() + "");
                holder.note.setText(beanRetail.getNote() + "");
                if (beanRetail.getExpireDate() == 0) {
                    holder.expiryDate.setText("dd/MM/yyyy");
                } else {
                    holder.expiryDate.setText(new DateTime(beanRetail.getExpireDate()).toString("dd/MM/yyyy"));
                }


                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        retails.remove(position);
                        notifyDataSetChanged();

                        RetailOrderFragment retailOrderFragment = (RetailOrderFragment) FragmentHelper.findFragmentByTag(RetailOrderFragment.class);
                        if (retailOrderFragment != null) {
                            retailOrderFragment.updateList(beanRetail.getId());
                        }
                    }
                });

                holder.note.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        beanRetail.setNote(s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            if (beanRetail.getNote().equals("")) {
                                if (holder.note.getTag().equals(R.drawable.background_note_retail)) {
                                    //notifyDataSetChanged();
                                    holder.note.setBackgroundResource(R.drawable.background_note_retail_error);
                                    holder.note.setTag(R.drawable.background_note_retail_error);
                                }
                            } else {
                                if (holder.note.getTag().equals(R.drawable.background_note_retail_error)) {
                                    //notifyDataSetChanged();
                                    holder.note.setBackgroundResource(R.drawable.background_note_retail);
                                    holder.note.setTag(R.drawable.background_note_retail);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

                holder.expiryDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (beanRetail.getExpireDate() == 0) {
                            beanRetail.setExpireDate(System.currentTimeMillis());
                        }
                        DateTime dateTime = new DateTime(beanRetail.getExpireDate());
                        DatePickerDialog dialog = new DatePickerDialog(fragment.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                DateTime dt = new DateTime(year, month + 1, dayOfMonth, 0, 0);
                                beanRetail.setExpireDate(dt.getMillis());
                                notifyDataSetChanged();
                            }
                        }, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());
                        dialog.show();
                    }
                });

                holder.quantityContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateQuantityRetailFragment updateQuantityRetailFragment = UpdateQuantityRetailFragment.newInstance(position, beanRetail.getQuantity(), beanRetail.getMinUnit());
                        updateQuantityRetailFragment.quantityListener = new UpdateQuantityRetailFragment.QuantityListener() {
                            @Override
                            public void excute(int position, final int value) {
                                fragment.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            beanRetail.setQuantity(value);
                                            notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                        };
                        FragmentHelper.add(updateQuantityRetailFragment);
                    }
                });

                if (confirmRetailOrderFragment.isError) {
                    if (beanRetail.getExpireDate() == 0) {
                        holder.lineDate.setBackgroundResource(R.color.red_500);
                    } else {
                        holder.lineDate.setBackgroundResource(R.color.gray_300);
                    }

                    if (TextUtils.isEmpty(beanRetail.getNote())) {
                        holder.note.setBackgroundResource(R.drawable.background_note_retail_error);
                        holder.note.setTag(R.drawable.background_note_retail_error);
                    } else {
                        holder.note.setBackgroundResource(R.drawable.background_note_retail);
                        holder.note.setTag(R.drawable.background_note_retail);
                    }
                } else {
                    holder.lineDate.setBackgroundResource(R.color.gray_300);
                    holder.note.setBackgroundResource(R.drawable.background_note_retail);
                    holder.note.setTag(R.drawable.background_note_retail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return retails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, remove;
        TextView name, expiryDate, quantity, minUnit;
        EditText note;
        View quantityContainer, lineDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            remove = itemView.findViewById(R.id.remove);
            name = itemView.findViewById(R.id.name);
            expiryDate = itemView.findViewById(R.id.expiry_date);
            quantity = itemView.findViewById(R.id.quantity);
            quantityContainer = itemView.findViewById(R.id.quantity_container);
            note = itemView.findViewById(R.id.note);
            minUnit = itemView.findViewById(R.id.min_unit);
            lineDate = itemView.findViewById(R.id.line_date);

        }
    }
}
