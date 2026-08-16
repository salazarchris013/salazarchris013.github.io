package com.example.csalazarcs360m5projecttwo;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// Imports inventory items from a CSV file.
// Creates categories and suppliers automatically if they don't already exist.
public class InventoryImporter {

    private InventoryImporter() {
    }

    // Returns the number of items imported.
    public static int importFromRaw(Context context, DatabaseHelper dbHelper, int rawResId) {
        int importedCount = 0;

        try (InputStream inputStream = context.getResources().openRawResource(rawResId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                // Skip the header row.
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length < 5) {
                    continue;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(fields[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                long categoryId = fields[3].trim().isEmpty()
                        ? -1
                        : dbHelper.getOrCreateCategory(fields[3].trim());

                long supplierId = fields[4].trim().isEmpty()
                        ? -1
                        : dbHelper.getOrCreateSupplier(fields[4].trim(), "");

                dbHelper.addItemWithDetails(
                        fields[0].trim(),
                        quantity,
                        fields[2].trim(),
                        categoryId,
                        supplierId
                );

                importedCount++;
            }

        } catch (IOException e) {
            // Return the number of items imported before the error.
        }

        return importedCount;
    }
}