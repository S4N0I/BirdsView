

class A {

    private boolean aBooleanField = true;
    private A anObject = new A();
    // private Unknown anotherObject;

    public void doSomething() {
        anObject.doSomething();
        new A().anObject.doSomething();
        System.out.println("Hello world");
        doSomething();
    }

    static {
        new B().foo();
        new B().foo();
    }
}
