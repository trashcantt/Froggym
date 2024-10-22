package com.sabdev.froggym.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.*
import com.sabdev.froggym.data.dao.*
import com.sabdev.froggym.data.entities.*

@Database(entities = [User::class, Routine::class, Exercise::class, RoutineExerciseCrossRef::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routineDao(): RoutineDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}