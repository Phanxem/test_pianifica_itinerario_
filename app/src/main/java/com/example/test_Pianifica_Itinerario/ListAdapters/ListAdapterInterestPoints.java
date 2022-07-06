package com.example.test_Pianifica_Itinerario.ListAdapters;

import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.test_Pianifica_Itinerario.Controllers.PianificaItinerarioController;
import com.example.test_Pianifica_Itinerario.R;
import com.example.test_Pianifica_Itinerario.Utils.AddressUtils;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterInterestPoints extends ArrayAdapter<Address> {

    private PianificaItinerarioController pianificaItinerarioController;

    public ListAdapterInterestPoints(Context context, List<Address> interestPoints, PianificaItinerarioController controller) {
        super(context, R.layout.list_element_punto_intermedio,interestPoints);

        this.pianificaItinerarioController = controller;
    }

    @NonNull
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        Address interestPoint = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.list_element_punto_intermedio, parent, false);
        }

        TextView textView_pos =  convertView.findViewById(R.id.listElement_textView_pos);
        TextView textView_name =  convertView.findViewById(R.id.listElement_textView_name);
        ImageView imageView_iconCancel = convertView.findViewById(R.id.listElementIntermediatePoint_imageView_listElementCancel);
        RelativeLayout relativeLayout_listItem = convertView.findViewById(R.id.listElementIntermediatePoint_relativeLayout_listElement);

        textView_name.setText(AddressUtils.getAddressName(interestPoint));
        textView_pos.setText( (position + 1) + " :");

        relativeLayout_listItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("TAGDF: ", "CLICK " + position);
                pianificaItinerarioController.selectIntermediatePoint(position);
                notifyDataSetChanged();
            }
        });

        imageView_iconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAGDF: ", "canc " + position);
                pianificaItinerarioController.cancelIntermediatePoint(position);
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

}
