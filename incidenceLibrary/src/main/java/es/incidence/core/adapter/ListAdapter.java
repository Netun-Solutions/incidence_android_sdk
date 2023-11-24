package es.incidence.core.adapter;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.e510.commons.fragment.BaseFragment;
import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.StringUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.entity.ListItem;
import es.incidence.core.entity.holder.ListEditableHolder;
import es.incidence.core.entity.holder.ListViewHolder;
import es.incidence.core.manager.ImageManager;
import es.incidence.core.utils.Tooltip;
import es.incidence.core.utils.view.IDropField;
import es.incidence.core.utils.view.IField;
import es.incidence.library.IncidenceLibraryManager;

public class ListAdapter extends BaseAdapter
{
    public enum Type
    {
        TITLE_SUBTITLE,
        TITLE,
        EDITABLE,
        EDITABLE_DELETE
    }

    private static final int ROW = 0;
    private static String TAG = makeLogTag(ListAdapter.class);
    private BaseFragment fragment;
    ArrayList<ListItem> items;
    private Type type;

    public ListAdapter(BaseFragment fragment, Type type, ArrayList<ListItem> items)
    {
        this.fragment = fragment;
        this.type = type;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return ROW;
    }

    public View getView (final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;

        try
        {
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) this.fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                int layout = R.layout.row_list;
                if (type == Type.TITLE_SUBTITLE)
                    layout = R.layout.row_list_title_subtitle;
                else if (type == Type.EDITABLE || type == Type.EDITABLE_DELETE)
                    layout = R.layout.row_list_editable;

                rowView = inflater.inflate(layout, parent, false);

                createViewHolder(rowView);
            }

