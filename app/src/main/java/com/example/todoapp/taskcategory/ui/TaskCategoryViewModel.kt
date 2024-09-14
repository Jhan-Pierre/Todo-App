package com.example.todoapp.taskcategory.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtasks.domain.GetTaskByCategoryUseCase
import com.example.todoapp.taskcategory.domain.AddCategoryUseCase
import com.example.todoapp.taskcategory.domain.DeleteCategoryUseCase
import com.example.todoapp.taskcategory.domain.GetCategoryUseCase
import com.example.todoapp.taskcategory.domain.UpdateCategoryUseCase
import com.example.todoapp.taskcategory.ui.TaskCategoryUiState.Success
import com.example.todoapp.taskcategory.ui.model.TaskCategoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskCategoryViewModel @Inject constructor(
    getCategoryUseCase: GetCategoryUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val getTaskByCategoryUseCase: GetTaskByCategoryUseCase
) : ViewModel() {

    private val _showDropDown = MutableLiveData(false)
    val showDropDown: MutableLiveData<Boolean> = _showDropDown

    fun setShowDropDown(show: Boolean) {
        _showDropDown.value = show
    }

    private val _selectedCategory = MutableLiveData<String?>(null)

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
        _showDropDown.value = !_showDropDown.value!!
    }

    // Propiedad para las categorías
    val categories: StateFlow<List<TaskCategoryModel>> = getCategoryUseCase()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<TaskCategoryUiState> = getCategoryUseCase()
        .map { categories -> Success(categories) }
        .catch { exception -> TaskCategoryUiState.Error(exception) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskCategoryUiState.Loading
        )

    fun onTaskCategoryCreated(category: String) {
        viewModelScope.launch {
            addCategoryUseCase(TaskCategoryModel(category = category))
        }
    }

    fun onTaskCategoryUpdate(category: TaskCategoryModel) {
        viewModelScope.launch {
            updateCategoryUseCase(category)
        }
    }

    fun onTaskCategoryRemove(category: TaskCategoryModel) {
        viewModelScope.launch {
            deleteCategoryUseCase(category)
        }
    }
}