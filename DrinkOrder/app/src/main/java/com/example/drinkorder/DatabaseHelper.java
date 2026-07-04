package com.example.drinkorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 数据库帮助类 — SQLite封装
 * 管理User/Product/Cart/Order四张表的建表、CRUD以及预置数据
 * 密码使用SHA-256加密存储
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "drink_order.db";
    private static final int DATABASE_VERSION = 3;

    // User表字段
    public static final String TABLE_USER = "User";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role";
    public static final String COLUMN_USER_NICKNAME = "nickname";

    // Product表字段
    public static final String TABLE_PRODUCT = "Product";
    public static final String COLUMN_PRODUCT_ID = "id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_CATEGORY = "category";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_RATING = "rating";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    public static final String COLUMN_PRODUCT_IMAGE_RES_ID = "imageResId";

    // Cart表字段
    public static final String TABLE_CART = "Cart";
    public static final String COLUMN_CART_ID = "id";
    public static final String COLUMN_CART_USER_ID = "userId";
    public static final String COLUMN_CART_PRODUCT_ID = "productId";
    public static final String COLUMN_CART_QUANTITY = "quantity";
    public static final String COLUMN_CART_PRICE = "price";
    public static final String COLUMN_CART_SWEETNESS = "sweetness";
    public static final String COLUMN_CART_ICE = "ice";
    public static final String COLUMN_CART_TOPPINGS = "toppings";
    public static final String COLUMN_CART_TOPPINGS_PRICE = "toppingsPrice";

    // Order表字段
    public static final String TABLE_ORDER = "Orders";
    public static final String COLUMN_ORDER_ID = "id";
    public static final String COLUMN_ORDER_USER_ID = "userId";
    public static final String COLUMN_ORDER_TIME = "orderTime";
    public static final String COLUMN_ORDER_TOTAL_PRICE = "totalPrice";
    public static final String COLUMN_ORDER_ITEMS = "items";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建User表
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_USERNAME + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_ROLE + " TEXT,"
                + COLUMN_USER_NICKNAME + " TEXT"
                + ")";
        db.execSQL(CREATE_USER_TABLE);

        // 创建Product表
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PRODUCT_NAME + " TEXT,"
                + COLUMN_PRODUCT_CATEGORY + " TEXT,"
                + COLUMN_PRODUCT_PRICE + " REAL,"
                + COLUMN_PRODUCT_RATING + " REAL,"
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT,"
                + COLUMN_PRODUCT_IMAGE_RES_ID + " INTEGER"
                + ")";
        db.execSQL(CREATE_PRODUCT_TABLE);

        // 创建Cart表
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CART_USER_ID + " INTEGER,"
                + COLUMN_CART_PRODUCT_ID + " INTEGER,"
                + COLUMN_CART_QUANTITY + " INTEGER,"
                + COLUMN_CART_PRICE + " REAL,"
                + COLUMN_CART_SWEETNESS + " TEXT DEFAULT '',"
                + COLUMN_CART_ICE + " TEXT DEFAULT '',"
                + COLUMN_CART_TOPPINGS + " TEXT DEFAULT '',"
                + COLUMN_CART_TOPPINGS_PRICE + " REAL DEFAULT 0"
                + ")";
        db.execSQL(CREATE_CART_TABLE);

        // 创建Order表
        String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDER + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ORDER_USER_ID + " INTEGER,"
                + COLUMN_ORDER_TIME + " TEXT,"
                + COLUMN_ORDER_TOTAL_PRICE + " REAL,"
                + COLUMN_ORDER_ITEMS + " TEXT"
                + ")";
        db.execSQL(CREATE_ORDER_TABLE);

        // 预置管理员账号和商品数据
        initData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
            onCreate(db);
            return;
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_CART + " ADD COLUMN " + COLUMN_CART_SWEETNESS + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_CART + " ADD COLUMN " + COLUMN_CART_ICE + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_CART + " ADD COLUMN " + COLUMN_CART_TOPPINGS + " TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_CART + " ADD COLUMN " + COLUMN_CART_TOPPINGS_PRICE + " REAL DEFAULT 0");
        }
    }

    /** 初始化预置数据：管理员账号 + 8款饮品样本 */
    private void initData(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USER_USERNAME, "admin");
        adminValues.put(COLUMN_USER_PASSWORD, hashPassword("123456"));
        adminValues.put(COLUMN_USER_ROLE, "admin");
        adminValues.put(COLUMN_USER_NICKNAME, "管理员");
        db.insert(TABLE_USER, null, adminValues);

        // 咖啡类
        insertProduct(db, "拿铁咖啡", "咖啡", 28.0, 4.8, "浓郁的意式浓缩与丝滑牛奶的完美结合", R.drawable.coffee1);
        insertProduct(db, "美式咖啡", "咖啡", 22.0, 4.5, "经典美式，纯粹的咖啡香", R.drawable.coffee2);
        insertProduct(db, "卡布奇诺", "咖啡", 32.0, 4.9, "绵密奶泡配上浓郁咖啡", R.drawable.coffee3);
        // 奶茶类
        insertProduct(db, "珍珠奶茶", "奶茶", 18.0, 4.6, "Q弹珍珠遇上醇香奶茶", R.drawable.milk_tea1);
        insertProduct(db, "红豆奶茶", "奶茶", 20.0, 4.7, "甜蜜红豆与丝滑奶茶", R.drawable.milk_tea2);
        insertProduct(db, "芒果奶茶", "奶茶", 22.0, 4.4, "新鲜芒果与奶茶的碰撞", R.drawable.milk_tea3);
        // 果茶类
        insertProduct(db, "柠檬茶", "果茶", 15.0, 4.3, "清爽柠檬，酸甜可口", R.drawable.fruit_tea1);
        insertProduct(db, "草莓果茶", "果茶", 18.0, 4.5, "新鲜草莓，果香四溢", R.drawable.fruit_tea2);
    }

    /** 插入一条商品记录 */
    private void insertProduct(SQLiteDatabase db, String name, String category, double price, double rating, String description, int imageResId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_CATEGORY, category);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_RATING, rating);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(COLUMN_PRODUCT_IMAGE_RES_ID, imageResId);
        db.insert(TABLE_PRODUCT, null, values);
    }

    /** SHA-256哈希加密 */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    /** 插入新用户（密码自动SHA-256加密） */
    public long insertUser(String username, String password, String role, String nickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, hashPassword(password));
        values.put(COLUMN_USER_ROLE, role);
        values.put(COLUMN_USER_NICKNAME, nickname);
        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }

    /** 验证用户登录（用户名+密码加密比对），返回User对象或null */
    public User checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null,
                COLUMN_USER_USERNAME + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                new String[]{username, hashPassword(password)}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE)));
            user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NICKNAME)));
        }
        cursor.close();
        db.close();
        return user;
    }

    /** 检查用户名是否已被注册 */
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null,
                COLUMN_USER_USERNAME + "=?",
                new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    /** 获取全部商品 */
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
                product.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_RATING)));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)));
                product.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_RES_ID)));
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productList;
    }

    /** 按分类获取商品 */
    public List<Product> getProductsByCategory(String category) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, null,
                COLUMN_PRODUCT_CATEGORY + "=?",
                new String[]{category}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
                product.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_RATING)));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)));
                product.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_RES_ID)));
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productList;
    }

    /** 按名称模糊搜索商品（LIKE %keyword%） */
    public List<Product> searchProducts(String keyword) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, null,
                COLUMN_PRODUCT_NAME + " LIKE ?",
                new String[]{"%" + keyword + "%"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
                product.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_RATING)));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)));
                product.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_RES_ID)));
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return productList;
    }

    /** 根据ID获取单个商品 */
    public Product getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, null,
                COLUMN_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)}, null, null, null);
        Product product = null;
        if (cursor.moveToFirst()) {
            product = new Product();
            product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
            product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
            product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY)));
            product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
            product.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_RATING)));
            product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)));
            product.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_RES_ID)));
        }
        cursor.close();
        db.close();
        return product;
    }

    /** 加入购物车 — 若相同商品且相同定制已在购物车中则累加数量，否则新增 */
    public long addToCart(int userId, int productId, int quantity, double price, String sweetness, String ice, String toppings, double toppingsPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CART, null,
                COLUMN_CART_USER_ID + "=? AND " + COLUMN_CART_PRODUCT_ID + "=?"
                        + " AND " + COLUMN_CART_SWEETNESS + "=?"
                        + " AND " + COLUMN_CART_ICE + "=?"
                        + " AND " + COLUMN_CART_TOPPINGS + "=?",
                new String[]{String.valueOf(userId), String.valueOf(productId),
                        sweetness, ice, toppings}, null, null, null);
        long id;
        if (cursor.moveToFirst()) {
            int existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY));
            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_QUANTITY, existingQuantity + quantity);
            id = db.update(TABLE_CART, values,
                    COLUMN_CART_ID + "=?",
                    new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_ID)))});
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_USER_ID, userId);
            values.put(COLUMN_CART_PRODUCT_ID, productId);
            values.put(COLUMN_CART_QUANTITY, quantity);
            values.put(COLUMN_CART_PRICE, price);
            values.put(COLUMN_CART_SWEETNESS, sweetness);
            values.put(COLUMN_CART_ICE, ice);
            values.put(COLUMN_CART_TOPPINGS, toppings);
            values.put(COLUMN_CART_TOPPINGS_PRICE, toppingsPrice);
            id = db.insert(TABLE_CART, null, values);
        }
        cursor.close();
        db.close();
        return id;
    }

    /** 获取购物车列表（JOIN Product表获取名称和图片） */
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT Cart.*, Product.name, Product.imageResId FROM Cart " +
                "JOIN Product ON Cart.productId = Product.id " +
                "WHERE Cart.userId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                CartItem cartItem = new CartItem();
                cartItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_ID)));
                cartItem.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_USER_ID)));
                cartItem.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_PRODUCT_ID)));
                cartItem.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY)));
                cartItem.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CART_PRICE)));
                cartItem.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                cartItem.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow("imageResId")));
                cartItem.setSweetness(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CART_SWEETNESS)));
                cartItem.setIce(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CART_ICE)));
                cartItem.setToppings(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CART_TOPPINGS)));
                cartItem.setToppingsPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CART_TOPPINGS_PRICE)));
                cartItemList.add(cartItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cartItemList;
    }

    /** 更新购物车项数量 */
    public void updateCartItemQuantity(int cartId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CART_QUANTITY, quantity);
        db.update(TABLE_CART, values,
                COLUMN_CART_ID + "=?",
                new String[]{String.valueOf(cartId)});
        db.close();
    }

    /** 删除购物车项 */
    public void deleteCartItem(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART,
                COLUMN_CART_ID + "=?",
                new String[]{String.valueOf(cartId)});
        db.close();
    }

    /** 清空某用户的购物车（下单后调用） */
    public void clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART,
                COLUMN_CART_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
    }

    /** 创建订单（商品清单以JSON格式存储） */
    public long createOrder(int userId, double totalPrice, String items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_USER_ID, userId);
        values.put(COLUMN_ORDER_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        values.put(COLUMN_ORDER_TOTAL_PRICE, totalPrice);
        values.put(COLUMN_ORDER_ITEMS, items);
        long id = db.insert(TABLE_ORDER, null, values);
        db.close();
        return id;
    }

    /** 获取某用户的所有订单（按时间倒序） */
    public List<OrderItem> getOrdersByUserId(int userId) {
        List<OrderItem> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ORDER, null,
                COLUMN_ORDER_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, COLUMN_ORDER_ID + " DESC");
        if (cursor.moveToFirst()) {
            do {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)));
                orderItem.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_USER_ID)));
                orderItem.setOrderTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TIME)));
                orderItem.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL_PRICE)));
                orderItem.setItems(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ITEMS)));
                orderList.add(orderItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orderList;
    }

    /** 管理员：新增商品 */
    public long addProduct(String name, String category, double price, double rating, String description, int imageResId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_CATEGORY, category);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_RATING, rating);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(COLUMN_PRODUCT_IMAGE_RES_ID, imageResId);
        long id = db.insert(TABLE_PRODUCT, null, values);
        db.close();
        return id;
    }

    /** 管理员：获取所有用户（排除管理员自己） */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null,
                COLUMN_USER_ROLE + "=?",
                new String[]{"user"}, null, null, COLUMN_USER_ID + " ASC");
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME)));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE)));
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NICKNAME)));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    /** 管理员：删除用户及其关联数据（购物车、订单） */
    public void deleteUserWithData(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_CART_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.delete(TABLE_ORDER, COLUMN_ORDER_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.delete(TABLE_USER, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    /** 管理员：删除商品 */
    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT,
                COLUMN_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)});
        db.close();
    }
}
