package com.example.beam;

import android.bluetooth.BluetoothGattService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CentralRecyclerAdapter extends RecyclerView.Adapter<CentralRecyclerAdapter.CentralViewHolder> {
    public CentralRecyclerAdapter() {
        services = new ArrayList<>();
    }

    public static class CentralViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;

        public CentralViewHolder(@NonNull View itemView) {
            super(itemView);

            deviceName = itemView.findViewById(R.id.device_name);
        }
    }
    private List<BluetoothGattService> services;

    public void setServices(List<BluetoothGattService> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    public void addService(BluetoothGattService service) {
        services.add(service);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CentralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new CentralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CentralViewHolder holder, int position) {
        holder.deviceName.setText(services.get(position).getUuid().toString());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }
}
