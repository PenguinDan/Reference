# Realm

## Initializing Realm
```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize here
        Realm.init(this);
    }
}
```

## Creating Realm Objects
```
public class Dog extends RealmObject {
    // Define you model class by extending RealmObject
    private String name;
    private int age;
}

public class Person extends RealmObject {
    @PrimaryKey
    private long id;
    private String name;
    // Declare a one-to-many relationship
    private RealmList<Dog> dogs;
}

// Use them like regular java objects
Dog dog = new Dog();
dog.setName("Rex");
dog.setAge(1);
```
**Overriding Property Names**
The default behavior is that Realm will use the name defined in the Java model class as the name to represent classes and fields internally in the realm file
* Define a naming policy at the module level, which will affect all classes part of the module
```
@RealmModule(
    allClasses = true,
    classNamingPolicy = RealmNamingPolicy.LOWER_CASE_WITH_UNDERSCORES,
    fieldNamingPolicy = RealmNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
)
public class MyModule{

}
```
* Define a custom name for the class or a field naming policy that will effect all fields in that class. This will override any module level settings:
```
@RealmClass(
    name = "__Person", 
    fieldNamingPolicy = RealmNamingPolicy.PASCAL_CASE
)
public class Person extends RealmObject {
    public String name;
}
```
* Define a custom name for a field which will override any Class and Module level settings:
```
public class extends RealmObject {
    @RealmField(name = "person_name")
    public String name;
}
```

## Annotations
**@PrimaryKey**
* Makes it possible to use the *copyToRealmOrUpdate* or *insertOrUpdate* methods. These look for an object with a given primary key, and either updates it or creates it.
* Read querries are slightly faster, but writes are slightly slower
* Realm.createObject(..) returns a new object with all fields set to their default values. Make to to create unmanaged objects, set its fields, and then add it to the Realm with *copyToRealm* or *insert*
```
final MyObject obj = new MyObject();
obj.setId(42);
obj.setName("Fish");
realm.executeTransaction((realm) -> {
    realm.copyToRealmOrUpdate(obj);
});
```
**@Index**
* Makes writes slower, but reads faster
* Best to only add indexes when you're optimizing the read performance for specific situations
**@Ignore**
* If you don't want to save a field in your model to its Realm
* Fields marked **static** and **transient** are always ignored, and do not need this annotation

## Commiting Transactions
```
// Query Realm for all dogs younger than 2 years old
Realm realm = Realm.getDefaultInstance();
final RealmResults<Dog> puppies = realm.where(Dog.class).lessThan("age", 2).findAll();
// The following will print 0 since no dogs have been added to Realm yet
puppies.size(); 

// Begin a transaction to add items to a database
realm.beginTransaction();
final Dog managedDog = realm.copyToRealm(dog);

// You can directly add an item to the database and edit their information
Person person = realm.createObject(Person.class);
person.getDogs().add(managedDog);
realm.commitTransaction();
```
**Asynchronously Update**
```
// Asynchrously update objects on a background thread
realm.executeTransactionAsync(new Realm.Transaction() {
    @Override
    public void execute(Realm bgRealm) {
        Dog dog = bgRealm.where(Dog.class).equalTo("age", 1).findFirst();
        dog.setAge(3);
    }
}, new Realm.Transaction.OnSuccess() {
    @Override
    public void onSuccess() {
        // Original queries and Realm objects are automatically updated
        puppies.size();
        // On success, the dogs age has been updated
        managedDog.getAge();
    }
})
```

## Additing Listeners 
```
// Listeners will be notified when data changes
// Query Realm for all dogs younger than 2 years old
Realm realm = Realm.getDefaultInstance();
final RealmResults<Dog> puppies = realm.where(Dog.class).lessThan("age", 2).findAll();

puppies.addChangeListener((results, changeSet) -> {
    // Query results are updated in real time with fine grained notification
    changeSet.getInsertions();
    }
);
```

