package net.amirsayonara.jadwalmingguan;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class TambahJadwalActivity extends AppCompatActivity {
    EditText agenda, waktu_mulai, waktu_selesai, tempat;
    String id_nama_jadwal, id_hari;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_jadwal);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        agenda = findViewById(R.id.agenda);
        waktu_mulai = findViewById(R.id.waktu_mulai);
        waktu_selesai = findViewById(R.id.waktu_selesai);
        tempat = findViewById(R.id.tempat);

        Intent i = getIntent();
        id_hari = i.getStringExtra("id-hari");
        id_nama_jadwal = i.getStringExtra("id-nama-jadwal");

        setTitle("Tambah Jadwal Harian");
    }

    public void waktu_selesai(View view) {
        String t1 = waktu_selesai.getText().toString(), t2;
        if (t1.length() > 0) t2 = t1;
        else t2 = "00:00";
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String t1 = String.valueOf(i), t2 = String.valueOf(i1);
                if (t1.length()<2) t1 = "0" + t1;
                if (t2.length()<2) t2 = "0" + t2;
                waktu_selesai.setText(t1+":"+t2);
            }
        }, Integer.parseInt(t2.split(":")[0]), Integer.parseInt(t2.split(":")[1]), true);
        timePickerDialog.show();
    }

    public void waktu_mulai(View view) {
        String t1 = waktu_mulai.getText().toString(), t2;
        if (t1.length() > 0) t2 = t1;
        else t2 = "00:00";
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String t1 = String.valueOf(i), t2 = String.valueOf(i1);
                if (t1.length()<2) t1 = "0" + t1;
                if (t2.length()<2) t2 = "0" + t2;
                waktu_mulai.setText(t1+":"+t2);
            }
        }, Integer.parseInt(t2.split(":")[0]), Integer.parseInt(t2.split(":")[1]), true);
        timePickerDialog.show();
    }

    public void tambah(View view) {
        String a, w_m, w_s, t;
        a = agenda.getText().toString();
        w_m = waktu_mulai.getText().toString();
        w_s = waktu_selesai.getText().toString();
        t = tempat.getText().toString();
        if (a.length()>0 & w_m.length()>0 & w_s.length()>0 & t.length()>0) {
            MainActivity.db.tambahJadwal(id_nama_jadwal, id_hari, a, t, w_m, w_s);
            finish();
        } else Toast.makeText(this, "Periksa kembali masukan Anda", Toast.LENGTH_SHORT).show();
    }
}
