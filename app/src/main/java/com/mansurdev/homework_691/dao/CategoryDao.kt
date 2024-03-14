package com.mansurdev.homework_691.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mansurdev.homework_691.entitiy.Category
import io.reactivex.Completable
import io.reactivex.Flowable


@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category) : Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCategory(category: Category) :Completable

    @Query("DELETE FROM Category WHERE categoryId = :categoryId")
    fun deleteCategory(categoryId: Int):Completable

    @Query("SELECT * FROM Category")
    fun getAllCategory(): Flowable<List<Category>>
    @Query("SELECT categoryName FROM Category")
    fun getAllCategoryString(): Flowable<List<String>>
    @Query("SELECT categoryId FROM Category")
    fun getAllCategoryId(): List<Int>




}