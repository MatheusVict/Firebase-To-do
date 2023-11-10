package com.pet.foundation.taskfirebase.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pet.foundation.taskfirebase.R
import com.pet.foundation.taskfirebase.databinding.FragmentHomeBinding
import com.pet.foundation.taskfirebase.utils.ToDoAdapter
import com.pet.foundation.taskfirebase.utils.ToDoData

class HomeFragment : Fragment(), AddTaskFragment.DialogNextBtnClickListener,
    ToDoAdapter.TodoAdapterClicksInterface {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private var popUpFragment: AddTaskFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var list: MutableList<ToDoData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentHomeBinding.bind(inflater.inflate(R.layout.fragment_home, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvent()
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask = taskSnapshot.key?.let {
                        ToDoData(it.toString(), taskSnapshot.value.toString())
                    }
                    if (todoTask != null) {
                        list.add(todoTask)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun registerEvent() {
        binding.addBtn.setOnClickListener {
            if (popUpFragment != null)
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = AddTaskFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                AddTaskFragment::class.java.simpleName
            )
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks")
            .child(auth.currentUser?.uid.toString())
        initRecyclerView(requireContext())
    }

    private fun initRecyclerView(context: Context) {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        list = mutableListOf()
        adapter = ToDoAdapter(list)

        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        databaseRef.push().setValue(todo).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show()
                todoEt.text?.clear()
                popUpFragment!!.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error: ${it.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databaseRef.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Task updated", Toast.LENGTH_SHORT).show()
                todoEt.text?.clear()
                popUpFragment!!.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error: ${it.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            todoEt.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onDeleteClick(taskId: String) {
        databaseRef.child(taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error: ${it.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onEditClick(task: ToDoData) {
        if (popUpFragment != null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()

        popUpFragment = AddTaskFragment.newInstance(task.taskId, task.task)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(
            childFragmentManager,
            AddTaskFragment::class.java.simpleName
        )
    }
}