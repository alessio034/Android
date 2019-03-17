package com.tecnoservicescr.songo.sqliterecordatorios;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tecnoservicescr.songo.sqliterecordatorios.adaptadores.adaptadorRecordatorios;
import com.tecnoservicescr.songo.sqliterecordatorios.clase.recordatorios;
import com.tecnoservicescr.songo.sqliterecordatorios.persistencia.recordatorioSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<recordatorios> listaRecordatorios;

    private ListView listViewRecordatorios;

    private adaptadorRecordatorios myadaptador;

    private int PositionRecordatorio;

    //instancia base datos sqlite
    private SQLiteDatabase db;

    //instancia helper persona
    private recordatorioSQLiteHelper recordatorioHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creamos nueva instancia
        recordatorioHelper = new recordatorioSQLiteHelper(MainActivity.this, "DBRecordatorios", null, 1);

        //creamos una base datos para escribir y leer
        db = recordatorioHelper.getWritableDatabase();

        listViewRecordatorios = (ListView) findViewById(R.id.listViewRecordatorios);

        listaRecordatorios = new ArrayList<recordatorios>();

        listViewRecordatorios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                PositionRecordatorio = -1;
                Toast.makeText(MainActivity.this,"Recordatorio: " + listaRecordatorios.get(position).getNombre() + " - " + listaRecordatorios.get(position).getRecordatorio() ,Toast.LENGTH_SHORT).show();
            }
        });

        //mostramos la lista de los recordatorios
        myadaptador = new adaptadorRecordatorios(MainActivity.this, R.layout.activity_layout_recordatorio,listaRecordatorios);

        listViewRecordatorios.setAdapter(myadaptador);
        registerForContextMenu(listViewRecordatorios);

        Actualizar();
    }

    //actualizamos los registros
    private void Actualizar() {
        buscarTodos();
        myadaptador.notifyDataSetChanged();
    }
    //borra todos los registros de la base de datos
    private void BorrarTodo() {
        db.delete("Recordatorio" , "", null);
    }

    //insertar persona a la base datos
    private void FuncionAgregarRecordatorio(String Nombre, String Texto) {
        //listapersonas.add(new persona(Nombre,Identificacion,Genero,FNacimiento));
        //myadaptador.notifyDataSetChanged();

        if(db != null) {
            //creamos el registro a insertar con el objeto ContentValues
            ContentValues nuevaRecordatorio = new ContentValues();

            //No se ingresa el id porque se incrementa automaticamente
            nuevaRecordatorio.put("Nombre",Nombre);
            nuevaRecordatorio.put("Texto",Texto);

            //se inserta el registro en la base de datos
            db.insert("Recordatorio", null, nuevaRecordatorio);
        }
    }
    private void FuncionActualizaRecordatorio(String Id, String Nombre, String Texto) {
        if(db != null) {
            //creamos el registro a insertar con el objeto ContentValues
            ContentValues actualizaRecordatorio = new ContentValues();

            //No se ingresa el id porque se incrementa automaticamente
            actualizaRecordatorio.put("Nombre",Nombre);
            actualizaRecordatorio.put("Texto",Texto);


            //se actualiza el registro en la base de datos
            db.update("Recordatorio", actualizaRecordatorio,"Id=" + Id, null);
        }

    }

    private void FuncionEliminaRecordatorio(String Id) {
        db.delete("Recordatorio" , "id=" + Id, null);
    }

    private void buscarTodos(){
        Cursor cursor = db.rawQuery("SELECT * FROM Recordatorio", null);
        listaRecordatorios.clear();

        //se verifica que al menos exista un registro
        if(cursor.moveToFirst()) {
            //iteramos el cursos con los resultados
            //y se llena array list
            while(cursor.isAfterLast() == false) {
                int Id = cursor.getInt(cursor.getColumnIndex("Id"));
                String Nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                String Texto = cursor.getString(cursor.getColumnIndex("Texto"));


                listaRecordatorios.add(new recordatorios(Id, Nombre,Texto));

                cursor.moveToNext();
            }
        }

    }

    //al destruir la actividad cerramos la conexión
    @Override
    protected void onDestroy() {
        //cerramos la conexión
        db.close();
        super.onDestroy();
    }



    public void AgregarRecordatorio(View view) {
        PositionRecordatorio = -1;
        AlertDialog alertaInflado = alertaInflado();
        alertaInflado.show();
    }
    public void EliminarRecordatorio(View view) {
        BorrarTodo();
        Actualizar();
    }

    /**
     * Crea un diálogo de alerta inflado
     * @return Nuevo diálogo
     */
    public AlertDialog alertaInflado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        //obtenemos layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        String btnPositivo = "Agregar";

        View alertInflateLayout = inflater.inflate(R.layout.activity_layout_alert, null);

        final EditText txtId = (EditText) alertInflateLayout.findViewById(R.id.txtId);
        final EditText txtNombre = (EditText) alertInflateLayout.findViewById(R.id.txtNombre);
        final AppCompatAutoCompleteTextView txtTexto = (AppCompatAutoCompleteTextView) alertInflateLayout.findViewById(R.id.txtTexto);

        if(PositionRecordatorio > -1) {
            recordatorios currentRecordatorio = (recordatorios) listaRecordatorios.get(PositionRecordatorio);
            txtId.setText(String.valueOf(currentRecordatorio.getId()));
            txtNombre.setText(currentRecordatorio.getNombre());
            txtTexto.setText(currentRecordatorio.getRecordatorio());


            btnPositivo = "Actualizar";
        }

        builder.setView(alertInflateLayout)
                .setPositiveButton(btnPositivo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(MainActivity.this, "Ingresar acción: " + txtUsuario.getText().toString(),Toast.LENGTH_SHORT).show();

                        if(PositionRecordatorio == -1) {
                            FuncionAgregarRecordatorio(txtNombre.getText().toString(), txtTexto.getText().toString());
                        }else {
                            FuncionActualizaRecordatorio(txtId.getText().toString(), txtNombre.getText().toString(), txtTexto.getText().toString());
                        }
                        Actualizar();
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "Salir acción",Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }



}
