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

## RxJava Basics: Observable, Observer

RxJava is all about two key components: **Observable** and **Observer**. In addition there are following

**Observable**: Observable is a data stream that do some work and emits data.

**Observer**: Observer is the counter part of Observable. It receives the data emitted by Observable.

**Subscription**: The bonding between Observable and Observer is called as Subscription. There can be multiple Observers subscribed to a single Observable.

**Operator / Transformation**: Operators modifies the data emitted by Observable before an observer receives them.

**Schedulers**: Schedulers decides the thread on which Observable should emit the data and on which Observer should receives the data i.e background thread, main thread etc.,

#### The Basic Steps for an RxJava App

1. Add dependencies for RxJava and RxAndroid
```
// RxJava
implementation 'io.reactivex.rxjava2:rxjava:2.1.9'
 
// RxAndroid
implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'
```
2. Create an **Observable** that emits data. Below we have created an Observable that emits list of animal names. Here just() operator is used to emit few animal names.
`Observable<String> animalsObservable = Observable.just("Ant", "Bee", "Cat", "Dog", "Fox");`

3. Create an **Observer** that listen to Observable. Observer provides the below interface methods to know the the state of Observable.

* onSubscribe(): Method will be called when an Observer subscribes to Observable.
* onNext(): This method will be called when Observable starts emitting the data.
* onError(): In case of any error, onError() method will be called.
* onComplete(): When an Observable completes the emission of all the items, onComplete() will be called.
```
Observer<String> animalsObserver = getAnimalsObserver();
 
private Observer<String> getAnimalsObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }
 
            @Override
            public void onNext(String s) {
                Log.d(TAG, "Name: " + s);
            }
 
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
 
            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }
```
4. Make **Observer subscribe to Observable** so that it can start receiving the data. Here, you can notice two more methods, *observeOn()* and *subscribeOn()*.

* subscribeOn(Schedulers.io()): This tell the Observable to run the task on a background thread.
* observeOn(AndroidSchedulers.mainThread()): This tells the Observer to receive the data on android UI thread so that you can take any UI related actions.
```
animalsObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(animalsObserver);
```
If you run the program, you can see the below output in the logcat

```
onSubscribe
Name: Ant
Name: Bee
Name: Cat
Name: Dog
Name: Fox
All items are emitted!
```

### Introducing Disposable

**Disposable**: Disposable is used to dispose the subscription when an Observer no longer wants to listen to Observable. In android disposable are very useful in avoiding memory leaks.

Let’s say you are making a long running network call and updating the UI. By the time network call completes its work, if the activity / fragment is already destroyed, as the Observer subscription is still alive, it tries to update already destroyed activity. In this case it can throw a memory leak. So using the Disposables, the un-subscription can be done when the activity is destroyed.

create a private **Disposable** variable and call **disposable.dispose()** in Activity's *onDestroy()* method and also assign the disposable value to the variable create in the observer method.
```
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // don't send events once the activity is destroyed
        mDisposable.dispose();
    }
```
```
   private Observer<String> getAnimalObserver() {
      return new Observer<String>() {
        ...
     @Override
     public void onSubscribe(Disposable d) {
       Log.d(TAG, "onSubscribe");
       mDisposable = d;
     }
     ...
```
### Introducing Operator

Now another example is by introducing an operator to transform the emitted data. In the below example **filter()** operator is used to filter out the emitted data.

* filter() operator filters the data by applying a conditional statement. The data which meets the condition will be emitted and the remaining will be ignored.

In the below example the animal names which starts with **letter `b`** will be filtered. Two places will need code fixes
```
//observer subscribing to observable
        animalsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.toLowerCase().startsWith("b");
                    }
                })
                .subscribe(animalsObserver);
```
Adding more animals that start with **b**
```
    private Observable<String> getAnimalsObservable() {
        return Observable.fromArray(
                "Ant", "Ape",
                "Bat", "Bee", "Bear", "Butterfly",
                "Cat", "Crab", "Cod",
                "Dog", "Dove",
                "Fox", "Frog");
    }
```
### Multiple Observers and CompositeDisposable

Consider a case where you have multiple Observables and Observers. Disposing them in Destroy one bye one is a tedious task and it can be error prone as you might forgot to dispose. In this case we can use **CompositeDisposable**.

