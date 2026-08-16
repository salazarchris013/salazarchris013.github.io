package com.example.csalazarcs360m5projecttwo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class database extends AppCompatActivity {

    InventoryRepository repository;
    EditText itemName, itemQty, itemDesc;
    Button addItemBtn, smsBtn;
    GridLayout inventoryGrid;

    private static final int LOW_STOCK_THRESHOLD = 10;

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
            refreshGrid();
        });

        // SMS Button navigation
        smsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, sms.class))
        );

        refreshGrid();
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

    private void refreshGrid() {
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

        Cursor c = repository.getAllItems();
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String name = c.getString(c.getColumnIndexOrThrow("name"));
            int qty = c.getInt(c.getColumnIndexOrThrow("quantity"));
            String desc = c.getString(c.getColumnIndexOrThrow("description"));

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
                if (!hasFocus) repository.updateDescription(id, tDesc.getText().toString());
            });

            // Delete Button
            Button delBtn = new Button(this);
            delBtn.setText("X");
            delBtn.setLayoutParams(makeParams(3, weights[3]));
            delBtn.setOnClickListener(v -> { repository.deleteItem(id); refreshGrid(); });

            inventoryGrid.addView(tName);
            inventoryGrid.addView(tQty);
            inventoryGrid.addView(tDesc);
            inventoryGrid.addView(delBtn);
        }
        c.close();
    }
}