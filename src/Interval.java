
import java.util.*;

public class Interval<S, E> {
    private S start;
    private E end;

    public Interval(S start, E end) {
        this.start = start;
        this.end = end;
    }

    public S getStart() {
        return start;
    }

    public E getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        try{
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Interval<?, ?> that = (Interval<?, ?>) o;
            return getStart().equals(that.getStart()) &&
                    getEnd().equals(that.getEnd());
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStart(), getEnd());
    }


}
