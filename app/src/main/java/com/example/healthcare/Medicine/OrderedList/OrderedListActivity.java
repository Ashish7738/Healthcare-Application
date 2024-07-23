package com.example.healthcare.Medicine.OrderedList;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Model.MedicineModel;
import com.example.healthcare.R;
import com.example.healthcare.code;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderedListActivity extends AppCompatActivity {

    private TextView logoText;
    private RecyclerView orderedRecycler;
    private OrderedListAdapter orderedListAdapter;
    private List<MedicineModel> orderedItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_list);

        // Initialize views and adapter
        logoText = findViewById(R.id.logo_text2);
        orderedRecycler = findViewById(R.id.ordered_recycler);
        orderedItemList = new ArrayList<>();
        orderedListAdapter = new OrderedListAdapter(this, orderedItemList);
        orderedRecycler.setLayoutManager(new LinearLayoutManager(this));
        orderedRecycler.setAdapter(orderedListAdapter);

        // Load ordered items from Firestore
        loadOrderedItems();
    }

    private void loadOrderedItems() {
        // Get the current user's username
        String username = code.getLoggedInUsername(this);

        // Set Firestore collection path for ordered items
        CollectionReference orderedItemsCollection = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(username)
                .collection("ordered_items");

        // Fetch ordered items from Firestore
        orderedItemsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Clear the list before adding items
                    orderedItemList.clear();

                    // Add ordered items to the list
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        MedicineModel orderedItem = document.toObject(MedicineModel.class);
                        orderedItemList.add(orderedItem);
                    }

                    // Update the adapter and show/hide 'no result' text
                    orderedListAdapter.notifyDataSetChanged();
                    showNoResultText(orderedItemList.isEmpty());
                } else {
                    // Display a Toast message on task failure
                    Toast.makeText(OrderedListActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showNoResultText(boolean show) {
        // Show or hide 'no result' text based on the list's emptiness
        TextView noResultText = findViewById(R.id.noresult_text);
        noResultText.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
