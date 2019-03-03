

class B {

    public B(){
        foo();
    }

    public void foo() {
        new A().doSomething();
    }

    static {
        new B().foo();
        C.anotherFoo();
    }

    static class C {
        public C() {}

        public static void anotherFoo() {
            System.out.println("Yo");
        }
    }
}
