package com.example.healthcare.Medicine.BuyMedicine;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Medicine.CartView.CartViewActivity;
import com.example.healthcare.Model.MedicineModel;
import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.android.gms.tasks.Tasks;
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
import com.google.firebase.firestore.Source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BuyMedicineActivity extends AppCompatActivity {

    private RecyclerView medicineRecycler;
    private FirebaseFirestore db;
    private List<MedicineModel> medicineModelList;
    private BuyMedicineAdapter buyMedicineAdapter;
    private TextView cartItemCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine);

        // Initialize views and variables
        cartItemCountTextView = findViewById(R.id.cartItemCountTextView);
        medicineRecycler = findViewById(R.id.medicine_recycler);
        medicineRecycler.setLayoutManager(new LinearLayoutManager(this));
        medicineModelList = new ArrayList<>();
        buyMedicineAdapter = new BuyMedicineAdapter(this, medicineModelList);
        medicineRecycler.setAdapter(buyMedicineAdapter);
        EditText searchEditText = findViewById(R.id.search_bar);
        TextView internetText = findViewById(R.id.internet_text);
        db = FirebaseFirestore.getInstance();

        // Check internet connection
        checkInternet(internetText);

        ImageView cartImageView = findViewById(R.id.cartImageView);

        // Set click listener for cart icon
        cartImageView.setOnClickListener(v -> {
            Intent intent = new Intent(BuyMedicineActivity.this, CartViewActivity.class);
            startActivity(intent);
        });

        // Set up search functionality
        setupSearchEditText(searchEditText);

        // Set up item click listeners
        buyMedicineAdapter.setOnItemClickListener(new BuyMedicineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click if needed
            }

            @Override
            public void onAddToCartButtonClick(int position) {
                // Handle "Add to Cart" button click
                addToCart(position);
            }

            @Override
            public void onBuyButtonClick(int position) {
                handleBuyButtonClick(position);
            }
        });

        // Initial setup of right drawable visibility
        updateRightDrawableVisibility(searchEditText);

        // Set a TextWatcher on the search bar
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearchAction(v, internetText, searchEditText);
                return true;
            }
            return false;
        });

        // Load all medicines initially
        loadAllMedicines();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCartItemCount();
    }

    // Helper method to check internet connection
    private void checkInternet(TextView text) {
        boolean isInternetConnected = code.isInternetConnected(this);

        runOnUiThread(() -> {
            if (isInternetConnected) {
                text.setVisibility(View.GONE);
            } else {
                text.setVisibility(View.VISIBLE);
                Toast.makeText(BuyMedicineActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle purchase when "Buy" button is clicked
    private void handleBuyButtonClick(int position) {
        MedicineModel selectedMedicine = medicineModelList.get(position);

        // Check if the user's address is empty
        String username = code.getLoggedInUsername(this);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(username);

        userReference.child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userAddress = dataSnapshot.getValue(String.class);

                if (userAddress == null || userAddress.isEmpty()) {
                    // Display a toast to prompt the user to update their address in settings
                    Toast.makeText(BuyMedicineActivity.this, "Please update your address in settings before making a purchase.", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with the purchase
                    handlePurchase(selectedMedicine);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
                Toast.makeText(BuyMedicineActivity.this, "Failed to retrieve user address", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Add selected medicine to the cart
    private void addToCart(int position) {
        MedicineModel selectedMedicine = medicineModelList.get(position);
        String username = code.getLoggedInUsername(this);

        String medicineId = selectedMedicine.getMedicineId();

        if (medicineId != null && !medicineId.isEmpty()) {
            CollectionReference userCartCollection = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(username)
                    .collection("cart_items");

            Map<String, Object> cartItemData = new HashMap<>();
            cartItemData.put("medicineId", medicineId);

            userCartCollection.add(cartItemData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(BuyMedicineActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BuyMedicineActivity.this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(BuyMedicineActivity.this, "Unable to retrieve medicine information", Toast.LENGTH_SHORT).show();
        }
        checkCartItemCount();
    }

    // Search for medicines based on user input
    private void searchMedicines(String searchText) {
        medicineModelList.clear();
        CollectionReference medicinesCollection = db.collection("Medicine");

        medicinesCollection
                .orderBy("medicineName")
                .get(Source.SERVER)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        MedicineModel medicineModel = document.toObject(MedicineModel.class);
                        if (medicineModel.getMedicineName().toLowerCase().contains(searchText.toLowerCase())) {
                            medicineModelList.add(medicineModel);
                        }
                    }
                    buyMedicineAdapter.notifyDataSetChanged();
                    showNoResultText(medicineModelList.isEmpty());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BuyMedicineActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Show or hide the "No result" text view based on search results
    private void showNoResultText(boolean show) {
        TextView noResultText = findViewById(R.id.noresult_text);
        noResultText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // Load all medicines from Firestore
    private void loadAllMedicines() {
        medicineModelList.clear();
        CollectionReference medicinesCollection = db.collection("Medicine");

        medicinesCollection.get(Source.SERVER)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        MedicineModel medicineModel = document.toObject(MedicineModel.class);
                        medicineModelList.add(medicineModel);
                    }
                    buyMedicineAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BuyMedicineActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Check the number of items in the user's cart and update UI
    private void checkCartItemCount() {
        String username = code.getLoggedInUsername(BuyMedicineActivity.this);
        CollectionReference userCartCollection = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(username)
                .collection("cart_items");

        userCartCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        cartItemCountTextView.setVisibility(View.VISIBLE);
                    } else {
                        cartItemCountTextView.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("BuyMedicineActivity", "Error checking cart items", e);
                });
    }

    // Update the visibility of the right drawable in the search bar
    private void updateRightDrawableVisibility(EditText searchBar) {
        if (searchBar.getText().length() > 0) {
            searchBar.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_search_24, 0, R.drawable.baseline_clear_24, 0);
        } else {
            searchBar.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_search_24, 0, 0, 0);
        }
    }

    // Handle the purchase of medicine
    private void handlePurchase(MedicineModel selectedMedicine) {
        String username = code.getLoggedInUsername(this);

        CollectionReference orderedItemsCollection = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(username)
                .collection("ordered_items");

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DocumentReference newOrderRef = orderedItemsCollection.document();

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderDate", currentDate);
        orderData.put("img_url", selectedMedicine.getImg_url());
        orderData.put("medicineId", selectedMedicine.getMedicineId());
        orderData.put("medicineName", selectedMedicine.getMedicineName());
        orderData.put("medicinePrice", selectedMedicine.getMedicinePrice());
        orderData.put("medicineQuantity", selectedMedicine.getMedicineQuantity());
        orderData.put("medicineType", selectedMedicine.getMedicineType());
        orderData.put("medicineUnit", selectedMedicine.getMedicineUnit());

        newOrderRef.set(orderData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BuyMedicineActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BuyMedicineActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                    Log.e("BuyMedicineActivity", "Error placing order", e);
                });
    }

    // Set up search bar functionality
    private void setupSearchEditText(EditText searchEditText) {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed, but must be implemented
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Handle the visibility of the right drawable based on the text length
                updateRightDrawableVisibility(searchEditText);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed, but must be implemented
            }
        });

        // Set a click listener on the right drawable
        searchEditText.setOnTouchListener((view, event) -> {
            Drawable rightDrawable = searchEditText.getCompoundDrawables()[2];
            if (event.getAction() == MotionEvent.ACTION_UP && rightDrawable != null) {
                // Check if the click is on the right drawable
                if (event.getRawX() >= (searchEditText.getRight() - rightDrawable.getBounds().width())) {
                    // Clear the search bar
                    searchEditText.setText("");
                    loadAllMedicines();
                    return true;
                }
            }
            return false;
        });
    }

    // Handle search action in the search bar
    private void handleSearchAction(View v, TextView internetText, EditText searchEditText) {
        checkInternet(internetText);
        String searchText = searchEditText.getText().toString().trim();
        if (!searchText.isEmpty()) {
            searchMedicines(searchText);
        } else {
            loadAllMedicines();
        }
    }
}
