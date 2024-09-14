package com.example.todoapp.addtasks.domain

import com.example.todoapp.addtasks.data.TaskRepository
import com.example.todoapp.addtasks.ui.model.TaskModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskByCategoryUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(category: Int): Flow<List<TaskModel>> {
        return taskRepository.getTasksByCategory(category)
    }
}
