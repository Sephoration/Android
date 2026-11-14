package com.example.readingshelf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.net.Uri;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** 本地阅读清单：记录书目、状态和读书笔记。 */
public class MainActivity extends Activity {
    private static final int INK = 0xff303646, MUTED = 0xff78808e, ACCENT = 0xff5b638d, BACK = 0xfff5f5f9;
    private static final String[] STATES = {"想读", "在读", "读完"};
    private BookDb helper;
    private LinearLayout list, filters;
    private TextView summary;
    private EditText search;
    private int selected = -1;
    private Spinner sort;
    private static final int EXPORT_REQUEST = 101, BACKUP_REQUEST = 102, RESTORE_REQUEST = 103;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state); helper = new BookDb(this);
        if (state != null) selected = state.getInt("filter", -1);
        getWindow().setStatusBarColor(BACK); getWindow().setNavigationBarColor(BACK); getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        buildScreen();
        if (state != null) { search.setText(state.getString("query", "")); sort.setSelection(Math.max(0, Math.min(2, state.getInt("sort", 0)))); }
        refresh();
    }
    @Override protected void onSaveInstanceState(Bundle out) { out.putInt("filter", selected); out.putString("query", search.getText().toString()); out.putInt("sort", sort.getSelectedItemPosition()); super.onSaveInstanceState(out); }
    @Override protected void onDestroy() { helper.close(); super.onDestroy(); }
    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    private GradientDrawable shape(int color, int radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private TextView text(String value, int size, int color, boolean bold) { TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color); if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t; }
    private Button button(String title) { Button b = new Button(this); b.setText(title); b.setTextColor(ACCENT); b.setAllCaps(false); b.setBackground(shape(0xffe7e8f2, 12)); return b; }
    private void buildScreen() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20), dp(20), dp(20), dp(12)); root.setBackgroundColor(BACK); setContentView(root);
        LinearLayout title = new LinearLayout(this); title.setGravity(Gravity.CENTER_VERTICAL); root.addView(title);
        LinearLayout words = new LinearLayout(this); words.setOrientation(LinearLayout.VERTICAL); title.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        words.addView(text("阅读书架", 27, INK, true)); summary = text("", 13, MUTED, false); summary.setPadding(0, dp(4), 0, 0); words.addView(summary);
        Button stats = button("统计"); title.addView(stats, new LinearLayout.LayoutParams(dp(60), dp(44))); stats.setOnClickListener(v -> showStats());
        Button add = button("＋ 添加"); LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(dp(82), dp(44)); addParams.leftMargin = dp(6); title.addView(add, addParams); add.setOnClickListener(v -> edit(null));
        search = new EditText(this); search.setSingleLine(true); search.setTextSize(15); search.setHint("搜索书名、作者或笔记"); search.setPadding(dp(16), 0, dp(16), 0); search.setBackground(shape(Color.WHITE, 14));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, dp(50)); sp.topMargin = dp(22); root.addView(search, sp);
        search.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s, int st, int c, int a) {} public void onTextChanged(CharSequence s, int st, int before, int count) { refresh(); } public void afterTextChanged(Editable e) {} });
        filters = new LinearLayout(this); filters.setPadding(0, dp(10), 0, dp(8)); root.addView(filters);
        LinearLayout sortRow = new LinearLayout(this); sortRow.setGravity(Gravity.CENTER_VERTICAL); root.addView(sortRow);
        sortRow.addView(text("排序", 13, MUTED, false), new LinearLayout.LayoutParams(0, -2, 1));
        Button data = button("数据管理"); sortRow.addView(data, new LinearLayout.LayoutParams(dp(90), dp(42))); data.setOnClickListener(v -> dataMenu());
        sort = new Spinner(this); sort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"最近更新", "书名", "阅读进度"})); sortRow.addView(sort, new LinearLayout.LayoutParams(dp(140), dp(42)));
        sort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() { public void onItemSelected(android.widget.AdapterView<?> p, View v, int position, long id) { refresh(); } public void onNothingSelected(android.widget.AdapterView<?> p) {} });
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1)); list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); scroll.addView(list);
    }
    private void refresh() {
        if (list == null) return;
        filters.removeAllViews(); String[] labels = {"全部", "想读", "在读", "读完"};
        for (int i = 0; i < labels.length; i++) {
            final int filter = i - 1; TextView tab = text(labels[i], 14, selected == filter ? Color.WHITE : MUTED, selected == filter); tab.setGravity(Gravity.CENTER); tab.setPadding(dp(5), dp(9), dp(5), dp(9)); tab.setBackground(shape(selected == filter ? ACCENT : Color.TRANSPARENT, 10));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1); p.rightMargin = dp(4); filters.addView(tab, p); tab.setOnClickListener(v -> { selected = filter; refresh(); });
        }
        list.removeAllViews(); int all = 0, done = 0;
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT COUNT(*), SUM(CASE WHEN status=2 THEN 1 ELSE 0 END) FROM books", null)) { if (c.moveToFirst()) { all = c.getInt(0); done = c.getInt(1); } }
        summary.setText("共 " + all + " 本 · 已读 " + done + " 本");
        String query = search.getText().toString().trim(); String escaped = query.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_"); String pattern = "%" + escaped + "%";
        String where = "(title LIKE ? ESCAPE '\\' OR author LIKE ? ESCAPE '\\' OR note LIKE ? ESCAPE '\\')";
        String[] args;
        if (selected >= 0) { where += " AND status=?"; args = new String[]{pattern, pattern, pattern, String.valueOf(selected)}; } else args = new String[]{pattern, pattern, pattern};
        int shown = 0;
        String order = sort == null || sort.getSelectedItemPosition() == 0 ? "updated DESC, id DESC" : sort.getSelectedItemPosition() == 1 ? "title COLLATE NOCASE, id DESC" : "CASE WHEN pages > 0 THEN CAST(current_page AS REAL) / pages ELSE -1 END DESC, updated DESC";
        try (Cursor c = helper.getReadableDatabase().query("books", null, where, args, null, null, order)) {
            while (c.moveToNext()) {
                Book b = new Book(); b.id = c.getLong(c.getColumnIndexOrThrow("id")); b.title = c.getString(c.getColumnIndexOrThrow("title")); b.author = c.getString(c.getColumnIndexOrThrow("author")); b.note = c.getString(c.getColumnIndexOrThrow("note")); b.status = c.getInt(c.getColumnIndexOrThrow("status")); b.current = c.getInt(c.getColumnIndexOrThrow("current_page")); b.pages = c.getInt(c.getColumnIndexOrThrow("pages")); addRow(b); shown++;
            }
        }
        if (shown == 0) { TextView empty = text(query.isEmpty() ? "这里还没有书\n点击右上角添加一本" : "没有找到相关书目", 15, MUTED, false); empty.setGravity(Gravity.CENTER); empty.setPadding(0, dp(70), 0, 0); list.addView(empty); }
    }
    private void addRow(Book b) {
        LinearLayout card = new LinearLayout(this); card.setPadding(dp(16), dp(16), dp(16), dp(16)); card.setGravity(Gravity.TOP); card.setBackground(shape(Color.WHITE, 16)); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2); p.bottomMargin = dp(10); list.addView(card, p);
        TextView cover = text(b.title.substring(0, b.title.offsetByCodePoints(0, 1)), 24, ACCENT, true); cover.setGravity(Gravity.CENTER); cover.setBackground(shape(0xffe9eaf3, 12)); card.addView(cover, new LinearLayout.LayoutParams(dp(58), dp(76)));
        LinearLayout details = new LinearLayout(this); details.setOrientation(LinearLayout.VERTICAL); LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0, -2, 1); p2.leftMargin = dp(14); card.addView(details, p2);
        TextView name = text(b.title, 17, INK, true); name.setMaxLines(2); details.addView(name);
        TextView author = text(b.author.isEmpty() ? "作者未填写" : b.author, 13, MUTED, false); author.setPadding(0, dp(5), 0, 0); details.addView(author);
        String progress = b.pages > 0 ? "  ·  " + b.current + " / " + b.pages + " 页" : "";
        TextView state = text(STATES[Math.max(0, Math.min(b.status, 2))] + progress, 13, ACCENT, true); state.setPadding(0, dp(10), 0, 0); details.addView(state);
        if (b.pages > 0) { ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); progressBar.setMax(b.pages); progressBar.setProgress(b.current); LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, dp(8)); pp.topMargin = dp(8); details.addView(progressBar, pp); }
        if (!b.note.isEmpty()) { TextView note = text(b.note, 13, MUTED, false); note.setMaxLines(2); note.setPadding(0, dp(8), 0, 0); details.addView(note); }
        LinearLayout actions = new LinearLayout(this); LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(-1, dp(40)); actionParams.topMargin = dp(8); details.addView(actions, actionParams);
        Button log = button("＋ 阅读记录"); actions.addView(log, new LinearLayout.LayoutParams(0, -1, 1)); log.setOnClickListener(v -> addSession(b));
        Button history = button("记录"); LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(dp(62), -1); hp.leftMargin = dp(6); actions.addView(history, hp); history.setOnClickListener(v -> showHistory(b));
        card.setOnClickListener(v -> edit(b)); card.setContentDescription(b.title + "，" + state.getText() + "，点击编辑");
    }
    private EditText field(String hint, String value, boolean multiline) {
        EditText e = new EditText(this); e.setHint(hint); e.setText(value); e.setTextSize(15); e.setSingleLine(!multiline);
        if (multiline) { e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); e.setMinLines(2); e.setMaxLines(4); }
        return e;
    }
    private void edit(Book old) {
        ScrollView scroll = new ScrollView(this); LinearLayout form = new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL); form.setPadding(dp(24), 0, dp(24), dp(8)); scroll.addView(form);
        EditText title = field("书名（必填）", old == null ? "" : old.title, false); title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)}); form.addView(title);
        EditText author = field("作者（选填）", old == null ? "" : old.author, false); author.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)}); form.addView(author);
        Spinner status = new Spinner(this); status.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, STATES)); if (old != null) status.setSelection(old.status); form.addView(status);
        LinearLayout pagesRow = new LinearLayout(this); form.addView(pagesRow);
        EditText current = field("已读页数", old == null || old.current == 0 ? "" : String.valueOf(old.current), false); current.setInputType(InputType.TYPE_CLASS_NUMBER); pagesRow.addView(current, new LinearLayout.LayoutParams(0, -2, 1));
        EditText pages = field("总页数", old == null || old.pages == 0 ? "" : String.valueOf(old.pages), false); pages.setInputType(InputType.TYPE_CLASS_NUMBER); pagesRow.addView(pages, new LinearLayout.LayoutParams(0, -2, 1));
        EditText note = field("笔记（选填）", old == null ? "" : old.note, true); note.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2000)}); form.addView(note);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(old == null ? "添加书目" : "编辑书目").setView(scroll).setNegativeButton("取消", null).setPositiveButton("保存", null);
        if (old != null) builder.setNeutralButton("删除", (d, w) -> new AlertDialog.Builder(this).setMessage("确定从书架移除这本书？").setNegativeButton("取消", null).setPositiveButton("删除", (x, y) -> { deleteBook(old.id); refresh(); }).show());
        AlertDialog dialog = builder.create(); dialog.setOnShowListener(v -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(x -> {
            String name = title.getText().toString().trim(); if (name.isEmpty()) { title.setError("请填写书名"); return; }
            int c, p;
            try { c = parsePage(current); p = parsePage(pages); if (c < 0 || p < 0 || c > 1000000 || p > 1000000) throw new NumberFormatException(); if (p > 0 && c > p) { current.setError("已读页数不能大于总页数"); return; } }
            catch (NumberFormatException ex) { pages.setError("请输入有效页数"); return; }
            ContentValues values = new ContentValues(); values.put("title", name); values.put("author", author.getText().toString().trim()); values.put("status", status.getSelectedItemPosition()); values.put("current_page", c); values.put("pages", p); values.put("note", note.getText().toString().trim()); values.put("updated", System.currentTimeMillis());
            if (old == null) helper.getWritableDatabase().insertOrThrow("books", null, values); else helper.getWritableDatabase().update("books", values, "id=?", new String[]{String.valueOf(old.id)});
            dialog.dismiss(); refresh();
        })); dialog.show();
    }
    private int parsePage(EditText field) { String s = field.getText().toString().trim(); return s.isEmpty() ? 0 : Integer.parseInt(s); }
    private void deleteBook(long id) {
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try { String[] arg = {String.valueOf(id)}; db.delete("sessions", "book_id=?", arg); db.delete("books", "id=?", arg); db.setTransactionSuccessful(); }
        finally { db.endTransaction(); }
    }
    private void addSession(Book b) {
        LinearLayout form = new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL); form.setPadding(dp(24), 0, dp(24), dp(8));
        form.addView(text("当前进度：" + b.current + (b.pages > 0 ? " / " + b.pages : "") + " 页", 14, MUTED, false));
        EditText read = field("这次读了多少页", "", false); read.setInputType(InputType.TYPE_CLASS_NUMBER); form.addView(read);
        EditText minutes = field("阅读分钟数（选填）", "", false); minutes.setInputType(InputType.TYPE_CLASS_NUMBER); form.addView(minutes);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("记录阅读 · " + b.title).setView(form).setNegativeButton("取消", null).setPositiveButton("保存", null).create();
        dialog.setOnShowListener(v -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(x -> {
            int pagesRead, time;
            try { pagesRead = parsePage(read); time = parsePage(minutes); if (pagesRead < 0 || time < 0 || pagesRead > 1000000 || time > 1440 || (pagesRead == 0 && time == 0)) throw new NumberFormatException(); }
            catch (NumberFormatException ex) { read.setError("请填写页数或分钟数（分钟数不超过 1440）"); return; }
            SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
            try (Cursor c = db.query("books", new String[]{"current_page", "pages", "status"}, "id=?", new String[]{String.valueOf(b.id)}, null, null, null)) {
                if (!c.moveToFirst()) { Toast.makeText(this, "书目已不存在", Toast.LENGTH_SHORT).show(); dialog.dismiss(); return; }
                int current = c.getInt(0), total = c.getInt(1);
                if ((long)current + pagesRead > 1000000 || (total > 0 && (long)current + pagesRead > total)) { read.setError("已读页数不能超过总页数"); return; }
                ContentValues session = new ContentValues(); session.put("book_id", b.id); session.put("pages_read", pagesRead); session.put("minutes", time); session.put("created", System.currentTimeMillis()); db.insertOrThrow("sessions", null, session);
                ContentValues book = new ContentValues(); int next = current + pagesRead; book.put("current_page", next); book.put("updated", System.currentTimeMillis());
                if (total > 0 && next == total) book.put("status", 2); else if (pagesRead > 0 && c.getInt(2) == 0) book.put("status", 1);
                db.update("books", book, "id=?", new String[]{String.valueOf(b.id)}); db.setTransactionSuccessful();
            } finally { db.endTransaction(); }
            dialog.dismiss(); refresh();
        })); dialog.show();
    }
    private void showHistory(Book b) {
        StringBuilder text = new StringBuilder(); SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try (Cursor c = helper.getReadableDatabase().query("sessions", new String[]{"created", "pages_read", "minutes"}, "book_id=?", new String[]{String.valueOf(b.id)}, null, null, "created DESC, id DESC", "50")) {
            while (c.moveToNext()) text.append(fmt.format(new Date(c.getLong(0)))).append("  ·  ").append(c.getInt(1)).append(" 页  ·  ").append(c.getInt(2)).append(" 分钟\n");
        }
        new AlertDialog.Builder(this).setTitle(b.title + " · 最近 50 条").setMessage(text.length() == 0 ? "还没有阅读记录" : text.toString()).setPositiveButton("完成", null).show();
    }
    private void showStats() {
        long pages = 0, minutes = 0, sessions = 0; int all = 0, reading = 0, completed = 0;
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT COUNT(*), SUM(pages_read), SUM(minutes) FROM sessions", null)) { if (c.moveToFirst()) { sessions = c.getLong(0); pages = c.getLong(1); minutes = c.getLong(2); } }
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT COUNT(*), SUM(CASE WHEN status=1 THEN 1 ELSE 0 END), SUM(CASE WHEN status=2 THEN 1 ELSE 0 END) FROM books", null)) { if (c.moveToFirst()) { all = c.getInt(0); reading = c.getInt(1); completed = c.getInt(2); } }
        long weekPages = 0, weekMinutes = 0, weekSessions = 0;
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT COUNT(*), SUM(pages_read), SUM(minutes) FROM sessions WHERE created >= ?", new String[]{String.valueOf(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000)})) {
            if (c.moveToFirst()) { weekSessions = c.getLong(0); weekPages = c.getLong(1); weekMinutes = c.getLong(2); }
        }
        String message = "近 7 天：" + weekSessions + " 次 · " + weekPages + " 页 · " + weekMinutes + " 分钟\n\n" + "书架 " + all + " 本，在读 " + reading + " 本，已读完 " + completed + " 本\n\n阅读记录 " + sessions + " 次\n累计 " + pages + " 页 · " + minutes + " 分钟\n\n统计来自手动添加的阅读记录。";
        new AlertDialog.Builder(this).setTitle("阅读统计").setMessage(message).setPositiveButton("完成", null).setNeutralButton("导出书架 CSV", (d,w) -> exportBooks()).show();
    }
    private void exportBooks() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("text/csv"); intent.putExtra(Intent.EXTRA_TITLE, "reading-shelf.csv");
        try { startActivityForResult(intent, EXPORT_REQUEST); } catch (android.content.ActivityNotFoundException e) { Toast.makeText(this, "没有可用的文件保存应用", Toast.LENGTH_LONG).show(); }
    }
    private String csv(String value) {
        int first = 0; while (first < value.length() && Character.isWhitespace(value.charAt(first))) ++first;
        if (first < value.length() && "=+-@".indexOf(value.charAt(first)) >= 0) value = "'" + value;
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    private void dataMenu() {
        new AlertDialog.Builder(this).setTitle("数据管理")
            .setItems(new String[]{"备份全部数据", "从备份恢复"}, (d, which) -> {
                if (which == 0) chooseBackup(false); else chooseBackup(true);
            }).setNegativeButton("取消", null).show();
    }
    private void chooseBackup(boolean restore) {
        Intent intent = new Intent(restore ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE).setType("application/json");
        if (!restore) intent.putExtra(Intent.EXTRA_TITLE, "reading-shelf-backup.json");
        try { startActivityForResult(intent, restore ? RESTORE_REQUEST : BACKUP_REQUEST); }
        catch (android.content.ActivityNotFoundException ex) { Toast.makeText(this, "没有可用的文件管理应用", Toast.LENGTH_LONG).show(); }
    }
    private void restoreBackup(Uri uri) {
        try (java.io.InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) throw new IOException("无法打开文件");
            org.json.JSONObject backup = ShelfBackup.read(in);
            ShelfBackup.restore(helper.getWritableDatabase(), backup);
            refresh(); Toast.makeText(this, "备份已恢复", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) { Toast.makeText(this, "恢复失败：" + ex.getMessage(), Toast.LENGTH_LONG).show(); }
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (result != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (request == RESTORE_REQUEST) {
            new AlertDialog.Builder(this).setTitle("恢复备份")
                .setMessage("恢复会替换当前所有书目和阅读记录。建议先备份当前数据。继续吗？")
                .setNegativeButton("取消", null).setPositiveButton("替换并恢复", (d, w) -> restoreBackup(uri)).show(); return;
        }
        if (request == BACKUP_REQUEST) {
            try (java.io.OutputStream out = getContentResolver().openOutputStream(uri)) {
                if (out == null) throw new IOException("无法打开文件");
                ShelfBackup.write(helper.getReadableDatabase(), out);
                Toast.makeText(this, "备份已保存", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) { Toast.makeText(this, "备份失败：" + ex.getMessage(), Toast.LENGTH_LONG).show(); }
            return;
        }
        if (request != EXPORT_REQUEST) return;
        try (java.io.OutputStream stream = getContentResolver().openOutputStream(uri)) {
            if (stream == null) throw new IOException("无法打开文件");
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
                 Cursor c = helper.getReadableDatabase().query("books", new String[]{"title", "author", "status", "current_page", "pages", "note"}, null, null, null, null, "updated DESC")) {
                out.write("\ufeff书名,作者,状态,已读页数,总页数,笔记\r\n");
                while (c.moveToNext()) { int status = Math.max(0, Math.min(2, c.getInt(2))); out.write(csv(c.getString(0)) + "," + csv(c.getString(1)) + "," + csv(STATES[status]) + "," + c.getInt(3) + "," + c.getInt(4) + "," + csv(c.getString(5)) + "\r\n"); }
            }
            Toast.makeText(this, "书架已导出", Toast.LENGTH_SHORT).show();
        } catch (IOException | SecurityException e) { Toast.makeText(this, "导出失败：" + e.getMessage(), Toast.LENGTH_LONG).show(); }
    }
    static class Book { long id; int status, current, pages; String title, author, note; }
    static class BookDb extends SQLiteOpenHelper {
        BookDb(Context c) { super(c, "reading.db", null, 2); }
        @Override public void onCreate(SQLiteDatabase db) { db.execSQL("CREATE TABLE books (id INTEGER PRIMARY KEY, title TEXT NOT NULL, author TEXT NOT NULL, status INTEGER NOT NULL, current_page INTEGER NOT NULL, pages INTEGER NOT NULL, note TEXT NOT NULL, updated INTEGER NOT NULL)"); createSessions(db); }
        private void createSessions(SQLiteDatabase db) { db.execSQL("CREATE TABLE sessions (id INTEGER PRIMARY KEY, book_id INTEGER NOT NULL, pages_read INTEGER NOT NULL, minutes INTEGER NOT NULL, created INTEGER NOT NULL)"); db.execSQL("CREATE INDEX sessions_book ON sessions(book_id, created)"); }
        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { if (oldVersion < 2) createSessions(db); }
    }
}
