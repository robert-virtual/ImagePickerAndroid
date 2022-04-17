package com.example.imagepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import kotlinx.android.synthetic.main.activity_main.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI


class MainActivity : AppCompatActivity() {
    private val mainScope = MainScope()
    lateinit var image : Uri
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val value = it.data?.data
            println("Data:")
            println(value)
            if (value != null){
                image = value
            }
            img_1.setImageURI(value)
            btn_send.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        btn_openGallery.setOnClickListener{
            getResult.launch(intent)
        }
         btn_send.setOnClickListener {
             mainScope.launch {
                 kotlin.runCatching {
                     sendData()
                 }.onSuccess {

                     println(" envio de datos exitoso")
                 }.onFailure {


                     println("Hubo un error al realizar el envio de datos:${it.localizedMessage}")
                 }
             }

        }
    }
    private suspend fun sendData(){
        val client = HttpClient(CIO)
       client.submitFormWithBinaryData(
           url = "http://192.168.0.13:3000/posts/ktor",
           formData = formData{
               append("name","Imagen  de Kotlin")
               append("images",File(image.path).readBytes())
               Headers.build {
                   append(HttpHeaders.ContentType,"image/jpg")
                   append(HttpHeaders.ContentDisposition,"filename=image.jpg")
                }
           }

       )



    }

}