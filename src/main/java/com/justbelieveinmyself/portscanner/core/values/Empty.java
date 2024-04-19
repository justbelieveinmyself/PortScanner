package com.justbelieveinmyself.portscanner.core.values;

public class Empty implements Comparable<Object> {
    static int sortDirection = 1;

    /**
     * @param ascending изменяет поведение сортировки всех Empty объектов,
     *      при true -> все Empty объекты больше чем любой другой объект,
     *      при false -> наоборот.
     * Это необходимо для отображения их всегда в конце сортированного списка.
     */
    public static void setSortDirection(boolean ascending) {
        Empty.sortDirection = ascending? 1 : -1;
    }

    @Override
    public int compareTo(Object that) {
        if (this == that) return 0;
        return sortDirection;
    }

}
