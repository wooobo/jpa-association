package CGLib;

class HelloTargetImpl implements HelloService{
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }
    @Override
    public String sayThankYou(String name) {
        return "Thank You " + name;
    }
}
