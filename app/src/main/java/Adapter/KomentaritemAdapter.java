package Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.coba.els_connect.R;

import java.util.ArrayList;

import Pojo.DC_POJO;

public class KomentaritemAdapter extends BaseAdapter {

    Context c;
    ArrayList<DC_POJO> dcalls;
    TextView tv_itemname,tv_itemkomen;

    int pageDetector;

    TableLayout tbLay;
    AppCompatButton btnLihat;

    public KomentaritemAdapter(Context c, ArrayList<DC_POJO> dcalls){

        this.c = c;
        this.dcalls = dcalls;
     }
    @Override
    public int getCount() {
        return dcalls.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.custom_item_komentar, parent, false);


            tv_itemname = convertView.findViewById(R.id.itemname);
            tv_itemkomen= convertView.findViewById(R.id.itemkomen);

            tv_itemname.setText(dcalls.get(position).getUsermail());
            tv_itemkomen.setText(dcalls.get(position).getKomentar());




        }

        return convertView;
    }
}
