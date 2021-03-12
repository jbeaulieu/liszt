package com.jbproductions.liszt;

/**
 * Interface to handle callback from ListAdapter to MainActivity when a task is checked/unchecked.
 */
public interface TaskClickInterface {
    void onCheckCallback(Task task);
}
