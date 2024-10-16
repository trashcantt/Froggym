package com.sabdev.froggym.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.database.Cursor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sabdev.froggym.data.dao.UserDao
import com.sabdev.froggym.data.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.FileProvider
import java.io.FileOutputStream
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel(private val userDao: UserDao, private val application: Application) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = userDao.getUser()
        }
    }

    suspend fun register(username: String, height: Float, weight: Float, profilePicturePath: String) {
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser != null) {
            _loginState.value = LoginState.Error("Username already exists")
            return
        }

        try {
            val finalProfilePicturePath = copyImageToInternalStorage(profilePicturePath)

            println("Ruta de la imagen de perfil: $finalProfilePicturePath")

            if (!File(finalProfilePicturePath).exists()) {
                println("Error: El archivo de imagen no existe en la ruta especificada")
                _loginState.value = LoginState.Error("Profile picture file not found")
                return
            }

            val newUser = User(username, username, height, weight, finalProfilePicturePath)
            userDao.insertUser(newUser)
            _loginState.value = LoginState.Success(newUser)
            _currentUser.value = newUser
        } catch (e: Exception) {
            _loginState.value = LoginState.Error("Error during registration: ${e.message}")
        }
    }

    private suspend fun copyImageToInternalStorage(sourcePath: String): String {
        return withContext(Dispatchers.IO) {
            val sourceUri = Uri.parse(sourcePath)
            val inputStream = application.contentResolver.openInputStream(sourceUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val fileName = "profile_${UUID.randomUUID()}.jpg"
            val file = File(application.filesDir, fileName)
            
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            file.absolutePath
        }
    }

    suspend fun isFirstLaunch(): Boolean {
        return userDao.getUserCount() == 0
    }

    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginState.Initial
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            try {
                userDao.updateUser(updatedUser)
                _currentUser.value = updatedUser
                _loginState.value = LoginState.Success(updatedUser)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error updating user: ${e.message}")
            }
        }
    }

    private fun Context.getFilePathFromUri(uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(this, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            when {
                uri.authority == "com.android.externalstorage.documents" -> {
                    val split = docId.split(":")
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        filePath = "${Environment.getExternalStorageDirectory()}/${split[1]}"
                    }
                }
                // Add more cases for other URI types if needed
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            filePath = getDataColumn(this, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            filePath = uri.path
        }
        return filePath
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }
}

sealed class LoginState {
    object Initial : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModelFactory(private val userDao: UserDao, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}