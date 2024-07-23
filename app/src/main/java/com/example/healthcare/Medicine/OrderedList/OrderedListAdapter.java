package com.example.healthcare.Medicine.OrderedList;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderedListAdapter extends RecyclerView.Adapter<OrderedListAdapter.CardViewViewHolder> {

    private Context context;
    private List<MedicineModel> orderedItemList;

    public OrderedListAdapter(Context context, List<MedicineModel> orderedItemList) {
        this.context = context;
        this.orderedItemList = orderedItemList;
    }

    @NonNull
    @Override
    public CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ordered_item, parent, false);
        return new CardViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewViewHolder holder, int position) {
        MedicineModel orderedItem = orderedItemList.get(position);

        // Load image using Glide library
        Glide.with(context).load(orderedItem.getImg_url()).into(holder.medicineImage);

        // Set medicine details
        holder.medicineName.setText(orderedItem.getMedicineName());
        holder.medicinePrice.setText(orderedItem.getMedicinePrice());
        String typeText = getTypeText(orderedItem);
        holder.medicineType.setText(typeText);

        // Calculate and show the date after 7 days using Calendar
        setDeliveryDate(holder, orderedItem.getOrderDate());

        // Set click listener for the "Remove" button
        holder.removeButton.setOnClickListener(view -> {
            // Call a method to remove the item from the ordered_items collection
            removeFromOrderedItems(position);
        });
    }

    @Override
    public int getItemCount() {
        return orderedItemList.size();
    }

    public class CardViewViewHolder extends RecyclerView.ViewHolder {
        ImageView medicineImage;
        TextView medicineName, medicinePrice, medicineType, orderDate;
        Button removeButton;

        public CardViewViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            medicineImage = itemView.findViewById(R.id.order_image);
            medicineName = itemView.findViewById(R.id.order_name);
            medicinePrice = itemView.findViewById(R.id.order_price);
            medicineType = itemView.findViewById(R.id.order_type);
            removeButton = itemView.findViewById(R.id.remove_button);
            orderDate = itemView.findViewById(R.id.order_date);
        }
    }

    private String getTypeText(MedicineModel orderedItem) {
        // Generate type text based on medicine type
        if ("tablets".equalsIgnoreCase(orderedItem.getMedicineType())) {
            return "Strip of " + orderedItem.getMedicineQuantity() + " " + orderedItem.getMedicineUnit();
        } else if ("syrup".equalsIgnoreCase(orderedItem.getMedicineType())) {
            return "Bottle of " + orderedItem.getMedicineQuantity() + " " + orderedItem.getMedicineUnit();
        } else {
            // Default text or handle other types if needed
            return orderedItem.getMedicineQuantity() + " " + orderedItem.getMedicineUnit();
        }
    }

    private void setDeliveryDate(CardViewViewHolder holder, String orderDate) {
        try {
            // Parse order date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(orderDate);

            // Calculate and show the date after 7 days using Calendar
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 7);

            // Set the calculated date directly
            String newDateString = sdf.format(calendar.getTime());
            holder.orderDate.setText(newDateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeFromOrderedItems(int position) {
        String username = code.getLoggedInUsername(context);

        // Access the ordered_items collection and get the document at the specified position
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(username)
                .collection("ordered_items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (position >= 0 && position < documents.size()) {
                            // Get the document reference at the specified position
                            String documentId = documents.get(position).getId();

                            // Delete the document from the collection using document ID
                            FirebaseFirestore.getInstance()
                                    .collection("Users")
                                    .document(username)
                                    .collection("ordered_items")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Remove the item from the list
                                        orderedItemList.remove(position);

                                        // Notify the adapter
                                        notifyDataSetChanged();

                                        // Display a toast message
                                        Toast.makeText(context, "Item removed from ordered items", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure, e.g., display an error message
                                        Toast.makeText(context, "Failed to remove item from ordered items", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Handle the case where the position is out of bounds
                            Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
