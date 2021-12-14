package tech.zeroed.ConveyorTest.utilities;

public class GridNode<T> {
    Grid<GridNode<T>> grid;
    public int x;
    public int y;

    public T entity;

    public GridNode(Grid<GridNode<T>> grid, int x, int y){
        this.grid = grid;
        this.x = x;
        this.y = y;
        this.entity = null;
    }

    public GridNode<T> setEntity(T entity){
        this.entity = entity;
        return this;
    }

    public boolean canBuild(){
        return entity == null;
    }

    public boolean hasEntity(){
        return entity != null;
    }

    @Override
    public String toString() {
        return entity.toString() + " "+x+","+y;
    }
}