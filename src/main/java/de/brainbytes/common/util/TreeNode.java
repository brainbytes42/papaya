package de.brainbytes.common.util;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @param <T> Type of the concrete TreeNode-Implementation.
 * @author Fabian Krippendorff
 */
public abstract class TreeNode<T extends TreeNode<T>> {

    private T parent = null;
    private Set<T> children = new HashSet<>();

    private Set<HierarchyObserver<T>> hierarchyObservers = new CopyOnWriteArraySet<>();

    private HierarchyObserver<T> childHierarchyObservationForwarder = new HierarchyObserver<T>() {
        @Override
        public void onChildrenAdded(T eventSource, T changedNode, Set<T> children) {
            notifyObservers(o -> o.onChildrenAdded(TreeNode.this, changedNode, children));
        }

        @Override
        public void onChildrenRemoved(T eventSource, T changedNode, Set<T> children) {
            notifyObservers(o -> o.onChildrenRemoved(TreeNode.this, changedNode, children));
        }
    };


    public Optional<T> getParent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(T newParent) {
        if (this.parent != newParent) {

            getParent().ifPresent(parent -> ((TreeNode) parent).removeChild(this));

            this.parent = newParent;

            if (newParent != null) {
                ((TreeNode) newParent).addChild(this);
            }

            notifyObservers(o -> o.onParentChanged(this, getParent()));

        }
    }

    public Collection<T> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public boolean addChild(T child) {
        if (child == null) {
            throw new NullPointerException("Added child may not be null!");
        }

        boolean added = children.add(child);
        if (added) {
            ((TreeNode) child).setParent(this);
            child.addObserver(this.childHierarchyObservationForwarder);
            notifyObservers(o -> o.onChildrenAdded(this, this, Collections.singleton(child)));
        }
        return added;
    }

    public boolean addChildren(final Collection<? extends T> children) {
        // null-check for collection and contained elements
        if (children == null) {
            throw new NullPointerException("Collection of DataSources to be added may not be null!");
        } else {
            children.forEach(child -> {
                if (child == null) {
                    throw new NullPointerException(
                            "Collection of DataSources to be added may not contain null-elements!");
                }
            });
        }

        // add only children, that aren't already contained
        final Set<? extends T> filteredChildren = children.stream()
                                                          .filter(c -> !this.children.contains(c))
                                                          .collect(Collectors.toSet());

        // add new children
        boolean added = this.children.addAll(filteredChildren);

        if (added) {
            filteredChildren.forEach(child -> {
                ((TreeNode) child).setParent(this);
                ((TreeNode) child).addObserver(this.childHierarchyObservationForwarder);
            });
            notifyObservers(
                    o -> o.onChildrenAdded(this, this, Collections.unmodifiableSet(new HashSet<>(filteredChildren))));
        }

        return added;
    }

    public boolean removeChild(T child) {

        boolean removed = children.remove(child);
        if (removed) {
            child.setParent(null);
            child.removeObserver(this.childHierarchyObservationForwarder);
            notifyObservers(o -> o.onChildrenRemoved(this, this, Collections.singleton(child)));
        }
        return removed;
    }

    public boolean removeChildren(final Collection<? extends T> children) {

        // remove only elements that were contained.
        final Set<? extends T> filteredChildren = children.stream()
                                                          .filter(c -> this.children.contains(c))
                                                          .collect(Collectors.toSet());

        boolean removed = this.children.removeAll(filteredChildren);

        if (removed) {
            filteredChildren.forEach(child -> {
                child.setParent(null);
                child.removeObserver(this.childHierarchyObservationForwarder);
            });
            notifyObservers(
                    o -> o.onChildrenRemoved(this, this, Collections.unmodifiableSet(new HashSet<>(filteredChildren))));
        }
        return removed;
    }

    synchronized public void clearChildren() {
        removeChildren(children);

        if (!children.isEmpty()) {
            throw new IllegalStateException("TreeNode has still Children after clearing!");
        }
    }

    public HierarchyObserver<T> addObserver(HierarchyObserver<T> hierarchyObserver) {
        if (hierarchyObserver == null) {
            throw new NullPointerException("Observer may not be null!");
        }
        this.hierarchyObservers.add(hierarchyObserver);
        return hierarchyObserver;
    }

    public boolean removeObserver(HierarchyObserver<T> hierarchyObserver) {
        return this.hierarchyObservers.remove(hierarchyObserver);
    }

    private void notifyObservers(Consumer<? super HierarchyObserver> notification) {
        this.hierarchyObservers.forEach(notification);
    }

    public interface HierarchyObserver<T extends TreeNode<T>> {

        default void onChildrenAdded(T eventSource, T changedNode, Set<T> children) {
        }

        default void onChildrenRemoved(T eventSource, T changedNode, Set<T> children) {
        }

        default void onParentChanged(T source, Optional<T> newParent) {
        }
    }
}
