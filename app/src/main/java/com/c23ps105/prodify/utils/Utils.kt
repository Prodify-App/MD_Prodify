package com.c23ps105.prodify.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import com.c23ps105.prodify.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)

    var compressQuality = 100
    var streamLength: Int

    do {
        streamLength = getImageSize(bitmap, compressQuality)
        compressQuality -= 5
    } while (streamLength > 1000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}

fun getImageSize(bitmap: Bitmap, compressQuality: Int): Int {
    val bmpStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
    val bmpPicByteArray = bmpStream.toByteArray()
    return bmpPicByteArray.size
}

fun resizeImage(bitmap: Bitmap, maxHeight: Int, maxWidth: Int): Bitmap {
    val height = bitmap.height
    val width = bitmap.width

    val ratio: Float = width.toFloat() / height.toFloat()
    val maxRatio: Float = maxWidth.toFloat() / maxWidth.toFloat()

    maxWidth.apply {
        if (maxRatio > ratio) (maxHeight.toFloat() * ratio).toInt()
        else (maxWidth.toFloat() / ratio).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, maxWidth, maxHeight, true)
}

fun flipImage(bitmap: Bitmap): Bitmap {
    val matrix = Matrix()
    matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) // flip gambar
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun urlToBitmap(src: String): Bitmap? {
    return try {
        val url = URL(src)
        val connection = url.openConnection()
        connection.doInput = true
        connection.connect()
        val input = connection.getInputStream()
        BitmapFactory.decodeStream(input)
    } catch (e: Exception) {
        print(e)
        null
    }
}

fun isEmailValid(email: CharSequence): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun String.getDateFromTimeStamp(): String = substring(0, 10)

fun String.getTimeFromTimeStamp(): String = substring(11, 16)

fun String.getFirstName(): String = if (contains(" ")) {
    split(" ")[0].replaceFirstChar { it.uppercase() }
} else replaceFirstChar { it.uppercase() }

fun String.withDateFormat(): String {
    return this.toDate("yyyy-MM-dd").formatTo("dd, MMM yyyy")
}

fun String.withTimeFormat(): String {
    return this.toDate("k:mm").formatTo("k:mm")
}

fun String.toDate(dateFormat: String, timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}