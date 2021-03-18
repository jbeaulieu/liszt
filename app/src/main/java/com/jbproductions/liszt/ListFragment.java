package com.jbproductions.liszt;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Host fragment for viewing a list. This fragment provides the main view for the application.
 */
public class ListFragment extends Fragment {

    SelectionTracker<Long> mSelectionTracker;
    TaskClickInterface mTaskClickInterface;
    private EditText newTaskText;
    private ViewModel mViewModel;
    private boolean singleItemSelected;
    private boolean multipleItemsSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem editItem = menu.findItem(R.id.action_edit);
        deleteItem.setVisible(singleItemSelected || multipleItemsSelected);
        editItem.setVisible(singleItemSelected);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                Selection<Long> selectedItems = mSelectionTracker.getSelection();
                for (Long itemId : selectedItems) {
                    mViewModel.deleteTaskById(itemId);
                }
                mSelectionTracker.clearSelection();
                return true;
            }
            case R.id.action_edit: {
                Selection<Long> selectedItems = mSelectionTracker.getSelection();

                TextEditInterface getAlertDialogText = (text, id) -> {
                    Task thisTask;
                    thisTask = mViewModel.getTaskById(id);
                    thisTask.setName(text);
                    mViewModel.update(thisTask);
                };

                for (Long selectedItem : selectedItems) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("New Task Name");

                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    builder.setView(input);

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAlertDialogText.onTextEntered(input.getText().toString(), selectedItem);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                    mSelectionTracker.clearSelection();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        newTaskText = (EditText) view.findViewById(R.id.newTaskText);

        newTaskText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                Task thisTask = new Task(newTaskText.getText().toString(), false);
                mViewModel.insert(thisTask);
                newTaskText.getText().clear();
                Log.d("myTag", "Keyboard Enter Captured: " + newTaskText.getText());
                newTaskText.requestFocus();
                handled = true;
            }
            return handled;
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            newTaskText.requestFocus();
            InputMethodManager imm =
                    (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(newTaskText, InputMethodManager.SHOW_IMPLICIT);
        });

        mTaskClickInterface = task -> {
            mViewModel.update(task);
            Log.d("HERE", "HERE");
        };

        RecyclerView listRecyclerView = view.findViewById(R.id.list_recycler_view);
        final TaskListAdapter adapter = new TaskListAdapter(mTaskClickInterface, new TaskListAdapter.TaskDiff());
        listRecyclerView.setAdapter(adapter);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if (task.getId() == -1) {
                    break;
                } else if (task.getComplete()) {
                    Task divider = new Task("", false);
                    divider.setId(-1);
                    tasks.add(i, divider);
                    break;
                }
            }

            adapter.submitList(tasks);
        });

        SelectionTracker.SelectionPredicate<Long> noDividerSelection = new SelectionTracker.SelectionPredicate<Long>() {
            @Override
            public boolean canSetStateForKey(@NonNull Long key, boolean nextState) {
                return key != -1;
            }

            @Override
            public boolean canSetStateAtPosition(int position, boolean nextState) {
                return true;
            }

            @Override
            public boolean canSelectMultiple() {
                return true;
            }
        };

        mSelectionTracker = new SelectionTracker.Builder<>(
                "selection-id",
                listRecyclerView,
                new ListFragment.TaskKeyProvider(listRecyclerView),
                new ListFragment.TaskDetailsLookup(listRecyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(noDividerSelection).build();

        adapter.setSelectionTracker(mSelectionTracker);

        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                int selectionSize = mSelectionTracker.getSelection().size();
                switch (selectionSize) {
                    case 0:
                        singleItemSelected = false;
                        multipleItemsSelected = false;
                        break;
                    case 1:
                        singleItemSelected = true;
                        multipleItemsSelected = false;
                        break;
                    default:
                        singleItemSelected = false;
                        multipleItemsSelected = true;
                        break;
                }
                requireActivity().invalidateOptionsMenu();
            }
        });
    }

    interface TextEditInterface {
        void onTextEntered(String text, long id);
    }

    private static class TaskKeyProvider extends ItemKeyProvider<Long> {

        private final RecyclerView mRecyclerView;

        public TaskKeyProvider(RecyclerView recyclerView) {
            super(SCOPE_CACHED);
            this.mRecyclerView = recyclerView;
        }

        @Nullable
        @Override
        public Long getKey(int position) {
            return mRecyclerView.getAdapter().getItemId(position);
        }

        @Override
        public int getPosition(@NonNull Long key) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(key);
            return viewHolder.getLayoutPosition();
        }
    }

    private static class TaskDetailsLookup extends ItemDetailsLookup<Long> {

        private final RecyclerView mRecyclerView;

        public TaskDetailsLookup(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent event) {
            View view = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
            if (view != null) {
                final RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
                if (holder instanceof TaskListAdapter.TaskViewHolder) {
                    final TaskListAdapter.TaskViewHolder taskViewHolder = (TaskListAdapter.TaskViewHolder) holder;
                    return new ItemDetailsLookup.ItemDetails<Long>() {
                        @Override
                        public int getPosition() {
                            return holder.getAdapterPosition();
                        }

                        @NonNull
                        @Override
                        public Long getSelectionKey() {
                            return taskViewHolder.getItemId();
                        }
                    };
                } else if (holder instanceof TaskListAdapter.DividerViewHolder) {
                    final TaskListAdapter.DividerViewHolder taskViewHolder = (TaskListAdapter.DividerViewHolder) holder;
                    return new ItemDetailsLookup.ItemDetails<Long>() {
                        @Override
                        public int getPosition() {
                            return holder.getAdapterPosition();
                        }

                        @NonNull
                        @Override
                        public Long getSelectionKey() {
                            return taskViewHolder.getItemId();
                        }
                    };
                }
            }
            return null;
        }
    }

    /*    @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mViewModel = new ViewModelProvider(this).get(ViewModel.class);
            // TODO: Use the ViewModel
        }*/

}