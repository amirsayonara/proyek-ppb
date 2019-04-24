package net.amirsayonara.jadwalmingguan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jadwal.db";
    private static final int DATABASE_VERSION = 1;
    private String[] nama_hari;

    @SuppressLint("ResourceType")
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        nama_hari = context.getResources().getStringArray(R.array.nama_hari);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS nama_jadwal(id INTEGER PRIMARY KEY AUTOINCREMENT, nama CHAR(30), aktif CHAR(1))");
        db.execSQL("CREATE TABLE IF NOT EXISTS jadwal(id INTEGER PRIMARY KEY AUTOINCREMENT, nama_jadwal INTEGER, hari INTEGER, agenda CHAR(50), tempat CHAR(50), waktu_mulai TIME, waktu_selesai TIME, FOREIGN KEY(nama_jadwal) REFERENCES nama_jadwal(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS nama_jadwal");
        db.execSQL("DROP TABLE IF EXISTS jadwal");
        onCreate(db);
    }

    public ArrayList<NamaJadwal> getNamaJadwal(String aktif) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id_hari_ini = (new Date()).getDay();String list_jadwal;
        SimpleDateFormat format_waktu = new SimpleDateFormat("HH:mm");
        Date sekarang = new Date(), d0 = new Date(), d1 = new Date();
        String s;
        Cursor c = db.rawQuery("SELECT id, nama, (SELECT COUNT(id) FROM jadwal j WHERE j.nama_jadwal=n.id) FROM nama_jadwal n WHERE n.aktif='" + aktif + "'", null), c2;
        ArrayList<NamaJadwal> data = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                c2 = db.rawQuery("SELECT * FROM jadwal WHERE nama_jadwal='"+c.getString(0)+"' AND hari='"+id_hari_ini+"' ORDER BY waktu_mulai", null);
                list_jadwal = "<big><b>☑ Kosong</b></big>";
                if (c2.moveToFirst()) {
                    list_jadwal = "";
                    do {
                        try {
                            d0 = format_waktu.parse(c2.getString(5));
                            d1 = format_waktu.parse(c2.getString(6));
                            sekarang = format_waktu.parse(format_waktu.format(new Date()));
                        } catch (Exception e) {e.printStackTrace();}
                        if (d0.getTime() > d1.getTime()) s = "☒";
                        else if (sekarang.getTime() > d0.getTime() & sekarang.getTime() >= d1.getTime()) s = "☑";
                        else s = "☐";
                        list_jadwal += "<big><b>" + s + " " + c2.getString(3) + "</b></big><br>  " + c2.getString(5) + " - " + c2.getString(6) + " | " + c2.getString(4);
                        if (c2.moveToNext()) {
                            list_jadwal += "<br><br>";
                            c2.moveToPrevious();
                        }
                    } while (c2.moveToNext());
                }
                data.add(new NamaJadwal(c.getString(0), c.getString(1), c.getString(2), list_jadwal));
            } while (c.moveToNext());
        }
        db.close();
        return data;
    }

    public String namaJadwal(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM nama_jadwal WHERE id='" + id + "'", null);
        if (c.moveToFirst()) return c.getString(1);
        else return "";
    }

    public void insertNamaJadwal(String nama) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("INSERT INTO nama_jadwal (nama, aktif) VALUES ('%s', '1')", nama));
        db.close();
    }

    public void aktifkan(String id, String aktif) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("UPDATE nama_jadwal SET aktif='%s' WHERE id='%s'", aktif, id));
        db.close();
    }

    public void ubahNamaJadwal(String id, String nama_baru) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("UPDATE nama_jadwal SET nama='%s' WHERE id='%s'", nama_baru, id));
        db.close();
    }

    public void hapusNamaJadwal(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM jadwal WHERE nama_jadwal='%s'", id));
        db.execSQL(String.format("DELETE FROM nama_jadwal WHERE id='%s'", id));
        db.close();
    }

    public void hapusSemuaJadwal(String aktif) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM jadwal WHERE nama_jadwal IN (SELECT id FROM nama_jadwal WHERE aktif='%s')", aktif));
        db.execSQL(String.format("DELETE FROM nama_jadwal WHERE aktif='%s'", aktif));
        db.close();
    }

    public ArrayList<Jadwal> getJadwalHarian(String id_nama_jadwal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Jadwal> jadwal_harian = new ArrayList<>();
        int id_hari_ini = (new Date()).getDay();
        for (int x=id_hari_ini; x<id_hari_ini+7; x++) {
            Cursor c = db.rawQuery("SELECT * FROM jadwal WHERE nama_jadwal='"+id_nama_jadwal+"' AND hari='"+(x%7)+"' ORDER BY waktu_mulai", null);
            String list_jadwal = "<big><b>◯ Kosong</b></big>";
            if (c.moveToFirst()) {
                list_jadwal = "";
                do {
                    list_jadwal += "<big><b>⬤ " + c.getString(3) + "</b></big><br>  " + c.getString(5) + " - " + c.getString(6) + " | " + c.getString(4);
                    if (c.moveToNext()) {
                        list_jadwal += "<br><br>";
                        c.moveToPrevious();
                    }
                } while (c.moveToNext());
            }
            if (x==id_hari_ini) jadwal_harian.add(new Jadwal(String.valueOf(x % 7), nama_hari[x % 7]+" (hari ini)", list_jadwal));
            else jadwal_harian.add(new Jadwal(String.valueOf(x % 7), nama_hari[x % 7], list_jadwal));
        }
        db.close();
        return jadwal_harian;
    }

    public void tambahJadwal(String id_nama_jadwal, String id_hari, String agenda, String tempat, String waktu_mulai, String waktu_selesai) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("INSERT INTO jadwal (nama_jadwal, hari, agenda, tempat, waktu_mulai, waktu_selesai) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", id_nama_jadwal, id_hari, agenda, tempat, waktu_mulai, waktu_selesai));
        db.close();
    }

    public ArrayList<ArrayList<String>> getJadwal(String id_nama_jadwal, String id_hari) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM jadwal WHERE nama_jadwal='"+id_nama_jadwal+"' AND hari='"+id_hari+"' ORDER BY waktu_mulai", null);
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(c.getString(0));
                tmp.add(c.getString(1));
                tmp.add(c.getString(2));
                tmp.add(c.getString(3));
                tmp.add(c.getString(4));
                tmp.add(c.getString(5));
                tmp.add(c.getString(6));
                data.add(tmp);
            } while (c.moveToNext());
        }
        db.close();
        return data;
    }

    public void hapusJadwal(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM jadwal WHERE id='"+id+"'");
        db.close();
    }

    public void ubahJadwal(String id, String agendaBaru, String tempatBaru, String waktuMulaiBaru, String waktuSelesaiBaru) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("UPDATE jadwal SET agenda='%s', tempat='%s', waktu_mulai='%s', waktu_selesai='%s' WHERE id='%s'", agendaBaru, tempatBaru, waktuMulaiBaru, waktuSelesaiBaru, id));
        db.close();
    }

    public void ubahHariJadwal(String id, int id_hari_baru) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE jadwal SET hari='"+id_hari_baru+"' WHERE id='"+id+"'");
        db.close();
    }
}
