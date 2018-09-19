# What is Reactive Programming?

Reactive Programming is basically event-based asynchronous programming. Everything you see is an asynchronous data stream, which can be observed and an action will be taken place when it emits values. You can create data stream out of anything; variable changes, click events, http calls, data storage, errors and what not. When it says asynchronous, that means every code module runs on its own thread thus executing multiple code blocks simultaneously.

An advantage of asynchronous approach is, as every task runs on its own thread, all the task can start simultaneously and amount of time takes complete all the tasks is equivalent to the longer task in the list. When it comes to mobile apps, as the tasks runs on background thread, you can achieve seamless user experience without blocking main thread.

# What is RxJava?

[RxJava](https://github.com/ReactiveX/RxJava) is Java implementation of Reactive Extension (from [Netflix](https://github.com/ReactiveX/RxJava/wiki)). Basically it’s a library that composes asynchronous events by following **Observer Pattern**. You can create asynchronous data stream on any thread, transform the data and consumed it by an Observer on any thread. The library offers wide range of amazing operators like map, combine, merge, filter and lot more that can be applied onto data stream.

# What is RxAndroid?

[RxAndroid](https://github.com/ReactiveX/RxAndroid) is specific to Android Platform with few added classes on top of RxJava. More specifically, [Schedulers](http://reactivex.io/documentation/scheduler.html) are introduced in *RxAndroid (AndroidSchedulers.mainThread())* which plays major role in supporting multithreading concept in android applications. Schedulers basically decides the thread on which a particular code runs whether on background thread or main thread. Apart from it everything we use is from RxJava library only.

Below are the list of schedulers available and their brief introduction.

* Schedulers.io() – This is used to perform non CPU-intensive operations like making network calls, reading disc / files, database operations etc., This maintains pool of threads.
* AndroidSchedulers.mainThread() – This provides access to android Main Thread / UI Thread. Usually operations like updating UI, user interactions happens on this thread. We shouldn’t perform any intensive operations on this thread as it makes the app glitchy or ANR dialog can be thrown.
* Schedulers.newThread() – Using this, a new thread will be created each time a task is scheduled. It’s usually suggested not to use schedular unless there is a very long running operation. The threads created via newThread() won’t be reused.
* Schedulers.computation() – This schedular can be used to perform CPU-intensive operations like processing huge data, bitmap processing etc., The number of threads created using this scheduler completely depends on number CPU cores available.
* Schedulers.single() – This scheduler will execute all the tasks in sequential order they are added. This can be used when there is necessity of sequential execution is required.
* Schedulers.immediate() – This scheduler executes the the task immediately in synchronous way by blocking the main thread.
* Schedulers.trampoline() – It executes the tasks in First In – First Out manner. All the scheduled tasks will be executed one by one by limiting the number of background threads to one.
* Schedulers.from() – This allows us to create a scheduler from an executor by limiting number of threads to be created. When thread pool is occupied, tasks will be queued.

Even through there are lot of Schedulers available, **Schedulers.io()** and **AndroidSchedulers.mainThread()** are extensively used in android programming.



