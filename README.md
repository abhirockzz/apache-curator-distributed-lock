## Distributed/interprocess mutexes/locks using Apache Curator

For details, read the [blog](https://simplydistributed.wordpress.com/2016/12/21/apache-curator-distributed-try-locks). Run two instances of each process (one after the other in quick succession) to observe the behavior

### Behavior

In the [block for lock test](https://github.com/abhirockzz/apache-curator-distributed-lock/blob/master/src/main/java/com/wordpress/simplydistributed/curator/disributedlock/BlockingLockTest.java#L30)

- for both the processes that run the task, the `for` loop will be executed twice
- this is because of the blocking/queuing effect - the thread in each process will wait until the lock is released by the thread in another process (this will happen for both iterations of the loop)

![Block for Lock](https://simplydistributed.files.wordpress.com/2016/12/blocking-lock1.jpg)

In the [non-blocking request for the lock test](https://github.com/abhirockzz/apache-curator-distributed-lock/blob/master/src/main/java/com/wordpress/simplydistributed/curator/disributedlock/NonBlockingLockTest.java#L30)

- the thread wait time for work simulation (3 seconds) has been kept lower than the lock acquire time out (2 seconds) on purpose
- for both the processes, only one iteration of the loop in the task is executed (either first or second)
- this is because the thread in the process which fails to get the lock does not block - it simply returns and repeats the loop (hence misses that iteration of task/job)

![Non block lock](https://simplydistributed.files.wordpress.com/2016/12/non-block-lock.jpg)

### Note

Please change the Zookeeper instance details before testing things out - [here](https://github.com/abhirockzz/apache-curator-distributed-lock/blob/master/src/main/java/com/wordpress/simplydistributed/curator/disributedlock/BlockingLockTest.java#L15) and [here](https://github.com/abhirockzz/apache-curator-distributed-lock/blob/master/src/main/java/com/wordpress/simplydistributed/curator/disributedlock/NonBlockingLockTest.java#L15)
