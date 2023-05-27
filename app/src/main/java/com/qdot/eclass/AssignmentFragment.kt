package com.qdot.eclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.qdot.eclass.databinding.FragmentAssignmentBinding
import io.appwrite.Client
import io.appwrite.Query
import io.appwrite.extensions.jsonCast
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Teams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssignmentFragment(private var classId: String) : Fragment() {

    private lateinit var binding: FragmentAssignmentBinding
    private lateinit var dialogFragment: AssignmentFileFragment
    private lateinit var databases: Databases


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addAssignmentFab.visibility = View.GONE
        val client = Client(requireContext())
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        val account = Account(client)
        val teams = Teams(client)
        databases = Databases(client)
        dialogFragment = AssignmentFileFragment(client,classId)
        CoroutineScope(Dispatchers.IO).launch {
            val acc = account.get()
            val meList = teams.listMemberships(teamId = classId,
                queries = listOf(Query.equal("userId",acc.id)))
            if (meList.memberships[0].roles.contains("owner")){
                withContext(Dispatchers.Main) {
                    binding.addAssignmentFab.visibility = View.VISIBLE
                }
            }
        }
        binding.addAssignmentFab.setOnClickListener {
            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction
                .add(android.R.id.content, dialogFragment)
                .addToBackStack(null)
                .commit()
        }
        loadAssignments()
    }

    private fun loadAssignments() {
        CoroutineScope(Dispatchers.IO).launch {
            val listAnn = ArrayList<AssignmentModel>()
            databases.listDocuments(
                databaseId = "6460b1828e657cc66ef5",
                collectionId = "6460b19e5ec11aa6725c",
                queries = listOf(Query.equal("team", classId),
                    Query.orderDesc("time"))
            ).apply {
                this.documents.forEach {
                    listAnn.add(it.data.jsonCast(AssignmentModel::class.java))
                }
                withContext(Dispatchers.Main) {
                    binding.assignmentRv.layoutManager = LinearLayoutManager(requireContext())
                    binding.assignmentRv.adapter = AssignmentAdapter(listAnn)
                }
            }
        }
    }

}