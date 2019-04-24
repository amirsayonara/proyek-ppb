package net.amirsayonara.jadwalmingguan;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class JadwalActivity extends AppCompatActivity {
    ArrayList<Jadwal> jadwal_harian;
    ListView listView;
    String id_nama_jadwal;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        Intent i = getIntent();
        id_nama_jadwal = i.getStringExtra("id");
        setTitle(MainActivity.db.namaJadwal(id_nama_jadwal));

        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            jadwal_harian_act(id_nama_jadwal, jadwal_harian.get(i).getId_hari(), jadwal_harian.get(i).getNama_hari());
            }
        });
        refresh();
    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    private void jadwal_harian_act(String id_nama_jadwal, String id_hari, String nama_hari) {
        Intent i = new Intent(this, JadwalHarianActivity.class);
        i.putExtra("id-nama-jadwal", id_nama_jadwal);
        i.putExtra("id-hari", id_hari);
        i.putExtra("nama-hari", nama_hari);
        startActivity(i);
    }

    public void refresh() {
        jadwal_harian = MainActivity.db.getJadwalHarian(id_nama_jadwal);
        JadwalAdapter adapter = new JadwalAdapter(this, jadwal_harian);
        listView.setAdapter(adapter);
    }
}


class Jadwal {
    private String id_hari, nama_hari, list_jadwal;

    public Jadwal(String id_hari, String nama_hari, String list_jadwal) {
        this.id_hari = id_hari;
        this.nama_hari = nama_hari;
        this.list_jadwal = list_jadwal;
    }

    public String getId_hari() {
        return id_hari;
    }

    public String getNama_hari() {
        return nama_hari;
    }

    public String getList_jadwal() {
        return list_jadwal;
    }
}

class JadwalAdapter extends ArrayAdapter<Jadwal> {
    private List<Jadwal> data;

    public JadwalAdapter(Context context, List<Jadwal> objects) {
        super(context, 0, objects);
        this.data = objects;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        Jadwal tmp = this.data.get(position);
        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.jadwal_harian_item, null);
        }
        if (tmp != null) {
            TextView nama = convertView.findViewById(R.id.nama_hari);
            TextView berjalan = convertView.findViewById(R.id.jadwal_berjalan);
            nama.setText(tmp.getNama_hari());
            berjalan.setText(Html.fromHtml(tmp.getList_jadwal()));
        }
        return convertView;
    }
}