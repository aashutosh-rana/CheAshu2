package com.bcebhagalpur.cheashu.dashboard.helper

import android.app.Dialog
import android.os.Bundle
import com.bcebhagalpur.cheashu.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


open class RoundedBottomSheetDialogFragment: BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

}

