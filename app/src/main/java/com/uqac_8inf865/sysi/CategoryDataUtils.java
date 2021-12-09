package com.uqac_8inf865.sysi;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;

public class CategoryDataUtils {

    public static ArrayList<Category> getCategories(){
        Category sports = new Category(1, "Sports", R.color.sports);
        Category culture = new Category(2, "Culture", R.color.culture);
        Category nature = new Category(3, "Nature", R.color.nature);
        Category other = new Category(3, "Others", R.color.others);

        ArrayList<Category> list = new ArrayList<>();
        list.add(sports);
        list.add(culture);
        list.add(nature);
        list.add(other);

        return list;
    }

    public static float getFloatIcon(String category){
        switch (category){
            case "Sports":
                return BitmapDescriptorFactory.HUE_BLUE;
            case "Culture":
                return BitmapDescriptorFactory.HUE_VIOLET;
            case "Nature":
                return BitmapDescriptorFactory.HUE_GREEN;
            case "Others":
                return BitmapDescriptorFactory.HUE_RED;
            default:
                return BitmapDescriptorFactory.HUE_YELLOW;
        }
    }
}
