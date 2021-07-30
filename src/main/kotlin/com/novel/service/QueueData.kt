package com.novel.service

data class QueueData<T>(val signal: Int, var data: T? = null)
