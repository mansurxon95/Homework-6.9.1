package com.mansurdev.homework_691.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mansurdev.homework_691.dao.CategoryDao
import com.mansurdev.homework_691.dao.WordDao
import com.mansurdev.homework_691.entitiy.Category
import com.mansurdev.homework_691.entitiy.Word

@Database(entities = [Category::class, Word::class], version = 1, exportSchema = true)

abstract class TranslationDatabase:RoomDatabase() {
    abstract fun CategoryDao(): CategoryDao
    abstract fun WordDao(): WordDao


    companion object{
        private var instance: TranslationDatabase?=null

        @Synchronized
        fun getInstance(context: Context):TranslationDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(context,TranslationDatabase::class.java,"trans_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }

}