* CompositeDisposable: Can maintain list of subscriptions in a pool and can dispose them all at once. Usually we call **compositeDisposable.clear()** in *onDestroy()* method, but you can call anywhere in the code.

In the below example, we use two observers **animalsObserver** and **animalsObserverAllCaps** subscribed to same Observable. The both observers receives the same data but the data changes as different operators are applied on the stream.

* animalsObserver: – The *filter()* operator is used to filter the animal names starting with *letter `b`*.
* animalsObserverAllCaps: – The *filter()* operator is used to filter the animal names starting with *letter `c`*. Later **map()** operator is used to convert each animal name to all capital letters. Using multiple operators on a single observer is called **chaining of operators**.
```
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Observable
        Observable<String> animalsObservable = getAnimalsObservable();

        //Observer
        DisposableObserver<String> animalsObserver = getAnimalsObserver();

        DisposableObserver<String> animalsObserverAllCaps = getAnimalsAllCapsObserver();


         //filter() is used to filter out the animal names starting with `b`

        mCompositeDisposable .add(
                animalsObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.toLowerCase().startsWith("b");
                            }
                        })
                        .subscribeWith(animalsObserver));


          // filter() is used to filter out the animal names starting with 'c'
         // map() is used to transform all the characters to UPPER case


        mCompositeDisposable.add(
                animalsObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.toLowerCase().startsWith("c");
                            }
                        })
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(String s) throws Exception {
                                return s.toUpperCase();
                            }
                        })
                        .subscribeWith(animalsObserverAllCaps));
    }

    private DisposableObserver<String> getAnimalsObserver() {
        return new DisposableObserver<String>() {

            @Override
            public void onNext(String s) {
                Log.d(TAG, "Name: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    private DisposableObserver<String> getAnimalsAllCapsObserver() {
        return new DisposableObserver<String>() {


            @Override
            public void onNext(String s) {
                Log.d(TAG, "Name: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    private Observable<String> getAnimalsObservable() {
        return Observable.fromArray(
                "Ant", "Ape",
                "Bat", "Bee", "Bear", "Butterfly",
                "Cat", "Crab", "Cod",
                "Dog", "Dove",
                "Fox", "Frog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // don't send events once the activity is destroyed
        mCompositeDisposable.clear();
    }
}
```
If you run this example, you can see the below output. Here the animal names that starts with `B` are observed by **animalsObserver**. All the names starting with letter `c` and in all capital letters are observed by **animalsObserverAllCaps**.

Output
```
Name: Bat
Name: Bee
Name: Bear
Name: Butterfly
All items are emitted!
Name: CAT
Name: CRAB
Name: COD
All items are emitted!
```
### Custom Data Type, Operators

In this, instead of using just primitive data types, we are going to use a custom data type i.e **Note** model. We use same Observable and Observer concept here except the streamed data is of Note data type.

* Here **map()** operator is used to convert all the notes to all uppercase format. Create a separate Note class containing 2 member variables `int id` and `String note`

```
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add to Composite observable
        // .map() operator is used to turn the note into all uppercase letters
        mCompositeDisposable.add(getNotesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Note, Note>() {
                    @Override
                    public Note apply(Note note) throws Exception {
                        // Making the note to all uppercase
                        note.setNote(note.getNote().toUpperCase());
                        return note;
                    }
                })
                .subscribeWith(getNotesObserver()));
    }

    private DisposableObserver<Note> getNotesObserver() {
        return new DisposableObserver<Note>() {

            @Override
            public void onNext(Note note) {
                Log.d(TAG, "Note: " + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All notes are emitted!");
            }
        };
    }

    private Observable<Note> getNotesObservable() {
        final List<Note> notes = prepareNotes();

        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for (Note note : notes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }

                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "buy tooth paste!"));
        notes.add(new Note(2, "call brother!"));
        notes.add(new Note(3, "watch narcos tonight!"));
        notes.add(new Note(4, "pay power bill!"));

        return notes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
```
Output on running the code
```
Id: 1, note: BUY TOOTH PASTE!
Id: 2, note: CALL BROTHER!
Id: 3, note: WATCH NARCOS TONIGHT!
Id: 4, note: PAY POWER BILL!
All notes are emitted!
```


