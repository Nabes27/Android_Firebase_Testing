package com.example.firebastesting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EditText etNama, etNim;
    private Button btnSave;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();

        etNama = findViewById(R.id.et_nama);
        etNim = findViewById(R.id.et_nim);
        btnSave = findViewById(R.id.btn_save);
        tvResult = findViewById(R.id.tv_result);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etNama.getText().toString().trim();
                String nim = etNim.getText().toString().trim();

                if (nama.isEmpty() || nim.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Nama dan NIM tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveData(nama, nim);
            }
        });

        fetchData();
    }

    private void saveData(String nama, String nim) {
        Map<String, Object> mahasiswa = new HashMap<>();
        mahasiswa.put("nama", nama);
        mahasiswa.put("nim", nim);

        firestore.collection("mahasiswa").add(mahasiswa)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                        etNama.setText("");
                        etNim.setText("");
                        fetchData(); // Refresh data
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchData() {
        firestore.collection("mahasiswa").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        StringBuilder data = new StringBuilder();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            data.append("- Nama: ").append(document.getString("nama"))
                                .append(", NIM: ").append(document.getString("nim")).append("\n");
                        }
                        tvResult.setText(data.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvResult.setText("Gagal mengambil data: " + e.getMessage());
                    }
                });
    }
}
