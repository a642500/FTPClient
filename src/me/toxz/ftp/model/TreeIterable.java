package me.toxz.ftp.model;

/**
 * Created by Carlos on 2015/10/27.
 */
public abstract class TreeIterable<T extends TreeIterable> implements Comparable<TreeIterable> {
    @Override
    public int compareTo(TreeIterable o) {
        if (isParent() && o.isParent())
            return 0;

        if (isParent())
            return -1;
        else if (o.isParent())
            return 1;
        if (hasChild() && o.hasChild()) {
            return o.hasChildCompareToHasChild(this);
        }
        if (hasChild()) {
            return -1;
        } else if (o.hasChild()) {
            return 1;
        } else {
            return o.basicCompareToBasic(this);
        }
    }

    public abstract boolean hasChild();

    public abstract boolean isParent();

    public abstract boolean isRootParent();

    protected abstract int hasChildCompareToHasChild(T o);

    protected abstract int basicCompareToBasic(T o);
}
