package com.uqac_8inf865.sysi;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;

public class PopupAddSpot extends Dialog {

    //fields
    private Button yesButton, noButton;

    // constructor
    public PopupAddSpot(Activity activity)
    {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.activity_popup_add_spot);
        this.yesButton = findViewById(R.id.yesButton);
        this.noButton = findViewById(R.id.noButton);
    }

    public Button getNoButton() {
        return noButton;
    }

    public Button getYesButton() {
        return yesButton;
    }

    public void build()
    {
        show();
    }
}