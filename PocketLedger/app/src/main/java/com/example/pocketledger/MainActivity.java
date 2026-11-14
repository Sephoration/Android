package com.example.pocketledger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.text.TextWatcher;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** 一个只保存在本机的支出记录本。金额以分存储，避免浮点误差。 */
public class MainActivity extends Activity {
    private static final int INK = Color.rgb(36, 50, 44), MUTED = Color.rgb(109, 123, 115);
    private static final int ACCENT = Color.rgb(45, 107, 78), BACK = Color.rgb(244, 247, 242);
    private static final String[] CATEGORIES = {"餐饮", "交通", "购物", "居住", "娱乐", "其他"};
    private LedgerDb helper;
    private Calendar month = Calendar.getInstance();
    private LinearLayout root, list;
    private TextView heading, total, count, totalLabel;
    private EditText search;
    private Spinner categoryFilter;
    private static final int EXPORT_REQUEST = 101, BACKUP_REQUEST = 102, RESTORE_REQUEST = 103;
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        helper = new LedgerDb(this);
        if (state != null) month.setTimeInMillis(state.getLong("month", System.currentTimeMillis()));
        getWindow().setStatusBarColor(BACK); getWindow().setNavigationBarColor(BACK);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        buildScreen();
        if (state != null) { search.setText(state.getString("query", "")); categoryFilter.setSelection(Math.max(0, Math.min(CATEGORIES.length, state.getInt("category", 0)))); }
        refresh();
    }
    @Override protected void onSaveInstanceState(Bundle out) { out.putLong("month", month.getTimeInMillis()); out.putString("query", search.getText().toString()); out.putInt("category", categoryFilter.getSelectedItemPosition()); super.onSaveInstanceState(out); }
    @Override protected void onDestroy() { helper.close(); super.onDestroy(); }

    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    private GradientDrawable shape(int color, int radius) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t;
    }
    private Button button(String title) {
        Button b = new Button(this); b.setText(title); b.setTextColor(ACCENT); b.setAllCaps(false);
        b.setBackground(shape(Color.rgb(230, 239, 230), 12)); return b;
    }
    private void buildScreen() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20), dp(20), dp(20), dp(12)); root.setBackgroundColor(BACK); setContentView(root);
        TextView title = text("随手记账", 27, INK, true); root.addView(title);
        TextView subtitle = text("把每一笔日常，留在这里", 13, MUTED, false); subtitle.setPadding(0, dp(4), 0, dp(20)); root.addView(subtitle);
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(20), dp(18), dp(20), dp(18)); card.setBackground(shape(ACCENT, 20)); root.addView(card);
        LinearLayout nav = new LinearLayout(this); nav.setGravity(Gravity.CENTER_VERTICAL); card.addView(nav);
        TextView prev = text("‹", 28, Color.WHITE, false); prev.setContentDescription("上个月"); prev.setPadding(dp(8), 0, dp(14), 0); nav.addView(prev); prev.setOnClickListener(v -> changeMonth(-1));
        heading = text("", 16, Color.WHITE, true); heading.setGravity(Gravity.CENTER); nav.addView(heading, new LinearLayout.LayoutParams(0, -2, 1));
        TextView next = text("›", 28, Color.WHITE, false); next.setContentDescription("下个月"); next.setPadding(dp(14), 0, dp(8), 0); nav.addView(next); next.setOnClickListener(v -> changeMonth(1));
        totalLabel = text("本月支出", 13, 0xffd3e6d8, false); totalLabel.setPadding(0, dp(10), 0, 0); card.addView(totalLabel);
        total = text("", 32, Color.WHITE, true); card.addView(total); count = text("", 13, 0xffd3e6d8, false); card.addView(count);
        LinearLayout bar = new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(0, dp(22), 0, dp(12)); root.addView(bar);
        TextView records = text("支出明细", 18, INK, true); bar.addView(records, new LinearLayout.LayoutParams(0, -2, 1));
        Button data = button("数据"); bar.addView(data, new LinearLayout.LayoutParams(dp(60), dp(44))); data.setOnClickListener(v -> dataMenu());
        Button stats = button("分析"); bar.addView(stats, new LinearLayout.LayoutParams(dp(60), dp(44))); stats.setOnClickListener(v -> showStats());
        Button add = button("＋ 记一笔"); LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(dp(100), dp(44)); ap.leftMargin = dp(8); bar.addView(add, ap); add.setOnClickListener(v -> edit(null));
        LinearLayout filterRow = new LinearLayout(this); filterRow.setGravity(Gravity.CENTER_VERTICAL); root.addView(filterRow);
        search = field("搜索备注", ""); search.setTextSize(14); filterRow.addView(search, new LinearLayout.LayoutParams(0, dp(48), 1));
        categoryFilter = new Spinner(this); String[] options = new String[CATEGORIES.length + 1]; options[0] = "全部分类"; System.arraycopy(CATEGORIES, 0, options, 1, CATEGORIES.length);
        categoryFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options)); filterRow.addView(categoryFilter, new LinearLayout.LayoutParams(dp(112), dp(48)));
        categoryFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() { public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) { refresh(); } public void onNothingSelected(android.widget.AdapterView<?> parent) {} });
        search.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s, int start, int count, int after) {} public void onTextChanged(CharSequence s, int start, int before, int count) { refresh(); } public void afterTextChanged(Editable e) {} });
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); scroll.addView(list);
    }
    private void changeMonth(int offset) { month.set(Calendar.DAY_OF_MONTH, 1); month.add(Calendar.MONTH, offset); refresh(); }
    private String money(long cents) { return BigDecimal.valueOf(cents, 2).toPlainString(); }
    private void refresh() {
        if (list == null) return;
        list.removeAllViews(); String selected = monthFormat.format(month.getTime()); heading.setText(selected.replace('-', '年') + "月");
        long sum = 0; int n = 0;
        String needle = search.getText().toString().trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        String category = categoryFilter.getSelectedItemPosition() <= 0 ? null : CATEGORIES[categoryFilter.getSelectedItemPosition() - 1];
        totalLabel.setText(category == null && needle.isEmpty() ? "本月支出" : "筛选后支出");
        String where = "day >= ? AND day < ? AND note LIKE ? ESCAPE '\\'" + (category == null ? "" : " AND category = ?");
        String[] args = category == null ? new String[]{selected + "-01", nextMonth(), "%" + needle + "%"} : new String[]{selected + "-01", nextMonth(), "%" + needle + "%", category};
        try (Cursor c = helper.getReadableDatabase().query("entries", null, where, args, null, null, "day DESC, id DESC")) {
            while (c.moveToNext()) {
                Entry e = new Entry(); e.id = c.getLong(c.getColumnIndexOrThrow("id")); e.cents = c.getLong(c.getColumnIndexOrThrow("cents")); e.day = c.getString(c.getColumnIndexOrThrow("day")); e.category = c.getString(c.getColumnIndexOrThrow("category")); e.note = c.getString(c.getColumnIndexOrThrow("note"));
                sum += e.cents; n++; addRow(e);
            }
        }
        total.setText("¥ " + money(sum)); count.setText(n + " 笔记录");
        if (n == 0) { TextView empty = text("没有符合条件的记录\n可以调整筛选或记一笔", 15, MUTED, false); empty.setGravity(Gravity.CENTER); empty.setPadding(0, dp(70), 0, 0); list.addView(empty); }
    }
    private String nextMonth() { Calendar c = (Calendar) month.clone(); c.set(Calendar.DAY_OF_MONTH, 1); c.add(Calendar.MONTH, 1); return dateFormat.format(c.getTime()); }
    private void addRow(Entry e) {
        LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(16), dp(14), dp(16), dp(14)); row.setBackground(shape(Color.WHITE, 14));
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2); rp.bottomMargin = dp(8); list.addView(row, rp);
        LinearLayout info = new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL); row.addView(info, new LinearLayout.LayoutParams(0, -2, 1));
        info.addView(text(e.note.isEmpty() ? e.category : e.note, 16, INK, true)); TextView meta = text(e.day + "  ·  " + e.category, 12, MUTED, false); meta.setPadding(0, dp(5), 0, 0); info.addView(meta);
        row.addView(text("−¥" + money(e.cents), 16, INK, true)); row.setOnClickListener(v -> edit(e));
        row.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this).setTitle("复制记录").setMessage("将这笔支出复制到今天？")
                .setNegativeButton("取消", null).setPositiveButton("复制", (dialog, which) -> {
                    ContentValues values = new ContentValues(); values.put("cents", e.cents); values.put("category", e.category);
                    values.put("note", e.note); values.put("day", dateFormat.format(Calendar.getInstance().getTime()));
                    try { helper.getWritableDatabase().insertOrThrow("entries", null, values); month = Calendar.getInstance(); refresh(); }
                    catch (android.database.SQLException ex) { Toast.makeText(this, "复制失败：" + ex.getMessage(), Toast.LENGTH_LONG).show(); }
                }).show(); return true;
        });
        row.setContentDescription((e.note.isEmpty() ? e.category : e.note) + "，" + e.day + "，" + money(e.cents) + "元，点击编辑");
    }
    private EditText field(String hint, String value) { EditText f = new EditText(this); f.setSingleLine(true); f.setHint(hint); f.setText(value); return f; }
    private void edit(Entry old) {
        LinearLayout form = new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL); form.setPadding(dp(24), 0, dp(24), dp(8));
        EditText amount = field("金额（元）", old == null ? "" : money(old.cents)); amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); form.addView(amount);
        Spinner category = new Spinner(this); category.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORIES)); form.addView(category);
        if (old != null) for (int i=0; i<CATEGORIES.length; i++) if (CATEGORIES[i].equals(old.category)) category.setSelection(i);
        EditText note = field("备注（选填）", old == null ? "" : old.note); note.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(80)}); form.addView(note);
        Calendar chosen = Calendar.getInstance();
        if (old != null) { try { chosen.setTime(dateFormat.parse(old.day)); } catch (Exception ignored) {} }
        else if (!monthFormat.format(chosen.getTime()).equals(monthFormat.format(month.getTime()))) { chosen.setTimeInMillis(month.getTimeInMillis()); chosen.set(Calendar.DAY_OF_MONTH, 1); }
        Button date = button(dateFormat.format(chosen.getTime())); LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(-1, dp(48)); dp.topMargin = dp(12); form.addView(date, dp);
        date.setOnClickListener(v -> new DatePickerDialog(this, (view, year, m, day) -> { chosen.set(year, m, day); date.setText(dateFormat.format(chosen.getTime())); }, chosen.get(Calendar.YEAR), chosen.get(Calendar.MONTH), chosen.get(Calendar.DAY_OF_MONTH)).show());
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(old == null ? "记一笔" : "编辑记录").setView(form).setNegativeButton("取消", null).setPositiveButton("保存", null);
        if (old != null) builder.setNeutralButton("删除", (d, w) -> new AlertDialog.Builder(this).setMessage("确定删除这笔记录？").setNegativeButton("取消", null).setPositiveButton("删除", (x, y) -> { helper.getWritableDatabase().delete("entries", "id=?", new String[]{String.valueOf(old.id)}); refresh(); }).show());
        AlertDialog dialog = builder.create(); dialog.setOnShowListener(v -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(x -> {
            long cents;
            try { BigDecimal value = new BigDecimal(amount.getText().toString().trim()); cents = value.movePointRight(2).longValueExact(); if (cents <= 0 || cents > 100000000000L) throw new ArithmeticException(); }
            catch (NumberFormatException | ArithmeticException ex) { amount.setError("请输入大于 0、最多两位小数的金额"); return; }
            ContentValues values = new ContentValues(); values.put("cents", cents); values.put("category", category.getSelectedItem().toString()); values.put("note", note.getText().toString().trim()); values.put("day", dateFormat.format(chosen.getTime()));
            if (old == null) helper.getWritableDatabase().insertOrThrow("entries", null, values); else helper.getWritableDatabase().update("entries", values, "id=?", new String[]{String.valueOf(old.id)});
            month.setTimeInMillis(chosen.getTimeInMillis()); dialog.dismiss(); refresh();
        })); dialog.show();
    }
    private void showStats() {
        String selectedMonth = monthFormat.format(month.getTime());
        long totalCents = 0; int count = 0;
        StringBuilder detail = new StringBuilder();
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT category, COUNT(*), SUM(cents) FROM entries WHERE day >= ? AND day < ? GROUP BY category ORDER BY SUM(cents) DESC, category", new String[]{selectedMonth + "-01", nextMonth()})) {
            while (c.moveToNext()) { long value = c.getLong(2); totalCents += value; count += c.getInt(1); detail.append(c.getString(0)).append("  ").append(c.getInt(1)).append(" 笔  ¥ ").append(money(value)).append("\n"); }
        }
        long budget = getPreferences(MODE_PRIVATE).getLong("budget", 0);
        String budgetInfo = budget == 0 ? "未设置月预算" : "月预算 ¥ " + money(budget) + "，" + (totalCents > budget ? "超出 ¥ " + money(totalCents - budget) : "剩余 ¥ " + money(budget - totalCents));
        String message = selectedMonth + "  共 " + count + " 笔\n总支出 ¥ " + money(totalCents) + "\n" + budgetInfo + "\n\n" + (detail.length() == 0 ? "本月没有记录" : detail.toString());
        new AlertDialog.Builder(this).setTitle("月度分析").setMessage(message).setPositiveButton("完成", null)
            .setNeutralButton("设置预算", (d,w) -> editBudget()).setNegativeButton("导出本月 CSV", (d,w) -> exportMonth()).show();
    }
    private void editBudget() {
        long current = getPreferences(MODE_PRIVATE).getLong("budget", 0);
        EditText input = field("月预算，留空或 0 表示不设置", current == 0 ? "" : money(current));
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("设置月预算").setView(input).setNegativeButton("取消", null).setPositiveButton("保存", null).create();
        dialog.setOnShowListener(v -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(x -> {
            String raw = input.getText().toString().trim(); long value;
            try { value = raw.isEmpty() ? 0 : new BigDecimal(raw).movePointRight(2).longValueExact(); if (value < 0 || value > 100000000000L) throw new ArithmeticException(); }
            catch (NumberFormatException | ArithmeticException ex) { input.setError("请输入非负金额，最多两位小数"); return; }
            getPreferences(MODE_PRIVATE).edit().putLong("budget", value).apply(); dialog.dismiss(); showStats();
        })); dialog.show();
    }
    private void exportMonth() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "ledger-" + monthFormat.format(month.getTime()) + ".csv");
        try { startActivityForResult(intent, EXPORT_REQUEST); } catch (android.content.ActivityNotFoundException e) { Toast.makeText(this, "没有可用的文件保存应用", Toast.LENGTH_LONG).show(); }
    }
    private String csv(String value) {
        // 电子表格可能把文本识别为公式。
        int first = 0; while (first < value.length() && Character.isWhitespace(value.charAt(first))) ++first;
        if (first < value.length() && "=+-@".indexOf(value.charAt(first)) >= 0) value = "'" + value;
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    private void showTrend() {
        Calendar start = (Calendar) month.clone(); start.set(Calendar.DAY_OF_MONTH, 1); start.add(Calendar.MONTH, -5);
        String first = dateFormat.format(start.getTime());
        StringBuilder result = new StringBuilder();
        java.util.HashMap<String, Long> values = new java.util.HashMap<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT substr(day,1,7), SUM(cents) FROM entries WHERE day >= ? AND day < ? GROUP BY substr(day,1,7)", new String[]{first, nextMonth()})) {
            while (c.moveToNext()) values.put(c.getString(0), c.getLong(1));
        }
        for (int i = 0; i < 6; ++i) {
            String key = monthFormat.format(start.getTime()); long cents = values.containsKey(key) ? values.get(key) : 0;
            result.append(key).append("   ¥ ").append(money(cents)).append("\n"); start.add(Calendar.MONTH, 1);
        }
        new AlertDialog.Builder(this).setTitle("近六个月支出").setMessage(result.toString()).setPositiveButton("完成", null).show();
    }
    private void dataMenu() {
        new AlertDialog.Builder(this).setTitle("数据管理")
            .setItems(new String[]{"近六个月支出", "备份全部数据", "从备份恢复"}, (d, which) -> {
                if (which == 0) showTrend(); else chooseBackup(which == 2);
            }).setNegativeButton("取消", null).show();
    }
    private void chooseBackup(boolean restore) {
        Intent intent = new Intent(restore ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE).setType("application/json");
        if (!restore) intent.putExtra(Intent.EXTRA_TITLE, "pocket-ledger-backup.json");
        try { startActivityForResult(intent, restore ? RESTORE_REQUEST : BACKUP_REQUEST); }
        catch (android.content.ActivityNotFoundException ex) { Toast.makeText(this, "没有可用的文件管理应用", Toast.LENGTH_LONG).show(); }
    }
    private void restoreBackup(Uri uri) {
        try (java.io.InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) throw new IOException("无法打开文件");
            org.json.JSONObject backup = LedgerBackup.read(in);
            LedgerBackup.restore(helper.getWritableDatabase(), backup);
            getPreferences(MODE_PRIVATE).edit().putLong("budget", backup.getLong("budget")).apply();
            refresh(); Toast.makeText(this, "备份已恢复", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) { Toast.makeText(this, "恢复失败：" + ex.getMessage(), Toast.LENGTH_LONG).show(); }
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (result != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (request == RESTORE_REQUEST) {
            new AlertDialog.Builder(this).setTitle("恢复备份")
                .setMessage("恢复会替换当前所有记账记录和预算。建议先备份当前数据。继续吗？")
                .setNegativeButton("取消", null).setPositiveButton("替换并恢复", (d, w) -> restoreBackup(uri)).show(); return;
        }
        if (request == BACKUP_REQUEST) {
            try (java.io.OutputStream out = getContentResolver().openOutputStream(uri)) {
                if (out == null) throw new IOException("无法打开文件");
                LedgerBackup.write(helper.getReadableDatabase(), getPreferences(MODE_PRIVATE).getLong("budget", 0), out);
                Toast.makeText(this, "备份已保存", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) { Toast.makeText(this, "备份失败：" + ex.getMessage(), Toast.LENGTH_LONG).show(); }
            return;
        }
        if (request != EXPORT_REQUEST) return; String selectedMonth = monthFormat.format(month.getTime());
        try (java.io.OutputStream stream = getContentResolver().openOutputStream(uri)) {
            if (stream == null) throw new IOException("无法打开文件");
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
                 Cursor c = helper.getReadableDatabase().query("entries", new String[]{"day", "category", "cents", "note"}, "day >= ? AND day < ?", new String[]{selectedMonth + "-01", nextMonth()}, null, null, "day, id")) {
                out.write("\ufeff日期,分类,金额（元）,备注\r\n");
                while (c.moveToNext()) { out.write(csv(c.getString(0)) + "," + csv(c.getString(1)) + "," + money(c.getLong(2)) + "," + csv(c.getString(3)) + "\r\n"); }
            }
            Toast.makeText(this, "已导出本月记录", Toast.LENGTH_SHORT).show();
        } catch (IOException | SecurityException e) { Toast.makeText(this, "导出失败：" + e.getMessage(), Toast.LENGTH_LONG).show(); }
    }
    static class Entry { long id, cents; String day, category, note; }
    static class LedgerDb extends SQLiteOpenHelper {
        LedgerDb(Context c) { super(c, "ledger.db", null, 1); }
        @Override public void onCreate(SQLiteDatabase db) { db.execSQL("CREATE TABLE entries (id INTEGER PRIMARY KEY, cents INTEGER NOT NULL, category TEXT NOT NULL, note TEXT NOT NULL, day TEXT NOT NULL)"); db.execSQL("CREATE INDEX entries_day ON entries(day)"); }
        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }
}
