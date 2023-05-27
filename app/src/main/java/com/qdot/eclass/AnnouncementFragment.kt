package com.qdot.eclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.qdot.eclass.databinding.FragmentAnnouncementBinding
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.extensions.jsonCast
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Teams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnnouncementFragment(private var classId: String) : Fragment() {

    private lateinit var binding: FragmentAnnouncementBinding
    private lateinit var databases: Databases

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnnouncementBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addAnnouncementFab.visibility = View.GONE
        val client = Client(requireContext())
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        val account = Account(client)
        val teams = Teams(client)
        databases = Databases(client)
        CoroutineScope(Dispatchers.IO).launch {
            val acc = account.get()
            val meList = teams.listMemberships(teamId = classId,
                queries = listOf(Query.equal("userId",acc.id)))
            if (meList.memberships[0].roles.contains("owner")){
                withContext(Dispatchers.Main) {
                    binding.addAnnouncementFab.visibility = View.VISIBLE
                }
            }
            loadAnnouncement()
        }

        binding.addAnnouncementFab.setOnClickListener {
            InputSheet().show(requireContext()) {
                title("Create an announcement")
                with(InputEditText{
                    required()
                    label("Title")
                })
                with(InputEditText{
                    required()
                    label("Description")
                })
                onPositive {result ->
                    CoroutineScope(Dispatchers.IO).launch{
                        databases.createDocument(
                            databaseId = "6460b1828e657cc66ef5",
                            collectionId = "6460b19236c77b567f63",
                            documentId = ID.unique(),
                            data = AnnouncementModel(result.getString("0").toString(),
                                result.getString("1").toString(),classId,
                            System.currentTimeMillis())
                        ).apply {
                            withContext(Dispatchers.Main){
                                Toast.makeText(binding.root.context,"Announcement created",Toast.LENGTH_SHORT).show()
                            }
                            loadAnnouncement()
                        }
                    }
                }
            }
        }
    }

    private fun loadAnnouncement() {
        CoroutineScope(Dispatchers.IO).launch {
            val listAnn = ArrayList<AnnouncementModel>()
            databases.listDocuments(
                databaseId = "6460b1828e657cc66ef5",
                collectionId = "6460b19236c77b567f63",
                queries = listOf(Query.equal("team", classId),
                Query.orderDesc("time"))
            ).apply {
                this.documents.forEach {
                    listAnn.add(it.data.jsonCast(AnnouncementModel::class.java))
                }
                withContext(Dispatchers.Main) {
                    binding.announcementRv.layoutManager = LinearLayoutManager(requireContext())
                    binding.announcementRv.adapter = AnnouncementAdapter(listAnn)
                }
            }
        }
    }

}