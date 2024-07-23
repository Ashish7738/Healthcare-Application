package com.example.healthcare.Medicine.BuyMedicine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcare.Model.MedicineModel;
import com.example.healthcare.R;

import java.util.List;

public class BuyMedicineAdapter extends RecyclerView.Adapter<BuyMedicineAdapter.MedicineViewHolder> {

    private Context context;
    private List<MedicineModel> medicineModelList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onBuyButtonClick(int position);

        void onItemClick(int position);

        void onAddToCartButtonClick(int position);
    }

    public BuyMedicineAdapter(Context context, List<MedicineModel> medicineModelList) {
        this.context = context;
        this.medicineModelList = medicineModelList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_item, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        MedicineModel currentMedicine = medicineModelList.get(position);

        // Load the medicine image using Glide library
        Glide.with(context).load(currentMedicine.getImg_url()).into(holder.medicineImage);

        // Set the text for various TextViews based on the medicine data
        holder.medicineName.setText(currentMedicine.getMedicineName());
        holder.medicinePrice.setText(currentMedicine.getMedicinePrice());

        // Set the text for the medicine_type TextView based on the type of the medicine
        String typeText = getMedicineTypeText(currentMedicine);
        holder.medicineType.setText(typeText);

        // Set click listener for the "Buy" button
        holder.buyButton.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onBuyButtonClick(position);
            }
        });

        // Set click listener for the "Add to Cart" button
        holder.addToCartButton.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onAddToCartButtonClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineModelList.size();
    }

    private String getMedicineTypeText(MedicineModel medicine) {
        if ("tablets".equalsIgnoreCase(medicine.getMedicineType())) {
            return "Strip of " + medicine.getMedicineQuantity() + " " + medicine.getMedicineUnit();
        } else if ("syrup".equalsIgnoreCase(medicine.getMedicineType())) {
            return "Bottle of " + medicine.getMedicineQuantity() + " " + medicine.getMedicineUnit();
        } else {
            return medicine.getMedicineQuantity() + " " + medicine.getMedicineUnit();
        }
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        ImageView medicineImage;
        TextView medicineName, medicineType, medicinePrice;
        Button addToCartButton, buyButton;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.medicine_image);
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineType = itemView.findViewById(R.id.medicine_type);
            medicinePrice = itemView.findViewById(R.id.medicine_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
            buyButton = itemView.findViewById(R.id.buy_button);
        }
    }
}
