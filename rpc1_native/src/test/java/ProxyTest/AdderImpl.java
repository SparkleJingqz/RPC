package ProxyTest;

public class AdderImpl implements Adder{
    @Override
    public int add1(int a) {
        return ++a;
    }
}
