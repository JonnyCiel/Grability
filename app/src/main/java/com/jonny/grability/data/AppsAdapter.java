package com.jonny.grability.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jonny.grability.R;

import java.util.List;

/**
 * Created by Jonny on 09/03/2016.
 */
public class AppsAdapter extends ArrayAdapter<Apps> {

    //Atributos
    private List<Apps> listaApps;

    private int resource;
    private LayoutInflater mInflater;

    public AppsAdapter(Context context, int resource, List<Apps> objects) {
        super(context, resource, objects);

        this.listaApps = objects;
        this.resource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }


    @Override
    public Apps getItem(int position) {
        return listaApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Regresando la cantidad de items que contiene el array
     * @return
     */
    @Override
    public int getCount() {
        if (listaApps != null){
            return listaApps.size();
        }else return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder= null;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layouts_items, null);
            holder.mImageViewIcon = (ImageView) convertView.findViewById(R.id.imagenApp);
            holder.mTextViewTitle = (TextView) convertView.findViewById(R.id.textoTitulo);
            holder.mTextViewDescription = (TextView) convertView.findViewById(R.id.textoDescripcion);
            holder.mTextViewArtist = (TextView) convertView.findViewById(R.id.textoArtista);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Apps item = listaApps.get(position);

        holder.mTextViewTitle.setText(item.getTitulo());
        holder.mTextViewArtist.setText(item.getCategoria());
        holder.mImageViewIcon.setImageBitmap(item.getImagen2());

        return convertView;
    }

    public static class ViewHolder{

        public ImageView mImageViewIcon;
        public TextView mTextViewTitle;
        public TextView mTextViewDescription;
        public TextView mTextViewArtist;
    }


}
