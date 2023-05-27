package com.qdot.eclass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.qdot.eclass.databinding.ActivityFileViewerBinding
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FileViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFileViewerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fileId = intent.getStringExtra(getString(R.string.fileid))
        val submissionDate = intent.getStringExtra(getString(R.string.subDate))
        binding.topAppBar.subtitle= "Submission date : $submissionDate"
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        val storage = Storage(client)
        CoroutineScope(Dispatchers.IO).launch {
            storage.getFileDownload(
                bucketId = "646cd9560f8329210302",
                fileId = fileId.toString()
            ).apply {
                withContext(Dispatchers.Main){
                    binding.pdfView.fromBytes(this@apply)
                        .autoSpacing(true)
                        .enableDoubletap(true)
                        .nightMode(false)
                        .enableSwipe(true)
                        .defaultPage(0)
                        .enableAntialiasing(true)
                        .scrollHandle(DefaultScrollHandle(this@FileViewerActivity))
                        .load()
                }
            }
        }
    }
}