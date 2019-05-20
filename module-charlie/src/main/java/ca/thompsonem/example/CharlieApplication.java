package ca.thompsonem.example;

public class CharlieApplication {

    public static void main(String[] args) {
        String name = new BravoService().getName();
        System.out.println("Hello, " + name + " from Charlie!");
    }

}
