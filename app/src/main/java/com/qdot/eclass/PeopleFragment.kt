package com.qdot.eclass

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.qdot.eclass.databinding.FragmentPeopleBinding
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Teams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PeopleFragment(private var classId: String) : Fragment() {

    private lateinit var binding: FragmentPeopleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeopleBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toggleButton.visibility = View.GONE
        val client = Client(requireContext())
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        val account = Account(client)
        val teams = Teams(client)
        val teacherList = ArrayList<People>()
        val studentList = ArrayList<People>()
        val teacherIdList = ArrayList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val accountId = account.get().id
            val membersList = teams.listMemberships(teamId = classId)
            membersList.memberships.forEach {
                if (it.roles.contains("owner")){
                    teacherList.add(People(it.userName,it.userEmail,it.userId))
                    teacherIdList.add(it.userId)
                }else{
                    studentList.add(People(it.userName,it.userEmail,it.userId))
                }
            }
            val teacherAdapter = PeopleAdapter(teacherList)
            val studentAdapter = PeopleAdapter(studentList)
            val layManger1 = LinearLayoutManager(requireContext())
            val layManger2 = LinearLayoutManager(requireContext())

            withContext(Dispatchers.Main){
                binding.teacherRv.adapter = teacherAdapter
                binding.teacherRv.layoutManager = layManger1
                binding.studentRv.adapter = studentAdapter
                binding.studentRv.layoutManager = layManger2
                binding.teachersNo.text = "Teachers ("+teacherList.size+")"
                binding.studentsNo.text = "Students ("+studentList.size+")"
                if (teacherIdList.contains(accountId)){
                    binding.toggleButton.visibility = View.VISIBLE
                }
            }
        }

        binding.addStudentBtn.setOnClickListener {
            InputSheet().show(requireContext()) {
                title("Add a student")
                with(InputEditText{
                    required()
                    label("Student's name")
                })
                with(InputEditText{
                    required()
                    label("Student's email")
                })
                onPositive {result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            teams.createMembership(
                                teamId = classId,
                                name = result.getString("0").toString(),
                                email = result.getString("1").toString(),
                                roles = listOf(getString(R.string.student)),
                                url = "https://com.qdot.eclass"
                            ).apply {
                                withContext(Dispatchers.Main){
                                    Toast.makeText(binding.root.context,"Student added",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }catch (e:Exception){
                            withContext(Dispatchers.Main){
                                Toast.makeText(binding.root.context,e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
            }
        }

        binding.addTeacherBtn.setOnClickListener {
            InputSheet().show(requireContext()) {
                title("Add a teacher")
                with(InputEditText{
                    required()
                    label("Teacher's name")
                })
                with(InputEditText{
                    required()
                    label("Teacher's email")
                })
                onPositive {result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            teams.createMembership(
                                teamId = classId,
                                name = result.getString("0").toString(),
                                email = result.getString("1").toString(),
                                roles = listOf("owner"),
                                url = "https://com.qdot.eclass"
                            ).apply {
                                withContext(Dispatchers.Main){
                                    Toast.makeText(binding.root.context,"Student added",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }catch (e:Exception){
                            withContext(Dispatchers.Main){
                                Toast.makeText(binding.root.context,e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}