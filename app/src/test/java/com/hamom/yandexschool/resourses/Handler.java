package android.os;

/**
 * Created by hamom on 09.12.17.
 */

public class Handler {

    public Handler() {
    }

    public Handler(Looper looper) {
    }

    public final boolean post(Runnable r) {
        r.run();
        return true;
    }
}
