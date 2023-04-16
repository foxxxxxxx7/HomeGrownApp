package com.wit.homegrownapp.ui.becomeProducer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wit.homegrownapp.model.UserModel

class BecomeProducerViewModel : ViewModel() {

    private val _validationStatus = MutableLiveData<ValidationStatus>()
    val validationStatus: LiveData<ValidationStatus> get() = _validationStatus

    fun validateUser(user: UserModel): Boolean {
        if (user.fName.isNullOrBlank()) {
            _validationStatus.value = ValidationStatus.FirstNameEmpty
            return false
        }

        if (user.sName.isNullOrBlank()) {
            _validationStatus.value = ValidationStatus.SurnameEmpty
            return false
        }

        if (user.username.isNullOrBlank()) {
            _validationStatus.value = ValidationStatus.UsernameEmpty
            return false
        }

        if (user.eircode.isNullOrBlank()) {
            _validationStatus.value = ValidationStatus.EircodeEmpty
            return false
        }

        if (user.phoneNumber.isNullOrBlank()) {
            _validationStatus.value = ValidationStatus.PhoneNumberEmpty
            return false
        }

        if (user.businessEmail.isNullOrBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(user.businessEmail).matches()) {
            _validationStatus.value = ValidationStatus.BusinessEmailInvalid
            return false
        }

        return true
    }

    enum class ValidationStatus {
        FirstNameEmpty,
        SurnameEmpty,
        UsernameEmpty,
        EircodeEmpty,
        PhoneNumberEmpty,
        BusinessEmailInvalid
    }
}
