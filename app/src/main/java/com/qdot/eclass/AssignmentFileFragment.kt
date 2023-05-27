package com.qdot.eclass

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.atwa.filepicker.core.FilePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.qdot.eclass.databinding.AssignmentFileLayoutBinding
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.DateFormat
import java.util.Date

class AssignmentFileFragment(private val client: Client, private val teamId : String) :DialogFragment() {
    private lateinit var binding: AssignmentFileLayoutBinding
    private lateinit var filePicker: FilePicker
    private lateinit var inpFile :File
    private lateinit var assignDate:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AssignmentFileLayoutBinding.inflate(layoutInflater)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storage = Storage(client)
        val databases = Databases(client)
        filePicker = FilePicker.getInstance(this)
        binding.pickFileBtn.setOnClickListener {
            filePicker.pickPdf {
                binding.pickFileBtn.setIconResource(R.drawable.baseline_picture_as_pdf_24)
                binding.pickFileBtn.text = "File picked : "+it?.name.toString()
                inpFile=it!!.file!!
            }
        }
        binding.closeBtn.setOnClickListener {
            this.dismiss()
        }
        binding.datePickBtn.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.show(parentFragmentManager,"Show")

            datePicker.addOnPositiveButtonClickListener {
                binding.datePickBtn.text = "Submission date : " + DateFormat.getDateInstance().format(Date(it))
                assignDate= DateFormat.getDateInstance().format(Date(it))
            }
        }

        binding.submitBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                storage.createFile(
                    bucketId = "646cd9560f8329210302",
                    fileId = ID.unique(),
                    file = InputFile.fromFile(inpFile)
                ).apply {
                    databases.createDocument(
                        databaseId = "6460b1828e657cc66ef5",
                        collectionId = "6460b19e5ec11aa6725c",
                        documentId = ID.unique(),
                        data = AssignmentModel(
                            binding.titleText.text.toString(),
                            binding.descText.text.toString(),
                            teamId,
                            System.currentTimeMillis(),
                            this.id,
                            assignDate
                        )
                    ).apply {
                        withContext(Dispatchers.Main){
                            Toast.makeText(binding.root.context,"Assignment submitted",
                                Toast.LENGTH_SHORT).show()
                        }
                        this@AssignmentFileFragment.dismiss()
                    }
                }
            }
        }
    }
}