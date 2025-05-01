package es.incidence.core.interfaces;

import es.incidence.core.entity.ListItem;

public interface OnContactItemClickListener {
    void contactClick();
    void addContactClick();
    void deleteContact(ListItem item);
}
