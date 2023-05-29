package com.c23ps105.prodify.data

import com.c23ps105.prodify.R


object CardData {
    private val image = intArrayOf(
        R.drawable.lesson1,
        R.drawable.lesson2,
        R.drawable.lesson3,
    )

    private val title = arrayOf(
        "Kelas Android Developer",
        "Kelas Web Programming",
        "Machine Learning Developer",
    )

    private val content = arrayOf(
        "Tingkatkan keterampilan coding Android Anda dalam pelatihan mandiri dan gratis tentang Android dalam Kotlin Lanjutan. Kursus ini menggunakan bahasa pemrograman Kotlin dan mengajarkan Anda tentang notifikasi, grafis dan animasi di Android, cara login pengguna, menambahkan peta ke aplikasi, serta menguji aplikasi dengan benar. Setiap pelajaran dilengkapi dengan tutorial beserta kode solusi di GitHub.",
        "Belajar materi kelas Full-Stack Web Developer secara online dan gratis berkonsultasi dengan mentor yang berpengalaman pada bidangnya di BuildWith Angga.",
        "Bangun keterampilan machine learning Anda dengan kursus pelatihan digital, pelatihan ruang kelas, dan sertifikasi untuk peran machine learning khusus.",
    )

    val listData: ArrayList<CardDataClass>
        get() {
            val list = arrayListOf<CardDataClass>()
            for (position in title.indices) {
                val data = CardDataClass()
                data.apply {
                    imgData = image[position]
                    titleData = title[position]
                    contentData = content[position]
                    list.add(data)
                }
            }
            return list
        }
}


