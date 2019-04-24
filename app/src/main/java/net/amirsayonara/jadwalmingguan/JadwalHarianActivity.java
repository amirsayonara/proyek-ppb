package net.amirsayonara.jadwalmingguan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class JadwalHarianActivity extends AppCompatActivity {
    String id_nama_jadwal, id_hari;
    ListView listView;
    ArrayList<ArrayList<String>> jadwal;
    ArrayAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_harian);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        Intent i = getIntent();
        id_hari = i.getStringExtra("id-hari");
        id_nama_jadwal = i.getStringExtra("id-nama-jadwal");
        setTitle(MainActivity.db.namaJadwal(id_nama_jadwal) + " - " + i.getStringExtra("nama-hari"));

        listView = findViewById(R.id.listview);
        listView.setEmptyView(findViewById(R.id.kosong));
        registerForContextMenu(listView);
        refresh();
    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contex_menu_jadwal_harian, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(jadwal.get(info.position).get(3));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                Intent i = new Intent(this, EditJadwalActivity.class);
                i.putStringArrayListExtra("jadwal", jadwal.get(info.position));
                startActivity(i);
                return true;
            case R.id.pindahkan_hari:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pindahkan Hari "+jadwal.get(info.position).get(3));
                View v = getLayoutInflater().inflate(R.layout.form_pindahkan_hari, null);
                final Spinner spinner = v.findViewById(R.id.hari_jadwal);
                spinner.setSelection(Integer.parseInt(jadwal.get(info.position).get(2)));
                builder.setView(v);
                builder.setPositiveButton("Pindahkan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.db.ubahHariJadwal(jadwal.get(info.position).get(0), spinner.getSelectedItemPosition());
                        refresh();
                    }
                });
                builder.setNegativeButton("Batal", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.hapus:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("Hapus jadwal "+jadwal.get(info.position).get(3)+"?");
                builder2.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.db.hapusJadwal(jadwal.get(info.position).get(0));
                        refresh();
                    }
                });
                builder2.setNegativeButton("Tidak", null);
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void tambah(View view) {
        Intent i = new Intent(this, TambahJadwalActivity.class);
        i.putExtra("id-hari", id_hari);
        i.putExtra("id-nama-jadwal", id_nama_jadwal);
        startActivity(i);
    }

    public void refresh() {
        jadwal = MainActivity.db.getJadwal(id_nama_jadwal, id_hari);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, jadwal) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView text1 = v.findViewById(android.R.id.text1);
                text1.setTextSize(20);
                TextView text2 = v.findViewById(android.R.id.text2);

                text1.setText(jadwal.get(position).get(3));
                text2.setText(jadwal.get(position).get(5)+" - "+jadwal.get(position).get(6)+" | "+jadwal.get(position).get(4));
                return v;
            }
        };
        listView.setAdapter(adapter);
    }
}