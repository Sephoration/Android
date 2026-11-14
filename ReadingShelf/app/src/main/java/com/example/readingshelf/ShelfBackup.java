package com.example.readingshelf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

/** 备份包含书目和阅读记录；恢复前完整检查引用和字段。 */
final class ShelfBackup {
    private static final int LIMIT = 8 * 1024 * 1024;
    static void write(SQLiteDatabase db, OutputStream stream) throws JSONException, IOException {
        JSONObject root = new JSONObject(); root.put("format", "reading-shelf"); root.put("version", 1);
        JSONArray books = new JSONArray(), sessions = new JSONArray();
        try (Cursor c = db.query("books", new String[]{"id", "title", "author", "status", "current_page", "pages", "note", "updated"}, null, null, null, null, "id")) {
            while (c.moveToNext()) {
                JSONObject row = new JSONObject(); row.put("id", c.getLong(0)); row.put("title", c.getString(1)); row.put("author", c.getString(2)); row.put("status", c.getInt(3)); row.put("current", c.getInt(4)); row.put("pages", c.getInt(5)); row.put("note", c.getString(6)); row.put("updated", c.getLong(7)); books.put(row);
            }
        }
        try (Cursor c = db.query("sessions", new String[]{"book_id", "pages_read", "minutes", "created"}, null, null, null, null, "id")) {
            while (c.moveToNext()) {
                JSONObject row = new JSONObject(); row.put("book", c.getLong(0)); row.put("pages", c.getInt(1)); row.put("minutes", c.getInt(2)); row.put("created", c.getLong(3)); sessions.put(row);
            }
        }
        root.put("books", books); root.put("sessions", sessions); byte[] bytes = root.toString().getBytes(StandardCharsets.UTF_8); if (bytes.length > LIMIT) throw new IOException("数据超过 8 MB，无法生成可恢复的备份"); stream.write(bytes); stream.flush();
    }
    static JSONObject read(InputStream stream) throws IOException, JSONException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); byte[] buf = new byte[8192]; int n;
        while ((n = stream.read(buf)) != -1) { if (bytes.size() + n > LIMIT) throw new IOException("备份文件超过 8 MB"); bytes.write(buf, 0, n); }
        JSONObject root = new JSONObject(new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        if (!"reading-shelf".equals(root.optString("format")) || root.optInt("version", -1) != 1) throw new JSONException("不是可识别的阅读书架备份");
        JSONArray books = root.getJSONArray("books"), sessions = root.getJSONArray("sessions");
        if (books.length() > 10000 || sessions.length() > 100000) throw new JSONException("记录数量超过上限");
        HashSet<Long> ids = new HashSet<>();
        for (int i = 0; i < books.length(); ++i) {
            JSONObject row = books.getJSONObject(i); long id = row.getLong("id"), updated = row.getLong("updated");
            String title = row.getString("title"), author = row.getString("author"), note = row.getString("note");
            int status = row.getInt("status"), current = row.getInt("current"), pages = row.getInt("pages");
            if (id <= 0 || !ids.add(id) || title.trim().isEmpty() || title.length() > 120 || author.length() > 120 || note.length() > 2000 || status < 0 || status > 2 || current < 0 || pages < 0 || (pages > 0 && current > pages) || updated < 0) throw new JSONException("第 " + (i + 1) + " 条书目无效");
        }
        for (int i = 0; i < sessions.length(); ++i) {
            JSONObject row = sessions.getJSONObject(i); int pages = row.getInt("pages"), minutes = row.getInt("minutes");
            if (!ids.contains(row.getLong("book")) || pages < 0 || pages > 1000000 || minutes < 0 || minutes > 1440 || (pages == 0 && minutes == 0) || row.getLong("created") < 0) throw new JSONException("第 " + (i + 1) + " 条阅读记录无效");
        }
        return root;
    }
    static void restore(SQLiteDatabase db, JSONObject root) throws JSONException {
        JSONArray books = root.getJSONArray("books"), sessions = root.getJSONArray("sessions"); db.beginTransaction();
        try {
            db.delete("sessions", null, null); db.delete("books", null, null);
            for (int i = 0; i < books.length(); ++i) {
                JSONObject row = books.getJSONObject(i); ContentValues v = new ContentValues();
                v.put("id", row.getLong("id")); v.put("title", row.getString("title")); v.put("author", row.getString("author")); v.put("status", row.getInt("status")); v.put("current_page", row.getInt("current")); v.put("pages", row.getInt("pages")); v.put("note", row.getString("note")); v.put("updated", row.getLong("updated")); db.insertOrThrow("books", null, v);
            }
            for (int i = 0; i < sessions.length(); ++i) {
                JSONObject row = sessions.getJSONObject(i); ContentValues v = new ContentValues();
                v.put("book_id", row.getLong("book")); v.put("pages_read", row.getInt("pages")); v.put("minutes", row.getInt("minutes")); v.put("created", row.getLong("created")); db.insertOrThrow("sessions", null, v);
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
}
