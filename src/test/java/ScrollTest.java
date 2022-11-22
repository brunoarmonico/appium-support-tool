import elements.scroll.Scroll;
import elements.scroll.Toward;
import org.testng.annotations.Test;

public class ScrollTest {

    @Test
    public void testScroll() {
        new Scroll(null).scrollVertical(null, 10, 1, 1, Toward.RIGHT);
    }

}
