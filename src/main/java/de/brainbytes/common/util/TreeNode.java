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
        public void onChildrenAdded(T eventSource, T changedNode, Set<T> addedChildren) {

            boolean moveInSubtree = false;

            if(addedChildren.size() == 1){
                TreeNode<T> addedChild = addedChildren.iterator().next();
                if(addedChild.isMoving!=null && TreeNode.this.subtreeContains(addedChild.isMoving.from))
                    moveInSubtree=true;
            }

            if (!moveInSubtree) {
                notifyObservers(o -> o.onChildrenAdded(TreeNode.this, changedNode, addedChildren));
            }
        }

        @Override
        public void onChildrenRemoved(T eventSource, T changedNode, Set<T> removedChildren) {

            boolean moveInSubtree = false;

            if(removedChildren.size() == 1){
                TreeNode<T> addedChild = removedChildren.iterator().next();
                if(addedChild.isMoving!=null && TreeNode.this.subtreeContains(addedChild.isMoving.to))
                    moveInSubtree=true;
            }

            if (!moveInSubtree) {
                notifyObservers(o -> o.onChildrenRemoved(TreeNode.this, changedNode, removedChildren));
            }
        }
    };

    private boolean subtreeContains(TreeNode<T> node) {

        if (node == null) {
            throw new NullPointerException("Node to check is null!");
        }

        TreeNode<T> traverseNode = node;

        while (traverseNode != null) {
            if (this == traverseNode) {
                return true;
            }
            traverseNode = traverseNode.getParent().orElse(null);
        }

        return false;
    }

    private Moving isMoving = null;

    public Optional<T> getParent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(final T newParent) {
        if (newParent == this) {
            throw new IllegalArgumentException("TreeNode " + this + " cannot be parent to itself!");
        } else if (this.parent != newParent && isMoving == null) {

            if(this.parent!=null && newParent!=null){
                this.isMoving = new Moving(this.parent, newParent);
            }

            getParent().ifPresent(p -> ((TreeNode) p).removeChild(this));

            this.parent = newParent;

            getParent().ifPresent(p -> ((TreeNode) p).addChild(this));

            notifyObservers(o -> o.onParentChanged(this, getParent()));

            this.isMoving = null; // clean up
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

        default void onChildrenAdded(T eventSource, T changedNode, Set<T> addedChildren) {
        }

        default void onChildrenRemoved(T eventSource, T changedNode, Set<T> removedChildren) {
        }

        default void onParentChanged(T source, Optional<T> newParent) {
        }
    }

    private class Moving {
        private final T from;
        private final T to;

        private Moving(T from, T to) {
            this.from = from;
            this.to = to;
        }
    }
}
