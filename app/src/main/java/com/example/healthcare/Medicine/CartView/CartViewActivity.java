package com.example.healthcare.Medicine.CartView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Medicine.BuyMedicine.BuyMedicineActivity;
import com.example.healthcare.Model.MedicineModel;
import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartViewActivity extends AppCompatActivity {

    private RecyclerView cartViewRecycler;
    private CartViewAdapter cartViewAdapter;
    private List<MedicineModel> medicineList;
    private TextView totalPriceTextView;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);

        // Initialize views and variables
        cartViewRecycler = findViewById(R.id.cartview_recycler);
        medicineList = new ArrayList<>();
        cartViewAdapter = new CartViewAdapter(this, medicineList);
        cartViewRecycler.setLayoutManager(new LinearLayoutManager(this));
        cartViewRecycler.setAdapter(cartViewAdapter);

        totalPriceTextView = findViewById(R.id.total_price_text_view);
        buyButton = findViewById(R.id.buy_button);

        // Get the medicine ID from the intent
        String medicineId = getIntent().getStringExtra("medicineId");

        if (medicineId != null && !medicineId.isEmpty()) {
            // Retrieve medicine details based on the ID
            loadMedicineDetails(medicineId);
        } else {
            // Load medicine ID from the cart_items collection
            loadMedicineIdFromCartItems(code.getLoggedInUsername(this));
        }

        // Set a click listener for the "Buy" button
        buyButton.setOnClickListener(view -> {
            // Call a method to handle the purchase logic, e.g., start a new activity or perform a transaction
            handlePurchase();
        });
    }

    // Load medicine details based on the given medicine ID
    private void loadMedicineDetails(String medicineId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Medicine")
                .document(medicineId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            MedicineModel medicineModel = documentSnapshot.toObject(MedicineModel.class);

                            if (medicineModel != null) {
                                // Add the medicine to the list and notify the adapter
                                medicineList.clear();
                                medicineList.add(medicineModel);
                                cartViewAdapter.notifyDataSetChanged();
                                // Update the total price
                                updateTotalPrice();
                            } else {
                                Log.e("CartViewActivity", "Medicine details not found");
                            }
                        } else {
                            Log.e("CartViewActivity", "Medicine not found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("CartViewActivity", "Error loading medicine details", e);
                    }
                });
    }

    // Load medicine ID from the cart_items collection
    private void loadMedicineIdFromCartItems(String username) {
        CollectionReference userCartCollection = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(username)
                .collection("cart_items");

        userCartCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String medicineId = document.getString("medicineId");
                        if (medicineId != null && !medicineId.isEmpty()) {
                            // Now you have the medicine ID, you can use it as needed
                            Log.d("CartViewActivity", "Medicine ID from cart_items: " + medicineId);
                            // Call a method to load medicine details based on this ID
                            loadMedicineDetailsFromCart(medicineId);
                        } else {
                            Log.e("CartViewActivity", "Invalid medicine ID in cart_items");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CartViewActivity", "Error loading cart_items", e);
                });
    }

    // Load medicine details based on the medicine ID from the cart_items collection
    private void loadMedicineDetailsFromCart(String medicineId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Medicine")
                .document(medicineId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            MedicineModel medicineModel = documentSnapshot.toObject(MedicineModel.class);

                            if (medicineModel != null) {
                                // Add the medicine to the list and notify the adapter
                                medicineList.add(medicineModel);
                                cartViewAdapter.notifyDataSetChanged();
                                // Update the total price
                                updateTotalPrice();
                            } else {
                                Log.e("CartViewActivity", "Medicine details not found");
                            }
                        } else {
                            Log.e("CartViewActivity", "Medicine not found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("CartViewActivity", "Error loading medicine details", e);
                    }
                });
    }

    // Update the total price displayed in the UI
    public void updateTotalPrice() {
        double totalPrice = 0;
        for (MedicineModel medicine : medicineList) {
            // Assuming that the price is in a format like "₹42.8" and needs to be converted to a numeric value
            double price = Double.parseDouble(medicine.getMedicinePrice().replace("₹", ""));
            totalPrice += price;
        }

        String formattedTotalPrice;
        if (medicineList.isEmpty()) {
            // Set "Please add item" when the cart is empty
            formattedTotalPrice = "Please add item";
        } else {
            // Format the total price to display two decimal places
            formattedTotalPrice = String.format(Locale.getDefault(), "%.2f", totalPrice);
            formattedTotalPrice = "Total Price: ₹" + formattedTotalPrice;
        }

        // Set the formatted total price to the TextView
        totalPriceTextView.setText(formattedTotalPrice);
    }

    // Handle the purchase logic
    private void handlePurchase() {
        // Check if there are items in the cart before attempting to place an order
        if (medicineList.isEmpty()) {
            Toast.makeText(this, "Your cart is empty. Add items before placing an order.", Toast.LENGTH_SHORT).show();
        } else {
            // Check if the user's address is empty
            String username = code.getLoggedInUsername(this);
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(username);

            userReference.child("address").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userAddress = dataSnapshot.getValue(String.class);

                    if (userAddress == null || userAddress.isEmpty()) {
                        // Display a toast to prompt the user to update their address in settings
                        Toast.makeText(CartViewActivity.this, "Please update your address in settings before placing an order.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Call the method to handle the purchase logic and pass the list of selected medicines
                        handlePurchase(medicineList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error if needed
                    Toast.makeText(CartViewActivity.this, "Failed to retrieve user address", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Handle the purchase logic for a list of selected medicines
    private void handlePurchase(List<MedicineModel> selectedMedicines) {
        String username = code.getLoggedInUsername(this);

        // Create a new collection reference for ordered_items
        CollectionReference orderedItemsCollection = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(username)
                .collection("ordered_items");

        // Get the current date and time
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Iterate through the selected medicines and save each one separately
        for (MedicineModel selectedMedicine : selectedMedicines) {
            // Create a new document in the ordered_items collection with an auto-generated ID
            DocumentReference newOrderRef = orderedItemsCollection.document();

            // Create a map to represent the order data for the current medicine
            Map<String, Object> orderData = getMedicineDetails(selectedMedicine);

            // Add the order data to the ordered_items collection for the current medicine
            newOrderRef.set(orderData)
                    .addOnSuccessListener(aVoid -> {
                        // Handle individual medicine order placement success
                    })
                    .addOnFailureListener(e -> {
                        // Handle individual medicine order placement failure
                        Log.e("CartViewActivity", "Error placing order for medicine " + selectedMedicine.getMedicineId(), e);
                    });
        }

        // Additional actions after all orders are placed, if needed
        Toast.makeText(CartViewActivity.this, "Orders placed successfully", Toast.LENGTH_SHORT).show();

        // Clear the cart or perform any other necessary actions
        clearCart();
    }

    // Method to get details of a single medicine
    private Map<String, Object> getMedicineDetails(MedicineModel medicine) {
        Map<String, Object> medicineDetails = new HashMap<>();
        medicineDetails.put("medicineId", medicine.getMedicineId());
        medicineDetails.put("medicineName", medicine.getMedicineName());
        medicineDetails.put("medicinePrice", medicine.getMedicinePrice());
        medicineDetails.put("img_url", medicine.getImg_url());
        medicineDetails.put("medicineQuantity", medicine.getMedicineQuantity());
        medicineDetails.put("medicineType", medicine.getMedicineType());
        medicineDetails.put("medicineUnit", medicine.getMedicineUnit());
        // Add other details as needed

        return medicineDetails;
    }

    // Method to clear the cart after a successful order
    private void clearCart() {
        // Perform actions to clear the cart, e.g., remove items from cart_items collection
        CollectionReference userCartCollection = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(code.getLoggedInUsername(this))
                .collection("cart_items");

        userCartCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    // Clear the local list
                    medicineList.clear();
                    // Notify the adapter
                    cartViewAdapter.notifyDataSetChanged();
                    // Update the total price
                    updateTotalPrice();
                    Toast.makeText(CartViewActivity.this, "Cart cleared successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartViewActivity", "Error clearing cart_items", e);
                    Toast.makeText(CartViewActivity.this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
                });
    }
}
