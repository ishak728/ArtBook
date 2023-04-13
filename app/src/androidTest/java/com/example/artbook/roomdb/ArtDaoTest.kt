package com.example.artbook.roomdb

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.artbook.HiltTestRunner
import com.example.artbook.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named


@SmallTest
@ExperimentalCoroutinesApi
//hilti kullanarak test edeceğimizi söylüyoruz
@HiltAndroidTest
class ArtDaoTest {

    @get:Rule
    var instantTaskExecutorRule=InstantTaskExecutorRule()

    @get:Rule
    //hilttin başlamasını sağlıyor
    var hiltRule=HiltAndroidRule(this)

    //field injectionlar private yapılamaz
    @Inject
    //bu projede 2 tane Room.DataBaseBuilder döndürüyoruz.
    // karışıklık olmasın diye(inmemorydatabase olanı alsın diye)isim veriyoruz
    @Named("testDatabase")
    lateinit var database:ArtDataBase

    private lateinit var dao:ArtDao

    @Before
    fun setup() {
/* hilti kullanmadan bu database'i yaparaktan testi yapabiliriz
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),ArtDataBase::class.java)
            .allowMainThreadQueries() //this is a test case, we don't want other thread pools
            .build()
*/
        hiltRule.inject()
        dao=database.artDao()
    }

    @After
    fun teardown() {
        database.close()
    }
    @Test
    fun insertArtTesting() = runBlockingTest {
        val exampleArt = Art("Mona Lisa","Da Vinci",1700,"test.com",1)
        dao.insertArt(exampleArt)

        val list = dao.observeArts().getOrAwaitValue()
        assertThat(list).contains(exampleArt)

    }

    @Test
    fun deleteArtTesting() = runBlocking {
        val exampleArt = Art("Mona Lisa","Da Vinci",1700,"test.com",1)
        dao.insertArt(exampleArt)
        dao.deleteArt(exampleArt)

        val list = dao.observeArts().getOrAwaitValue()
        assertThat(list).doesNotContain(exampleArt)

    }

}