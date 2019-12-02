package mcc.group14.apiclientapp.views

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

// TODO: -- @Max no need to pass an activity, see TimePickerFragment; solve the strange error.
class DatePickerFragment (var activity: Activity,
                          var listener: DatePickerDialog.OnDateSetListener):
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val rst = DatePickerDialog(activity, listener, year, month, day)
        rst.datePicker.minDate = c.timeInMillis
        // Create a new instance of DatePickerDialog and return it
        return rst
    }

}