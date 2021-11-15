package com.uqac_8inf865.sysi;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.TextView;

public class PopupSpot extends Dialog {

    //fields
    private Button yesButton,neutralButton, noButton;
    private String title,description,yesText,neutralText,noText, notetext;
    private Integer note;
    private TextView titleView, descriptionView, noteView;

    // constructor
    public PopupSpot(Activity activity)
    {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.activity_popup_spot);
        this.yesButton = findViewById(R.id.yesButtonInfo);
        this.neutralButton = findViewById(R.id.neutralButtonInfo);
        this.noButton = findViewById(R.id.noButtoninfo);
        this.yesText = "Yes";
        this.neutralText = "Neutral";
        this.noText = "No";
        this.title = "default title";
        this.description = "default description";
        this.notetext = "note";
        this.note = 0;
        this.titleView=findViewById(R.id.titleInfo);
        this.descriptionView=findViewById(R.id.descInfo);
        this.noteView=findViewById(R.id.note);
    }

    public Button getNoButton() {
        return noButton;
    }

    public Button getYesButton() {
        return yesButton;
    }

    public Button getNeutralButton() { return neutralButton;}

    public void setYesButtonText(String yesText){this.yesButton.setText(yesText);}

    public void setNeutralButtonText(String neutralText){this.neutralButton.setText(neutralText);}

    public void setNoButtonText(String noText){this.noButton.setText(noText);}

    public void setTitle(String title){ this.title=title; }

    public void setDescription(String description){ this.description=description; }

    public void setNotepop(String notetext){this.notetext = notetext;}

    public void setNote(Integer note){this.note = note + 1;}

    public Integer getNote(){return note;}



    public void build()
    {
        show();
        titleView.setText(title);
        descriptionView.setText(description);
        noteView.setText(notetext);
    }
}