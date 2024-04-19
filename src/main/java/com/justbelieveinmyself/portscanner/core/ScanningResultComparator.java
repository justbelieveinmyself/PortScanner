package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.core.values.Empty;
import com.justbelieveinmyself.portscanner.core.values.NotAvailable;

import java.util.Comparator;

@SuppressWarnings("unchecked")
public class ScanningResultComparator implements Comparator<ScanningResult> {
    private int index;
    private boolean ascending;

    @Override
    public int compare(ScanningResult r1, ScanningResult r2) {
        Object o1 = r1.getValues().get(index);
        Object o2 = r2.getValues().get(index);

        if (o1 == null) {
            o1 = NotAvailable.VALUE;
        }
        if (o2 == null) {
            o2 = NotAvailable.VALUE;
        }

        int result;
        if (o1 == o2) {
            result = 0;
        } else if (o1.getClass() == o2.getClass() && !(o1 instanceof String) && o2 instanceof Comparable) {
            // оба одного типа и Comparable
            result = ((Comparable) o1).compareTo(o2);
        } else {

            if (o1 instanceof Empty) result = ((Empty) o1).compareTo(o2);
            else if (o2 instanceof Empty) result = -((Empty) o2).compareTo(o1);
            else {
                result = o1.toString().compareToIgnoreCase(o2.toString());
            }
        }

        if (result == 0 && index != 0) {
            // если значения равны, то сортировать их по IPs
            result = ((Comparable) r1.getValues().get(0)).compareTo(r2.getValues().get(0));
        }

        return result * (ascending ? 1 : -1);
    }

    public void byIndex(int index, boolean ascending) {
        this.index = index;
        this.ascending = ascending;

        // это гарантирует, что все Empty объекты всегда будут в конце
        Empty.setSortDirection(ascending);
    }
}
