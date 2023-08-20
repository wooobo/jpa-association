package CGLib;

class HelloTarget {
    private String name;
    private String name2;

    public HelloTarget() {
    }

    public HelloTarget(String name) {
        this.name = name;
    }

    public String lazy() {
        return name;
    }

    public String lazy2() {
        return name2;
    }
    public String sayHello(String name) {
        return "Hello " + name;
    }

    public String sayHi(String name) {
        return "Hi " + name;
    }

    public String sayThankYou(String name) {
        return "Thank You " + name;
    }
}
