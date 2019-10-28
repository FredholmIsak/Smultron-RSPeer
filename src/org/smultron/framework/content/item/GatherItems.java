package org.smultron.framework.content.item;

import org.rspeer.runetek.api.component.tab.Inventory;
import org.smultron.framework.content.item.gathering.GatheringTasks;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranch;
import org.smultron.framework.thegreatforest.LeafNode;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 *  Does not handle noted items
 *  Assumes unlimited inventory space
 */
public class GatherItems extends TreeNode
{
    private List<String> items;
    private Map<String, TreeNode> tasks;
    private boolean grandExchange;
    private Iterator<TreeNode> iterator;

    /**
     *
     */
    public GatherItems(List<String> items, TaskListener listener, boolean grandExchange) {
        this.items = items;
        this.grandExchange = grandExchange;
        tasks = new HashMap<>();

        // Set up all tasks
        for (String item : items) {
            Task gatherItem = new GatherItem(listener, item, grandExchange);
            tasks.put(item, new LeafNode(gatherItem));
        }
    }

    /**
     * Finds the first item that isnt in our inventory.
     * Return the task for that item
     * If no missing item is found a empty {@link LeafNode} is returned.
     */
    @Override public Iterator<TreeNode> iterator() {
        if(iterator != null)
            return iterator;

        iterator = new Iterator<TreeNode>()
        {
            @Override public boolean hasNext() {
                Predicate<String> notInInventory = item -> !Inventory.contains(item);
                int itemsMissing = (int) items.stream().filter(notInInventory).count();
                return itemsMissing != 0;
            }

            @Override public TreeNode next() {
                for (String item : items) {
                    if (!Inventory.contains(item)) {
                        return tasks.get(item);
                    }
                }
                return new LeafNode();
            }
        };
        return iterator;
    }


}
