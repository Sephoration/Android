package com.example.pocketledger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

/** 完整 JSON 备份。读取和校验成功后才替换数据库。 */
final class LedgerBackup {
    private static final int LIMIT = 8 * 1024 * 1024;
    private static final String[] CATEGORIES = {"餐饮", "交通", "购物", "居住", "娱乐", "其他"};
    static void write(SQLiteDatabase db, long budget, OutputStream stream) throws JSONException, IOException {
        JSONObject root = new JSONObject(); root.put("format", "pocket-ledger"); root.put("version", 1); root.put("budget", budget);
        JSONArray entries = new JSONArray();
        try (Cursor c = db.query("entries", new String[]{"day", "category", "cents", "note"}, null, null, null, null, "day, id")) {
            while (c.moveToNext()) {
                JSONObject row = new JSONObject(); row.put("day", c.getString(0)); row.put("category", c.getString(1)); row.put("cents", c.getLong(2)); row.put("note", c.getString(3)); entries.put(row);
            }
        }
        root.put("entries", entries); byte[] bytes = root.toString().getBytes(StandardCharsets.UTF_8); if (bytes.length > LIMIT) throw new IOException("数据超过 8 MB，无法生成可恢复的备份"); stream.write(bytes); stream.flush();
    }
    static JSONObject read(InputStream stream) throws IOException, JSONException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); byte[] buf = new byte[8192]; int n;
        while ((n = stream.read(buf)) != -1) { if (bytes.size() + n > LIMIT) throw new IOException("备份文件超过 8 MB"); bytes.write(buf, 0, n); }
        JSONObject root = new JSONObject(new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        if (!"pocket-ledger".equals(root.optString("format")) || root.optInt("version", -1) != 1) throw new JSONException("不是可识别的记账备份");
        long budget = root.getLong("budget"); if (budget < 0 || budget > 100000000000L) throw new JSONException("预算无效");
        JSONArray entries = root.getJSONArray("entries"); if (entries.length() > 50000) throw new JSONException("记录数量超过上限");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US); format.setLenient(false);
        for (int i = 0; i < entries.length(); ++i) {
            JSONObject row = entries.getJSONObject(i); String day = row.getString("day"), category = row.getString("category"), note = row.getString("note");
            long cents = row.getLong("cents"); ParsePosition pos = new ParsePosition(0);
            if (day.length() != 10 || format.parse(day, pos) == null || pos.getIndex() != day.length()) throw new JSONException("第 " + (i + 1) + " 条日期无效");
            boolean valid = false; for (String item : CATEGORIES) if (item.equals(category)) valid = true;
            if (!valid || cents <= 0 || cents > 100000000000L || note.length() > 80) throw new JSONException("第 " + (i + 1) + " 条记录无效");
        }
        return root;
    }
    static void restore(SQLiteDatabase db, JSONObject root) throws JSONException {
        JSONArray entries = root.getJSONArray("entries"); db.beginTransaction();
        try {
            db.delete("entries", null, null);
            for (int i = 0; i < entries.length(); ++i) {
                JSONObject row = entries.getJSONObject(i); ContentValues values = new ContentValues();
                values.put("day", row.getString("day")); values.put("category", row.getString("category")); values.put("cents", row.getLong("cents")); values.put("note", row.getString("note"));
                db.insertOrThrow("entries", null, values);
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
}
