package net.amirsayonara.jadwalmingguan;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<NamaJadwal> nama_jadwal;
    public static DatabaseHelper db;
    public static String AKTIF = "1";
    Menu menu_option;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        db = new DatabaseHelper(this);

        listView = findViewById(R.id.listview);
        listView.setEmptyView(findViewById(R.id.kosong));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            jadwal(nama_jadwal.get(i).getId());
            }
        });
        registerForContextMenu(listView);
        refresh();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(nama_jadwal.get(info.position).getNama_jadwal());
        getMenuInflater().inflate(R.menu.contex_menu_main, menu);
        if (this.AKTIF.equals("0")) menu.getItem(1).setTitle("Aktifkan");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Ubah Nama - "+nama_jadwal.get(info.position).getNama_jadwal());
                View v = getLayoutInflater().inflate(R.layout.form_tambah_nama_jadwal, null);
                final EditText editText = v.findViewById(R.id.nama_jadwal);
                editText.append(nama_jadwal.get(info.position).getNama_jadwal());
                builder.setView(v);
                builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String teks = editText.getText().toString();
                        if (teks.length()>1) {
                            db.ubahNamaJadwal(nama_jadwal.get(info.position).getId(), teks);
                            refresh();
                        }
                    }
                });
                builder.setNegativeButton("Batal", null);
                Dialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
                return true;
            case R.id.aktif:
                if (MainActivity.AKTIF.equals("1")) db.aktifkan(nama_jadwal.get(info.position).getId(), "0");
                else db.aktifkan(nama_jadwal.get(info.position).getId(), "1");
                refresh();
                return true;
            case R.id.hapus:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("Hapus semua jadwal "+nama_jadwal.get(info.position).getNama_jadwal()+"?");
                builder2.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    db.hapusNamaJadwal(nama_jadwal.get(info.position).getId());
                    refresh();
                    }
                });
                builder2.setNegativeButton("Tidak", null);
                builder2.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_main, menu);
        this.menu_option = menu;
        if (this.AKTIF.equals("0")) menu_option.getItem(0).setTitle(getResources().getString(R.string.jadwal_aktif));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void jadwal_aktif(MenuItem item) {
        if (this.AKTIF.equals("1")) this.AKTIF = "0";
        else this.AKTIF = "1";
        refresh();
    }

    public void refresh() {
        nama_jadwal = db.getNamaJadwal(this.AKTIF);
        if (this.AKTIF.equals("1")) {
            setTitle(getResources().getString(R.string.app_name));
            if (menu_option!=null) this.menu_option.getItem(0).setTitle(getResources().getString(R.string.jadwal_tidak_aktif));
        } else {
            setTitle(getResources().getString(R.string.app_name)+" (Tidak Aktif)");
            if (menu_option!=null) this.menu_option.getItem(0).setTitle(getResources().getString(R.string.jadwal_aktif));
        }
        NamaJadwalAdapter adapter = new NamaJadwalAdapter(this, nama_jadwal);
        listView.setAdapter(adapter);
    }

    private void jadwal(String id) {
        Intent i = new Intent(this, JadwalActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    public void tambah(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Nama Jadwal");
        View v = getLayoutInflater().inflate(R.layout.form_tambah_nama_jadwal, null);
        builder.setView(v);
        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            String teks = ((EditText)((Dialog) dialogInterface).findViewById(R.id.nama_jadwal)).getText().toString();
            if (teks.length()>1) {
                db.insertNamaJadwal(teks);
                MainActivity.AKTIF = "1";
                refresh();
            }
            }
        });
        builder.setNegativeButton("Batal", null);
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void hapus_semua_jadwal(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda yakin ingin menghapus semua jadwal yang ada di daftar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            db.hapusSemuaJadwal(MainActivity.AKTIF);
            refresh();
            }
        });
        builder.setNegativeButton("Tidak", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void tentang_aplikasi(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.tentang_aplikasi, null);
        builder.setView(v);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}

class NamaJadwal {
    private String id, nama_jadwal, jumlah, hari_ini;

    NamaJadwal(String id, String nama_jadwal, String jumlah, String hari_ini) {
        this.id = id;
        this.nama_jadwal = nama_jadwal;
        this.jumlah = jumlah;
        this.hari_ini = hari_ini;
    }

    public String getNama_jadwal() {
        return nama_jadwal;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getId() {
        return id;
    }

    public String getHari_ini() {
        return hari_ini;
    }
}

class NamaJadwalAdapter extends ArrayAdapter<NamaJadwal> {
    private List<NamaJadwal> data;

    public NamaJadwalAdapter(Context context, List<NamaJadwal> objects) {
        super(context, 0, objects);
        this.data = objects;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        NamaJadwal tmp = this.data.get(position);
        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.nama_jadwal_item, null);
        }
        if (tmp != null) {
            TextView nama = convertView.findViewById(R.id.nama);
            TextView jumlah = convertView.findViewById(R.id.jumlah);
            TextView hari_ini = convertView.findViewById(R.id.hari_ini);
            nama.setText(tmp.getNama_jadwal());
            jumlah.setText("Jumlah jadwal: "+tmp.getJumlah());
            hari_ini.setText(Html.fromHtml(tmp.getHari_ini()));
        }
        return convertView;
    }
}