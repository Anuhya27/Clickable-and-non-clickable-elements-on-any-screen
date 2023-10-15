package com.github.simonpham.verification.ui.guide.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.simonpham.verification.R
import kotlinx.android.synthetic.main.fragment_welcome.*

/**
 * Created by Simon Pham on 6/1/18.
 * Email: simonpham.dn@gmail.com
 */

class WelcomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = (activity as? View.OnClickListener)
                ?: (parentFragment as? View.OnClickListener)
        btnGettingStarted.setOnClickListener(listener)
    }
}