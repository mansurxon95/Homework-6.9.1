package com.mansurdev.homework_691.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mansurdev.homework_691.entitiy.Word
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.net.ssl.SSLEngineResult.Status


@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: Word) : Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWord(word: Word) :Completable

    @Query("UPDATE Word SET wordStatus=:status WHERE wordId = :wordId")
    fun updateWordStatus(wordId: Int,status:Int) :Completable

    @Query("DELETE FROM Word WHERE wordId = :wordId")
    fun deleteWord(wordId: Int):Completable

    @Query("SELECT * FROM Word")
    fun getAllWord(): Flowable<List<Word>>
    @Query("SELECT * FROM Word WHERE wordCategoryId=:categoryId")
    fun getAllWordcategoryId(categoryId:Int): Flowable<List<Word>>


    @Query("SELECT * FROM Word WHERE wordId=:wordId")
    fun getAllWordId(wordId:Int): Word

    @Query("SELECT * FROM Word WHERE wordStatus=:status")
    fun getAllWordStatus(status:Int): Flowable<List<Word>>


}