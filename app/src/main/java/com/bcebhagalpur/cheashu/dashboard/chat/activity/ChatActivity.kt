package com.bcebhagalpur.cheashu.dashboard.chat.activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcebhagalpur.cheashu.R
import com.bcebhagalpur.cheashu.dashboard.chat.adapter.ChatsAdapter
import com.bcebhagalpur.cheashu.dashboard.chat.model.Chat
import com.bcebhagalpur.cheashu.dashboard.chat.model.Users
import com.bcebhagalpur.cheashu.dashboard.chat.notifications.*
import com.bcebhagalpur.cheashu.dashboard.chat.service.APIService
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatActivity : AppCompatActivity() {

//    var userIdVisit:String?=""
    var firebaseUser: FirebaseUser?=null
    var chatsAdapter:ChatsAdapter?=null
    var chatList:List<Chat>?=null
    lateinit var messageRecyclerView: RecyclerView
   lateinit var reference: DatabaseReference
    var notify=false
    var apiService: APIService?=null

    private lateinit var messageToolbar: Toolbar
    private lateinit var txtMessageUserName:TextView
    private lateinit var imgMessageAttachImage:ImageView
    private lateinit var etMessageText:EditText
    private lateinit var imgMessageSend:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageToolbar=findViewById(R.id.messageToolbar)
        txtMessageUserName=findViewById(R.id.txtMessageUserName)
        imgMessageAttachImage=findViewById(R.id.imgMessageAttachImage)
        imgMessageSend=findViewById(R.id.imgMessageSend)
        etMessageText=findViewById(R.id.etMessageText)
        setSupportActionBar(messageToolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        apiService=Client.Client.getClint("https://fcm.googleapis.com/")!!.create(APIService::class.java)

//        intent=intent
//        userIdVisit=intent.getStringExtra("visit_id")
        // senderProfile=intent.getStringExtra("sender_pic")
        firebaseUser= FirebaseAuth.getInstance().currentUser

        messageRecyclerView=findViewById(R.id.messageRecyclerView)
        messageRecyclerView.setHasFixedSize(true)
        var linearLayoutManager= LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        messageRecyclerView.layoutManager=linearLayoutManager

        reference= FirebaseDatabase.getInstance().getReference("users").child(firebaseUser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user: Users?=p0.getValue(Users::class.java)
//                txtMessageUserName.text=user!!.getUsername()
//                Picasso.get().load(user.getProfile()).into(imgMessageProfileImage)

                try {
                    retrieveMessage(firebaseUser!!.uid,user!!.getProfile())
                }catch (e:NullPointerException){
                    e.message
                }

            }

        })


        imgMessageSend.setOnClickListener {
            notify=true
            val message=etMessageText.text.toString()
            if(message==""){
                Toast.makeText(this@ChatActivity,"Please Write A message....",Toast.LENGTH_LONG).show()

            }else{
                sendMessageToUser(firebaseUser!!.uid,firebaseUser!!.uid,message)
            }
            etMessageText.setText("")
        }
        imgMessageAttachImage.setOnClickListener {
            notify=true
            val intent=Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"pick image"),438)
        }
        seenMessage(firebaseUser!!.uid)
    }


    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        val reference=FirebaseDatabase.getInstance().reference
        val messageKey=reference.push().key

        val messageHashMap=HashMap<String,Any?>()
        messageHashMap["sender"]=senderId
        messageHashMap["message"]=message
        messageHashMap["receiver"]=receiverId
        messageHashMap["isseen"]=false
        messageHashMap["url"]=""
        messageHashMap["messageId"]=messageKey
        reference.child("Chats").child(messageKey!!).setValue(messageHashMap).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                val chatListReference=FirebaseDatabase.getInstance().reference.child("newChatList").child(firebaseUser!!.uid).child(firebaseUser!!.uid)

                chatListReference.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (!p0.exists()){
                            chatListReference.child("id").setValue(firebaseUser!!.uid)
                        }
                        val chatsListReceiverRef=FirebaseDatabase.getInstance().reference.child("newChatList").child(firebaseUser!!.uid).child(firebaseUser!!.uid)
                        chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                    }

                })




            }

        }
        //implement the push notifications using fcm

        // chatListReference.child("id").setValue(firebaseUser!!.uid)

        val userReference=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        userReference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(p0: DataSnapshot) {
                val user=p0.getValue(Users::class.java)
                if (notify){
                    sendNotification(receiverId,user!!.getUsername(),message)
                }
                notify=false
            }

        })

    }
    private fun sendNotification(receiverId: String?,userName:String?,message:String){
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val query=ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children){
                    val token:Token?=dataSnapshot.getValue(Token::class.java)

                    val data= Data(firebaseUser!!.uid,R.drawable.welcome_walkthrough,"$userName:$message","New Message",firebaseUser!!.uid)
                    val sender= Sender(data!!,token!!.getToken().toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object :Callback<MyResponse>{
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code()==200){
                                    if (response.body()!!.success!=1){
                                        Toast.makeText(this@ChatActivity,"failed,nothing happened",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                        })
                }

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==438 && resultCode== RESULT_OK && data!=null && data!!.data!=null){
            val progressBar= ProgressDialog(this)
            progressBar.setMessage("image is uploading,please wait.... ")
            progressBar.show()

            val fileUri=data.data
            val storageReference= FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref =FirebaseDatabase.getInstance().reference
            val messageId=ref.push().key
            val filePath=storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask=filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{
                    task ->
                if (!task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    val downloadUrl=task.result
                    val url=downloadUrl.toString()

                    val messageHashMap=HashMap<String,Any?>()
                    messageHashMap["sender"]=firebaseUser!!.uid
                    messageHashMap["message"]="sent you an image."
                    messageHashMap["receiver"]=firebaseUser!!.uid
                    messageHashMap["isseen"]=false
                    messageHashMap["url"]=url
                    messageHashMap["messageId"]=messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            progressBar.dismiss()
                            //implement the push notifications using fcm
                            val reference=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
                            reference.addValueEventListener(object :ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {


                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user=p0.getValue(Users::class.java)
                                    if (notify){
                                        sendNotification(firebaseUser!!.uid,user!!.getUsername(),"sent you an image.")
                                    }
                                    notify=false
                                }

                            })

                        }
                    }

                }
            }
        }
    }
    private fun retrieveMessage(senderId: String, receiverId: String?) {
        chatList=ArrayList()
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (chatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children){
                    val chat=snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId)&& chat!!.getSender().equals(receiverId)||chat!!.getReceiver().equals(receiverId)&&chat!!.getSender().equals(senderId)){
                        (chatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter= ChatsAdapter(this@ChatActivity,chatList!!)
                    messageRecyclerView.adapter=chatsAdapter
                }
            }

        })

    }

    var seenListner:ValueEventListener?=null
    private fun seenMessage(userId:String){
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")
        seenListner=reference!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children){
                    val chat =dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid)&& chat!!.getSender().equals(userId)){
                        val hashMap=HashMap<String,Any>()
                        hashMap["isseen"]=true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

        })
    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListner!!)
    }
}