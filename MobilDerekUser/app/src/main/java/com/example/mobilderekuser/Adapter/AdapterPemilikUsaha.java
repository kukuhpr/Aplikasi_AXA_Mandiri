package com.example.mobilderekuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Model.EventBus.LoadPemilikUsaha;
import com.example.mobilderekuser.Model.EventBus.LoadTripDetailEvent;
import com.example.mobilderekuser.Model.UsahaModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Ui.RegisterSupirActivity;
import com.example.mobilderekuser.Ui.TripDetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPemilikUsaha extends RecyclerView.Adapter<AdapterPemilikUsaha.HolderUsaha>{

    private Context context;
    private ArrayList<UsahaModel> usahaModels;

    public AdapterPemilikUsaha(Context context, ArrayList<UsahaModel> usahaModels) {
        this.context = context;
        this.usahaModels = usahaModels;
    }

    @NonNull
    @Override
    public AdapterPemilikUsaha.HolderUsaha onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.style_spinner, parent,false);
        return new HolderUsaha(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterPemilikUsaha.HolderUsaha holder, int position) {

        UsahaModel usahaModel = usahaModels.get(position);
        String uid_supir = usahaModel.getUid();
        String nama_pemilik = usahaModel.getNamaPemilik();
        String nama_toko = usahaModel.getTokoPemilik();
        String harga_supir = usahaModel.getDeliveryPemilik();
        String poto_supir = usahaModel.getProfileImage();

        holder.namaPemilik_TV.setText(nama_pemilik);
        holder.namaToko_TV.setText(nama_toko);


        Glide.with(context)
                .load(poto_supir)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .fitCenter()
                .into(holder.IV_photoDriver);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ID Pemilik : ",""+uid_supir);
                Log.d("HARGA : ",""+harga_supir);
                Log.d("NAMA USAHA : ",""+nama_toko);
                Log.d("NAMA OWNER : ",""+nama_pemilik);
                Intent intent = new Intent(context, RegisterSupirActivity.class);
                context.startActivity(intent);
                EventBus.getDefault().postSticky(new LoadPemilikUsaha(uid_supir));

                //addToEditText(usahaModel);
                //addToEditText(uid_supir, nama_pemilik, nama_toko,harga_supir, holder);

            }
        });

    }

    private void addToEditText(UsahaModel usahaModel) {
    }

    private void addToEditText(String uid_supir, String nama_pemilik, String nama_toko, String harga_supir, @NotNull HolderUsaha holder) {
        holder.sTotalLabel.setVisibility(View.VISIBLE);
        holder.sTotal.setVisibility(View.VISIBLE);
        holder.sTotalLabel.setText("Pilihan pemilik usaha Anda:");
        holder.sTotal.setText(nama_toko);
    }

    @Override
    public int getItemCount() {
        return usahaModels.size();
    }

    class HolderUsaha extends RecyclerView.ViewHolder{

        //Ui View
        //private ImageView IV_photoDriver;
        private CircleImageView IV_photoDriver;
        private Button btn_pilih;
        private TextView namaPemilik_TV, namaToko_TV, sTotalLabel, sTotal;

        public HolderUsaha(@NonNull @NotNull View itemView) {
            super(itemView);
            IV_photoDriver = itemView.findViewById(R.id.photoDriver_IV);
            namaPemilik_TV = itemView.findViewById(R.id.txt_nama_pemilik);
            namaToko_TV = itemView.findViewById(R.id.txt_nama_usaha);


        }
    }

}
