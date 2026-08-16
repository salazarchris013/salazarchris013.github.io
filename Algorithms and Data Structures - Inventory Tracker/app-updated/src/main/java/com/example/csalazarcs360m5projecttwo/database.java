package com.example.csalazarcs360m5projecttwo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class database extends AppCompatActivity {

    InventoryRepository repository;
    EditText itemName, itemQty, itemDesc, searchBox;
    Button addItemBtn, smsBtn, sortNameBtn, sortQtyBtn;
    GridLayout inventoryGrid;

    private static final int LOW_STOCK_THRESHOLD = 10;

    // In-memory inventory list, used for sorting and searching
    private List<InventoryItem> masterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        repository = new InventoryRepository(this);
        itemName = findViewById(R.id.itemName);
        itemQty = findViewById(R.id.itemQty);
        itemDesc = findViewById(R.id.itemDesc);
        addItemBtn = findViewById(R.id.addItemBtn);
        smsBtn = findViewById(R.id.smsBtn);
        inventoryGrid = findViewById(R.id.inventoryGrid);
        searchBox = findViewById(R.id.searchBox);
        sortNameBtn = findViewById(R.id.sortNameBtn);
        sortQtyBtn = findViewById(R.id.sortQtyBtn);

        // Add item button
        addItemBtn.setOnClickListener(v -> {
            String name = itemName.getText().toString().trim();
            String desc = itemDesc.getText().toString().trim();
            String qtyStr = itemQty.getText().toString().trim();

            InputValidator.ValidationResult result = InputValidator.validateItem(name, qtyStr, desc);
            if (!result.isValid()) {
                Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            repository.addItem(name, Integer.parseInt(qtyStr), desc);

            // Clear input fields after adding
            itemName.setText("");
            itemQty.setText("");
            itemDesc.setText("");
            loadInventory();
        });

        // SMS Button navigation
        smsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, sms.class))
        );

        // Sort alphabetically by item name
        sortNameBtn.setOnClickListener(v -> {
            masterList.sort(Comparator.comparing(InventoryItem::getName, String.CASE_INSENSITIVE_ORDER));
            applyFilterAndRender();
        });

        // Sort by quantity, lowest first, so low-stock items surface quickly
        sortQtyBtn.setOnClickListener(v -> {
            masterList.sort(Comparator.comparingInt(InventoryItem::getQuantity));
            applyFilterAndRender();
        });

        // Live search as the user types
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                applyFilterAndRender();
            }
        });

        loadInventory();
    }

    // Helper to control column layout
    private GridLayout.LayoutParams makeParams(int col, float weight) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1),
                GridLayout.spec(col, 1, weight)
        );
        params.width = 0;
        params.setMargins(4, 4, 4, 4);
        return params;
    }

    // Reloads inventory from the database into masterList
    private void loadInventory() {
        masterList = repository.getAllItemsAsList();
        applyFilterAndRender();
    }

    // Filters masterList by the search box text, then renders
    private void applyFilterAndRender() {
        String query = searchBox.getText().toString().trim().toLowerCase();
        List<InventoryItem> displayed = new ArrayList<>();
        for (InventoryItem item : masterList) {
            if (query.isEmpty() || item.getName().toLowerCase().contains(query)) {
                displayed.add(item);
            }
        }
        renderGrid(displayed);
    }

    // Builds the grid from an already-loaded list of items
    private void renderGrid(List<InventoryItem> items) {
        inventoryGrid.removeAllViews();
        inventoryGrid.setColumnCount(4);

        String[] headers = {"Item", "Qty", "Description", "Delete"};
        float[] weights = {2f, 1f, 3f, 1f};

        // Header row
        for (int i = 0; i < headers.length; i++) {
            TextView h = new TextView(this);
            h.setText(headers[i]);
            h.setTypeface(null, android.graphics.Typeface.BOLD);
            h.setLayoutParams(makeParams(i, weights[i]));
            inventoryGrid.addView(h);
        }

        for (InventoryItem item : items) {
            int id = item.getId();
            String name = item.getName();
            int qty = item.getQuantity();
            String desc = item.getDescription();

            // Item name
            TextView tName = new TextView(this);
            tName.setText(name);
            tName.setLayoutParams(makeParams(0, weights[0]));

            // Quantity which is editable
            EditText tQty = new EditText(this);
            tQty.setInputType(InputType.TYPE_CLASS_NUMBER);
            tQty.setText(String.valueOf(qty));
            tQty.setLayoutParams(makeParams(1, weights[1]));
            tQty.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) return;

                String qtyText = tQty.getText().toString();
                InputValidator.ValidationResult result = InputValidator.validateQuantity(qtyText);
                if (!result.isValid()) {
                    Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    // Revert to last valid quantity if input is invalid
                    tQty.setText(String.valueOf(qty));
                    return;
                }

                int newQty = Integer.parseInt(qtyText.trim());
                repository.updateQuantity(id, newQty);
                item.setQuantity(newQty);

                // Alert when quantity is at or below the low-stock threshold
                if (repository.isBelowThreshold(newQty, LOW_STOCK_THRESHOLD)) {
                    String phone = sms.getSavedPhoneNumber(this);
                    if (!phone.isEmpty())
                        sms.sendSms(this, phone,
                                "Low Inventory Alert: \"" + name + "\" has reached " + newQty + " left!");
                }
            });

            // Description which is editable
            EditText tDesc = new EditText(this);
            tDesc.setText(desc != null ? desc : "");
            tDesc.setLayoutParams(makeParams(2, weights[2]));
            tDesc.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    repository.updateDescription(id, tDesc.getText().toString());
                    item.setDescription(tDesc.getText().toString());
                }
            });

            // Delete Button
            Button delBtn = new Button(this);
            delBtn.setText("X");
            delBtn.setLayoutParams(makeParams(3, weights[3]));
            delBtn.setOnClickListener(v -> { repository.deleteItem(id); loadInventory(); });

            inventoryGrid.addView(tName);
            inventoryGrid.addView(tQty);
            inventoryGrid.addView(tDesc);
            inventoryGrid.addView(delBtn);
        }
    }
}