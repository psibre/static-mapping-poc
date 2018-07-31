import org.testng.annotations.Test

class FooBarTest {

    @Test
    void testFoo() {
        assert FooBar.foo.a.bar == '1'
        assert FooBar.foo.b.bar == '2'
        assert FooBar.foo.a.baz == 'α'
        assert FooBar.foo.b.baz == 'β'
    }

    @Test
    void testBar() {
        assert FooBar.bar.'1'.foo == 'a'
        assert FooBar.bar.'2'.foo == 'b'
        assert FooBar.bar.'1'.baz == 'α'
        assert FooBar.bar.'2'.baz == 'β'
    }

    @Test
    void testBaz() {
        assert FooBar.baz.α.foo == 'a'
        assert FooBar.baz.β.foo == 'b'
        assert FooBar.baz.α.bar == '1'
        assert FooBar.baz.β.bar == '2'
    }
}
