package com.example.healthcare.Medicine.CartView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthcare.Model.MedicineModel;
import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.CardViewViewHolder> {

    private Context context;
    private List<MedicineModel> medicineList;

    public CartViewAdapter(Context context, List<MedicineModel> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.cartview_item, parent, false);
        return new CardViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewViewHolder holder, int position) {
        // Bind data to the ViewHolder
        MedicineModel medicine = medicineList.get(position);

        // Load medicine image using Glide
        Glide.with(context).load(medicine.getImg_url()).into(holder.medicineImage);

        // Set medicine name and price
        holder.medicineName.setText(medicine.getMedicineName());
        holder.medicinePrice.setText(medicine.getMedicinePrice());

        // Set click listener for the "Remove" button
        holder.removeButton.setOnClickListener(view -> {
            // Call a method to remove the item from the cart_items collection
            removeFromCart(medicine.getMedicineId(), position);
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the data set
        return medicineList.size();
    }

    // ViewHolder class to hold references to the views in each item of the RecyclerView
    public class CardViewViewHolder extends RecyclerView.ViewHolder {
        ImageView medicineImage;
        TextView medicineName, medicinePrice;
        Button removeButton;

        public CardViewViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            medicineImage = itemView.findViewById(R.id.medicine_image);
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicinePrice = itemView.findViewById(R.id.medicine_price);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }

    // Remove item from the cart_items collection and update the UI
    private void removeFromCart(String medicineId, int position) {
        // Get the username of the logged-in user
        String username = code.getLoggedInUsername(context);

        // Access the cart_items collection and remove the item based on medicineId
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(username)
                .collection("cart_items")
                .whereEqualTo("medicineId", medicineId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Delete the document from the collection
                        document.getReference().delete();
                    }

                    // Remove the item from the list
                    MedicineModel removedMedicine = medicineList.remove(position);

                    // Notify the adapter
                    notifyDataSetChanged();

                    // Update the total price in the parent activity
                    ((CartViewActivity) context).updateTotalPrice();

                    // Display a toast message
                    Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure, e.g., display an error message
                    Toast.makeText(context, "Failed to remove item from cart", Toast.LENGTH_SHORT).show();
                });
    }
}
