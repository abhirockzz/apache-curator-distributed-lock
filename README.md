## Distributed/interprocess mutexes/locks using using Apache Curator

For details, read the [blog](https://simplydistributed.wordpress.com/2016/12/21/apache-curator-distributed-try-locks)

In the task which [blocks for the lock](https://github.com/abhirockzz/apache-curator-distributed-lock/blob/master/src/main/java/com/wordpress/simplydistributed/curator/disributedlock/BlockingLockTest.java#L30)

- for both the threads that run the task, the for loop will be executed twice
- this is because of the blocking/queuing effect - the thread will wait until the lock is released by the other thread (this will happen for both iterations of the loop)

In the task which initiates a [non-blocking request for the lock](https://github.com/abhirockzz/apache-curator-distributed-lock/blob/master/src/main/java/com/wordpress/simplydistributed/curator/disributedlock/NonBlockingLockTest.java#L30)

- for both threads, only one iteration of the loop in the task is executed
- this is because the thread which fails to get the lock in the first attempt does not block - it simply returns and repeats the loop
