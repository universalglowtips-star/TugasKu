package com.bagus.tugasku.ui.navigation

/** Nama argumen navigasi, dibaca ViewModel lewat SavedStateHandle. */
const val TASK_ID_ARG = "taskId"

/** Nilai default argumen taskId pada route form = mode "Tambah". */
const val NO_TASK_ID = -1L

object Routes {
    const val LIST = "list"
    const val FORM = "form"
    const val DETAIL = "detail"

    const val FORM_PATTERN = "$FORM?$TASK_ID_ARG={$TASK_ID_ARG}"
    const val DETAIL_PATTERN = "$DETAIL/{$TASK_ID_ARG}"

    /** Tanpa id -> layar Tambah. Dengan id -> layar Edit. */
    fun form(taskId: Long? = null): String =
        if (taskId == null) FORM else "$FORM?$TASK_ID_ARG=$taskId"

    fun detail(taskId: Long): String = "$DETAIL/$taskId"
}
