package org.storm.core.event

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EventManagerTest {

    companion object {
        @AfterAll
        @JvmStatic
        fun cleanup() {
            EventManager.close()
        }
    }

    data class TestEvent(val data: String)

    @Test
    fun testSimpleEventStream() {
        val stream = EventManager.createEventStream<TestEvent>("test-event", autoStart = false)

        runBlocking {
            val results = mutableListOf<TestEvent>()
            stream.addConsumer {
                results.add(it)
            }

            stream.produce(TestEvent("hello"))
            stream.produce(TestEvent("world"))

            stream.consume()
            stream.consume()

            assert(results.size == 2) {
                "Expected 2 events to be consumed, but ${results.size} were consumed instead"
            }

            assert(results[0].data == "hello") {
                "Expected first consumed event to have 'hello' as the data"
            }

            assert(results[0].data != results[1].data) {
                "Expected two different events to be produced and consumed"
            }
        }
    }

    @Test
    fun testGetEventStream() {
        // Doesn't exist
        val notFound = assertThrows<IllegalArgumentException> {
            EventManager.getEventStream<TestEvent>("test-event")
        }

        assert(notFound.message!!.contains("not found")) {
            "Expected the exception to explain that the event was not found"
        }

        EventManager.createEventStream<TestEvent>("test-event", autoStart = false)

        val stream = EventManager.getEventStream<TestEvent>("test-event")

        runBlocking {
            stream.produce(TestEvent("hello world"))
            val event = stream.consume()
            assert(event.data == "hello world") {
                "Expected to consume event with data 'hello world'"
            }
        }

    }
}