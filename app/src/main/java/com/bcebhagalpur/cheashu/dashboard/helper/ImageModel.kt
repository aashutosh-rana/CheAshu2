package com.bcebhagalpur.cheashu.dashboard.helper

 class ImageModel {

    private var image: String? = null
    private var title: String? = null
    private var resImg = 0
   internal var isSelected = false

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getResImg(): Int {
        return resImg
    }

    fun setResImg(resImg: Int) {
        this.resImg = resImg
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }
}