            final ListItem item = items.get(position);
            populateView(rowView, item);
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "Error: " + e.getMessage(), e);
        }

        return rowView;
    }

    public void createViewHolder(View rowView)
    {
        if (type == Type.EDITABLE || type == Type.EDITABLE_DELETE)
        {
            ListEditableHolder viewHolder = new ListEditableHolder();
            viewHolder.layoutRoot = rowView.findViewById(R.id.layoutRoot);
            viewHolder.layoutRow = rowView.findViewById(R.id.layoutRow);
            viewHolder.field = rowView.findViewById(R.id.field);
            viewHolder.dropfield = rowView.findViewById(R.id.dropfield);

            Drawable drawable = Utils.getDrawable(fragment.getContext(), R.drawable.icon_edit);
            if (type == Type.EDITABLE_DELETE)
            {
                drawable = Utils.getDrawable(fragment.getContext(), R.drawable.icon_delete);
            }
            viewHolder.field.setImageOK(drawable);
            if (type == Type.EDITABLE_DELETE)
            {
                viewHolder.field.setImageOKTint(Utils.getColor(fragment.getContext(), R.color.error));
            }
            else
            {
                viewHolder.field.setImageOKSize(Utils.dpToPx(16));
            }
            viewHolder.field.showOK();
            viewHolder.field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b)
                    {
                        viewHolder.field.showOK();
                    }
                }
            });

            viewHolder.dropfield.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b)
                    {
                        viewHolder.dropfield.showOK();
                    }
                }
            });

            rowView.setTag(viewHolder);
        }
        else
        {
            ListViewHolder viewHolder = new ListViewHolder();
            viewHolder.layoutRoot = rowView.findViewById(R.id.layoutRoot);
            viewHolder.layoutRow = rowView.findViewById(R.id.layoutRow);
            viewHolder.layoutRight = rowView.findViewById(R.id.layoutRight);
            viewHolder.line = rowView.findViewById(R.id.line);
            viewHolder.imgLeft = rowView.findViewById(R.id.imgLeft);
            viewHolder.imgRight = rowView.findViewById(R.id.imgRight);
            viewHolder.txtSwitch = rowView.findViewById(R.id.txtSwitch);
            viewHolder.txtTitle = rowView.findViewById(R.id.txtTitle);
            viewHolder.txtSubtitle = rowView.findViewById(R.id.txtSubtitle);
            viewHolder.imgExclamation = rowView.findViewById(R.id.imgExclamation);
            if (viewHolder.txtTitle != null) {
                FontUtils.setTypeValueText(viewHolder.txtTitle, Constants.FONT_REGULAR, fragment.getContext());
                IncidenceLibraryManager.instance.setTextColor(viewHolder.txtTitle);
            }
            if (viewHolder.txtSubtitle != null)
                FontUtils.setTypeValueText(viewHolder.txtSubtitle, Constants.FONT_REGULAR, fragment.getContext());

            rowView.setTag(viewHolder);
        }
    }

    public void populateView(View rowView, ListItem item)
    {
        if (type == Type.EDITABLE || type == Type.EDITABLE_DELETE)
        {
            final ListEditableHolder holder = (ListEditableHolder) rowView.getTag();

            holder.field.setTitle(item.title);
            holder.field.setHint(item.title);
            holder.field.setType(item.type);
            holder.field.setText(item.subtitle);

            if (item.idBackgroundColor != null)
            {
                holder.field.setBackground(fragment.getContext(), item.idBackgroundColor);
            }
            if (item.idTextColor != null)
            {
                holder.field.setTextColor(item.idTextColor);
            }

            if (!item.editable)
            {
                holder.field.disable();
                holder.field.setTextWatcher(null);

                //ocultamos icono edit o borrar
                holder.field.hideOK();

                if (item.dropfield)
                {
                    holder.dropfield.disable();
                }
            }
            else if (item.editClicable)
            {
                //mostramos icono edit o borrar
                holder.field.showOK();
                Drawable drawable = Utils.getDrawable(fragment.getContext(), R.drawable.icon_edit);
                if (item.rightDrawable != null)
                {
                    drawable = item.rightDrawable;
                }
                if (item.rightDrawableSize != null)
                {
                    holder.field.setImageOKSize(item.rightDrawableSize);
                }
                holder.field.setImageOK(drawable);

                holder.field.setVisibility(View.VISIBLE);
                holder.dropfield.setVisibility(View.INVISIBLE);
                holder.field.disable();
                holder.field.setTextWatcher(null);
                holder.field.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.onClickRow(item);
                    }
                });
            }
            else
            {
                //mostramos icono edit o borrar
                holder.field.showOK();

                if (item.type == IField.TYPE_DATE)
                {
                    holder.field.enableDatePicker();
                }
                else
                {
                    holder.field.enable();
                    if (item.dropfield)
                    {
                        holder.dropfield.enable();
                    }
                }

                if (item.dropfield)
                {
                    holder.field.setVisibility(View.INVISIBLE);
                    holder.dropfield.setVisibility(View.VISIBLE);
                    Drawable drawable = Utils.getDrawable(fragment.getContext(), R.drawable.icon_edit);
                    if (item.rightDrawable != null)
                    {
                        drawable = item.rightDrawable;
                    }
                    else if (type == Type.EDITABLE_DELETE)
                    {
                        drawable = Utils.getDrawable(fragment.getContext(), R.drawable.icon_delete);
                    }

                    if (item.rightDrawableSize != null)
                    {
                        holder.field.setImageOKSize(item.rightDrawableSize);
                    }

                    holder.dropfield.setImageOK(drawable);

                    if (type == Type.EDITABLE_DELETE)
                    {
                        holder.dropfield.setImageOKTint(Utils.getColor(fragment.getContext(), R.color.error));
                    }
                    else
                    {
                        holder.dropfield.setImageOKSize(Utils.dpToPx(16));
                    }
                    holder.dropfield.showOK();
                    holder.dropfield.setMenuTitle(item.titleDrop);
                    holder.dropfield.setMenu(item.menuDrop);
                    holder.dropfield.setOnMenuChangeListener(new IDropField.DropMenuListener() {
                        @Override
                        public void onItemSelected(String value) {
                            item.titleDrop = value;
                        }
                    });

                    holder.dropfield.setTitle(item.title);
                    holder.dropfield.setHint(item.title);
                    holder.dropfield.setText(item.subtitle);

                    holder.dropfield.setTextWatcher(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            String text = holder.dropfield.getText();
                            item.subtitle = text;

                            if (item.listItemListener != null) {
                                item.listItemListener.onChangeValue();
                            }
                        }
                    });
                }
                else
                {
                    holder.field.setVisibility(View.VISIBLE);
                    holder.dropfield.setVisibility(View.INVISIBLE);

                    holder.field.setTextWatcher(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            String text = holder.field.getText();
                            item.subtitle = text;

                            if (item.listItemListener != null) {
                                item.listItemListener.onChangeValue();
                            }
                        }
                    });
                }


                rowView.setOnClickListener(null);
            }
        }
        else
        {
            final ListViewHolder holder = (ListViewHolder) rowView.getTag();

            if (item.leftDrawableSize != null)
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imgLeft.getLayoutParams();
                params.width = item.leftDrawableSize;
                params.height = item.leftDrawableSize;
                holder.imgLeft.setLayoutParams(params);
            }
            else
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imgLeft.getLayoutParams();
                params.width = Utils.dpToPx(25);
                params.height = Utils.dpToPx(25);
                holder.imgLeft.setLayoutParams(params);
            }

            if (item.rightDrawableSize != null)
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imgRight.getLayoutParams();
                params.width = item.rightDrawableSize;
                params.height = item.rightDrawableSize;
                holder.imgRight.setLayoutParams(params);
            }

            if (item.image != null)
            {
                if (StringUtils.isValidURL(item.image))
                {
                    ImageManager.loadFitImage(fragment.getContext(), item.image, holder.imgLeft);
                }
                else
                {
                    holder.imgLeft.setImageDrawable(Utils.getDrawable(fragment.getContext(), item.image));
                }

                holder.imgLeft.setVisibility(View.VISIBLE);
            }
            else if (item.leftDrawable != null)
            {
                holder.imgLeft.setImageDrawable(item.leftDrawable);
                holder.imgLeft.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.imgLeft.setImageDrawable(null);
                holder.imgLeft.setVisibility(View.GONE);
            }

            if (item.rightDrawable != null)
            {
                holder.imgRight.setImageDrawable(item.rightDrawable);
                holder.layoutRight.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.imgRight.setImageDrawable(Utils.getDrawable(fragment.getContext(), R.drawable.icon_arrow_right));
            }

            holder.txtTitle.setText(item.title);
            if (item.titleColor != null)
            {
                holder.txtTitle.setTextColor(item.titleColor);
            }

            if (item.arrowColor != null)
            {
                holder.imgRight.setColorFilter(item.arrowColor);
            }

            if (holder.txtSubtitle != null)
            {
                if (item.subtitle != null)
                    holder.txtSubtitle.setText(item.subtitle);
                else
                    holder.txtSubtitle.setText("");
            }

            if (item.exclamation)
            {
                holder.imgExclamation.setVisibility(View.VISIBLE);
                if (item.exclamationMessage != null)
                {
                    holder.imgExclamation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTooltip(holder.imgExclamation, item);
                        }
                    });
                }
                else
                {
                    holder.imgExclamation.setOnClickListener(null);
                }

                if (item.exclamationDrawable != null)
                {
                    holder.imgExclamation.setImageDrawable(item.exclamationDrawable);
                }
            }
            else
            {
                holder.imgExclamation.setVisibility(View.INVISIBLE);
                holder.imgExclamation.setOnClickListener(null);
            }

            if (item.checkable)
            {
                holder.imgRight.setVisibility(View.GONE);
                holder.txtSwitch.setVisibility(View.VISIBLE);

                holder.txtSwitch.setOnCheckedChangeListener(null);
                holder.txtSwitch.setChecked(item.isChecked());
                holder.txtSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setChecked(isChecked);
                        fragment.onClickRow(item);
                    }
                });

                rowView.setOnClickListener(null);
            }
            else
            {
                holder.txtSwitch.setVisibility(View.GONE);
                holder.imgRight.setVisibility(View.VISIBLE);

                holder.txtSwitch.setOnCheckedChangeListener(null);

                if (item.clicable)
                {
                    rowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fragment.onClickRow(item);
                        }
                    });
                }
                else
                {
                    rowView.setOnClickListener(null);
                }
            }


        }
    }

    public void showTooltip(View rowView, ListItem item)
    {
        Tooltip.showTooltip(fragment.getContext(), rowView, item.exclamationMessage);
    }
}