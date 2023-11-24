package es.incidence.core.entity;

import android.graphics.drawable.Drawable;

public class ListItem
{
    public String title;
    public String subtitle;
    public String image;
    public Drawable leftDrawable;
    public Drawable rightDrawable;
    public Object object;

    public int type;
    public Integer leftDrawableSize;
    public Integer rightDrawableSize;
    public Integer idBackgroundColor;
    public Integer idTextColor;
    public Integer titleColor;
    public Integer arrowColor;

    public boolean clicable = true;
    public boolean editClicable = false;

    public boolean exclamation;
    public String exclamationMessage;
    public Drawable exclamationDrawable;

    public boolean checkable;
    private boolean checked;
    public boolean isChecked()
    {
        return checked;
    }
    public void setChecked(boolean c)
    {
        this.checked = c;
    }

    //dropField
    public boolean dropfield;
    public String titleDrop;
    public int menuDrop;

    //Can edit
    public boolean editable = true;

    public ListItemListener listItemListener;

    public ListItem(String title, Object object)
    {
        this.title = title;
        this.object = object;
    }

    public ListItem(String title, String subtitle)
    {
        this.title = title;
        this.subtitle = subtitle;
        this.object = object;
    }

    public ListItem(String title, String image, Object object)
    {
        this.title = title;
        this.image = image;
        this.object = object;
    }

    public ListItem(String title, Drawable drawable, Object object)
    {
        this.title = title;
        this.leftDrawable = drawable;
        this.object = object;
    }
}