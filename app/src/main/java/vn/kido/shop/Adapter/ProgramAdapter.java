package vn.kido.shop.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.kido.shop.Bean.BeanProgram;
import vn.kido.shop.Fragment.Program.DetailProgramFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.R;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.MyViewHolder> {
    Fragment fragment;
    List<BeanProgram> programs;
    RecyclerView recycler;

    public ProgramAdapter(Fragment fragment, List<BeanProgram> programs, RecyclerView recycler) {
        this.fragment = fragment;
        this.programs = programs;
        this.recycler = recycler;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_program, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            final BeanProgram program = programs.get(position);
            if (program != null) {
                holder.title.setText(program.getTitle() + "");
                if(!program.getImage().equals("")){
                    Picasso.get().load(program.getImage()).into(holder.thumbnail);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentHelper.add(DetailProgramFragment.newInstance(program.getId()));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
