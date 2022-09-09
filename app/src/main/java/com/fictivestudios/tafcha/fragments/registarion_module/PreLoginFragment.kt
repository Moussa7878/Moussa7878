package com.fictivestudios.tafcha.fragments.registarion_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.fictivestudios.tafcha.R
import com.fictivestudios.tafcha.Utils.*
import com.fictivestudios.tafcha.activities.MainActivity
import com.fictivestudios.tafcha.dialogFragments.*
import com.fictivestudios.tafcha.fragments.Challenge_event_Leader.EventsPageFragment
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
import kotlinx.android.synthetic.main.fragment_pre_login.view.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response


class PreLoginFragment : BaseFragment() {

    private lateinit var mView: View
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

       mView = inflater.inflate(R.layout.fragment_pre_login, container, false)


        mView.linearLayoutCompatEmail.setOnClickListener {

            getRegisterationActivity?.
            AddFragment(
                DialogueAgreement(),
                DialogueAgreement().javaClass.simpleName,
                true,false)


        }
        mView.linearLayoutCompatGoogle.setOnClickListener {
//            if(isSignedIn()){
//                startActivity(Intent(context, MainActivity::class.java))
//                requireActivity().finish()
//            }
//            else{
//                getRegisterationActivity()?.
//                replaceFragment(SelectUserFragment(true),
//                    SelectUserFragment(true).javaClass.simpleName,true,true)
//            }

            getRegisterationActivity?.AddFragment(
                RejectDialog(getString(R.string.service),
                    object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            getRegisterationActivity?.
                            onBackPressed()
                        }
                    }

                ),
                RejectDialog.javaClass.simpleName,
                true, false)

        }
        mView.linearLayoutCompatFb.setOnClickListener {
            getRegisterationActivity?.AddFragment(
                RejectDialog(getString(R.string.service),
                    object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            getRegisterationActivity?.
                            onBackPressed()
                        }
                    }

                ),
                RejectDialog.javaClass.simpleName,
                true, false)
        }
        mView.btn_back.setOnClickListener {
            getRegisterationActivity?.
            onBackPressed()
        }

        return mView
    }



    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(requireActivity()) != null
    }






    companion object {

    }



}