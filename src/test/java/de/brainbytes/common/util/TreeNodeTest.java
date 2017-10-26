package de.brainbytes.common.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TreeNodeTest {

    private TestTreeNode nodeUnderTest;

    @Before
    public void setUp() throws Exception {
        nodeUnderTest = new TestTreeNode();
    }

    @Test
    public void getChildrenIsInitiallyEmpty() throws Exception {
        assertNotNull(nodeUnderTest.getChildren());
        assertTrue(nodeUnderTest.getChildren().isEmpty());
    }

    @Test
    public void addingChildrenOneByOne() throws Exception {
        TestTreeNode a = new TestTreeNode();
        TestTreeNode b = new TestTreeNode();
        TestTreeNode c = new TestTreeNode();
        nodeUnderTest.addChild(a);
        nodeUnderTest.addChild(b);
        nodeUnderTest.addChild(c);

        assertThat(nodeUnderTest.getChildren(), containsInAnyOrder(a, b, c));
    }

    @Test(expected = NullPointerException.class)
    public void addedChildMyNotBeNull() throws Exception {
        nodeUnderTest.addChild(null);
    }

    @Test
    public void addingChildOfSubclass() throws Exception {
        nodeUnderTest.addChild(new TestTreeNodeExtended());
    }

    @Test
    public void addingChildrenBatch() throws Exception {
        TestTreeNode a = new TestTreeNode();
        TestTreeNode b = new TestTreeNode();
        TestTreeNode c = new TestTreeNode();
        TestTreeNode d = new TestTreeNode();

        nodeUnderTest.addChildren(Arrays.asList(a, b));
        nodeUnderTest.addChildren(Arrays.asList(c, d));

        assertThat(nodeUnderTest.getChildren(), containsInAnyOrder(a, b, c, d));
    }

    @Test(expected = NullPointerException.class)
    public void addedCollectionMayNotBeNull() throws Exception {
        nodeUnderTest.addChildren(null);
    }

    @Test(expected = NullPointerException.class)
    public void addedCollectionMayNotContainNull() throws Exception {
        TestTreeNode a = new TestTreeNode();
        TestTreeNode b = new TestTreeNode();
        nodeUnderTest.addChildren(Arrays.asList(a, null, b));
    }

    @Test
    public void noChildFromCollectionContainingNullIsAdded() throws Exception {
        TestTreeNode a = new TestTreeNode();
        TestTreeNode b = new TestTreeNode();
        TestTreeNode c = new TestTreeNode();
        nodeUnderTest.addChild(a);
        try {
            nodeUnderTest.addChildren(Arrays.asList(b, null, c));
        } catch (Exception e) {
            // expected exception - only side effects are investigated here
        }
        assertThat(nodeUnderTest.getChildren(), contains(a));
    }

    @Test
    public void childrenCollectionIsntExposed() throws Exception {
        TestTreeNode a = new TestTreeNode();
        nodeUnderTest.addChild(a);

        try {
            nodeUnderTest.getChildren().add(new TestTreeNode());
        } catch (Exception e) {
            // at this point, it's irrelevant if the returned collection is immutable or a copy - and only
            // the first case will raise an exception, so no assumption here.
        }

        assertThat(nodeUnderTest.getChildren(), contains(a));
    }

    @Test
    public void addingCollectionOfSubtypesIsPossible() throws Exception {
        TestTreeNodeExtended a = new TestTreeNodeExtended();
        TestTreeNodeExtended b = new TestTreeNodeExtended();
        List<TestTreeNodeExtended> extendeds = Arrays.asList(a, b);
        nodeUnderTest.addChildren(extendeds);
    }

    @Test
    public void noDuplicateChildrenInHierarchy() throws Exception {

        TestTreeNode parent = new TestTreeNode();
        TestTreeNode priorInParent = new TestTreeNode();
        parent.addChild(priorInParent);

        TestTreeNode child = new TestTreeNode();
        TestTreeNode priorInChild = new TestTreeNode();
        child.addChild(priorInChild);

        parent.addChild(nodeUnderTest);
        nodeUnderTest.addChild(child);
        assertThat(parent.getChildren(), containsInAnyOrder(nodeUnderTest, priorInParent));
        assertThat(nodeUnderTest.getChildren(), containsInAnyOrder(child));
        assertThat(child.getChildren(), containsInAnyOrder(priorInChild));


        // expect moved elements, but no duplicates
        nodeUnderTest.addChild(priorInParent);
        nodeUnderTest.addChild(priorInChild);
        assertThat(parent.getChildren(), containsInAnyOrder(nodeUnderTest));
        assertThat(nodeUnderTest.getChildren(), containsInAnyOrder(child, priorInChild, priorInParent));
        assertThat(child.getChildren(), is(empty()));
    }

    @Test
    public void removeContainedChild() throws Exception {
        TestTreeNode childA = new TestTreeNode();
        TestTreeNode childB = new TestTreeNode();

        nodeUnderTest.addChildren(Arrays.asList(childA, childB));
        nodeUnderTest.removeChild(childA);

        assertThat(nodeUnderTest.getChildren(), contains(childB));
    }

    @Test
    public void removeContainedChildren() throws Exception {
        TestTreeNode childA = new TestTreeNode();
        TestTreeNode childB = new TestTreeNode();
        TestTreeNode childC = new TestTreeNode();

        nodeUnderTest.addChildren(Arrays.asList(childA, childB, childC));
        nodeUnderTest.removeChildren(Arrays.asList(childA, childB));

        assertThat(nodeUnderTest.getChildren(), contains(childC));
    }

    @Test
    public void removeNullIsAllowed() throws Exception {
        boolean removed = nodeUnderTest.removeChild(null);

        assertFalse(removed);
    }

    @Test
    public void removeMultipleContainingNullIsAllowed() throws Exception {
        TestTreeNode child = new TestTreeNode();
        nodeUnderTest.addChild(child);

        boolean removed = nodeUnderTest.removeChildren(Arrays.asList(null, child));

        assertTrue(removed);
    }

    @Test
    public void removeNotContainedChildIsAllowed() throws Exception {
        boolean removed = nodeUnderTest.removeChild(new TestTreeNode());

        assertFalse(removed);
    }

    @Test
    public void removeMultipleContainingNotContainedChildIsAllowed() throws Exception {
        TestTreeNode child = new TestTreeNode();
        nodeUnderTest.addChild(child);

        boolean removed = nodeUnderTest.removeChildren(Arrays.asList(new TestTreeNode(), child));

        assertTrue(removed);
    }

    @Test
    public void emptyCollectionRemainingAfterClearingChildren() throws Exception {
        nodeUnderTest.addChildren(Arrays.asList(new TestTreeNode(), new TestTreeNode()));

        nodeUnderTest.clearChildren();

        assertTrue(nodeUnderTest.getChildren().isEmpty());
    }

    @Test
    public void parentIsSetOnAdding() throws Exception {
        TestTreeNode parent = new TestTreeNode();
        TestTreeNode child = new TestTreeNode();

        parent.addChild(child);

        assertEquals(child.getParent().get(), parent);
        assertFalse(parent.getParent().isPresent());
    }

    @Test
    public void parentIsSetOnAddingMultiple() throws Exception {
        TestTreeNode parent = new TestTreeNode();
        TestTreeNode childA = new TestTreeNode();
        TestTreeNode childB = new TestTreeNode();

        parent.addChildren(Arrays.asList(childA, childB));

        assertEquals(childA.getParent().get(), parent);
        assertEquals(childB.getParent().get(), parent);
        assertFalse(parent.getParent().isPresent());
    }

    @Test
    public void parentIsClearedOnRemoving() throws Exception {
        TestTreeNode parent = new TestTreeNode();
        TestTreeNode child = new TestTreeNode();

        parent.addChild(child);
        parent.removeChild(child);

        assertFalse(child.getParent().isPresent());
    }

    @Test
    public void parentIsClearedOnRemovingMultiple() throws Exception {
        TestTreeNode parent = new TestTreeNode();
        TestTreeNode childA = new TestTreeNode();
        TestTreeNode childB = new TestTreeNode();
        TestTreeNode childC = new TestTreeNode();

        parent.addChildren(Arrays.asList(childA, childB, childC));
        parent.removeChildren(Arrays.asList(childA, childB));

        assertFalse(childA.getParent().isPresent());
        assertFalse(childB.getParent().isPresent());
        assertEquals(childC.getParent().get(), parent);
    }

    @Test
    public void parentIsClearedOnClearing() throws Exception {
        TestTreeNode parent = new TestTreeNode();
        TestTreeNode childA = new TestTreeNode();
        TestTreeNode childB = new TestTreeNode();

        parent.addChildren(Arrays.asList(childA, childB));
        parent.clearChildren();

        assertFalse(childA.getParent().isPresent());
        assertFalse(childB.getParent().isPresent());
    }

    @Test
    public void changeParentReflectsToParentsChildren() throws Exception {
        TestTreeNode oldParent = new TestTreeNode();
        TestTreeNode childA = new TestTreeNode();
        TestTreeNode childB = new TestTreeNode();
        oldParent.addChildren(Arrays.asList(childA, childB));

        TestTreeNode newParent = new TestTreeNode();
        childA.setParent(newParent);

        assertEquals(childA.getParent().get(), newParent);
        assertThat(oldParent.getChildren(), contains(childB));
        assertThat(newParent.getChildren(), contains(childA));
    }

    @Test
    public void setParentNullReflectsToParentsChildren() throws Exception {
        TestTreeNode oldParent = new TestTreeNode();
        TestTreeNode childA = new TestTreeNode();
        TestTreeNode childB = new TestTreeNode();
        oldParent.addChildren(Arrays.asList(childA, childB));

        childA.setParent(null);

        assertFalse(childA.getParent().isPresent());
        assertThat(oldParent.getChildren(), contains(childB));
    }

    @Test
    public void addChildIsObservable() throws Exception {
        final Set<TestTreeNode> observedChildren = new HashSet<>();
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                observedChildren.addAll(children);
            }
        });

        TestTreeNode addedChild = new TestTreeNode();
        nodeUnderTest.addChild(addedChild);

        assertThat(observedChildren, containsInAnyOrder(addedChild));
    }

    @Test
    public void addChildrenIsObservable() throws Exception {
        final Set<TestTreeNode> observedChildren = new HashSet<>();
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                observedChildren.addAll(children);
            }
        });

        TestTreeNode[] addedChildren = {new TestTreeNode(), new TestTreeNode()};
        nodeUnderTest.addChildren(Arrays.asList(addedChildren));

        assertThat(observedChildren, containsInAnyOrder(addedChildren));
    }

    @Test
    public void removeChildIsObservable() throws Exception {
        final Set<TestTreeNode> observedChildren = new HashSet<>();
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                observedChildren.addAll(children);
            }
        });

        TestTreeNode removedChild = new TestTreeNode();
        TestTreeNode[] addedChildren = {removedChild, new TestTreeNode()};
        nodeUnderTest.addChildren(Arrays.asList(addedChildren));
        nodeUnderTest.removeChild(removedChild);

        assertThat(observedChildren, containsInAnyOrder(removedChild));
    }

    @Test
    public void removeChildrenIsObservable() throws Exception {

        final Set<TestTreeNode> observedChildren = new HashSet<>();
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                observedChildren.addAll(children);
            }
        });

        TestTreeNode[] removedChildren = {new TestTreeNode(), new TestTreeNode()};
        TestTreeNode[] addedChildren = {new TestTreeNode(), removedChildren[0], new TestTreeNode(), removedChildren[1]};
        nodeUnderTest.addChildren(Arrays.asList(addedChildren));
        nodeUnderTest.removeChildren(Arrays.asList(removedChildren));

        assertThat(observedChildren, containsInAnyOrder(removedChildren));
    }

    @Test
    public void observedAddedChildrenWereNotContainedBefore() throws Exception {
        TestTreeNode addedFirst = new TestTreeNode();
        nodeUnderTest.addChild(addedFirst);

        final Set<TestTreeNode> observedChildren = new HashSet<>();
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                observedChildren.addAll(children);
            }
        });

        TestTreeNode addedSecond = new TestTreeNode();
        nodeUnderTest.addChildren(Arrays.asList(addedFirst, addedSecond)); // add first again

        assertThat(observedChildren, containsInAnyOrder(addedSecond));
        assertThat("Nevertheless, two children are expected.", nodeUnderTest.getChildren(),
                   containsInAnyOrder(addedFirst, addedSecond));
    }

    @Test
    public void observedRemovedChildrenWereContainedBefore() throws Exception {
        final Set<TestTreeNode> observedChildren = new HashSet<>();
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                observedChildren.addAll(children);
            }
        });

        TestTreeNode[] addedChildren = {new TestTreeNode()};
        TestTreeNode[] removedChildren = {addedChildren[0], new TestTreeNode()}; // removing more than added!
        nodeUnderTest.addChildren(Arrays.asList(addedChildren));
        nodeUnderTest.removeChildren(Arrays.asList(removedChildren));

        assertThat(observedChildren, containsInAnyOrder(addedChildren[0]));
    }

    @Test
    public void batchAddTriggersObserverOnlyOnce() throws Exception {

        int[] triggered = {0};

        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                triggered[0]++;
            }
        });

        nodeUnderTest.addChildren(Arrays.asList(new TestTreeNode(), new TestTreeNode()));

        assertThat(triggered[0], is(1));
    }

    @Test
    public void batchRemoveTriggersObserverOnlyOnce() throws Exception {

        int[] triggered = {0};

        nodeUnderTest.addChildren(Arrays.asList(new TestTreeNode(), new TestTreeNode()));
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                triggered[0]++;
            }
        });

        nodeUnderTest.removeChildren(nodeUnderTest.getChildren());

        assertThat(triggered[0], is(1));
    }

    @Test
    public void sourceOfEventIsProvidedForAddingSingle() throws Exception {
        TestTreeNode[] observedEventSource = {null};
        TestTreeNode[] observedChangedNode = {null};
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                observedEventSource[0] = eventSource;
                observedChangedNode[0] = changedNode;
            }
        });

        nodeUnderTest.addChild(new TestTreeNode());

        assertThat(observedEventSource[0], is(nodeUnderTest));
        assertThat(observedChangedNode[0], is(nodeUnderTest));
    }

    @Test
    public void sourceOfEventIsProvidedForAddingMultiple() throws Exception {
        TestTreeNode[] observedEventSource = {null};
        TestTreeNode[] observedChangedNode = {null};
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                observedEventSource[0] = eventSource;
                observedChangedNode[0] = changedNode;
            }
        });

        nodeUnderTest.addChildren(Collections.singleton(new TestTreeNode()));

        assertThat(observedEventSource[0], is(nodeUnderTest));
        assertThat(observedChangedNode[0], is(nodeUnderTest));
    }

    @Test
    public void sourceOfEventIsProvidedForRemovingSingle() throws Exception {
        TestTreeNode child = new TestTreeNode();
        nodeUnderTest.addChild(child);
        TestTreeNode[] observedEventSource = {null};
        TestTreeNode[] observedChangedNode = {null};
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                observedEventSource[0] = eventSource;
                observedChangedNode[0] = changedNode;
            }
        });

        nodeUnderTest.removeChild(child);

        assertThat(observedEventSource[0], is(nodeUnderTest));
        assertThat(observedChangedNode[0], is(nodeUnderTest));
    }

    @Test
    public void sourceOfEventIsProvidedForRemovingMultiple() throws Exception {
        TestTreeNode child = new TestTreeNode();
        nodeUnderTest.addChild(child);
        TestTreeNode[] observedEventSource = {null};
        TestTreeNode[] observedChangedNode = {null};
        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                observedEventSource[0] = eventSource;
                observedChangedNode[0] = changedNode;
            }
        });

        nodeUnderTest.removeChildren(Collections.singleton(child));

        assertThat(observedEventSource[0], is(nodeUnderTest));
        assertThat(observedChangedNode[0], is(nodeUnderTest));
    }

    @Test
    public void transitiveChangesInSubtreeAreObservableForSingleAddedChild() throws Exception {
        Set<TestTreeNode> addedChildren = new HashSet<>();
        Set<TestTreeNode> removedChildren = new HashSet<>();

        TestTreeNode observedChild = new TestTreeNode();
        nodeUnderTest.addChild(observedChild); // single child

        TestTreeNode[] observedEventSource = {null, null};
        TestTreeNode[] observedChangedNode = {null, null};

        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                addedChildren.addAll(children);

                observedEventSource[0] = eventSource;
                observedChangedNode[0] = changedNode;
            }

            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                removedChildren.addAll(children);

                observedEventSource[1] = eventSource;
                observedChangedNode[1] = changedNode;
            }
        });

        TestTreeNode singleChild = new TestTreeNode();
        observedChild.addChild(singleChild);
        List<TestTreeNode> multiChild = Collections.singletonList(new TestTreeNode());
        observedChild.addChildren(multiChild);
        observedChild.removeChild(singleChild);
        observedChild.removeChildren(multiChild);

        assertThat(addedChildren, containsInAnyOrder(singleChild, multiChild.get(0)));
        assertThat(removedChildren, containsInAnyOrder(singleChild, multiChild.get(0)));

        assertThat(observedEventSource[0], is(nodeUnderTest));
        assertThat(observedChangedNode[0], is(observedChild));
        assertThat(observedEventSource[1], is(nodeUnderTest));
        assertThat(observedChangedNode[1], is(observedChild));

    }

    @Test
    public void transitiveChangesInSubtreeAreObservableForMultiAddedChild() throws Exception {
        Set<TestTreeNode> addedChildren = new HashSet<>();
        Set<TestTreeNode> removedChildren = new HashSet<>();

        TestTreeNode observedChild = new TestTreeNode();
        nodeUnderTest.addChildren(Collections.singleton(observedChild));  // add collection

        TestTreeNode[] observedEventSource = {null, null};
        TestTreeNode[] observedChangedNode = {null, null};

        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                addedChildren.addAll(children);

                observedEventSource[0] = eventSource;
                observedChangedNode[0] = changedNode;
            }

            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                removedChildren.addAll(children);

                observedEventSource[1] = eventSource;
                observedChangedNode[1] = changedNode;
            }
        });

        TestTreeNode singleChild = new TestTreeNode();
        observedChild.addChild(singleChild);
        List<TestTreeNode> multiChild = Collections.singletonList(new TestTreeNode());
        observedChild.addChildren(multiChild);
        observedChild.removeChild(singleChild);
        observedChild.removeChildren(multiChild);

        assertThat(addedChildren, containsInAnyOrder(singleChild, multiChild.get(0)));
        assertThat(removedChildren, containsInAnyOrder(singleChild, multiChild.get(0)));

        assertThat(observedEventSource[0], is(nodeUnderTest));
        assertThat(observedChangedNode[0], is(observedChild));
        assertThat(observedEventSource[1], is(nodeUnderTest));
        assertThat(observedChangedNode[1], is(observedChild));
    }

    @Test
    public void removingSingleMutesTransitiveChanges() throws Exception {
        Set<TestTreeNode> addedChildren = new HashSet<>();
        Set<TestTreeNode> removedChildren = new HashSet<>();

        TestTreeNode observedChild = new TestTreeNode();
        nodeUnderTest.addChild(observedChild);

        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                addedChildren.addAll(children);
            }

            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                removedChildren.addAll(children);
            }
        });

        nodeUnderTest.removeChild(observedChild); // single
        addedChildren.clear(); // cleanup for direct modifications
        removedChildren.clear();

        TestTreeNode singleChild = new TestTreeNode();
        observedChild.addChild(singleChild);
        List<TestTreeNode> multiChild = Collections.singletonList(new TestTreeNode());
        observedChild.addChildren(multiChild);
        observedChild.removeChild(singleChild);
        observedChild.removeChildren(multiChild);

        assertThat(addedChildren, is(empty()));
        assertThat(removedChildren, is(empty()));
    }

    @Test
    public void removingMultipleMutesTransitiveChanges() throws Exception {
        Set<TestTreeNode> addedChildren = new HashSet<>();
        Set<TestTreeNode> removedChildren = new HashSet<>();

        TestTreeNode observedChild = new TestTreeNode();
        nodeUnderTest.addChild(observedChild);

        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                addedChildren.addAll(children);
            }

            @Override
            public void onChildrenRemoved(TestTreeNode eventSource,
                                          TestTreeNode changedNode, Set<TestTreeNode> children) {
                removedChildren.addAll(children);
            }
        });

        nodeUnderTest.removeChildren(Collections.singleton(observedChild)); // multiple
        addedChildren.clear(); // cleanup for direct modifications
        removedChildren.clear();

        TestTreeNode singleChild = new TestTreeNode();
        observedChild.addChild(singleChild);
        List<TestTreeNode> multiChild = Collections.singletonList(new TestTreeNode());
        observedChild.addChildren(multiChild);
        observedChild.removeChild(singleChild);
        observedChild.removeChildren(multiChild);

        assertThat(addedChildren, is(empty()));
        assertThat(removedChildren, is(empty()));
    }

    @Test
    public void changeParentIsObservable() throws Exception {

        TestTreeNode[] observedSource = {null};
        Optional<TestTreeNode>[] observedParent = new Optional[]{null};

        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onParentChanged(TestTreeNode source,
                                        Optional<TestTreeNode> newParent) {
                observedSource[0] = source;
                observedParent[0] = newParent;
            }
        });

        TestTreeNode newParent = new TestTreeNode();
        nodeUnderTest.setParent(newParent);
        assertThat(observedSource[0], is(nodeUnderTest));
        assertThat(observedParent[0].get(), is(newParent));

        nodeUnderTest.setParent(null);
        assertThat(observedSource[0], is(nodeUnderTest));
        assertFalse(observedParent[0].isPresent());
    }

    @Test
    public void changeParentIsNotObservedTransitive() throws Exception {
        TestTreeNode singleChild = new TestTreeNode();
        TestTreeNode multiChild = new TestTreeNode();
        nodeUnderTest.addChild(singleChild);
        nodeUnderTest.addChildren(Collections.singleton(multiChild));

        nodeUnderTest.addObserver(new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onParentChanged(TestTreeNode source,
                                        Optional<TestTreeNode> newParent) {
                throw new IllegalStateException("Should not be observed!");
            }
        });

        singleChild.setParent(null);
        multiChild.setParent(null);
    }

    @Test
    public void multipleObserversCanCoexist() throws Exception {
        Set<TestTreeNode> firstObserverAdded = new HashSet<>();
        Set<TestTreeNode> secondObserverAdded = new HashSet<>();

        TreeNode.HierarchyObserver<TestTreeNode> firstReturned = nodeUnderTest.addObserver(
                new TreeNode.HierarchyObserver<TestTreeNode>() {
                    @Override
                    public void onChildrenAdded(TestTreeNode eventSource,
                                                TestTreeNode changedNode,
                                                Set<TestTreeNode> children) {
                        firstObserverAdded.addAll(children);
                    }
                });
        TreeNode.HierarchyObserver<TestTreeNode> secondReturned = nodeUnderTest.addObserver(
                new TreeNode.HierarchyObserver<TestTreeNode>() {
                    @Override
                    public void onChildrenAdded(TestTreeNode eventSource,
                                                TestTreeNode changedNode,
                                                Set<TestTreeNode> children) {
                        secondObserverAdded.addAll(children);
                    }
                });

        TestTreeNode node = new TestTreeNode();
        nodeUnderTest.addChild(node);

        assertThat(firstObserverAdded, contains(node));
        assertThat(secondObserverAdded, contains(node));
    }

    @Test
    public void observersAreRemoveable() throws Exception {

        List<TestTreeNode> reportedAdds = new ArrayList<>();

        TreeNode.HierarchyObserver<TestTreeNode> observer = new TreeNode.HierarchyObserver<TestTreeNode>() {
            @Override
            public void onChildrenAdded(TestTreeNode eventSource,
                                        TestTreeNode changedNode,
                                        Set<TestTreeNode> children) {
                reportedAdds.addAll(children);
            }
        };
        nodeUnderTest.addObserver(observer);
        nodeUnderTest.addChild(new TestTreeNode());
        assertThat(reportedAdds.size(), is(1));

        nodeUnderTest.removeObserver(observer);
        nodeUnderTest.addChild(new TestTreeNode());
        assertThat(reportedAdds.size(), is(1)); // should not have changed
    }

    @Test(expected = NullPointerException.class)
    public void observersMayNotBeNull() throws Exception {
        nodeUnderTest.addObserver(null);
    }

    @Test
    public void addingObserverReturnsReferenceToInstance() throws Exception {
        TreeNode.HierarchyObserver<TestTreeNode> firstObserver = new TreeNode.HierarchyObserver<TestTreeNode>() {
        };
        TreeNode.HierarchyObserver<TestTreeNode> secondObserver = new TreeNode.HierarchyObserver<TestTreeNode>() {
        };

        TreeNode.HierarchyObserver<TestTreeNode> firstReturned = nodeUnderTest.addObserver(firstObserver);
        TreeNode.HierarchyObserver<TestTreeNode> secondReturned = nodeUnderTest.addObserver(secondObserver);

        assertThat(firstReturned, is(firstObserver));
        assertThat(secondReturned, is(secondObserver));
    }


    private static class TestTreeNode extends TreeNode<TestTreeNode> {
        // no implementation
    }

    private static class TestTreeNodeExtended extends TestTreeNode {
        // no implementation
    }

}