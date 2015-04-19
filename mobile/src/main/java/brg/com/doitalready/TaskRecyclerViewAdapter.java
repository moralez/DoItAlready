package brg.com.doitalready;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import brg.com.doitalready.TasksDataSource.TaskType;
import brg.com.doitalready.helpers.LoggingHelper;
import brg.com.doitalready.model.Task;

/**
 * Created by jmo on 12/20/2014.
 */
public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private List<Object> mTaskSet;
    private AdvancedHashMap taskSet;

    private enum ListItemType {
        SECTION_HEADER,
        SECTION_TASK
    }

    public TaskRecyclerViewAdapter(List<Task> tasks, TaskType taskType) {
        taskSet = new AdvancedHashMap();
        for (Task task : tasks) {
            taskSet.putItem(task.getCompletedString(), task);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object listItem = null;
        int lowIndex = 0;
        for (Map.Entry<String, List<Object>> entry : taskSet.entrySet()) {
            if (position >= lowIndex && position < lowIndex + entry.getValue().size()) {
                listItem = entry.getValue().get(position-lowIndex);
                break;
            }
            lowIndex += entry.getValue().size();
        }
        return listItem instanceof String ? ListItemType.SECTION_HEADER.ordinal() : ListItemType.SECTION_TASK.ordinal();
    }

    @Override
    public TaskRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LoggingHelper.logFunctionEnter(parent, viewType);

        RelativeLayout v;
        if (viewType == ListItemType.SECTION_HEADER.ordinal()) {
            v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_section_header, parent, false);
        } else {
            v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(v, ListItemType.values()[viewType]);
        vh.setAdapter(this);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LoggingHelper.logFunctionEnter(holder, position);

        Object listItemAtPosition = taskSet.getItemAtIndex(position);
        if (listItemAtPosition instanceof String) {
            holder.mSectionTitle.setText((String)listItemAtPosition);
        } else if (listItemAtPosition instanceof Task) {
            Task task = (Task)listItemAtPosition;
            holder.mTaskName.setText(task.getName());
            holder.setTaskId(task.getId());
            holder.flipToFront();
        }
    }

    @Override
    public int getItemCount() {
        return taskSet.numberOfElements();
    }

    public void addItem(Task task) {
        taskSet.putItem(task.getCompletedString(), task);
        int position = taskSet.indexOfElement(task);
        if (position != -1) {
            notifyItemInserted(position);
        }
    }

    public void removeItem(int position) {
        mTaskSet.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position, String taskName) {
        Task taskAtPosition = (Task) mTaskSet.get(position);
        taskAtPosition.setName(taskName);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    public void itemCompleted(int position) {
        Task task = (Task) taskSet.getItemAtIndex(position);
        taskSet.removeItem(task.getCompletedString(), task);
        task.setCompleted(!task.getCompleted());
        taskSet.putItem(task.getCompletedString(), task);
        int newPosition = taskSet.indexOfElement(task);
        if (position != -1) {
            notifyItemMoved(position, newPosition);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout mListItem;
        public TextView       mTaskName;
        public ImageView      mTaskCategoryIcon;

        private long mTaskId;
        private CardView     mCardFront;
        private CardView     mCardBack;
        private LinearLayout mDeleteBtn;
        private LinearLayout mEditBtn;
        private LinearLayout mDoneBtn;
        private TaskRecyclerViewAdapter mAdapter;

        private TextView    mSectionTitle;

        public ViewHolder(RelativeLayout taskListItemLayout, ListItemType listItemType) {
            super(taskListItemLayout);
            if (listItemType == ListItemType.SECTION_HEADER) {
                mSectionTitle = (TextView) taskListItemLayout.findViewById(R.id.sectionTitle);
            } else if (listItemType == ListItemType.SECTION_TASK) {
                mListItem = taskListItemLayout;
                mTaskName = (TextView) taskListItemLayout.findViewById(R.id.task_name_card);
                mCardFront = (CardView) taskListItemLayout.findViewById(R.id.card_view_front);
                mCardBack = (CardView) taskListItemLayout.findViewById(R.id.card_view_back);
                mDeleteBtn = (LinearLayout) mCardBack.findViewById(R.id.delete_btn);
                mEditBtn = (LinearLayout) mCardBack.findViewById(R.id.edit_btn);
                mDoneBtn = (LinearLayout) mCardBack.findViewById(R.id.done_btn);

                taskListItemLayout.setOnClickListener(this);
                mDeleteBtn.setOnClickListener(this);
                mEditBtn.setOnClickListener(this);
                mDoneBtn.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.task_list_item:
                    onListItemClick();
                    break;
                case R.id.delete_btn:
                    onDeleteBtnClick(v.getContext());
                    break;
                case R.id.edit_btn:
                    onEditBtnClick(v.getContext());
                    break;
                case R.id.done_btn:
                    onDoneBtnClick(v.getContext());
                    break;
                default:
                    break;
            }
        }

        private void onListItemClick() {
            if (mCardFront.getVisibility() == View.VISIBLE) {
                flipToBack();
            } else {
                flipToFront();
            }
        }

        private void flipToFront() {
            if (mCardFront.getVisibility() != View.VISIBLE) {
                flip(mCardBack, mCardFront);
            }
        }

        private void flipToBack() {
            if (mCardBack.getVisibility() != View.VISIBLE) {
                flip(mCardFront, mCardBack);
            }
        }

        private void flip(final View start, final View end) {
            ValueAnimator flip = ObjectAnimator.ofFloat(start, "rotationY", 0f, -90f).setDuration(150);
            flip.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    start.setVisibility(View.INVISIBLE);
                    end.setVisibility(View.VISIBLE);

                    ValueAnimator flip = ObjectAnimator.ofFloat(end, "rotationY", 90f, 0f).setDuration(150);
                    flip.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            flip.start();
        }

        private void onDeleteBtnClick(Context context) {
            TasksDataSource tasksDataSource = TasksDataSource.getInstance(context);
            tasksDataSource.deleteTask(mTaskId);
            onListItemClick();
            if (mAdapter != null) {
                mAdapter.removeItem(getPosition());
            }
        }

        private void onEditBtnClick(final Context context) {
            final Dialog taskEntryDialog = new Dialog(context);
            taskEntryDialog.setContentView(R.layout.dialog_new_task);
            taskEntryDialog.setTitle(context.getString(R.string.edit_task_dialog_title));
            taskEntryDialog.setCancelable(true);
            EditText editText = (EditText) taskEntryDialog.findViewById(R.id.task);
            editText.setText(mTaskName.getText());
            editText.setSelection(editText.getText().length());
            taskEntryDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            });
            taskEntryDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    taskEntryDialog.dismiss();
                }
            });
            taskEntryDialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) taskEntryDialog.findViewById(R.id.task);
                    if (editText != null) {
                        final String taskName = editText.getText().toString();
                        if (taskName.isEmpty()) {
                            Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.empty_task_description_error),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            TasksDataSource.getInstance(context).editTask(mTaskId, taskName);
                            onListItemClick();

                            if (mAdapter != null) {
                                mAdapter.editItem(getPosition(), taskName);
                            }

                            taskEntryDialog.dismiss();
                        }
                    }
                }
            });
            taskEntryDialog.show();

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        private void onDoneBtnClick(Context context) {
            TasksDataSource tasksDataSource = TasksDataSource.getInstance(context);
            tasksDataSource.completeTask(mTaskId, true);
            onListItemClick();
            if (mAdapter != null) {
                mAdapter.itemCompleted(getAdapterPosition());
            }
        }

        public void setTaskId(long taskId) {
            mTaskId = taskId;
        }

        public void setAdapter(TaskRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }
    }
}
