package com.mobiledeveloper.vktube.extensions

enum class VideoType {
    Vk, Youtube, Unknown
}

fun String.extractVideoType(): VideoType {
    if (this.contains("youtube")) {
        return VideoType.Youtube
    }

    if (this.contains("vk.com")) {
        return VideoType.Vk
    }

    return VideoType.Unknown
}