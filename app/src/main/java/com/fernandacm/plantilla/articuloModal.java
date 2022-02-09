package com.fernandacm.plantilla;

import android.graphics.Bitmap;

import java.io.File;
import java.util.Arrays;

public class articuloModal {

    String nombre;
    String tipo_activo;
    String descripcion;
    String fecha;
    byte[] imagen;

   public articuloModal(String nombre, String tipo_activo,String descripcion,String fecha,  byte[] imagen){
        this.nombre = nombre;
        this.tipo_activo = tipo_activo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "articuloModal{" +
                "nombre='" + nombre + '\'' +
                ", tipo_activo='" + tipo_activo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                ", imagen=" + Arrays.toString(imagen) +
                '}';
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public String getTipo_activo() {
        return tipo_activo;
    }

    public void setTipo_activo(String tipo_activo) {
        this.tipo_activo = tipo_activo;
    }

    public  byte[] getImagen() {
        return imagen;
    }

    public void setImagen( byte[] imagen) {
        this.imagen = imagen;
    }

}
