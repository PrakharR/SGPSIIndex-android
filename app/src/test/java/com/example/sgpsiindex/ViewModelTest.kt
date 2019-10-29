package com.example.sgpsiindex

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.sgpsiindex.model.Response
import com.example.sgpsiindex.repository.Repository
import com.example.sgpsiindex.viewmodel.ViewModel
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ViewModelTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository: Repository = mock()
    lateinit private var viewModel: ViewModel

    private val responseObserver: Observer<Response> = mock()

    private val response = Response(1, listOf(), listOf())

    @Before
    fun setUpViewModel() {
        whenever(mockRepository.getData()).doReturn(MutableLiveData(response))

        viewModel = ViewModel(mockRepository)
        viewModel.response.observeForever(responseObserver)
    }

    @Test
    fun refreshData_ShouldReturnResponse() {
        viewModel.refresh("2019-04-24T12:00:00")
        val argumentCaptor = argumentCaptor<Response>()
        argumentCaptor.run {
            verify(responseObserver, times(1)).onChanged(capture())
            assertEquals(response, lastValue)
        }
    }

}