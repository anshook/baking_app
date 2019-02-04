package com.udacity.ak.bakingapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.udacity.ak.bakingapp.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    public interface StepClickListener {
        void onClick(int clickedItemIndex);
    }

    final private StepClickListener mOnClickListener;
    private List<Step> mStepList;

    void setDataset(List<Step> stepList) {
        mStepList = stepList;
        notifyDataSetChanged();
    }

    public StepAdapter(List<Step> stepList, StepClickListener clickListener) {
        this.mStepList = stepList;
        this.mOnClickListener = clickListener;
    }

    @Override
    public StepAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.steps_list_view, parent, false);
        return new StepAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepAdapter.ViewHolder holder, int position) {
        Step step = mStepList.get(position);

        String stepNum = "";
        if (position>0)
            stepNum = (position) + ". ";

        holder.mStepNameView.setText(stepNum + step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        return mStepList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_step_name) TextView mStepNameView;
        @BindView(R.id.ib_step_play) ImageButton mPlayButton;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            mOnClickListener.onClick(getAdapterPosition());
        }

    }
}
