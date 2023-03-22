package com.livechat.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * User: Quang Phúc
 * Date: 2023-02-15
 * Time: 11:24 PM
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providerContentResolver(application: Application): ContentResolver =
        application.contentResolver

    @Provides
    @Singleton
    fun providerContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun providerFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    //Gson for converting JSON String to Java Objects
    @Provides
    @Singleton
    fun providerGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun providerFirebaseAuth(): FirebaseAuth = Firebase.auth
}