package com.dj.flowpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.internal.ChannelFlow

class MainActivity : AppCompatActivity() {
    private lateinit var fixedFlow: Flow<Int>
    private lateinit var collectionFlow: Flow<Int>
    private lateinit var lamdaFlow: Flow<Int>
    private lateinit var channelFlow: Flow<Int>
    private val list = listOf(1, 2, 3, 4, 5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupFixedFlow()
        setupFlowFromCollection()
        setupFlowWithLamda()
        setupChannelFlowWithLamda()

        button.setOnClickListener {
            collectFixedFlow()
        }
        button2.setOnClickListener {
            collectCollectionFlow()
        }
        button3.setOnClickListener {
            collectLamdaFlow()
        }
        button4.setOnClickListener {
            collectChannelFlow()
        }

        button5.setOnClickListener {
//            runBlocking {
//                createFlowChain()
//            }
            CoroutineScope(Main).launch {
                createFlowChain()
            }
        }


    }

    private suspend fun createFlowChain() {
        flow {
            (0..10).forEach {
                Log.i("Flow", "in definition block ${Thread.currentThread().name}")
                delay(300) // Mock of api calls
                emit(it)
            }
        }.flowOn(IO)
                //Collect should be inside coroutinescope or suspend fun
                //Collect will only be work in scope where collect has been called. So it's Main
                .collect {
                    Log.i("Flow", "in collect ${Thread.currentThread().name}")
                    Log.i("Flow", "$it")
                }
    }

    private fun setupFixedFlow() {
        //fixed elements --> use flowOf(fixed elements)
        fixedFlow = flowOf(1, 2, 3, 4, 5).onEach {
            delay(300)
        }
    }

    private fun setupFlowFromCollection() {
        collectionFlow = list.asFlow().onEach {
            delay(300)
        }
    }

    private fun setupFlowWithLamda() {
        lamdaFlow = flow {

            (1..5).forEach {
                delay(300)
                //emit single item to the collector
                emit(it)
            }

        }
    }

    private fun setupChannelFlowWithLamda() {
        //channelFlow : uses channel to communicate to the collector
        channelFlow = channelFlow {

            (1..5).forEach {
                delay(300)
                //send==emit single item to the collector
                send(it)
            }

        }
    }

    /** Collect **/
    private fun collectFixedFlow() {
        CoroutineScope(Main).launch {
            fixedFlow.collect { item ->
                Log.i("Flow", "$item")
            }
        }
    }

    private fun collectCollectionFlow() {
        CoroutineScope(Main).launch {
            collectionFlow.collect { item ->
                Log.i("Flow", "$item")
            }
        }
    }

    private fun collectLamdaFlow() {
        CoroutineScope(Main).launch {
            lamdaFlow.collect { item ->
                Log.i("Flow", "$item")
            }
        }
    }

    private fun collectChannelFlow() {
        CoroutineScope(Main).launch {
            channelFlow.collect { item ->
                Log.i("Flow", "$item")
            }
        }
    }
}