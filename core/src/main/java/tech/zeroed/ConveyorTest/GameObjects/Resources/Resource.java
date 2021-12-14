package tech.zeroed.ConveyorTest.GameObjects.Resources;

public class Resource {
    public enum Type{
        Slime
    }

    public Type type;
    public int quantity;

    public Resource(Type type, int quantity){
        this.type = type;
        this.quantity = quantity;
    }
}
