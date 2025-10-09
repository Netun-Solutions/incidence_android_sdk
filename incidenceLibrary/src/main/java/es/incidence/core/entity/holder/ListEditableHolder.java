package es.incidence.core.entity.holder;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import es.incidence.core.utils.view.IDropField;
import es.incidence.core.utils.view.IField;

public class ListEditableHolder {
    public RelativeLayout layoutRoot;
    public RelativeLayout layoutRow;
    public IField field;
    public LinearLayout llSearchTop;
    public LinearLayout llSearch;
    public LinearLayout llSelectedContact;
    public ImageView ivDeleteContact;
    public IDropField dropfield;
}

