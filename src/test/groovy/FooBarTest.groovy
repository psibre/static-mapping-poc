import org.testng.annotations.Test

class FooBarTest {

    @Test
    void testFoo() {
        assert FooBar.foo.a.bar == '1'
        assert FooBar.foo.b.bar == '2'
    }

    @Test
    void testBar() {
        assert FooBar.bar.'1'.foo == 'a'
        assert FooBar.bar.'2'.foo == 'b'
    }
}
