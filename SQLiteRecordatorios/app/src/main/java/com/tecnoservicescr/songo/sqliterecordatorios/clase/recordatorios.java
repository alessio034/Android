package com.tecnoservicescr.songo.sqliterecordatorios.clase;

public class recordatorios {
    private int id;
    private String Nombre;
    private String recordatorio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(String recordatorio) {
        this.recordatorio = recordatorio;
    }

    public recordatorios(int id, String nombre, String recordatorio) {
        this.id = id;
        Nombre = nombre;
        this.recordatorio = recordatorio;
    }
}
