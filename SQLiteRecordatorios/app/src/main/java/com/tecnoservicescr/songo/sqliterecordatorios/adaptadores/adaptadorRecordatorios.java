package com.tecnoservicescr.songo.sqliterecordatorios.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecnoservicescr.songo.sqliterecordatorios.R;
import com.tecnoservicescr.songo.sqliterecordatorios.clase.recordatorios;

import java.util.List;

public class adaptadorRecordatorios extends BaseAdapter {
    private Context contexto;//donde se va a cargar el adaptador
    private int layout;
    private List<recordatorios> listRecordatorios;//lista se pasa por paramertros

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.listRecordatorios.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return this.listRecordatorios.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        //Inflamos la vista si es nula
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.contexto);
            convertView = layoutInflater.inflate(this.layout ,null);

            //Seteamos los campos al holder
            holder = new ViewHolder();
            holder.nombreTextView = (TextView) convertView.findViewById(R.id.txtNombreRecordatorio);
            holder.id = (TextView) convertView.findViewById(R.id.txtId);


            convertView.setTag(holder);
        }else {
            //obtenermos el holder seteado
            holder = (ViewHolder) convertView.getTag();
        }

        //obtenemos la clase persona según la posicion
        recordatorios currentRecordatorio = (recordatorios) this.getItem(position);

        //seteamos los campos a los elementos relacionados a la vista
        holder.nombreTextView.setText(currentRecordatorio.getNombre());
        //holder.identificacionTextView.setText(currentRecordatorio.getIdentificacin());
        //holder.fnacimientoTextView.setText(currentRecordatorio.getFNacimiento());
        holder.id.setText(String.valueOf(currentRecordatorio.getId()));

        //retornamos la vista inflada
        return convertView;
    }

    static class ViewHolder{
        private TextView nombreTextView;
        //private TextView identificacionTextView;
        private ImageView imagenImageView;
        //private TextView fnacimientoTextView;
        private TextView id;
    }

    public adaptadorRecordatorios(Context contexto, int layout, List<recordatorios> listRecordatorios) {
        this.contexto = contexto;
        this.layout = layout;
        this.listRecordatorios = listRecordatorios;
    }
}
