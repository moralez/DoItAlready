package brg.com.doitalready;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.List;

import brg.com.doitalready.model.Chore;

/**
 * Created by jmo on 12/20/2014.
 */
public class ChoreRecyclerViewAdapter extends RecyclerView.Adapter<ChoreRecyclerViewAdapter.ViewHolder> {

    private List<Chore> mChoreSet;

    public ChoreRecyclerViewAdapter(List<Chore> chores) {
        mChoreSet = chores;
    }

    @Override
    public ChoreRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chore_card_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.setAdapter(this);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chore choreAtPosition = mChoreSet.get(position);
        holder.mChoreName.setText(choreAtPosition.getName());
        holder.setChoreId(choreAtPosition.getId());
        holder.flipToFront();
    }

    @Override
    public int getItemCount() {
        return mChoreSet.size();
    }

    public void addItem(Chore chore) {
        mChoreSet.add(chore);
        notifyItemInserted(getItemCount()-1);
    }

    public void removeItem(int position) {
        mChoreSet.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position, String choreName) {
        Chore choreAtPosition = mChoreSet.get(position);
        choreAtPosition.setName(choreName);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout mListItem;
        public TextView       mChoreName;
        public ImageView      mChoreCategoryIcon;

        private long         mChoreId;
        private CardView     mCardFront;
        private CardView     mCardBack;
        private LinearLayout mDeleteBtn;
        private LinearLayout mEditBtn;
        private ChoreRecyclerViewAdapter mAdapter;

        public ViewHolder(RelativeLayout choreListItemLayout) {
            super(choreListItemLayout);
            mListItem  = choreListItemLayout;
            mChoreName = (TextView)choreListItemLayout.findViewById(R.id.chore_name_card);
            mCardFront = (CardView)choreListItemLayout.findViewById(R.id.card_view_front);
            mCardBack  = (CardView)choreListItemLayout.findViewById(R.id.card_view_back);
            mDeleteBtn = (LinearLayout)mCardBack.findViewById(R.id.delete_btn);
            mEditBtn   = (LinearLayout)mCardBack.findViewById(R.id.edit_btn);

            choreListItemLayout.setOnClickListener(this);
            mDeleteBtn.setOnClickListener(this);
            mEditBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chore_list_item:
                    onListItemClick();
                    break;
                case R.id.delete_btn:
                    onDeleteBtnClick(v.getContext());
                    break;
                case R.id.edit_btn:
                    onEditBtnClick(v.getContext());
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
            ChoresDataSource choresDataSource = ChoresDataSource.getInstance(context);
            choresDataSource.deleteChore(mChoreId);
            onListItemClick();
            if (mAdapter != null) {
                mAdapter.removeItem(getPosition());
            }
        }

        private void onEditBtnClick(final Context context) {
            Log.i("JMO", "Edit Button Pressed");
            final Dialog choreEntryDialog = new Dialog(context);
            choreEntryDialog.setContentView(R.layout.dialog_new_chore);
            choreEntryDialog.setTitle(context.getString(R.string.edit_chore_dialog_title));
            choreEntryDialog.setCancelable(true);
            choreEntryDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            });
            choreEntryDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    choreEntryDialog.dismiss();
                }
            });
            choreEntryDialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) choreEntryDialog.findViewById(R.id.chore);
                    if (editText != null) {
                        final String choreName = editText.getText().toString();
                        if (choreName.isEmpty()) {
                            Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.empty_chore_description_error),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            ChoresDataSource.getInstance(context).editChore(mChoreId, choreName);
                            onListItemClick();

                            if (mAdapter != null) {
                                mAdapter.editItem(getPosition(), choreName);
                            }

                            choreEntryDialog.dismiss();
                        }
                    }
                }
            });
            choreEntryDialog.show();

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        public void setChoreId(long choreId) {
            mChoreId = choreId;
        }

        public void setAdapter(ChoreRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }
    }
}
