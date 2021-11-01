package com.example.mobilderekuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Model.DriverInfoModel;
import com.example.mobilderekuser.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSupirDerek extends RecyclerView.Adapter<AdapterSupirDerek.HolderSupir> {

    private Context context;
    private ArrayList<DriverInfoModel> driverInfoList;

    public AdapterSupirDerek(Context context, ArrayList<DriverInfoModel> driverInfoList) {
        this.context = context;
        this.driverInfoList = driverInfoList;
    }

    @NonNull
    @Override
    public HolderSupir onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_supir_pengusaha, parent,false);
        return new HolderSupir(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterSupirDerek.HolderSupir holder, int position) {
        // get Data
        DriverInfoModel driverInfoModel = driverInfoList.get(position);
        String uid_supir = driverInfoModel.getUid();
        String nama_supir = driverInfoModel.getNamaSupir();
        String noHp_supir = driverInfoModel.getNoHpSupir();
        String email_supir = driverInfoModel.getEmailSupir();
        String poto_supir = driverInfoModel.getProfileImage();
        String online = driverInfoModel.getOnline();

        holder.txt_nama_supir.setText(nama_supir);
        holder.txt_noHp_supir.setText(noHp_supir);
        holder.txt_email_supir.setText(email_supir);

        Glide.with(context)
                .load(poto_supir)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .fitCenter()
                .into(holder.IV_photoDriver);

        if (online.equals("true")){
            holder.onlineShop_TV.setVisibility(View.VISIBLE);
            holder.tugasShop_TV.setVisibility(View.GONE);
            holder.tutupShop_TV.setVisibility(View.GONE);
            holder.onlineShop_TV.setText(R.string.online_status);

        } else if (online.equals("tugas")){
            holder.onlineShop_TV.setVisibility(View.GONE);
            holder.tugasShop_TV.setVisibility(View.VISIBLE);
            holder.tutupShop_TV.setVisibility(View.GONE);
            holder.onlineShop_TV.setText(R.string.tugas_status);

        } else {
            holder.onlineShop_TV.setVisibility(View.GONE);
            holder.tugasShop_TV.setVisibility(View.GONE);
            holder.tutupShop_TV.setVisibility(View.VISIBLE);
            holder.onlineShop_TV.setText(R.string.offline_status);


        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(context, DriverDetailActivity.class);
                context.startActivity(intent);
                EventBus.getDefault().postSticky(new LoadDriverDetailEvent(uid_supir));
                Toast.makeText(context, ""+uid_supir+" , Nama : "+nama_supir, Toast.LENGTH_SHORT).show();*/

            }
        });

    }

    @Override
    public int getItemCount() {
        return driverInfoList.size();
    }

    class HolderSupir extends RecyclerView.ViewHolder{

        //Ui View
        //private ImageView IV_photoDriver;
        private CircleImageView IV_photoDriver;
        private ImageView online_IV;
        private TextView tutupShop_TV, onlineShop_TV, tugasShop_TV ,txt_nama_supir, txt_noHp_supir,
                txt_email_supir;

        public HolderSupir(@NonNull View itemView) {
            super(itemView);

            //Init views holder
            IV_photoDriver = itemView.findViewById(R.id.photoDriver_IV);
            tutupShop_TV = itemView.findViewById(R.id.tutupShop_TV);
            onlineShop_TV = itemView.findViewById(R.id.onlineShop_TV);
            tugasShop_TV = itemView.findViewById(R.id.tugasShop_TV);
            txt_nama_supir = itemView.findViewById(R.id.txt_nama_supir);
            txt_noHp_supir = itemView.findViewById(R.id.txt_noHp_supir);
            txt_email_supir = itemView.findViewById(R.id.txt_email_supir);

        }
    }
}
