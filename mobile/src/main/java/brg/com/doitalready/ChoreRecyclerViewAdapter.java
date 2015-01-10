package brg.com.doitalready;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout mListItem;
        public TextView       mChoreName;
        public ImageView      mChoreCategoryIcon;

        private CardView mCardFront;
        private CardView mCardBack;

        public ViewHolder(RelativeLayout choreListItemLayout) {
            super(choreListItemLayout);
            mListItem          = choreListItemLayout;
            mChoreName         = (TextView)choreListItemLayout.findViewById(R.id.choreName_card);
            mCardFront         = (CardView)choreListItemLayout.findViewById(R.id.card_view_front);
            mCardBack          = (CardView)choreListItemLayout.findViewById(R.id.card_view_back);
            choreListItemLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final CardView start;
            final CardView end;
            if (mCardFront.getVisibility() == View.VISIBLE) {
                start = mCardFront;
                end = mCardBack;
            } else {
                start = mCardBack;
                end = mCardFront;
            }

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
    }

    public ChoreRecyclerViewAdapter(List<Chore> chores) {
        mChoreSet = chores;
    }

    @Override
    public ChoreRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chore_card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        // ...
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Chore choreAtPosition = mChoreSet.get(position);
        holder.mChoreName.setText(choreAtPosition.getName());

    }

    @Override
    public int getItemCount() {
        return mChoreSet.size();
    }

    public void addItem(Chore chore) {
        mChoreSet.add(chore);
        notifyItemInserted(getItemCount()-1);
    }

}
