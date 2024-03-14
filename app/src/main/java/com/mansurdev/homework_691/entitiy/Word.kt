package com.mansurdev.homework_691.entitiy

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(foreignKeys = [ForeignKey(
    entity = Category::class,
    parentColumns = arrayOf("categoryId"),
    childColumns = arrayOf("wordCategoryId"),
    onDelete = ForeignKey.CASCADE )
])
class Word : Serializable {
    @PrimaryKey(autoGenerate = true)
    var wordId:Int?=null
    var wordCategoryId:Int?=null
    var wordImage:String?=null
    var word:String?=null
    var wordTranslation:String?=null
    var wordStatus:Int?=null

    constructor(
        wordId: Int?,
        wordCategoryId: Int?,
        wordImage: String?,
        word: String?,
        wordTranslation: String?,
        wordStatus: Int?
    ) {
        this.wordId = wordId
        this.wordCategoryId = wordCategoryId
        this.wordImage = wordImage
        this.word = word
        this.wordTranslation = wordTranslation
        this.wordStatus = wordStatus
    }
}