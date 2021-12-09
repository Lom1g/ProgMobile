package com.uqac_8inf865.sysi;

import android.app.Activity;
import android.app.Dialog;
import android.media.Image;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class PopupAddSpot extends Dialog {

    //fields
    private Button addBtn;
    private ImageButton cancelBtn;
    private Spinner spinner;
    private TextView titleTextView, descriptionTextView;

    private List<Category> categoryList;

    // constructor
    public PopupAddSpot(Activity activity)
    {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.activity_popup_add_spot);
        this.addBtn = findViewById(R.id.addButton);
        this.cancelBtn = findViewById(R.id.cancelButton);

        this.titleTextView = findViewById(R.id.entertitle);
        this.descriptionTextView = findViewById(R.id.enterdescrip);

        this.spinner = findViewById(R.id.action_bar_spinner);
        this.categoryList = CategoryDataUtils.getCategories();
        CategoryAdapter categoryAdapter = new CategoryAdapter(activity,
                CategoryDataUtils.getCategories());
        this.spinner.setAdapter(categoryAdapter);

    }

    public ImageButton getNoButton() {
        return cancelBtn;
    }

    public Button getYesButton() {
        return addBtn;
    }

    public String getTitle(){return titleTextView.getText().toString();}

    public String getDescription(){return descriptionTextView.getText().toString();}

    public String getCategory(){
        Category category = (Category) spinner.getSelectedItem();
        return category.getName();
    }

    public void build()
    {
        show();
    }
}