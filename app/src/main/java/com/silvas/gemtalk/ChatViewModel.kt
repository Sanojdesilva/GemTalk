package com.silvas.gemtalk

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MassageModel>()   // using mutableStateList is neccassry because to ensure the UI updates whenever the list changes, wrap messageList in a MutableState.
    }

    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = Constant.apiKey

    )

    fun senMessage(question : String){
        try {
            viewModelScope.launch {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role){
                            text(it.message)
                        }
                    }.toList()
                )

                messageList.add(MassageModel(question,"User"))  // list the User side massages

                messageList.add(MassageModel("Typing...","model"))
                Log.i("Chatwith Gemini Ques", question)

                val responce = chat.sendMessage(question)
                messageList.removeAt(messageList.lastIndex)
                messageList.add((MassageModel(responce.text.toString(),"model"))) // list model responces

                Log.i("Chatwith Gemini Res", responce.text.toString())

            }

        }catch (e : Exception){
            messageList.removeAt(messageList.lastIndex)
            messageList.add(MassageModel("Error : " + e.message.toString(),"model"))
        }
    }
}