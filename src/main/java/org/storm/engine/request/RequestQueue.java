package org.storm.engine.request;

import org.storm.engine.exception.StormEngineException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A RequestQueue is a simple non-blocking queue (backed by a ConcurrentLinkedQueue) for queuing a list of Requests
 * and retrieving them.
 */
public class RequestQueue {

  public static final int UNLIMITED_REQUEST_SIZE = -1;

  private final Queue<List<Request>> queue;

  private final int requestSizeLimit;

  public RequestQueue(int requestSizeLimit) {
    this.queue = new ConcurrentLinkedQueue<>();
    this.requestSizeLimit = requestSizeLimit;
  }

  public RequestQueue() {
    this(UNLIMITED_REQUEST_SIZE);
  }

  /**
   * Submits the given list of Requests to the queue
   *
   * @param requests list of Requests to submit
   */
  public void submit(List<Request> requests) {
    if (requests.size() > this.requestSizeLimit && this.requestSizeLimit != UNLIMITED_REQUEST_SIZE) {
      throw new StormEngineException("single request size higher than allowed limit");
    }
    this.queue.add(requests);
  }

  /**
   * Submits the given Requests to the queue as a single list
   *
   * @param requests variable number of Requests to submit
   */
  public void submit(Request... requests) {
    this.submit(Arrays.asList(requests));
  }

  /**
   * Submits the given Requests to the queue separately
   *
   * @param requests variable number of Requests to submit
   */
  public void submitSeparately(Request... requests) {
    for (Request r : requests) {
      this.submit(r);
    }
  }

  /**
   * Clears the queue
   */
  public void clear() {
    this.queue.clear();
  }

  /**
   * This is a non-blocking retrieve of the next request in the queue.
   *
   * @return an Optional with either the new list of requests in the queue or empty if the queue is empty
   */
  public Optional<List<Request>> next() {
    return Optional.ofNullable(this.queue.poll());
  }

}
