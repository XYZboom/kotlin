// FILE: A.java

import io.reactivex.rxjava3.annotations.*;

public class A<T> {
    @Nullable public String field = null;

    @Nullable
    public String foo(@NonNull String x, @Nullable CharSequence y) {
        return "";
    }

    @NonNull
    public String bar() {
        return "";
    }

    @Nullable
    public T baz(@NonNull T x) { return x; }
}

// FILE: main.kt

fun main(a: A<String>, a1: A<String?>) {
    a.foo("", null)?.length
    a.foo("", null)<!UNSAFE_CALL!>.<!>length
    a.foo(<!NULL_FOR_NONNULL_TYPE!>null<!>, "")<!UNSAFE_CALL!>.<!>length
    a.foo("", null)!!.length

    a.bar().length
    a.bar()<!UNNECESSARY_NOT_NULL_ASSERTION!>!!<!>.length

    a.field?.length
    a.field<!UNSAFE_CALL!>.<!>length

    <!SMARTCAST_IMPOSSIBLE!>a.baz("")<!>.length
    a.baz("")?.length
    <!SMARTCAST_IMPOSSIBLE!>a.baz(<!NULL_FOR_NONNULL_TYPE!>null<!>)<!>.length

    a1.baz("")!!.length
    a1.baz(<!NULL_FOR_NONNULL_TYPE!>null<!>)!!.length
}