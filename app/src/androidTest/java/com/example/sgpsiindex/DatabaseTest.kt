package com.example.sgpsiindex

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sgpsiindex.database.Database
import com.example.sgpsiindex.database.dao.Dao
import com.example.sgpsiindex.model.Response
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private var dao: Dao? = null

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Database.TEST_MODE = true
        val database = Database.create(InstrumentationRegistry.getInstrumentation().targetContext)
        dao = database.dao()
    }

    @After
    fun tearDown() {}

    @Test
    fun shouldInsertResponse() {
        val id = (0..999).random()

        val response = Response(id, listOf(), listOf())
        dao?.save(response)

        val savedResponse = getValue(dao?.load()!!)
        Assert.assertEquals(id, savedResponse.id)
    }

    @Test
    fun shouldClearResponses(){
        dao?.clear()
        val savedResponse = getValue(dao?.load()!!)
        Assert.assertEquals(savedResponse, null)
    }

    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T?) {
                data[0] = t
                latch.countDown()
                liveData.removeObserver(this)
            }

        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        return data[0] as T
    }
}