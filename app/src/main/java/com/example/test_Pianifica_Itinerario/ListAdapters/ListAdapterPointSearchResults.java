package com.example.test_Pianifica_Itinerario.ListAdapters;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.test_Pianifica_Itinerario.Controllers.RicercaPuntoController;
import com.example.test_Pianifica_Itinerario.R;
import com.example.test_Pianifica_Itinerario.Utils.AddressUtils;

import java.util.ArrayList;

public class ListAdapterPointSearchResults extends ArrayAdapter<Address> {

    private RicercaPuntoController ricercaPuntoController;

    public ListAdapterPointSearchResults(Context context, ArrayList<Address> resultPoints, RicercaPuntoController controller) {
        super(context, R.layout.list_element_risultato_ricerca_punto,resultPoints);

        this.ricercaPuntoController = controller;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Address resultPoint = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.list_element_risultato_ricerca_punto, parent, false);
        }

        TextView textView_resultPoint =  convertView.findViewById(R.id.listElementResultPoint_textView_listElement);
        RelativeLayout relativeLayout_listItem = convertView.findViewById(R.id.listElementResultPoint_relativeLayout_listElement);

        textView_resultPoint.setText(AddressUtils.getAddressName(resultPoint));

        relativeLayout_listItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ricercaPuntoController.selectResultPoint(resultPoint);
            }
        });
        return convertView;
    }
}
