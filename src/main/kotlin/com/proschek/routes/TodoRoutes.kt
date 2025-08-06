package com.proschek.routes

import com.proschek.exception.TodoInvalidDataException
import com.proschek.exception.TodoNotFoundException
import com.proschek.model.CreateTodoRequest
import com.proschek.model.Todo
import com.proschek.repository.MongoTodoRepository
import com.proschek.utils.toUUIDOrNull
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

/** Configures all todo-related API routes under /api/todos endpoint. */
fun Route.todoRoutes(mongoTodoRepository: MongoTodoRepository) {
    route("/api/todos") {
        getAllTodos(mongoTodoRepository)
        createTodo(mongoTodoRepository)
        getTodoById(mongoTodoRepository)
        updateTodo(mongoTodoRepository)
        deleteTodo(mongoTodoRepository)
    }
}

// GET /api/todos - Get all todos
private fun Route.getAllTodos(mongoTodoRepository: MongoTodoRepository) {
    get {
        val todos = mongoTodoRepository.allTodos()
        call.respond<List<Todo>>(HttpStatusCode.OK, todos)
    }
}

// POST /api/todos - Create a new todo
private fun Route.createTodo(mongoTodoRepository: MongoTodoRepository) {
    post {
        val request = call.receive<CreateTodoRequest>()
        if (request.title.isEmpty()) {
            throw TodoInvalidDataException("Title is required")
        }

        val todo = mongoTodoRepository.addTodo(request)
        call.respond(HttpStatusCode.Created, todo)
    }
}

// GET /api/todos/{id} - Get a specific todo
private fun Route.getTodoById(mongoTodoRepository: MongoTodoRepository) {
    get("/{id}") {
        val id = call.parameters["id"].toString()
        id.toUUIDOrNull() ?: throw TodoInvalidDataException("Invalid ID Format")

        val todo = mongoTodoRepository.todoById(id) ?: throw TodoNotFoundException("Todo not Found")
        call.respond(todo)
    }
}

// DELETE /api/todos/{id} - Delete a specific todo
private fun Route.deleteTodo(mongoTodoRepository: MongoTodoRepository) {
    delete("/{id}") {
        val id = call.parameters["id"].toString()
        val uuid = id.toUUIDOrNull()
        if (uuid == null) {
            throw TodoInvalidDataException("Invalid Todo ID")
        }

        val isDeleted = mongoTodoRepository.removeTodo(id)
        if (isDeleted) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            throw TodoNotFoundException("Todo not Found")
        }
    }
}

// PUT /api/todos/{id} - Update a specific todo
private fun Route.updateTodo(mongoTodoRepository: MongoTodoRepository) {
    put("/{id}") {
        val id = call.parameters["id"].toString()
        val uuid = id.toUUIDOrNull()

        if (uuid == null) {
            throw TodoInvalidDataException("Invalid ID Format")
        }
        val request = call.receive<CreateTodoRequest>()

        val updatedTodo = mongoTodoRepository.updateTodo(id, request)
        if (updatedTodo != null) {
            call.respond(updatedTodo)
        } else {
            throw TodoNotFoundException("Todo not Found")
        }
    }
}
