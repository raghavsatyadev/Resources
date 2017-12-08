package com.support.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class GenRecyclerAdapter
        <ViewHolder extends RecyclerView.ViewHolder, Model>
        extends RecyclerView.Adapter<ViewHolder> {
    private static GenRecyclerAdapter.MyClickListener myClickListener;
    private ArrayList<Model> models;


    public GenRecyclerAdapter(ArrayList<Model> models) {
        this.models = models;
    }

    public static MyClickListener getMyClickListener() {
        return myClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return creatingViewHolder(parent, viewType);
    }

    protected abstract ViewHolder creatingViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindingViewHolder(holder, position);
    }

    protected abstract void bindingViewHolder(ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void addAll(ArrayList<Model> models) {
        int position = getItemCount() - 1;
        this.getModels().addAll(models);
        notifyItemRangeInserted(position, models.size());
    }

    public void addItem(Model model, int index) {
        getModels().add(model);
        notifyItemInserted(index);
    }

    public void addItem(Model model) {
        getModels().add(model);
        notifyItemInserted(getItemCount() - 1);
    }

    private ArrayList<Model> getModels() {
        return models;
    }

    public void deleteAll() {
        getModels().clear();
        notifyDataSetChanged();
    }

    public Model getItem(int index) {
        return getModels().get(index);
    }

    public void deleteItem(int index) {
        if (index >= 0 && index < getItemCount())
            getModels().remove(index);
    }

    public void setOnItemClickListener(GenRecyclerAdapter.MyClickListener myClickListener) {
        GenRecyclerAdapter.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
