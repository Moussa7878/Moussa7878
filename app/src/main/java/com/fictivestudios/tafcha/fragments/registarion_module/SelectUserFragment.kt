package com.fictivestudios.tafcha.fragments.registarion_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.fictivestudios.tafcha.R
import com.fictivestudios.tafcha.Utils.*
import com.fictivestudios.tafcha.Utils.Constants.Companion.IS_USER
import com.fictivestudios.tafcha.activities.MainActivity
import com.fictivestudios.tafcha.fragments.registarion_module.PreLoginFragment
import com.fictivestudios.tafcha.models.Login.LoginResult
import com.fictivestudios.tafcha.networkSetup.apiinterface.ApiInterface
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.getJsonRequestBody
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.technado.demoapp.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_select_user.view.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response


class SelectUserFragment(var isSocialLogin:Boolean) : BaseFragment() {

    var email:String?=null
    var a_token:String?=null
    var imgUrl:String?=null
    var displayName:String?=null
    var pNumber:String?=null
    private lateinit var mView: View
    val RC_SIGN_IN: Int = 1
    // private var mFbHelper: FacebookHelper? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun setTitlebar(titlebar: Titlebar) {


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_select_user, container, false)



        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), mGoogleSignInOptions)



        mView.btn_user.setOnClickListener {

            PreferenceUtils.saveBoolean(IS_USER,true)
               if(isSocialLogin == true){
                   googleSignIn()
               }
            else{
                   getRegisterationActivity()?.
                   replaceFragment(SignupTrainerFragment(),SignupTrainerFragment().javaClass.simpleName,true,true)
            }
        }
        mView.btn_trainer.setOnClickListener {

            PreferenceUtils.saveBoolean(IS_USER,false)

            if(isSocialLogin == true){
                googleSignIn()
            }
            else {
                getRegisterationActivity()?.replaceFragment(
                    SignupTrainerFragment(),
                    SignupTrainerFragment().javaClass.simpleName,
                    true,
                    true
                )
            }


        }

        return mView
    }
    fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            //Toast.makeText(requireActivity(),result.toString(),Toast.LENGTH_LONG).show()
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.d("error123456", e.toString())
            }
        } else {
            // mFbHelper!!.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun socialLogin() = getFirebaseToken { fcmToken ->

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(getRegisterationActivity!!, true, "",
                        object : CallHandler<Response<LoginResult>> {
                            override suspend fun sendRequest(apiInterFace: ApiInterface): Response<LoginResult> {

                                return apiInterFace.socialLogin(
                                    JSONObject().apply {
                                        if(PreferenceUtils.getBoolean(IS_USER)){
                                            put("role","user")
                                        }
                                        else{
                                            put("role","trainer")
                                        }
                                        put("access_token","Access Token")
                                        put("provider","google")
                                        put("device_type", "android")
                                        put("device_token", fcmToken)
                                        put("displayName",displayName)
                                        put("email",email)
                                        put("photoURL",imgUrl)
                                        put("phoneNumber",pNumber)
                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<LoginResult>) {
                                if(response.body()?.status==1  && response.body()?.message != "record not found..!"){
                                    PreferenceUtils.saveString("token", response.body()?.bearer_token.toString())
                                    PreferenceData.storeLoginData(requireActivity(), response.body()?.data)
                                    startActivity(Intent(context, MainActivity::class.java))
                                    requireActivity().finish()

                                }
                                else{
                                    Toast.makeText(requireActivity(),response.body()?.message,
                                        Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                            }


                        }
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else{
                Toast.makeText(requireActivity(), R.string.internetconnection, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        email = account?.email.toString()
        a_token = account?.idToken.toString()
        displayName = account?.displayName.toString()
        pNumber = ""
        imgUrl = if (account?.photoUrl == null || account.photoUrl.toString().isEmpty()) { //set default image
            R.drawable.user_blank_profile.toString()
        } else {
            account.photoUrl.toString() //photo_url is String
        }
        socialLogin()

    }
}