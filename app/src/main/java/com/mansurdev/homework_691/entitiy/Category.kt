package com.mansurdev.homework_691.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Category {

    @PrimaryKey(autoGenerate = true)
    var categoryId:Int?= null

    var categoryName:String?=null

    constructor(categoryId: Int?, categoryName: String?) {
        this.categoryId = categoryId
        this.categoryName = categoryName
    }

    override fun toString(): String {
        return "Category(categoryName=$categoryName)"
    }


}