package es.incidence.core.entity.sign;

import android.view.View;

public class SignStep
{
    public int id;
    public String navigationTitle;
    public String title;
    public String titleField;
    public String titleMenuField;
    public String hintField;
    public int typeField;
    public SignStepType type;
    public View customFullView;
    public View customView;
    public SignStepValidation validation;

    public SignStep(int id)
    {
        this.id = id;
    }
}
