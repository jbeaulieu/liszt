package com.jbproductions.liszt

import android.app.Application
import com.jbproductions.liszt.db.LisztDatabase
import com.jbproductions.liszt.db.LisztRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class LisztApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

//    val database by lazy { LisztDatabase.getDatabase(this, applicationScope) }
//    val repository by lazy { LisztRepository(database.taskDao(), database.listDao()) }

    companion object {
        var database: LisztDatabase? = null
        var repository: LisztRepository? = null
    }

    override fun onCreate() {
        super.onCreate()
        database = LisztDatabase.getDatabase(this, applicationScope)
        repository = LisztRepository(database!!.taskDao(), database!!.listDao())
    }
}