## Working with Realm Objects
**Auto-Updating Objects** <br>
A RealmObject is a live, auto-updating view into the underlying data which means that you never have to refresh objects. Changes to objects are instantly reflected in query results.
```
realm.executeTransaction((realm) ->{
    // Create and edit the Dog object
    Dog myDog = realm.createObject(Dog.class);
    myDog.setName("Fido");
    myDog.setAge(1);
});
// Perform a query
Dog myDog = realm.where(Dog.class).equalTo("age", 1).findFirst();

realm.executeTransaction((realm) -> {
    Dog myPuppy = realm.where(Dog.class).equalTo("age", 1).findFirst();
    myPuppy.setAge(2);
});
// The below item will return 2
myDog.getAge(); 
```
**Using JSON Objects**
* The JSON object can be a String, a JSONObject, or an InputStream.
* Realm will ignore any properties in the JSON not defined by the RealmObject
* Single objects using ``Realm.createObjectFromJson``
* Lists of objects through ``Realm.createAllFromJson``
```
// A RealmObject that represents a city
public class City extends RealmObject {
    private String city;
    private int id;
}

// Insert from a string
realm.executeTransaction((realm) -> {
    realm.createObjectFromJson(City.class, "{ city: \"Copenhagen\", id: 1 }");
});

// Insert multiple items using an InputStream
realm.executeTransaction((realm) -> {
    try {
        InputStream is = new FileInputStream(new File("path_to_file"));
        realm.createAllFromJson(City.class, is);
    }catch(IOException e) {
        throw new RuntimeException(e);
    }
});
```

## Adapters
```
dependencies {
    compile 'io.realm:android-adapters:2.1.1'
}
```
**RealmRecyclerViewAdapter**
```
class MyRecyclerViewAdapter extends RealmRecyclerViewAdapter<Item, MyRecyclerViewAdapter.MyViewHolder> {
    private boolean inDeletionMode = false;
    private Set<Integer> countersToDelete = new HashSet<>();

    MyRecyclerViewAdapter(OrderedRealmCollection<Item> data) {
        super(data, true);
         // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        setHasStableIds(true);
    }

    void enableDeletionMode(boolean enabled) {
        inDeletionMode = enabled;
        if(!enabled) {
            countersToDelete.clear();
        }
        notifyDataSetChanged();
    }

    Set<Intenger> getCountersToDelete() {
        return countersToDelete;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Item obj = getItem(position);
        holder.data = obj;
        final int itemId = obj.getId();
        //noinspection ConstantConditions
        holder.title.setText(obj.getCountString());
        holder.deletedCheckBox.setChecked(countersToDelete.contains(itemId));
        if (inDeletionMode) {
            holder.deletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        countersToDelete.add(itemId);
                    } else {
                        countersToDelete.remove(itemId);
                    }
                }
            });
        } else {
            holder.deletedCheckBox.setOnCheckedChangeListener(null);
        }
        holder.deletedCheckBox.setVisibility(inDeletionMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CheckBox deletedCheckBox;
        public Item data;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textview);
            deletedCheckBox = view.findViewById(R.id.checkBox);
        }
    }
}
```

## Advice
1. Always close Realm instances when you're done with them
* On each thread that you called .getInstance(), you have to call .close() as well.
```
// On a different thread
public class MyThread extends Thread {
    private Realm realm;

    @Override
    public void run() {
        Looper.prepare();
        realm = Realm.getDefaultInstance();
        try {
            // Setup the handlers using the Realm instance
            Looper.loop();
        } finally {
            realm.close();
        }
    }
}

// For async tasks
protected Void doInBackground(Void... params) {
    Realm realm = Realm.getDefaultInstance();
    try {
        // Use the realm instance
    }finally {
        realm.close();
    }
    return null;
}

// Thread or runnable for short lived tasks
Thread thread = () -> {
    Realm realm = Realm.getDefaultInstace();
    try {
        // Use the realm instance
    }finally {
        realm.close();
    }
};
thread.start();

// If minSdkVersion >= 19 and Java >= 7
try (Realm realm = Realm.getDefaultInstance()) {
    // No need to close the Realm instance manually
}
```