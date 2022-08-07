package com.example.mobilderekuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilderekuser.Common.Common;
import com.example.mobilderekuser.Model.EventBus.LoadTripDetailEvent;
import com.example.mobilderekuser.Model.TripPlanModel;
import com.example.mobilderekuser.R;
import com.example.mobilderekuser.Ui.HistoryUserActivity;
import com.example.mobilderekuser.Ui.TripDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterHistoryPerjalanan extends RecyclerView.Adapter<AdapterHistoryPerjalanan.HolderHistory>{

    private Context context;
    private ArrayList<TripPlanModel> tripPlanList;



    public AdapterHistoryPerjalanan(Context context, ArrayList<TripPlanModel> tripPlanList) {
        this.context = context;
        this.tripPlanList = tripPlanList;
    }

    @NonNull
    @Override
    public HolderHistory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_history, parent,false);
        return new HolderHistory(view);
    }



    @Override
    public void onBindViewHolder(@NonNull AdapterHistoryPerjalanan.HolderHistory holder, int position) {
        //get Data
        TripPlanModel tripPlanModel = tripPlanList.get(position);

        String orderId = tripPlanModel.getTripId();
        String nama_supir = tripPlanModel.getDriverInfoModel().getNamaSupir();
        String nama_usaha = tripPlanModel.getDriverInfoModel().getNamaUsaha();
        String tanggal_jalan = tripPlanModel.getTimText();
        String alamat_jemput = tripPlanModel.getOriginString();
        String alamat_tujuan = tripPlanModel.getDestinationString();
        String isDone = String.valueOf(tripPlanModel.isDone());
        String isCancel = String.valueOf(tripPlanModel.isCancel());
        String foto_user = tripPlanModel.getDriverInfoModel().getProfileImage();
        String durasi_perjalanan = tripPlanModel.getDurationValue();
        /*String status = tripPlanModel.isDone();*/



        if (isDone.equals("true")) {
            holder.layout_status_cancel.setVisibility(View.GONE);
            holder.layout_status_complete.setVisibility(View.VISIBLE);
        }
        if (isCancel.equals("true")) {
            holder.layout_status_complete.setVisibility(View.GONE);
            holder.layout_status_cancel.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(foto_user)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .fitCenter()
                .into(holder.potoUser_iv);

        /*try {
            Picasso.get().load(foto_user).placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.potoUser_iv);
        } catch (Exception e){
            holder.potoUser_iv.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }*/

        holder.txt_order_jalan.setText("Order: "+orderId);
        holder.txt_tanggal_jalan.setText(tanggal_jalan);
        holder.txt_nama_supir.setText(nama_supir);
        holder.txt_nama_usaha.setText(nama_usaha);
        holder.txt_alamat_jemput.setText(alamat_jemput);
        holder.txt_lokasi_tujuan.setText(alamat_tujuan);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TripDetailActivity.class);
                context.startActivity(intent);
                EventBus.getDefault().postSticky(new LoadTripDetailEvent(tripPlanModel.getTripId()));
                //Toast.makeText(context, "Supir: "+nama_supir+" , Usaha: "+nama_usaha, Toast.LENGTH_SHORT).show();

            }
        });
    }



    @Override
    public int getItemCount() {
        return tripPlanList.size();
    }

    class HolderHistory extends RecyclerView.ViewHolder{

        //Ui View
        //private ImageView IV_photoDriver;
        
        private CircleImageView potoUser_iv;
        private ImageView status_iv;
        private LinearLayout layout_status_complete, layout_status_cancel;
        private TextView txt_order_jalan,txt_tanggal_jalan, txt_nama_supir, txt_nama_usaha, txt_status,
                txt_alamat_jemput, txt_lokasi_tujuan;
        public HolderHistory(@NonNull @NotNull View itemView) {
            super(itemView);

            //Init views holder

            potoUser_iv = itemView.findViewById(R.id.iv_potoUser);
            layout_status_complete = itemView.findViewById(R.id.status_complete_layout);
            layout_status_cancel = itemView.findViewById(R.id.status_cancel_layout);
            txt_order_jalan = itemView.findViewById(R.id.orderId_TV);
            txt_tanggal_jalan = itemView.findViewById(R.id.date_TV);
            txt_nama_supir = itemView.findViewById(R.id.namaDriver_TV);
            txt_nama_usaha = itemView.findViewById(R.id.namaPerusahaan_TV);
            txt_alamat_jemput = itemView.findViewById(R.id.txt_originPickup);
            txt_lokasi_tujuan = itemView.findViewById(R.id.txt_destinationRider);

        }
    }
}
