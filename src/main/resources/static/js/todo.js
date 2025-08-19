class TodoAPI {
  static async request(endpoint, method = "GET", data = null) {
    try {
      const response = await fetch(`/api/todos${endpoint}`, {
        method,
        headers: data ? { "Content-Type": "application/json" } : {},
        body: data ? JSON.stringify(data) : null,
      });
      if (!response.ok)
        throw new Error(
          response.status === 404 ? "Item not found" : await response.text(),
        );
      return method === "DELETE" ? true : await response.json();
    } catch (error) {
      console.error("API Error:", error);
      throw error;
    }
  }
  static getAllTodos = () => this.request("");
  static getTodoById = (id) => this.request(`/${id}`);
  static createTodo = (data) => this.request("", "POST", data);
  static updateTodo = (id, data) => this.request(`/${id}`, "PUT", data);
  static deleteTodo = (id) => this.request(`/${id}`, "DELETE");
}

class TodoUI {
  constructor() {
    this.elements = {
      list: document.getElementById("todo-list"),
      form: document.getElementById("todo-form"),
      title: document.getElementById("todo-title"),
      description: document.getElementById("todo-description"),
      status: document.getElementById("todo-status"),
      filter: document.getElementById("status-filter"),
    };
    this.bindEvents();
    this.loadTodos();
  }

  createTodoElement(todo) {
    const li = document.createElement("li");
    li.className = "todo-item";
    li.dataset.id = todo.id;
    li.dataset.status = todo.status;

    const dateInfo = todo.updatedAt
      ? `Created: ${todo.createdAt.split("T")[0]}<br>Updated: ${todo.updatedAt.split("T")[0]}`
      : `Created: ${todo.createdAt.split("T")[0]}`;

    li.innerHTML = `
            <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700">
                <div class="p-4">
                    <div class="flex items-center justify-between">
                        <div class="flex items-center gap-3">
                            <span class="status-${todo.status}">${todo.status}</span>
                            <h3 class="text-lg font-semibold text-gray-800 dark:text-white">${todo.title}</h3>
                        </div>
                        <div class="flex gap-2">
                            <button class="edit-btn px-3 py-1.5 bg-primary-500 hover:bg-primary-600 text-white font-medium rounded-lg transition-colors">Edit</button>
                            <button class="delete-btn px-3 py-1.5 bg-red-500 hover:bg-red-600 text-white font-medium rounded-lg transition-colors">Delete</button>
                        </div>
                    </div>
                    <div class="mt-2">
                        <p class="text-gray-600 dark:text-gray-300 mb-2">${todo.description}</p>
                            <p class="text-sm text-gray-500 dark:text-gray-400">
                                    ${dateInfo}
                            </p>
                    </div>
                </div>
                <div class="edit-form max-h-0 overflow-hidden transition-all duration-200 border-t border-gray-200 dark:border-gray-700">
                    <div class="p-3 space-y-3">
                        <div class="flex gap-3">
                            <div class="flex-1">
                                <input type="text"
                                       class="edit-input w-full px-3 py-1.5 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg"
                                       value="${todo.title}"
                                       placeholder="Title">
                            </div>
                            <div class="w-32">
                                <select class="edit-status w-full px-3 py-1.5 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg">
                                    ${["TODO", "IN_PROGRESS", "DONE"]
                                      .map(
                                        (status) =>
                                          `<option value="${status}" ${status === todo.status ? "selected" : ""}>${status}</option>`,
                                      )
                                      .join("")}
                                </select>
                            </div>
                        </div>
                        <div>
                            <textarea class="edit-description w-full px-3 py-1.5 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg"
                                      rows="2"
                                      placeholder="Description">${todo.description}</textarea>
                        </div>
                        <div class="flex justify-end gap-2">
                            <button class="save-btn px-4 py-1.5 bg-primary-500 hover:bg-primary-600 text-white font-medium rounded-lg">Save</button>
                        </div>
                    </div>
                </div>
            </div>`;

    this.attachTodoListeners(li, todo);
    return li;
  }

  attachTodoListeners(li, todo) {
    const editForm = li.querySelector(".edit-form");
    const controls = {
      edit: li.querySelector(".edit-btn"),
      delete: li.querySelector(".delete-btn"),
      save: li.querySelector(".save-btn"),
      cancel: li.querySelector(".cancel-btn"),
      title: li.querySelector(".edit-input"),
      description: li.querySelector(".edit-description"),
      status: li.querySelector(".edit-status"),
    };
    let isEditing = false;

    const resetForm = () =>
      Object.assign(
        todo,
        ["title", "description", "status"].reduce(
          (acc, key) => ({ ...acc, [key]: controls[key].value }),
          {},
        ),
      );

    controls.edit.onclick = () => {
      isEditing = !isEditing;
      this.toggleEditMode(editForm, isEditing);
      if (!isEditing) resetForm();
    };

    controls.delete.onclick = async () => {
      if (confirm("Delete this todo?") && (await TodoAPI.deleteTodo(todo.id)))
        li.remove();
    };

    controls.save.onclick = async () => {
      const data = ["title", "description", "status"].reduce(
        (acc, key) => ({ ...acc, [key]: controls[key].value.trim() }),
        {},
      );
      if (!data.title || !data.description) return;
      try {
        const updated = await TodoAPI.updateTodo(todo.id, data);
        li.replaceWith(this.createTodoElement(updated));
      } catch (error) {
        alert("Failed to update: " + error.message);
      }
    };
  }

  toggleEditMode(editForm, isEdit) {
    const editBtn = editForm.parentElement.querySelector(".edit-btn");
    requestAnimationFrame(() => {
      editForm.style.maxHeight = isEdit ? `${editForm.scrollHeight}px` : "0";
      editBtn.textContent = isEdit ? "Cancel" : "Edit";
      ["gray", "primary"].forEach((color) => {
        editBtn.classList.toggle(
          `bg-${color}-500`,
          color === (isEdit ? "gray" : "primary"),
        );
        editBtn.classList.toggle(
          `hover:bg-${color}-600`,
          color === (isEdit ? "gray" : "primary"),
        );
      });
    });
  }

  bindEvents() {
    this.elements.form.onsubmit = async (e) => {
      e.preventDefault();
      const data = ["title", "description", "status"].reduce(
        (acc, key) => ({ ...acc, [key]: this.elements[key].value.trim() }),
        {},
      );
      if (!data.title || !data.description) return;
      try {
        const todo = await TodoAPI.createTodo(data);
        this.elements.list.appendChild(this.createTodoElement(todo));
        this.elements.form.reset();
      } catch (error) {
        alert("Failed to create todo: " + error.message);
      }
    };

    this.elements.filter.onchange = () => {
      const status = this.elements.filter.value;
      document
        .querySelectorAll(".todo-item")
        .forEach(
          (todo) =>
            (todo.style.display =
              status === "ALL" || todo.dataset.status === status ? "" : "none"),
        );
    };
  }

  async loadTodos() {
    try {
      const todos = await TodoAPI.getAllTodos();
      this.elements.list.innerHTML = "";
      todos.forEach((todo) =>
        this.elements.list.appendChild(this.createTodoElement(todo)),
      );
    } catch (error) {
      console.error("Error loading todos:", error);
      alert("Failed to load todos: " + error.message);
    }
  }
}

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => new TodoUI());
