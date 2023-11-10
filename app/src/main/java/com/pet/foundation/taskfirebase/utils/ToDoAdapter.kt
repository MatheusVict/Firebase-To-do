package com.pet.foundation.taskfirebase.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pet.foundation.taskfirebase.databinding.EachTodoItemBinding

class ToDoAdapter(
    private val list: MutableList<ToDoData>,

    ) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {


    private var listener: TodoAdapterClicksInterface? = null
    fun setListener(listener: TodoAdapterClicksInterface) {
        this.listener = listener
    }
    inner class ToDoViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteClick(this.taskId)
                }
                binding.editTask.setOnClickListener {
                    listener?.onEditClick(this)
                }
            }
        }
    }
    interface TodoAdapterClicksInterface {
        fun onDeleteClick(taskId: String)
        fun onEditClick(task: ToDoData)
    }
}