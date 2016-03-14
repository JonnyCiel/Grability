package com.jonny.grability.data;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Jonny on 09/03/2016.
 */
public class Apps implements Serializable{

    private String titulo;
    private String descripcion;
    private String autor;
    private String categoria;
    private Bitmap mImagen2;


    public Apps(){

    }

    public Apps(String titulo, String descripcion, String categoria, String autor){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.autor = autor;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setImagen2(Bitmap imagen2) {
        mImagen2 = imagen2;
    }

    public Bitmap getImagen2() {
        return mImagen2;
    }
}
