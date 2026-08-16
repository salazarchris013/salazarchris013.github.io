package com.example.csalazarcs360m5projecttwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Added categories, suppliers, and item_transactions tables linked to items via foreign keys
// Added JOIN-based query methods for retrieving related data and reporting

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "inventory.db";
    private static final int DB_VERSION = 2; // was 1

    // Items table
    private static final String TABLE_ITEMS = "items";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_QTY = "quantity";
    private static final String COL_DESC = "description";
    private static final String COL_ITEM_CATEGORY_ID = "category_id"; // new FK -> categories
    private static final String COL_ITEM_SUPPLIER_ID = "supplier_id"; // new FK -> suppliers

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Categories table
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COL_CATEGORY_ID = "id";
    private static final String COL_CATEGORY_NAME = "name";

    // Suppliers table
    private static final String TABLE_SUPPLIERS = "suppliers";
    private static final String COL_SUPPLIER_ID = "id";
    private static final String COL_SUPPLIER_NAME = "name";
    private static final String COL_SUPPLIER_CONTACT = "contact_info";

    // Item transactions table (log of quantity changes)
    private static final String TABLE_TRANSACTIONS = "item_transactions";
    private static final String COL_TXN_ID = "id";
    private static final String COL_TXN_ITEM_ID = "item_id"; // FK -> items
    private static final String COL_TXN_TYPE = "txn_type"; // "ADD" or "REMOVE"
    private static final String COL_TXN_CHANGE = "quantity_change";
    private static final String COL_TXN_TIMESTAMP = "txn_timestamp";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CATEGORY_NAME + " TEXT UNIQUE)");

        db.execSQL("CREATE TABLE " + TABLE_SUPPLIERS + " (" +
                COL_SUPPLIER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SUPPLIER_NAME + " TEXT, " +
                COL_SUPPLIER_CONTACT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_QTY + " INTEGER, " +
                COL_DESC + " TEXT, " +
                COL_ITEM_CATEGORY_ID + " INTEGER, " +
                COL_ITEM_SUPPLIER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_ITEM_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COL_CATEGORY_ID + ") ON DELETE SET NULL, " +
                "FOREIGN KEY(" + COL_ITEM_SUPPLIER_ID + ") REFERENCES " + TABLE_SUPPLIERS + "(" + COL_SUPPLIER_ID + ") ON DELETE SET NULL)");

        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_TXN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TXN_ITEM_ID + " INTEGER, " +
                COL_TXN_TYPE + " TEXT, " +
                COL_TXN_CHANGE + " INTEGER, " +
                COL_TXN_TIMESTAMP + " INTEGER, " +
                "FOREIGN KEY(" + COL_TXN_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COL_ID + ") ON DELETE CASCADE)");
    }

    // Before, updating the database erased all saved data.
    // Now, it only adds new tables or columns and keeps existing data.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + " (" +
                    COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CATEGORY_NAME + " TEXT UNIQUE)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SUPPLIERS + " (" +
                    COL_SUPPLIER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SUPPLIER_NAME + " TEXT, " +
                    COL_SUPPLIER_CONTACT + " TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + " (" +
                    COL_TXN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TXN_ITEM_ID + " INTEGER, " +
                    COL_TXN_TYPE + " TEXT, " +
                    COL_TXN_CHANGE + " INTEGER, " +
                    COL_TXN_TIMESTAMP + " INTEGER, " +
                    "FOREIGN KEY(" + COL_TXN_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COL_ID + ") ON DELETE CASCADE)");

            addColumnIfMissing(db, TABLE_ITEMS, COL_ITEM_CATEGORY_ID, "INTEGER");
            addColumnIfMissing(db, TABLE_ITEMS, COL_ITEM_SUPPLIER_ID, "INTEGER");
        }
    }

    private void addColumnIfMissing(SQLiteDatabase db, String table, String column, String type) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        boolean exists = false;
        int nameIndex = cursor.getColumnIndex("name");
        while (cursor.moveToNext()) {
            if (cursor.getString(nameIndex).equalsIgnoreCase(column)) {
                exists = true;
                break;
            }
        }
        cursor.close();
        if (!exists) {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        }
    }

    // User methods (unchanged)
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE username=? AND password=?",
                new String[]{username, password});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);
        long res = db.insert(TABLE_USERS, null, cv);
        return res != -1;
    }

    // Item methods
    public void addItem(String name, int qty, String desc) {
        addItemWithDetails(name, qty, desc, -1, -1);
    }

    public Cursor getItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
    }

    public void updateItemQuantity(int id, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        int previousQty = getCurrentQuantity(id);
        ContentValues cv = new ContentValues();
        cv.put(COL_QTY, qty);
        db.update(TABLE_ITEMS, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        recordTransaction(id, qty >= previousQty ? "ADD" : "REMOVE", qty - previousQty);
    }

    private int getCurrentQuantity(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_QTY + " FROM " + TABLE_ITEMS + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(id)});
        int qty = 0;
        if (c.moveToFirst()) {
            qty = c.getInt(0);
        }
        c.close();
        return qty;
    }

    public void updateItemDescription(int id, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DESC, desc);
        db.update(TABLE_ITEMS, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Returns the existing category id for this name, or creates it and returns the new id
    public long getOrCreateCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_CATEGORY_ID + " FROM " + TABLE_CATEGORIES + " WHERE " + COL_CATEGORY_NAME + "=?",
                new String[]{name});
        if (c.moveToFirst()) {
            long id = c.getLong(0);
            c.close();
            return id;
        }
        c.close();
        ContentValues cv = new ContentValues();
        cv.put(COL_CATEGORY_NAME, name);
        return db.insert(TABLE_CATEGORIES, null, cv);
    }

    // Returns the existing supplier id for this name, or creates it and returns the new id
    public long getOrCreateSupplier(String name, String contactInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_SUPPLIER_ID + " FROM " + TABLE_SUPPLIERS + " WHERE " + COL_SUPPLIER_NAME + "=?",
                new String[]{name});
        if (c.moveToFirst()) {
            long id = c.getLong(0);
            c.close();
            return id;
        }
        c.close();
        ContentValues cv = new ContentValues();
        cv.put(COL_SUPPLIER_NAME, name);
        cv.put(COL_SUPPLIER_CONTACT, contactInfo);
        return db.insert(TABLE_SUPPLIERS, null, cv);
    }

    // Saves the category, saves the supplier, and saves the inventory record linked to both, in one call
    public long addItemWithDetails(String name, int qty, String desc, long categoryId, long supplierId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_QTY, qty);
        cv.put(COL_DESC, desc);
        if (categoryId != -1) cv.put(COL_ITEM_CATEGORY_ID, categoryId);
        if (supplierId != -1) cv.put(COL_ITEM_SUPPLIER_ID, supplierId);
        long itemId = db.insert(TABLE_ITEMS, null, cv);
        recordTransaction(itemId, "ADD", qty);
        return itemId;
    }

    // Gets items along with their category and supplier names in one query.
    public Cursor getItemsWithDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT i." + COL_ID + ", i." + COL_NAME + ", i." + COL_QTY + ", i." + COL_DESC + ", " +
                "c." + COL_CATEGORY_NAME + " AS category_name, " +
                "s." + COL_SUPPLIER_NAME + " AS supplier_name " +
                "FROM " + TABLE_ITEMS + " i " +
                "LEFT JOIN " + TABLE_CATEGORIES + " c ON i." + COL_ITEM_CATEGORY_ID + " = c." + COL_CATEGORY_ID + " " +
                "LEFT JOIN " + TABLE_SUPPLIERS + " s ON i." + COL_ITEM_SUPPLIER_ID + " = s." + COL_SUPPLIER_ID + " " +
                "ORDER BY i." + COL_NAME + " ASC";
        return db.rawQuery(query, null);
    }

    // Full add/remove history for one item, most recent first
    public Cursor getTransactionHistory(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_TXN_ID + ", " + COL_TXN_TYPE + ", " + COL_TXN_CHANGE + ", " + COL_TXN_TIMESTAMP +
                " FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COL_TXN_ITEM_ID + "=? ORDER BY " + COL_TXN_TIMESTAMP + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(itemId)});
    }

    // Total quantity on hand grouped by category, for a summary/reporting view
    public Cursor getQuantityByCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c." + COL_CATEGORY_NAME + " AS category_name, SUM(i." + COL_QTY + ") AS total_quantity " +
                "FROM " + TABLE_ITEMS + " i " +
                "JOIN " + TABLE_CATEGORIES + " c ON i." + COL_ITEM_CATEGORY_ID + " = c." + COL_CATEGORY_ID + " " +
                "GROUP BY c." + COL_CATEGORY_NAME + " " +
                "ORDER BY total_quantity DESC";
        return db.rawQuery(query, null);
    }

    private void recordTransaction(long itemId, String type, int quantityChange) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TXN_ITEM_ID, itemId);
        cv.put(COL_TXN_TYPE, type);
        cv.put(COL_TXN_CHANGE, quantityChange);
        cv.put(COL_TXN_TIMESTAMP, System.currentTimeMillis());
        db.insert(TABLE_TRANSACTIONS, null, cv);
    }
}