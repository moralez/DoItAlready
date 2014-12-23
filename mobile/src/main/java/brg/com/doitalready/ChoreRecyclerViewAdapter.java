package brg.com.doitalready;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import brg.com.doitalready.model.Chore;

/**
 * Created by jmo on 12/20/2014.
 */
public class ChoreRecyclerViewAdapter extends RecyclerView.Adapter<ChoreRecyclerViewAdapter.ViewHolder> {

    private List<Chore> mChoreSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mListItem;
        public TextView     mChoreName;
        public ImageView    mChoreCategoryIcon;

        public ViewHolder(LinearLayout choreListItemLayout) {
            super(choreListItemLayout);
            mListItem          = choreListItemLayout;
            mChoreName         = (TextView)choreListItemLayout.findViewById(R.id.choreName_card);
//            mChoreCategoryIcon = (ImageView)choreListItemLayout.findViewById(R.id.choreCategoryIcon);
        }
    }

    public ChoreRecyclerViewAdapter(List<Chore> chores) {
        mChoreSet = chores;
    }

    @Override
    public ChoreRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chore_card_item, parent, false);
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
