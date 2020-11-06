package geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DotList extends ArrayList<Dot> implements List<Dot> {
    private boolean closed = true;

    public DotList(List<Dot> dots){
        super(dots);
    }

    public DotList(){
        super();
    }

    public DotList(Dot... dots){
        this(Arrays.asList(dots));
    }

    public List<DotPair> getPairs() {
        List<DotPair> dotPairList = new ArrayList<>();

        for (int i = 0; i < this.size(); i++){
            if(i == this.size()-1){ // last iteration
                if(closed) {
                    dotPairList.add(new DotPair(this.get(i), this.get(0)));
                }
            }else{
                dotPairList.add(new DotPair(this.get(i),this.get(i+1)));
            }
        }
        return dotPairList;
    }

    public boolean isClosed() {
        return closed;
    }

    public DotList setClosed(boolean closed) {
        this.closed = closed;
        return this;
    }
}
