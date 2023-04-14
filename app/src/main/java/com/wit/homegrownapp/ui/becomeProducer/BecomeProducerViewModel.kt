package com.wit.homegrownapp.ui.becomeProducer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wit.homegrownapp.model.UserModel

class BecomeProducerViewModel : ViewModel() {

    private val _validationStatus = MutableLiveData<ValidationStatus>()
    val validationStatus: LiveData<ValidationStatus> get() = _validationStatus

    fun validateUser(user: UserModel): Boolean {
        if (user.fName.isNullOrBlank() || user.sName.isNullOrBlank() || user.username.isNullOrBlank() || user.eircode.isNullOrBlank() || user.phoneNumber.toString().isNullOrBlank() || user.businessEmail.isNullOrBlank()) {
            _validationStatus.value = ValidationStatus.InvalidInput
            return false
        }
        return true
    }

    enum class ValidationStatus {
        InvalidInput
    }
}
